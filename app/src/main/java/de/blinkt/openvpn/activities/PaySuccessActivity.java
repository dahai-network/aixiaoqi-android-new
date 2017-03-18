package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.IsHavePacketHttp;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.IsHavePacketEntity;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER;

public class PaySuccessActivity extends BaseActivity implements InterfaceCallback {

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
		//支付成功即检测是否有套餐，有套餐则开始上电
		if (type == BUY_CALL_TIME) {
			//当有通话套餐的时候才允许注册操作
			IsHavePacketHttp http = new IsHavePacketHttp(this, HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET, "3");
			new Thread(http).start();
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

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (object.getStatus() == 1) {
			IsHavePacketHttp isHavePacketHttp = (IsHavePacketHttp) object;
			IsHavePacketEntity entity = isHavePacketHttp.getOrderDataEntity();
			if (entity.getUsed() == 1) {
				//如果之前无套餐的状态，就上电
				if (!SharedUtils.getInstance().readBoolean(Constant.ISHAVEORDER, false)) {
					SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER);
				}
				//标记新状态
				SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, true);
			} else {
				//TODO 没有通知到设备界面
				//如果是没有套餐，则通知我的设备界面更新状态并且停止转动
				SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, false);
				sendEventBusChangeBluetoothStatus(getString(R.string.index_no_packet), R.drawable.index_no_packet);
			}
		}
	}

	/**
	 * 修改蓝牙连接状态，通过EVENTBUS发送到各个页面。
	 */
	private void sendEventBusChangeBluetoothStatus(String status, int statusDrawableInt) {
		ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
		entity.setStatus(status);
		entity.setStatusDrawableInt(statusDrawableInt);
		EventBus.getDefault().post(entity);
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {

	}
}
