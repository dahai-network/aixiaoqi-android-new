package de.blinkt.openvpn.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.CommenActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BindDeviceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.IsBindHttp;
import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.ServiceOperationEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;


public class BindDeviceActivity extends CommenActivity implements InterfaceCallback, DialogInterfaceTypeBase {

	public static int FAILT = 4;
	@BindView(R.id.stopImageView)
	ImageView stopImageView;
	@BindView(R.id.all_device_rv)
	RecyclerView allDeviceRv;
	@BindView(R.id.connectedRelativeLayout)
	RelativeLayout connectedRelativeLayout;
	private Handler mHandler;
	private Handler findDeviceHandler;
	private Map deviceMap = new HashMap();
	private BluetoothAdapter mBluetoothAdapter;
	private static final long SCAN_PERIOD = 120000; //120 seconds
	private String deviceAddress = "";
	SharedUtils utils = SharedUtils.getInstance();
	private DialogBalance noDevicedialog;
	private int REQUEST_ENABLE_BT = 2;
	private String TAG = "BindDeviceActivity";
	private UartService mService = ICSOpenVPNApplication.uartService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {//没有发现设备
			CommonTools.showShortToast(this, getString(R.string.bluetooth_ble_not_support));
			finish();
			return;
		}
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			finish();
			return;
		}
		setContentView(R.layout.activity_bind_device);
		EventBus.getDefault().register(this);
		ButterKnife.bind(this);
		mHandler = new Handler();
		findDeviceHandler = new Handler();
		scanLeDevice(true);
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mService != null && mService.mConnectionState != UartService.STATE_CONNECTED && !isStartFindDeviceDelay) {
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						showDialog();
					}
				}
			}, SCAN_PERIOD);

			mBluetoothAdapter.startLeScan(mLeScanCallback);

		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}

	}

	private void showDialog() {
		//不能按返回键，只能二选其一
		noDevicedialog = new DialogBalance(BindDeviceActivity.this, BindDeviceActivity.this, R.layout.dialog_balance, 2);
		noDevicedialog.setCanClickBack(false);
		noDevicedialog.changeText(getResources().getString(R.string.no_find_device), getResources().getString(R.string.retry));
	}

	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		if (noDevicedialog != null && noDevicedialog.getDialog() != null && noDevicedialog.getDialog().isShowing()) {
			noDevicedialog.getDialog().dismiss();
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	//是否打开找到设备的计时器
	private boolean isStartFindDeviceDelay;
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {

				@Override
				public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (device.getName() == null) {
										return;
									}
									Log.i("test", "find the device:" + device.getName() + ",rssi :" + rssi);
									if (device.getName().contains(Constant.BLUETOOTH_NAME)) {
										deviceMap.put(device.getAddress(), rssi);
										if (!isStartFindDeviceDelay) {
											findDeviceHandler.postDelayed(new Runnable() {
												@Override
												public void run() {
													List<Map.Entry<String, Integer>> infos =
															new ArrayList<>(deviceMap.entrySet());
													Collections.sort(infos, new Comparator<Map.Entry<String, Integer>>() {
														public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
															return (o2.getValue() - o1.getValue());
//																return (o1.getKey()).toString().compareTo(o2.getKey());
														}
													});
													for (int i = 0; i < infos.size(); i++) {
														String id = infos.get(i).toString();
														Log.i(TAG, "排序后：" + id);
													}
													//排序后连接操作
													scanLeDevice(false);
													deviceAddress = infos.get(0).getKey();
													utils.writeString(Constant.IMEI, deviceAddress);
													IsBindHttp http = new IsBindHttp(BindDeviceActivity.this, HttpConfigUrl.COMTYPE_ISBIND_DEVICE, deviceAddress);
													new Thread(http).start();

													isStartFindDeviceDelay = false;
												}
											}, 5000);
											isStartFindDeviceDelay = true;
										}
									}
								}
							});
						}
					});
				}
			};


	@OnClick(R.id.stopImageView)
	public void onClick() {
		scanLeDevice(false);
		finish();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_ISBIND_DEVICE) {
			IsBindHttp http = (IsBindHttp) object;
			if (http.getIsBindEntity().getBindStatus() == 0 && http.getStatus() == 1) {
				if (mService != null) {
					mService.connect(deviceAddress);
				} else {
					CommonTools.showShortToast(BindDeviceActivity.this, getString(R.string.connect_failure));
					restartUartService();
					finish();
				}
			} else {
				CommonTools.showShortToast(this, "该设备已经绑定过了！");
				scanLeDevice(false);
				finish();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_BIND_DEVICE) {
			Log.i(TAG, "绑定设备返回：" + object.getMsg() + ",返回码：" + object.getStatus());
			if (object.getStatus() == 1) {
				Log.i("test", "保存设备名成功");
				utils.writeString(Constant.IMEI, deviceAddress);
				ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
				if (!utils.readBoolean(Constant.ISHAVEORDER, true)) {
					entity.setStatus(getString(R.string.index_no_packet));
					entity.setStatusDrawableInt(R.drawable.index_no_packet);
					EventBus.getDefault().post(entity);
				}
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
			finish();
		}
	}

	private void restartUartService() {
		Log.i(TAG, "restart Uart服务");
		new Thread(new Runnable() {
			@Override
			public void run() {
				//关闭UartService服务
				ServiceOperationEntity serviceOperationEntity = new ServiceOperationEntity();
				serviceOperationEntity.setServiceName(UartService.class.getName());
				serviceOperationEntity.setOperationType(ServiceOperationEntity.REMOVE_SERVICE);
				EventBus.getDefault().post(serviceOperationEntity);
				CommonTools.delayTime(200);
				serviceOperationEntity.setOperationType(ServiceOperationEntity.CREATE_SERVICE);
				EventBus.getDefault().post(serviceOperationEntity);
			}
		}).start();
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(mContext, errorMessage);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(mContext, getString(R.string.no_wifi));
	}

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					scanLeDevice(true);
				}
			}).start();
		} else {
			onBackPressed();
			finish();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onVersionEntity(BluetoothMessageCallBackEntity entity) {
		String type = entity.getBlueType();
		if (type == BluetoothConstant.BLUE_VERSION && !CommonTools.isFastDoubleClick(300)) {
			Log.i(TAG, "蓝牙注册返回:" + entity.getBlueType() + ",参数：MEI：" + utils.readString(Constant.IMEI) + ",版本号：" + utils.readString(Constant.BRACELETVERSION));
			BindDeviceHttp bindDevicehttp = new BindDeviceHttp(BindDeviceActivity.this, HttpConfigUrl.COMTYPE_BIND_DEVICE, utils.readString(Constant.IMEI), entity.getBraceletversion());
			new Thread(bindDevicehttp).start();
		}
	}
}
