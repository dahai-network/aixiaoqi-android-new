package de.blinkt.openvpn.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aixiaoqi.socket.EventBusUtil;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.service.CallPhoneService;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * 自启动
 */
public class StartBroadcast extends BroadcastReceiver implements InterfaceCallback{

	private static final String TAG = "StartBroadcast";

	BluetoothManager mBluetoothManager;
	BluetoothAdapter mBluetoothAdapter;
	private String deviceAddress;
	private UartService mService;
	//android.intent.action.ACTION_SHUTDOWN
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w(TAG, "StartBroadcast receive action is " + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			initialize(context);
			//检测蓝牙是否打开
			if(!isBluetoothOpen()){

				return ;
				//6.0以上的系统必须要开启定位。
			}else if  (Build.VERSION.SDK_INT >= 23 && !NetworkUtils.isLocationOpen(context)) {

				return;
			}else{
				//本地是否保存有设备地址和设备类型
				if(TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))||TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))){
					//如果没有保存设备地址和设备类型则去请求
						CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
				}else{
					//有绑定过，则搜索设备，没有搜索到，就用通知栏的方式提示用户
					BluetoothConstant.IS_BIND = true;
					//搜索到设备，则连接设备。
					if (!ICSOpenVPNApplication.getInstance().isServiceRunning(UartService.class.getName())) {
						Intent bindIntent = new Intent(context, UartService.class);
						try {
//							context.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
						} catch (Exception e) {
//							initBrocast();
							e.printStackTrace();
						}
					}

				}

			}



		}
	}

//	private ServiceConnection mServiceConnection = new ServiceConnection() {
//		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
//			mService = ((UartService.LocalBinder) rawBinder).getService();
//			//存在Application供全局使用
//			ICSOpenVPNApplication.uartService = mService;
//			initBrocast();
//		}
//
//		public void onServiceDisconnected(ComponentName classname) {
//			mService = null;
//		}
//	};
//	private void initBrocast() {
//		if (bleMoveReceiver == null) {
//			bleMoveReceiver = new ReceiveBLEMoveReceiver();
//			LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(bleMoveReceiver, makeGattUpdateIntentFilter());
//			LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(updateIndexTitleReceiver, makeGattUpdateIntentFilter());
//			registerReceiver(screenoffReceive, screenoffIntentFilter());
//			//打开蓝牙服务后开始搜索
//			searchBLE();
//		}
//	}

	private boolean initialize(Context context) {
		// For API level 18 and above, get a reference to BluetoothAdapter through
		// BluetoothManager.
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager)context. getSystemService(Context.BLUETOOTH_SERVICE);
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

	private boolean isBluetoothOpen() {
		if(mBluetoothAdapter!=null)
		return mBluetoothAdapter.isEnabled();
		else{
			return false;
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		//如果没有绑定过则所有的操作结束。否则继续下面的操作
		if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
			GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
			if (object.getStatus() == 1) {
				if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
					if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
						deviceAddress = getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI();
						if (deviceAddress != null) {
							deviceAddress = deviceAddress.toUpperCase();
							BluetoothConstant.IS_BIND = true;
						}
						SharedUtils utils = SharedUtils.getInstance();

						utils.writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI().toUpperCase());
						//按MAC地址保存版本号
						if (!TextUtils.isEmpty(deviceAddress))
							utils.writeString(deviceAddress, getBindDeviceHttp.getBlueToothDeviceEntityity().getVersion());
						//防止返回“”或者null
						String deviceTypeStr = getBindDeviceHttp.getBlueToothDeviceEntityity().getDeviceType();
						if (!TextUtils.isEmpty(deviceTypeStr)) {
							int deviceType = Integer.parseInt(deviceTypeStr);
							if (deviceType == 0) {
								utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNITOYS);
							} else {
								utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNIBOX);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {

	}
}
