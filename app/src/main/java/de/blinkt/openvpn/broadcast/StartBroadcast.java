package de.blinkt.openvpn.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 自启动
 */
public class StartBroadcast extends BroadcastReceiver  {

	private static final String TAG = "StartBroadcast";
	BluetoothManager mBluetoothManager;
	BluetoothAdapter mBluetoothAdapter;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w(TAG, "StartBroadcast receive action is " + intent.getAction());
//		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//			initialize(context);
//			//检测蓝牙是否打开
//			if(TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.TOKEN))){
//				Log.i(TAG,"token is null");
//				return ;
//			}
//			else	if(!isBluetoothOpen()){
//				Log.i(TAG,"bluetooth is close");
//				return ;
//				//6.0以上的系统必须要开启定位。
//			}else if  (Build.VERSION.SDK_INT >= 23 && !NetworkUtils.isLocationOpen(context)) {
//				Log.i(TAG,"location is close");
//				return;
//			}else{
//				if (!ICSOpenVPNApplication.getInstance().isServiceRunning(SimRegisterFlowService.class.getName())) {
//					Intent intentSimRegisterFlow = new Intent(context, SimRegisterFlowService.class);
//					context.startService(intentSimRegisterFlow);
//				}
//			}
//		}
	}



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


}
