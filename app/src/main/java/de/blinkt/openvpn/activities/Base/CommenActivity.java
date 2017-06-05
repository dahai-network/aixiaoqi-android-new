package de.blinkt.openvpn.activities.Base;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.views.MyProgressDialog;

/**
 * Created by Administrator on 2016/9/2 0002.
 */
public class CommenActivity extends FragmentActivity {
	protected Context mContext;
	protected MyProgressDialog myProgressDialog;
	protected ICSOpenVPNApplication application;
	private Configuration config;
	public String TAG = getClass().getSimpleName().toString();

	protected boolean isAndroidTV() {
		final UiModeManager uiModeManager = (UiModeManager) getSystemService(FragmentActivity.UI_MODE_SERVICE);
		return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
	}

	protected void w(String message) {
		if (Constant.PRINT_LOGS)
			Log.w(TAG, message);
	}

	protected void e(String message) {
		if (Constant.PRINT_LOGS)
			Log.e(TAG, message);
	}

	protected void d(String message) {
		if (Constant.PRINT_LOGS)
			Log.d(TAG, message);
	}

	protected void i(String message) {
		if (Constant.PRINT_LOGS)
			Log.i(TAG, message);
	}


	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (isAndroidTV()) {
			requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
		}
		//不需要的背景删掉
		setRequestedOrientation(ActivityInfo
				.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setBackgroundDrawable(null);
		application = (ICSOpenVPNApplication) getApplicationContext();
		mContext = this;
		application.addTask(this);
		init();
		super.onCreate(savedInstanceState);
	}

	public void toActivity(Class<?> activity) {
		Intent intent = new Intent(mContext, activity);
		startActivity(intent);
	}


	public void toActivity(Intent intent) {
		startActivity(intent);
	}

	private void init() {
		myProgressDialog = new MyProgressDialog(mContext, R.style.MyAlertDialog);
		myProgressDialog.setMyCancelable(true);
		myProgressDialog.setMyTouchOutside(false);

	}

	/**
	 * 显示进度条
	 *
	 * @param message
	 */
	public void showProgress(String message, boolean isCanTouchOutside) {
		try {
			myProgressDialog.setMyMessage(message);
			myProgressDialog.setCancelable(isCanTouchOutside);
			myProgressDialog.myShow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showProgress(int id) {
		myProgressDialog.setMyMessage(getString(id));
		myProgressDialog.myShow();
	}

	public void showProgress(String message) {
		myProgressDialog.setMyMessage(message);
		myProgressDialog.myShow();
	}

	/**
	 * 显示默认的进度条
	 */
	public void showDefaultProgress() {
		myProgressDialog.setMyMessage("数据加载中");
		myProgressDialog.myShow();
	}

	public void dismissProgress() {
		if (myProgressDialog != null) {
			myProgressDialog.mydismiss();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//释放config
		config = null;
		dismissProgress();
		myProgressDialog = null;
	}
}
