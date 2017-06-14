package de.blinkt.openvpn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseSensorActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.service.CallPhoneService;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.PhoneFormatUtil;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.SearchConnectterHelper;
import de.blinkt.openvpn.views.T9TelephoneDialpadView;

import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLCONTROLVOIDE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLHANGUP;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLPHONEQUIET;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class CallPhoneNewActivity extends BaseSensorActivity implements View.OnClickListener,T9TelephoneDialpadView.OnT9TelephoneDialpadView {
	TextView phonenumtxt;
	TextView callStatustxt;
	TextView mtview;
	TextView calmTextView;
	TextView cancelcallbtn;
	Chronometer timer;
	String maxinumPhoneCallTime;
	ConnectedReceive connectedReceive;
	int cellPhoneType;
	public static boolean isForeground = false;
	TextView keyboard;
	T9TelephoneDialpadView t9dialpadview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callphone);
		cellPhoneType = getIntent().getIntExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, -1);
		initView();
		initData();
		addListener();
		setQuite();
		IntentFilter filter = getIntentFilter();
		connectedReceive = new ConnectedReceive();
		registerReceiver(connectedReceive, filter);
		callPhone();
	}

	@Override
	public void onAddDialCharacter(String addCharacter) {
		if(!TextUtils.isEmpty(addCharacter)){
			ICSOpenVPNApplication.the_sipengineReceive.SendDtmf(0,addCharacter);

		}
	}

	@Override
	public void onDialInputTextChanged(String curCharacter) {
		if(!TextUtils.isEmpty(curCharacter)){
			phonenumtxt.setText(curCharacter);
		}
	}

	@Override
	public void onDialInputTextChanging(String curCharacter) {

	}

	@Override
	public void onDeleteDialCharacter(String deleteCharacter) {

	}



	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
	}

	@NonNull
	private IntentFilter getIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(CallPhoneService.endFlag);
		filter.addAction(CallPhoneService.connectedFlag);
		filter.addAction(CallPhoneService.reportFlag);
		filter.addAction(CallPhoneService.waitConnected);
		filter.addAction(CallPhoneService.CALL_FAIL);
		filter.addAction(CallPhoneService.callProcessing);
		return filter;
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//		}
		return true;
	}
	LinearLayout llControlVoide;
	ImageView hideKeyboard;

	private void initView() {
		phonenumtxt = (TextView) findViewById(R.id.phonenumtxt);
		callStatustxt = (TextView) findViewById(R.id.call_statustxt);
		mtview = (TextView) findViewById(R.id.mtview);
		timer = (Chronometer) findViewById(R.id.chronometer);
		calmTextView = (TextView) findViewById(R.id.calmTextView);
		cancelcallbtn = (TextView) findViewById(R.id.cancelcallbtn);
		keyboard = (TextView) findViewById(R.id.keyboard);
		t9dialpadview = (T9TelephoneDialpadView) findViewById(R.id.t9dialpadview);
		llControlVoide = (LinearLayout) findViewById(R.id.ll_control_voide);
		hideKeyboard = (ImageView) findViewById(R.id.hide_keyboard);
		t9dialpadview.phoneRelativeLayout.setVisibility(View.GONE);
		t9dialpadview.setOnT9TelephoneDialpadView(this);
		t9dialpadview.searchEtHidden();
		t9dialpadview.setBtnColor( Color.WHITE);
		t9dialpadview.setLineBackgroundColor(R.color.transparent_60);
		displayStatus(R.string.calling);

	}

	ContactRecodeEntity contactRecodeEntity;

	private void initData() {
		contactRecodeEntity = (ContactRecodeEntity) getIntent().getSerializableExtra(IntentPutKeyConstant.DATA_CALLINFO);
		maxinumPhoneCallTime = getIntent().getStringExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME);
		if (TextUtils.isEmpty(maxinumPhoneCallTime)) {
			maxinumPhoneCallTime = "0";
		}
		if (contactRecodeEntity == null || TextUtils.isEmpty(contactRecodeEntity.getPhoneNumber())) {
			CommonTools.showShortToast(this, "电话号码不能为空");
			return;
		}
		String realname = SearchConnectterHelper.getContactNameByPhoneNumber(this, contactRecodeEntity.getPhoneNumber());
		if (!TextUtils.isEmpty(contactRecodeEntity.getName()))
			phonenumtxt.setText(contactRecodeEntity.getName());
		else if (!TextUtils.isEmpty(realname)) {
			phonenumtxt.setText(realname);
		} else
			phonenumtxt.setText(contactRecodeEntity.getPhoneNumber());
	}


	private void addListener() {
		calmTextView.setOnClickListener(this);
		cancelcallbtn.setOnClickListener(this);
		mtview.setOnClickListener(this);
		keyboard.setOnClickListener(this);
		hideKeyboard.setOnClickListener(this);
	}

	private void callPhone() {

		if (contactRecodeEntity == null || TextUtils.isEmpty(contactRecodeEntity.getPhoneNumber())) {
			CommonTools.showShortToast(this, "电话号码不能为空");
			return;
		}

		if (ICSOpenVPNApplication.the_sipengineReceive == null) {
			CommonTools.showShortToast(this, "电话异常请稍后重试");
			return;
		}

		if (contactRecodeEntity.getPhoneNumber().startsWith("sip:")) {
			ICSOpenVPNApplication.the_sipengineReceive.MakeUrlCall(contactRecodeEntity.getPhoneNumber());

		} else if (cellPhoneType == Constant.NETWORK_CELL_PHONE) {
			ICSOpenVPNApplication.the_sipengineReceive.MakeCall("981" + PhoneFormatUtil.deleteprefix("-", contactRecodeEntity.getPhoneNumber()) + "#" + maxinumPhoneCallTime);
		} else if (cellPhoneType == Constant.SIM_CELL_PHONE) {
			ICSOpenVPNApplication.the_sipengineReceive.MakeCall("986" + SocketConstant.REGISTER_REMOTE_ADDRESS + SocketConstant.REGISTER_ROMOTE_PORT + PhoneFormatUtil.deleteprefix("-", contactRecodeEntity.getPhoneNumber()));
		}
		mtview.performClick();
		mtview.performClick();
	}







	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.mtview:
				//友盟方法统计
				MobclickAgent.onEvent(this, CLICKCALLCONTROLVOIDE);
				Boolean isselected = mtview.isSelected();
				if(ICSOpenVPNApplication.the_sipengineReceive!=null)
					ICSOpenVPNApplication.the_sipengineReceive.SetLoudspeakerStatus(!isselected);
				mtview.setSelected(!isselected);
				break;
			case R.id.calmTextView:
				//友盟方法统计
				MobclickAgent.onEvent(this, CLICKCALLPHONEQUIET);
				Boolean iscalmSelected = calmTextView.isSelected();
				if(ICSOpenVPNApplication.the_sipengineReceive!=null)
					ICSOpenVPNApplication.the_sipengineReceive.MuteMic(!iscalmSelected);
				calmTextView.setSelected(!iscalmSelected);
				break;
			case R.id.cancelcallbtn:
				//友盟方法统计
				MobclickAgent.onEvent(this, CLICKCALLHANGUP);
				if(CommonTools.isFastDoubleClick(1000)){
					return;
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (ICSOpenVPNApplication.the_sipengineReceive != null) {
							ICSOpenVPNApplication.the_sipengineReceive.Hangup();
							finish();

						}
					}
				}).start();
				t9dialpadview.clearT9Input();
				break;
			case  R.id.keyboard:
				t9dialpadview.setVisibility(View.VISIBLE);
				llControlVoide.setVisibility(View.INVISIBLE);
				hideKeyboard.setVisibility(View.VISIBLE);
				break;
			case R.id.hide_keyboard:
				t9dialpadview.setVisibility(View.GONE);
				llControlVoide.setVisibility(View.VISIBLE);
				hideKeyboard.setVisibility(View.GONE);
				break;
		}
	}

	//销毁界面，销毁资源
	@Override
	public void onDestroy() {
		super.onDestroy();
		setQuite();
		if (connectedReceive != null) {
			unregisterReceiver(connectedReceive);
			connectedReceive = null;
		}
		cancelNotify();
	}

	private void setQuite() {
		if (ICSOpenVPNApplication.the_sipengineReceive != null) {
			ICSOpenVPNApplication.the_sipengineReceive.MuteMic(true);
			ICSOpenVPNApplication.the_sipengineReceive.MuteSpk(true);
			ICSOpenVPNApplication.the_sipengineReceive.SetLoudspeakerStatus(true);
		}
	}


	private void stopTimer() {
		if (timer.isActivated()) {
			timer.stop();
			timer.setBase(SystemClock.elapsedRealtime());
			timer = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void setDataParam(Intent intent) {
		super.setDataParam(intent);
		intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
		intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, cellPhoneType);
		intent.putExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME, maxinumPhoneCallTime);
	}

	@Override
	protected void onStop() {
		super.onStop();
		isForeground = false;
		initNotify(phonenumtxt.getText().toString(), contactRecodeEntity.getPhoneNumber());
	}

	private void startTimer() {

		timer.setVisibility(View.VISIBLE);
		timer.setBase(SystemClock.elapsedRealtime());
		//开始计时
		timer.start();


	}


	public void displayStatus(final String msg) {
		callStatustxt.setText(msg);
	}

	public void displayStatus(final int id) {
		callStatustxt.setText(getString(id));
	}


	private void insertCallRecode() {
		ContactRecodeEntity contactRecodeEntity = (ContactRecodeEntity) getIntent().getSerializableExtra(IntentPutKeyConstant.DATA_CALLINFO);
		String realname = SearchConnectterHelper.getContactNameByPhoneNumber(this, contactRecodeEntity.getPhoneNumber());
		if (!TextUtils.isEmpty(realname)) {
			contactRecodeEntity.setName(realname);
		}
		long time = System.currentTimeMillis();
		contactRecodeEntity.setCallTime(time);
		contactRecodeEntity.setData(DateUtils.getTimeStampString(time + ""));
		if (!TextUtils.isEmpty(contactRecodeEntity.getAddress())) {
			contactRecodeEntity.setAddress(contactRecodeEntity.getAddress());
		} else {
			contactRecodeEntity.setAddress(getString(R.string.title_search_result_not_found));
		}
		contactRecodeEntity.setType(CallLog.Calls.OUTGOING_TYPE);
		contactRecodeEntity.setTypeString(Constant.CALL_OUTGOING);
		FindContactUtil.addCallRecode(this, contactRecodeEntity);
	}

	class ConnectedReceive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CallPhoneService.endFlag.equals(action)) {
				if (CallPhoneService.CALL_DIR == 1) {
					cancelNotify();
					stopTimer();
					try {
						if(!isFinishing())
						onBackPressed();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (CallPhoneService.connectedFlag.equals(action)) {
				displayStatus("");
				startTimer();
			} else if (CallPhoneService.callProcessing.equals(action)) {
				displayStatus(R.string.calling);

			} else if (CallPhoneService.waitConnected.equals(action)) {
				if (timer.getVisibility() == View.GONE)
					displayStatus(R.string.calling);
			} else if (CallPhoneService.reportFlag.equals(action)) {

				if (CallPhoneService.CALL_DIR == 1) {
					insertCallRecode();
				}
			} else if (CallPhoneService.CALL_FAIL.equals(action)) {
				displayStatus(R.string.call_fail);
				cancelNotify();
				cancelcallbtn.setEnabled(false);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						CallPhoneNewActivity.this.finish();
					}
				}, 2000);

			}
		}
	}

	private void cancelNotify() {
		if (mNotificationManager != null)
			mNotificationManager.cancel(notifyId);
	}

}
