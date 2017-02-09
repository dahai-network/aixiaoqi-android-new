package de.blinkt.openvpn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.OrderActivationHttp;
import de.blinkt.openvpn.http.OrderDataHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
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

	public static void launch(Context context) {
		Intent intent = new Intent(context, ActivateActivity.class);
		context.startActivity(intent);
	}

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
		filter.addAction(MyOrderDetailActivity.CARD_RULE_BREAK);
		filter.addAction(MyOrderDetailActivity.FIND_NULL_CARD_ID);
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
			} else if (TextUtils.equals(intent.getAction(), MyOrderDetailActivity.FIND_NULL_CARD_ID)) {
				orderDataHttp(intent.getStringExtra("nullcardNumber"));
			} else {
				if (ReceiveBLEMoveReceiver.orderStatus == 4) {
					HashMap<String, String> map = new HashMap<>();
					map.put("statue", 0 + "");
					//友盟方法统计
					MobclickAgent.onEvent(mContext, CLICKACTIVECARD, map);
					CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), "激活失败，请重试!");
				}
				dismissProgress();
				finish();
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
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKACTIVEPACKAGE);
				orderActivationHttp();
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
//		sureTextView.setEnabled(false);

		OrderActivationHttp orderActivationHttp = new OrderActivationHttp(this, HttpConfigUrl.COMTYPE_ORDER_ACTIVATION, orderId, effectTime);
		new Thread(orderActivationHttp).start();
	}
    //获取写卡数据，然后发给蓝牙写卡
	private void orderDataHttp(String nullcardNumber) {
		OrderDataHttp orderDataHttp = new OrderDataHttp(this, HttpConfigUrl.COMTYPE_ORDER_DATA, orderId, nullcardNumber);
		new Thread(orderDataHttp).start();
	}

	private String effectTime;

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			sendMessageToBlueTooth("AA112233AA");
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
				showProgress("正在激活");
				ReceiveBLEMoveReceiver.isGetnullCardid = true;
				sendMessageToBlueTooth(Constant.UP_TO_POWER);
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
		String[] messages = PacketeUtil.Separate(message);
		int length = messages.length;
		for (int i = 0; i < length; i++) {
			sendMessageToBlueTooth(messages[i]);
		}
	}

	private void sendMessageToBlueTooth(final String message) {
		byte[] value;
		value = HexStringExchangeBytesUtil.hexStringToBytes(message);
		Log.i("toBLue", message);
		if (mService != null) {
			if (mService.mConnectionState == UartService.STATE_CONNECTED) {
				mService.writeRXCharacteristic(value);
			} else {
				CommonTools.showShortToast(this, "设备已断开，请重新连接");
				dismissProgress();
			}
		} else {
			CommonTools.showShortToast(this, "请打开我的设备绑定设备");
			dismissProgress();
		}
	}

	private void showDialog() {
		//不能按返回键，只能二选其一
		DialogBalance cardRuleBreakDialog = new DialogBalance(this, ActivateActivity.this, R.layout.dialog_balance, 2);
		cardRuleBreakDialog.setCanClickBack(false);
		cardRuleBreakDialog.changeText(getResources().getString(R.string.no_card_or_rule_break), getResources().getString(R.string.reset));
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		sureTextView.setEnabled(true);
	}

	@Override
	public void noNet() {
		sureTextView.setEnabled(true);
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(isWriteReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(finishActivityReceiver);
		super.onDestroy();
	}
}
