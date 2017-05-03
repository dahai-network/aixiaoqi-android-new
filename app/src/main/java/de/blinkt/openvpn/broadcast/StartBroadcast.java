package de.blinkt.openvpn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.blinkt.openvpn.service.CallPhoneService;

public class StartBroadcast extends BroadcastReceiver {

	private static final String TAG = "StartBroadcast";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.w(TAG, "StartBroadcast receive action is " + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent serivceIntent = new Intent();
			serivceIntent.setClass(context, CallPhoneService.class);
			context.startService(serivceIntent);
		}
	}
}
