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
import de.blinkt.openvpn.activities.Base.CommenActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.util.SharedUtils;

public class LaunchActivity extends BaseActivity {


	private SharedUtils sharedUtils;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		initSet();
	}

	private void initSet() {
		//将注册流程的状态重置
		SocketConstant.REGISTER_STATUE_CODE = 0;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sharedUtils = SharedUtils.getInstance();
					String token = sharedUtils.readString(Constant.TOKEN);
					Thread.sleep(1000);
					if(!sharedUtils.readBoolean(IntentPutKeyConstant.IS_START_UP,false)){
						toActivity(StartUpHomePageActivity.class);
					}
					else if (!TextUtils.isEmpty(token)) {
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
