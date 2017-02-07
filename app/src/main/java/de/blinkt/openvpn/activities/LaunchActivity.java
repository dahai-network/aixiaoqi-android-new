package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;

import com.aixiaoqi.socket.SocketConstant;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

public class LaunchActivity extends BaseActivity   {

	@BindView(R.id.rootRelativeLayout)
	RelativeLayout rootRelativeLayout;
	private SharedUtils sharedUtils;
	private String TAG = "LaunchActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		//将注册流程的状态重置
		SocketConstant.REGISTER_STATUE_CODE = 0;
		Log.i(TAG,"重置REGISTER_STATUE_CODE");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sharedUtils = SharedUtils.getInstance();
					String token = sharedUtils.readString(Constant.TOKEN);
					Log.e("tokenlaunch","token="+token);
					Thread.sleep(1000);
					if (!TextUtils.isEmpty(token)) {
						if (System.currentTimeMillis() - sharedUtils.readLong(Constant.LOGIN_DATA) > (15*60 * 60 * 24 * 1000)){
							toLogin();
							return;
						}else{
							toProMainActivity();
						}
					} else {

						toLogin();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("LaunchActivity  e=","e.printStackTrace()");
					toLogin();
				}
			}
		}).start();
	}

	private void toLogin() {
		toActivity(LoginMainActivity.class);
		finish();
	}





	private void toProMainActivity() {
		toActivity(ProMainActivity.class);
		finish();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		sharedUtils=null;
	}



}
