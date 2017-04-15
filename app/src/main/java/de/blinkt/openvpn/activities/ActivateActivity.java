package de.blinkt.openvpn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.OrderActivationHttp;
import de.blinkt.openvpn.http.OrderDataHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogYearMonthDayPicker;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVEPACKAGE;


public class ActivateActivity extends BaseNetActivity implements View.OnClickListener, DialogInterfaceTypeBase {

	public static String FINISH_ACTIVITY = "finish_activity";
	TextView connectStatusTextView;
	TextView payForWhatTextView;
	TextView payWayTextView;
	TextView expireDaysTextView;
	TextView sureTextView;
	private String orderId;
	private UartService mService = ICSOpenVPNApplication.uartService;
	private boolean isActivateSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activate);
		initView();
		initData();
		hasLeftViewTitle(R.string.activate_packet, -1);
		addListener();
		LocalBroadcastManager.getInstance(this).registerReceiver(isWriteReceiver, setFilter());
		LocalBroadcastManager.getInstance(this).registerReceiver(finishActivityReceiver, setFinishFilter());

	}

	private IntentFilter setFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyOrderDetailActivity.FINISH_PROCESS);
		filter.addAction(MyOrderDetailActivity.FINISH_PROCESS_ONLY);
		filter.addAction(MyOrderDetailActivity.CARD_RULE_BREAK);
		return filter;
	}

	private IntentFilter setFinishFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActivateActivity.FINISH_ACTIVITY);
		return filter;
	}

	private void initData() {
		orderId = getIntent().getStringExtra(IntentPutKeyConstant.ORDER_ID);
		payWayTextView.setText(DateUtils.getCurrentDate());
		effectTime = (System.currentTimeMillis() / 1000) + "";
		expireDaysTextView.setText(getIntent().getIntExtra("ExpireDaysInt", 0) + "天");
	}


	private void initView() {
		expireDaysTextView = (TextView) findViewById(R.id.expireDaysTextView);
		sureTextView = (TextView) findViewById(R.id.sureTextView);
		payWayTextView = (TextView) findViewById(R.id.payWayTextView);
		payForWhatTextView = (TextView) findViewById(R.id.payForWhatTextView);
		connectStatusTextView = (TextView) findViewById(R.id.connectStatusTextView);

		//判断设备是否连接成功
		if (mService != null && mService.mConnectionState == UartService.STATE_CONNECTED) {
			connectStatusTextView.setText(getResources().getString(R.string.connect_success));
		} else {
			connectStatusTextView.setText(getResources().getString(R.string.activate_unconnected));
		}

	}

	private void addListener() {
		expireDaysTextView.setOnClickListener(this);
		sureTextView.setOnClickListener(this);
		payWayTextView.setOnClickListener(this);
		payForWhatTextView.setOnClickListener(this);
		connectStatusTextView.setOnClickListener(this);
	}

	//写卡成功关闭process
	private BroadcastReceiver isWriteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(intent.getAction(), MyOrderDetailActivity.CARD_RULE_BREAK)) {
				dismissProgress();
				showDialog();
			} else if (TextUtils.equals(intent.getAction(), MyOrderDetailActivity.FINISH_PROCESS)) {
				if (ReceiveBLEMoveReceiver.orderStatus == 4) {

					HashMap<String, String> map = new HashMap<>();
					map.put("statue", 0 + "");
					//友盟方法统计
					MobclickAgent.onEvent(mContext, CLICKACTIVECARD, map);
					CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), "激活失败，请重试!");
				} else {
					toActivity(new Intent(ActivateActivity.this, OutsideActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE, IntentPutKeyConstant.OUTSIDE).putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, getIntent().getBooleanExtra(IntentPutKeyConstant.IS_SUPPORT_4G, false)));
					isActivateSuccess = true;
				}
				dismissProgress();
				finish();
			} else if (TextUtils.equals(intent.getAction(), MyOrderDetailActivity.FINISH_PROCESS_ONLY)) {
				dismissProgress();
			}
		}
	};

	//上传数据成功关闭Activity
	private BroadcastReceiver finishActivityReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.expireDaysTextView:
				break;
			case R.id.sureTextView:
				if (!CommonTools.isFastDoubleClick(3000)) {
					//友盟方法统计
					MobclickAgent.onEvent(context, CLICKACTIVEPACKAGE);
					orderActivationHttp();
				}
				break;
			case R.id.payWayTextView:
				new DialogYearMonthDayPicker(this, this, R.layout.picker_year_month_day_layout, 0);
				break;
			case R.id.payForWhatTextView:
				break;
			case R.id.connectStatusTextView:
				break;

		}
	}

	private void orderActivationHttp() {
		if (TextUtils.isEmpty(effectTime)) {
			CommonTools.showShortToast(this, getString(R.string.effective_date_is_null));
			return;
		}
		createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_ACTIVATION, orderId, effectTime);
	}

	//获取写卡数据，然后发给蓝牙写卡
	private void orderDataHttp(String nullcardNumber) {
		if (nullcardNumber != null) {
			createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_DATA, orderId, nullcardNumber);
		} else {
			CommonTools.showShortToast(this, getString(R.string.no_nullcard_id));
		}
	}

	private String effectTime;

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			SendCommandToBluetooth.sendMessageToBlueTooth("AA112233AA");
		} else if (type == 0) {
			if (System.currentTimeMillis() > DateUtils.getStringToDate(text + " 00:00:00")) {
				CommonTools.showShortToast(this, getString(R.string.less_current_time));
				return;
			}
			effectTime = DateUtils.getStringToDate(text + " 00:00:00") / 1000 + "";
			String[] time = text.split("-");
			payWayTextView.setText(time[0] + getString(R.string.year) + time[1] + getString(R.string.month) + time[2] + getString(R.string.daliy));
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_ORDER_ACTIVATION) {
			OrderActivationHttp orderActivationHttp = (OrderActivationHttp) object;
			if (orderActivationHttp.getStatus() == 1) {
				//是否测试卡位置：否，这是写卡！
				IS_TEXT_SIM = false;
				ReceiveBLEMoveReceiver.orderStatus = 4;
				showProgress("正在激活", false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							SendCommandToBluetooth.sendMessageToBlueTooth(Constant.UP_TO_POWER);
							Thread.sleep(20000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (!isActivateSuccess) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									dismissProgress();
									CommonTools.showShortToast(ActivateActivity.this, getString(R.string.activate_fail));
								}
							});
						}
					}
				}).start();
				orderDataHttp(SharedUtils.getInstance().readString(Constant.NULLCARD_SERIALNUMBER));
			} else {
				CommonTools.showShortToast(this, orderActivationHttp.getMsg());
				sureTextView.setEnabled(true);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_ORDER_DATA) {
			OrderDataHttp orderDataHttp = (OrderDataHttp) object;
			if (orderDataHttp.getStatus() == 1) {
				sendMessageSeparate(orderDataHttp.getOrderDataEntity().getData());
			} else {
				CommonTools.showShortToast(ActivateActivity.this, orderDataHttp.getMsg());
			}
		}
	}

	private void sendMessageSeparate(final String message) {
		String[] messages = PacketeUtil.Separate(message, "1300");
		int length = messages.length;
		for (int i = 0; i < length; i++) {
			if (!SendCommandToBluetooth.sendMessageToBlueTooth(messages[i])) {
				dismissProgress();
			}
		}

	}


	private void showDialog() {
		//不能按返回键，只能二选其一
		DialogBalance cardRuleBreakDialog = new DialogBalance(this, ActivateActivity.this, R.layout.dialog_balance, 2);
		cardRuleBreakDialog.setCanClickBack(false);
		cardRuleBreakDialog.changeText(getResources().getString(R.string.no_aixiaoqi_or_rule_break), getResources().getString(R.string.reset));
	}

	@Override
	protected void onStop() {
		dismissProgress();
		super.onStop();
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		sureTextView.setEnabled(true);
	}

	@Override
	public void noNet() {
		sureTextView.setEnabled(true);
		dismissProgress();
	}

	@Override
	protected void onDestroy() {

		LocalBroadcastManager.getInstance(this).unregisterReceiver(isWriteReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(finishActivityReceiver);
		super.onDestroy();
	}
}
