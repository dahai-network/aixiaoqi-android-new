/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.blinkt.openvpn.bluetooth.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class UartService extends Service implements Serializable {
	private final static String TAG = UartService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothGatt mBluetoothGatt;
	public int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	public static final int STATE_CONNECTED = 2;


	public final static String ACTION_GATT_CONNECTED =
			"com.nordicsemi.nrfUART.ACTION_GATT_CONNECTED";//蓝牙连接
	public final static String ACTION_GATT_DISCONNECTED =
			"com.nordicsemi.nrfUART.ACTION_GATT_DISCONNECTED";//蓝牙断开
	public final static String ACTION_GATT_SERVICES_DISCOVERED =
			"com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED";//没有找到蓝牙设备
	public final static String ACTION_DATA_AVAILABLE =
			"com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE";//蓝牙往手机发送数据
	public final static String EXTRA_DATA =
			"com.nordicsemi.nrfUART.EXTRA_DATA";//蓝牙发送额外数据
	public final static String DEVICE_DOES_NOT_SUPPORT_UART =
			"com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";//不支持操作

	public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	public static final UUID RX_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
	//写
	public static final UUID RX_CHAR_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
	//读
	public static final UUID TX_CHAR_UUID1 = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
	//GATT通用特征值
	private final static UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
	private final static UUID SERVICE_CHANGED_CHARACTERISTIC = UUID.fromString("00002A05-0000-1000-8000-00805f9b34fb");
	//	public static final UUID TX_CHAR_UUID2 = UUID.fromString("6E400004-B5A3-F393-E0A9-E50E24DCCA9F");
//	public static final UUID TX_CHAR_UUID3 = UUID.fromString("6E400005-B5A3-F393-E0A9-E50E24DCCA9F");
	private List<BluetoothGattService> BluetoothGattServices;
	// Implements callback methods for GATT events that the app cares about.  For example,
	// Implements callback methods for GATT events that the app cares about.  For example,
	// connection change and services discovered.
	//蓝牙回调
	ArrayList<String> messages;
	private boolean isWholeDataPackage = false;//怕最后一个包搞混了
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		//监听蓝牙连接状态
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				CommonTools.delayTime(1000);
				boolean isFindServiceSuccess = mBluetoothGatt.discoverServices();
				Log.i(TAG, "Attempting to start service discovery:" + isFindServiceSuccess);

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				Log.i(TAG, "Disconnected from GATT server.");
				broadcastUpdate(intentAction);
			}
		}

		//监听搜索蓝牙设备
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				Log.w(TAG, "mBluetoothGatt = " + mBluetoothGatt);
//				BluetoothGattServices = mBluetoothGatt.getServices();
//				Log.e("getService", "mBluetoothGatt size = " + BluetoothGattServices.size());
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

		//蓝牙发送数据到应用
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
										 BluetoothGattCharacteristic characteristic,
										 int status) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
											BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}
	};

	//发送广播
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).sendBroadcast(intent);
	}

	//	private String dataType="";
//	private String messageFromBlueTooth;
	//发送广播
	private void broadcastUpdate(final String action,
								 final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		if (TX_CHAR_UUID1.equals(characteristic.getUuid())) {
			byte[] txValue = characteristic.getValue();
			int lengthData = (txValue[1] & 0x7f) + 1;
			int dataStatue = txValue[1] & 0x80;


			String messageFromBlueTooth = HexStringExchangeBytesUtil.bytesToHexString(characteristic.getValue());
			Log.e("UartService", messageFromBlueTooth);
			if (lengthData == 1 && dataStatue == 0x80) {
				//TODO 单包处理
//				messages.add(messageFromBlueTooth);
				ArrayList<String> onePackagemessage = new ArrayList<String>();
				onePackagemessage.add(messageFromBlueTooth);
				intent.putStringArrayListExtra(EXTRA_DATA, onePackagemessage);
				LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).sendBroadcast(intent);
				return;
			} else {
				//TODO 多包处理
				if (TextUtils.isEmpty(messageFromBlueTooth)) {
					return;
				}
				if (messages == null) {
					messages = new ArrayList<>();
				}
//				if (lengthData - 1 == 0) {
//					dataType = messageFromBlueTooth.substring(6, 10);
//				}
				messages.add(messageFromBlueTooth);
				if (messages.size() < lengthData) {
					if (dataStatue == 0x80) {
						isWholeDataPackage = true;
					}
					return;
				}
				if (isWholeDataPackage || dataStatue == 0x80) {
					sortMessage();
					intent.putStringArrayListExtra(EXTRA_DATA, messages);
					LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).sendBroadcast(intent);
				}

			}
