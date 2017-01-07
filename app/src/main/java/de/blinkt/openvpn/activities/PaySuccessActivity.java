package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;

public class PaySuccessActivity extends BaseActivity {

	@BindView(R.id.completeTextView)
	TextView completeTextView;
	@BindView(R.id.payForWhatTextView)
	TextView payForWhatTextView;
	@BindView(R.id.payWayTextView)
	TextView payWayTextView;
	@BindView(R.id.moneyTextView)
	TextView moneyTextView;
	@BindView(R.id.sureTextView)
	TextView sureTextView;
	private int type;

	public static int RECHARGE = 0;
	public static int BUY = 1;
	public static int BUY_CALL_TIME = 2;
	public static int WEIXIN = 4;
	public static int ALI = 5;
	public static int BALANCE = 6;
	public static int OFFICIAL_GIFTS = 7;

	public static void launch(Context context, int pay_type, int payway, String money, String orderId) {
		Intent intent = new Intent(context, PaySuccessActivity.class);
		intent.putExtra("pay_type", pay_type);
		intent.putExtra("payway", payway);
		intent.putExtra("money", money);
		intent.putExtra("orderId", orderId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_success);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {

		Intent getIntent = getIntent();
		type = getIntent.getIntExtra("pay_type", 0);
		if (type == 0) {
			hasLeftViewTitle(R.string.recharge_complete, 0);
			completeTextView.setText(getResources().getString(R.string.recharge_success));
			payForWhatTextView.setText(getResources().getString(R.string.recharge_way_in_pay_success));
		} else {
			hasLeftViewTitle(R.string.pay_success, 0);
			completeTextView.setText(getResources().getString(R.string.pay_success));
			payForWhatTextView.setText(getResources().getString(R.string.buy_way_in_pay_success));
		}

		int paywayString = getIntent.getIntExtra("payway", 0);
		if (paywayString == ALI) {
			payWayTextView.setText(getResources().getString(R.string.PAY_FOR_ALI));
		} else if (paywayString == WEIXIN) {
			payWayTextView.setText(getResources().getString(R.string.PAY_FOR_WEIXIN));
		} else if (paywayString == BALANCE) {
			payWayTextView.setText(getResources().getString(R.string.balance_pay));
		} else if (paywayString == OFFICIAL_GIFTS) {
			payWayTextView.setText(getResources().getString(R.string.official_gifts));
		}

		moneyTextView.setText("￥" + getIntent.getStringExtra("money"));
	}

	@OnClick(R.id.sureTextView)
	public void onClick() {
		if (type == RECHARGE) {
			finish();
		} else if (type == BUY) {
			MyOrderDetailActivity.launch(this, getIntent().getStringExtra("orderId"));
			finish();
		} else if (type == BUY_CALL_TIME) {
			CallTimeOrderDetailActitivy.launch(this, getIntent().getStringExtra("orderId"));
		}
	}
}
