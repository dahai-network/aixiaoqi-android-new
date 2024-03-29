package de.blinkt.openvpn.activities.MyModules.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.MyModules.presenter.PaySuccessPresenter;
import de.blinkt.openvpn.activities.MyModules.view.PaySuccessView;
import de.blinkt.openvpn.activities.Set.ui.CallPackageLlistActivity;
import de.blinkt.openvpn.activities.Set.ui.CallTimeOrderDetailActitivy;
import de.blinkt.openvpn.activities.ShopModules.ui.CountryPackageActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.PackageDetailActivity;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.IsHavePacketHttp;
import de.blinkt.openvpn.model.IsHavePacketEntity;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER_NO_RESPONSE;

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
	PaySuccessPresenter paySuccessPresenter;
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

		paySuccessPresenter = new PaySuccessPresenter(new PaySuccessView());
		//支付成功即检测是否有套餐，有套餐则开始上电
		if (type == BUY_CALL_TIME) {
			//当有通话套餐的时候才允许注册操作
			//createHttpRequest(HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET, "3");
			paySuccessPresenter.isHavePacket("3");
		}
		moneyTextView.setText("￥" + getIntent.getStringExtra("money"));
	}

	@OnClick(R.id.sureTextView)
	public void onClick() {
		if (type == RECHARGE) {
			finish();
		} else if (type == BUY) {
			if (CountryPackageActivity.activity != null) {
				CountryPackageActivity.activity.finish();
				CountryPackageActivity.activity = null;
			}
			if (PackageMarketActivity.activity != null) {
				PackageMarketActivity.activity.finish();
				PackageMarketActivity.activity = null;
			}
			if (PackageDetailActivity.activity != null) {
				PackageDetailActivity.activity.finish();
				PackageDetailActivity.activity = null;
			}
			MyOrderDetailActivity.launch(this, getIntent().getStringExtra("orderId"));
			finish();
		} else if (type == BUY_CALL_TIME) {
			if (CallTimeOrderDetailActitivy.actitivy != null) {
				CallTimeOrderDetailActitivy.actitivy.finish();
				CallTimeOrderDetailActitivy.actitivy = null;
			}
			if (PackageMarketActivity.activity != null) {
				PackageMarketActivity.activity.finish();
				PackageMarketActivity.activity = null;
			}
			if (CallTimePacketDetailActivity.activity != null) {
				CallTimePacketDetailActivity.activity.finish();
				CallTimePacketDetailActivity.activity = null;
			}

			if (CallPackageLlistActivity.activity != null) {
				CallPackageLlistActivity.activity.finish();
				CallPackageLlistActivity.activity = null;
			}
			CallTimeOrderDetailActitivy.launch(this, getIntent().getStringExtra("orderId"));
			finish();
		}
	}



}
