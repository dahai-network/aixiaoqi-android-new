package de.blinkt.openvpn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
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
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.SearchConnectterHelper;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLCONTROLVOIDE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLHANGUP;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLPHONEQUIET;

;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class CallPhoneNewActivity extends BaseSensorActivity implements View.OnClickListener {
	TextView phonenumtxt;
	TextView callStatustxt;
	TextView mtview;
	TextView calmTextView;
	Button cancelcallbtn;
	Chronometer timer;
	String maxinumPhoneCallTime;
	ConnectedReceive connectedReceive;
	int cellPhoneType;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callphone);
		try {
			cellPhoneType=getIntent().getIntExtra(IntentPutKeyConstant.CELL_PHONE_TYPE,-1);
		}catch (Exception e){

		}

		initView();
		initData();
		addListener();

		IntentFilter filter = getIntentFilter();
		connectedReceive = new ConnectedReceive();
		registerReceiver(connectedReceive, filter);
		callPhone();
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
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}
		return true;
	}

	private void initView() {
		phonenumtxt = (TextView) findViewById(R.id.phonenumtxt);
		callStatustxt = (TextView) findViewById(R.id.call_statustxt);
		mtview = (TextView) findViewById(R.id.mtview);
		timer = (Chronometer) findViewById(R.id.chronometer);
		calmTextView = (TextView) findViewById(R.id.calmTextView);
		cancelcallbtn = (Button) findViewById(R.id.cancelcallbtn);
		displayStatus(R.string.calling);
	}
	ContactRecodeEntity contactRecodeEntity;


	private void initData() {
		 contactRecodeEntity = (ContactRecodeEntity) getIntent().getSerializableExtra(IntentPutKeyConstant.DATA_CALLINFO);
		maxinumPhoneCallTime = getIntent().getStringExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME);
		if (TextUtils.isEmpty(maxinumPhoneCallTime)) {
			maxinumPhoneCallTime = "0";
		}
		if (TextUtils.isEmpty(contactRecodeEntity.getPhoneNumber())) {
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
	}

	private void callPhone() {
		if(contactRecodeEntity==null||TextUtils.isEmpty(contactRecodeEntity.getPhoneNumber())){
			return;
		}
		if (contactRecodeEntity.getPhoneNumber().startsWith("sip:")) {
			ICSOpenVPNApplication.the_sipengineReceive.MakeUrlCall(contactRecodeEntity.getPhoneNumber());
		} else if(cellPhoneType==Constant.NETWORK_CELL_PHONE){
			ICSOpenVPNApplication.the_sipengineReceive.MakeCall("981" + deleteprefix("-",contactRecodeEntity.getPhoneNumber()) + "#" + maxinumPhoneCallTime);

		}else if(cellPhoneType==Constant.SIM_CELL_PHONE){
			try{
				Log.e("CallPhoneNewActivity","ICSOpenVPNApplication.the_sipengineReceive"+(ICSOpenVPNApplication.the_sipengineReceive==null));
			ICSOpenVPNApplication.the_sipengineReceive.MakeCall("986"+ SocketConstant.REGISTER_REMOTE_ADDRESS+SocketConstant.REGISTER_ROMOTE_PORT + deleteprefix("-",contactRecodeEntity.getPhoneNumber()) );
			}catch (Exception e){

			}
		}
	}
	private String deleteprefix(String type,String s) {
		if(TextUtils.isEmpty(s)){
			return "";
		}
		String phoneNumber;
		if(s.replace(type,"").startsWith("+86")){

			phoneNumber= s.substring(3, s.length());

		}else if(s.replace(type,"").startsWith("86")){
			phoneNumber= s.substring(2, s.length());
		}else if(s.replace(type,"").startsWith("0086")){
			phoneNumber= s.substring(2, s.length());
		}
		else{
			phoneNumber= s;
		}
		Log.e("CallPhoneNewActivity","phoneNumber"+phoneNumber);
		return phoneNumber;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.mtview:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKCALLCONTROLVOIDE);
				Boolean isselected = mtview.isSelected();
				ICSOpenVPNApplication.the_sipengineReceive.SetLoudspeakerStatus(!isselected);
				mtview.setSelected(!isselected);
				break;
			case R.id.calmTextView:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKCALLPHONEQUIET);
				Boolean iscalmSelected = calmTextView.isSelected();
				ICSOpenVPNApplication.the_sipengineReceive.MuteMic(!iscalmSelected);
				calmTextView.setSelected(!iscalmSelected);
				break;
			case R.id.cancelcallbtn:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKCALLHANGUP);
				new Thread(new Runnable() {
					@Override
					public void run() {
						ICSOpenVPNApplication.the_sipengineReceive.Hangup();
					}
				}).start();

				if(timer.isActivated()){
					stopTimer();
				}
				finish();
				break;
		}
	}

	//销毁界面，销毁资源
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (ICSOpenVPNApplication.the_sipengineReceive != null) {
			ICSOpenVPNApplication.the_sipengineReceive.MuteMic(false);
			ICSOpenVPNApplication.the_sipengineReceive.SetLoudspeakerStatus(false);
		}
		if(connectedReceive!=null){
			unregisterReceiver(connectedReceive);
			connectedReceive=null;

		}
	}


	private void stopTimer() {
		timer.stop();
		timer.setBase(SystemClock.elapsedRealtime());
		timer=null;
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
		Intent intent = new Intent();
		intent.putExtra(IntentPutKeyConstant.CONTACT_RECODE_ENTITY, contactRecodeEntity);
		intent.setAction(ReceiveCallActivity.UPDATE_CONTACT_REDORE);
		sendBroadcast(intent);
	}

	class ConnectedReceive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CallPhoneService.endFlag.equals(action)) {
				stopTimer();
				finish();
			} else if (CallPhoneService.connectedFlag.equals(action)) {
				displayStatus("");
				startTimer();
			} else if (CallPhoneService.callProcessing.equals(action)) {
				displayStatus(R.string.calling);
//				displayStatus(getString(R.string.connecting));
			} else if (CallPhoneService.waitConnected.equals(action)) {
				if (timer.getVisibility() == View.GONE)
					displayStatus(R.string.calling);
//					displayStatus(getString(R.string.wait_other_connecting));
			} else if (CallPhoneService.reportFlag.equals(action)) {
				int callDir = intent.getIntExtra("CallDir", -1);
				if (callDir == 1) {
					insertCallRecode();
				}
			} else if (CallPhoneService.CALL_FAIL.equals(action)) {
				displayStatus(R.string.call_fail);
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

}