//			intent.putExtra(EXTRA_DATA, messageFromBlueTooth);
//			if(messages.size()==0){
//				return;
//			}

		}


	}

	private void sortMessage() {
		if (messages.size() > 1) {
			ArrayList<String> messagesList = new ArrayList<>();
			int z = 0;
			for (int i = 0; i < messages.size(); i++) {
				for (int j = 0; j < messages.size(); j++) {
					if ((Integer.parseInt(messages.get(j).substring(2, 4), 16) & 127) == i) {
						z = j;
						Log.e("messages", "messagesz" + z);
						break;
					}
				}
				messagesList.add(messages.get(z));
			}
			messages.clear();
			messages = messagesList;
			for (int i = 0; i < messages.size(); i++) {
				Log.e("messages", "messages=" + messages.get(i));
			}
			Log.e("messages", "===========================");
		}
	}

	public class LocalBinder extends Binder {
		public UartService getService() {
			return UartService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// After using a given device, you should make sure that BluetoothGatt.close() is called
		// such that resources are cleaned up properly.  In this particular example, close() is
		// invoked when the UI is disconnected from the Service.
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	//初始化蓝牙
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 *
	 * @param address The device address of the destination device.
	 * @return Return true if the connection is initiated successfully. The connection result
	 * is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	//通过mac地址连接设备
	public boolean connect(String address) {
		if (mBluetoothAdapter == null || address == null) {
			Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
			return false;
		}
		address = address.toUpperCase();

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			//LogUtil.info("-------------关闭mBluetoothGatt");
		}
		// We want to directly connect to the device, so we are setting the autoConnect
		// parameter to false.
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		Log.d(TAG, "Trying to create a new connection.");
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The disconnection result
	 * is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	//断开连接
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.disconnect();
		mConnectionState = STATE_DISCONNECTED;
		broadcastUpdate(ACTION_GATT_DISCONNECTED);
//		refreshDeviceCache();
		close();
//		mBluetoothGatt.close();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure resources are
	 * released properly.
	 */

	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		Log.w(TAG, "mBluetoothGatt closed");
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
	 * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic Characteristic to act on.
	 * @param enabled If true, enable notification.  False otherwise.
	 */
	/*
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);


        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }*/

	/**
	 * Enable TXNotification
	 *
	 * @return
	 */
	public void enableTXNotification() {
		boolean isUpgrade = false;
		if (mBluetoothGatt == null) {
			showMessage("mBluetoothGatt null" + mBluetoothGatt);
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		BluetoothGattService RxService = null;
		RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
		Log.i("getService", "获取服务：" + RxService);

		if (RxService == null) {
			showMessage("Rx service not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		setDescriptor(RxService, TX_CHAR_UUID1);
//		setDescriptor(RxService, TX_CHAR_UUID2);
//		setDescriptor(RxService, TX_CHAR_UUID3);
	}

	/**
	 * When the device is bonded and has the Generic Attribute service and the Service Changed characteristic this method enables indications on this characteristic.
	 * In case one of the requirements is not fulfilled this method returns <code>false</code>.
	 *
	 * @return <code>true</code> when the request has been sent, <code>false</code> when the device is not bonded, does not have the Generic Attribute service, the GA service does not have
	 * the Service Changed characteristic or this characteristic does not have the CCCD.
	 */
	public boolean ensureServiceChangedEnabled() {
		final BluetoothGatt gatt = mBluetoothGatt;
		if (gatt == null)
			return false;

		// The Service Changed indications have sense only on bonded devices
		final BluetoothDevice device = gatt.getDevice();
		if (device.getBondState() != BluetoothDevice.BOND_BONDED)
			return false;

		final BluetoothGattService gaService = gatt.getService(GENERIC_ATTRIBUTE_SERVICE);
		if (gaService == null)
			return false;

		final BluetoothGattCharacteristic scCharacteristic = gaService.getCharacteristic(SERVICE_CHANGED_CHARACTERISTIC);
		if (scCharacteristic == null)
			return false;
		return true;
	}

	//用于某个接收的UUID写入mBluetoothGatt的监听callback里面。在onCharacteristicChanged()会产生响应
	public void setDescriptor(BluetoothGattService rxService, UUID uuid) {
		CommonTools.delayTime(150);
		BluetoothGattCharacteristic TxChar = rxService.getCharacteristic(uuid);
		if (TxChar == null) {
			showMessage("Tx charateristic not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(TxChar, true);

		BluetoothGattDescriptor descriptor = TxChar.getDescriptor(CCCD);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//		mBluetoothGatt.readDescriptor(descriptor);
		mBluetoothGatt.writeDescriptor(descriptor);
	}

	public boolean writeRXCharacteristic(byte[] value) {
		//如果mBluetoothGatt为空，意味着连接中断，所以不允许继续传输数据
		if (mBluetoothGatt == null) {
			Log.e("Blue_Chanl", "蓝牙已断开，发送失败！");
			mConnectionState = STATE_DISCONNECTED;
			broadcastUpdate(ACTION_GATT_DISCONNECTED);
			return false;
		}
		BluetoothGattService RxService = null;
		if (RxService == null) {
			RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
		}

		showMessage("mBluetoothGatt null" + mBluetoothGatt);
		if (RxService == null) {
			showMessage("Rx service not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return false;
		}
		BluetoothGattCharacteristic RxChar = null;
		if (RxChar == null) {
			RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
		}
		if (RxChar == null) {
			showMessage("Rx charateristic not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return false;
		}
		RxChar.setValue(value);
		boolean status = mBluetoothGatt.writeCharacteristic(RxChar);

		Log.e("Blue_Chanl", "write TXchar - status=" + status);
		if (!status) {
			try {
				Thread.sleep(500);
				status = mBluetoothGatt.writeCharacteristic(RxChar);
				Log.e("Blue_Chanl", "重发：write TXchar - status=" + status);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void showMessage(String msg) {
		Log.e(TAG, msg);
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This should be
	 * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
	 *
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null) return null;

		return mBluetoothGatt.getServices();
	}

	public boolean isOpenBlueTooth() {
		if (mBluetoothAdapter != null) {
			if (mBluetoothAdapter.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public boolean isConnecttingBlueTooth() {
		return mConnectionState == STATE_CONNECTING;
	}

	/**
	 * * Clears the internal cache and forces a refresh of the services from the
	 * * remote device.
	 */
	public void refreshDeviceCache() {
		//关闭蓝牙
		mBluetoothAdapter.disable();
		//打开蓝牙
		mBluetoothAdapter.enable();
	}


}
