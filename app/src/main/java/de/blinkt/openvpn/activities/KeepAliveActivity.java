package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class KeepAliveActivity extends Activity {

	private static KeepAliveActivity activity;
	private String TAG = "KeepAliveActivity";

	public static void launch(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean screen = pm.isScreenOn();
		if (!screen) {
			Intent intent = new Intent(context, KeepAliveActivity.class);
			context.startActivity(intent);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "打开1PX ACTIVITY");
		this.activity = this;
		Window window = getWindow();
		window.setGravity(Gravity.LEFT | Gravity.TOP);
		WindowManager.LayoutParams params = window.getAttributes();
		params.x = 1;
		params.y = 1;
		window.setAttributes(params);
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "关闭1PX ACTIVITY");
		super.onDestroy();
	}

	public static void finishActivity() {
		activity.finish();
	}
}
