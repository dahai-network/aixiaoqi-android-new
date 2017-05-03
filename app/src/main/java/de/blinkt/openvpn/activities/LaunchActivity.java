package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import com.aixiaoqi.socket.SocketConstant;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.util.SharedUtils;

public class LaunchActivity extends BaseActivity {


	private SharedUtils sharedUtils;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_launch);
		initSet();
	}

	private void initSet() {
		//将注册流程的状态重置
		SocketConstant.REGISTER_STATUE_CODE = 0;
		Handler handler=new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				sharedUtils = SharedUtils.getInstance();
				String token = sharedUtils.readString(Constant.TOKEN);
				if(!sharedUtils.readBoolean(IntentPutKeyConstant.IS_START_UP,false)){
					toStartUpHomePage();
				}
				else if (!TextUtils.isEmpty(token)) {
					if (System.currentTimeMillis() - sharedUtils.readLong(Constant.LOGIN_DATA) > (15*60 * 60 * 24 * 1000)){
						toLogin();
					}else{
						toProMainActivity();
					}
				} else {
					toLogin();
				}
			}
		},1000);


	}

	private void toLogin() {
		toActivity(LoginMainActivity.class);
		finish();
	}

	private void toStartUpHomePage() {

		toActivity(StartUpHomePageActivity.class);
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
