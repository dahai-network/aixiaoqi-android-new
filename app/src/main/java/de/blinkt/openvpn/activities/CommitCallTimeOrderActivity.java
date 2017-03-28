package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BalanceGetPayIdHttp;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.OrderAddHttp;
import de.blinkt.openvpn.http.WeixinGetPayIdHttp;
import de.blinkt.openvpn.model.OrderAddEntity;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.model.PayResult;
import de.blinkt.openvpn.model.WeiXinResultEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.OrderInfoUtil2_0;

import static de.blinkt.openvpn.constant.Constant.ALI_APPID;
import static de.blinkt.openvpn.constant.Constant.RSA_PRIVATE;
import static de.blinkt.openvpn.constant.Constant.WEIXIN_APPID;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSUREPAGMENT;

public class CommitCallTimeOrderActivity extends BaseNetActivity implements InterfaceCallback {

	@BindView(R.id.countryImageView)
	ImageView countryImageView;
	@BindView(R.id.packageNameTextView)
	TextView packageNameTextView;
	@BindView(R.id.priceTextView)
	TextView priceTextView;
	@BindView(R.id.packetDetailRelativeLayout)
	RelativeLayout packetDetailRelativeLayout;
	@BindView(R.id.balanceTextView)
	TextView balanceTextView;
	@BindView(R.id.balancePayCheckBox)
	CheckBox balancePayCheckBox;
	@BindView(R.id.balancePayLienarLayout)
	LinearLayout balancePayLienarLayout;
	@BindView(R.id.weixin)
	ImageView weixin;
	@BindView(R.id.text_weixin)
	TextView textWeixin;
	@BindView(R.id.weixinPayCheckBox)
	CheckBox weixinPayCheckBox;
	@BindView(R.id.weixinPayLienarLayout)
	RelativeLayout weixinPayLienarLayout;
	@BindView(R.id.zhifubao_icon)
	ImageView zhifubaoIcon;
	@BindView(R.id.zhifubao_text)
	TextView zhifubaoText;
	@BindView(R.id.aliPayCheckBox)
	CheckBox aliPayCheckBox;
	@BindView(R.id.aliPayLienarLayout)
	RelativeLayout aliPayLienarLayout;
	@BindView(R.id.addUpTextView)
	TextView addUpTextView;
	@BindView(R.id.sureTextView)
	TextView sureTextView;
	private PacketDtailEntity.ListBean bean;
	private int BALANCE_PAY_METHOD = 3;
	private int WEIXIN_PAY_METHOD = 2;
	private int ALI_PAY_METHOD = 1;
	private boolean isAliPayClick;
	private static final int SDK_PAY_FLAG = 1;
	private IWXAPI api;
	private OrderAddEntity orderEntity;
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					@SuppressWarnings("unchecked")
					PayResult payResult = new PayResult((Map<String, String>) msg.obj);
					/**
					 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
					 */
//					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为9000则代表支付成功
					if (TextUtils.equals(resultStatus, "9000")) {
						// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
						Toast.makeText(CommitCallTimeOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
						PaySuccessActivity.launch(CommitCallTimeOrderActivity.this, PaySuccessActivity.BUY_CALL_TIME, PaySuccessActivity.ALI,
								orderEntity.getOrder().getTotalPrice() + "", orderEntity.getOrder().getOrderID());
						sureTextView.setEnabled(true);
						finish();
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						Toast.makeText(CommitCallTimeOrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
						sureTextView.setEnabled(true);
					}
					break;
				}
				default:
					break;
			}
		}

	};
	private float balanceFloat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commit_call_time_order);
		ButterKnife.bind(this);
		initSet();
	}

	public static void launch(Context context, PacketDtailEntity.ListBean bean) {
		Intent intent = new Intent(context, CommitCallTimeOrderActivity.class);
		intent.putExtra("order", bean);
		context.startActivity(intent);
	}

	private void initSet() {
		bean = (PacketDtailEntity.ListBean) getIntent().getSerializableExtra("order");
		hasLeftViewTitle(R.string.commit_order, 0);
		packageNameTextView.setText(bean.getPackageName());
		priceTextView.setText("￥" + bean.getPrice());
		addUpTextView.setText("￥" + bean.getPrice());
		Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(countryImageView);
		setSpan(priceTextView);
		setSpan(addUpTextView);
	}

	//设置大小字体
	public void setSpan(TextView textview) {
		Spannable WordtoSpan = new SpannableString(textview.getText().toString());
		int intLength = String.valueOf((int) (bean.getPrice())).length();
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@OnClick({R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout, R.id.sureTextView, R.id.balancePayLienarLayout})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.balancePayLienarLayout:
				weixinPayCheckBox.setChecked(false);
				aliPayCheckBox.setChecked(false);
				isAliPayClick = false;
				balancePayCheckBox.setChecked(true);
				break;
			case R.id.weixinPayLienarLayout:
				balancePayCheckBox.setChecked(false);
				aliPayCheckBox.setChecked(false);
				isAliPayClick = false;
				weixinPayCheckBox.setChecked(true);
				break;
			case R.id.aliPayLienarLayout:
				balancePayCheckBox.setChecked(false);
				weixinPayCheckBox.setChecked(false);
				isAliPayClick = true;
				aliPayCheckBox.setChecked(true);
				break;
			case R.id.sureTextView:
				HashMap<String, String> map = new HashMap<>();
				OrderAddHttp http;
				sureTextView.setEnabled(false);
				if (weixinPayCheckBox.isChecked()) {
					if (isWXAppInstalledAndSupported()) {
						map.put("type", WEIXIN_PAY_METHOD + "");
						//友盟方法统计
						MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
						http = new OrderAddHttp(this, HttpConfigUrl.COMTYPE_CREATE_ORDER, bean.getPackageId(), 1, WEIXIN_PAY_METHOD, false);
						new Thread(http).start();
					} else {
						CommonTools.showShortToast(this, getResources().getString(R.string.no_weixin_yet));
						sureTextView.setEnabled(true);
					}
				} else if (aliPayCheckBox.isChecked()) {
					map.put("type", ALI_PAY_METHOD + "");
					//友盟方法统计
					MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
					http = new OrderAddHttp(this, HttpConfigUrl.COMTYPE_CREATE_ORDER, bean.getPackageId(), 1, ALI_PAY_METHOD, false);
					new Thread(http).start();
				} else {
					map.put("type", BALANCE_PAY_METHOD + "");
					//友盟方法统计
					MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
					http = new OrderAddHttp(this, HttpConfigUrl.COMTYPE_CREATE_ORDER, bean.getPackageId(), 1, BALANCE_PAY_METHOD, true);
					new Thread(http).start();
				}
				break;
		}
	}

	private boolean isWXAppInstalledAndSupported() {
		IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
		msgApi.registerApp(Constant.WEIXIN_APPID);

		boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
				&& msgApi.isWXAppSupportAPI();

		return sIsWXAppInstalledAndSupported;
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_CREATE_ORDER) {
			if (object.getStatus() == 1) {
				OrderAddHttp http = (OrderAddHttp) object;
				orderEntity = http.getOrderEntity();
				if (aliPayCheckBox.isChecked()) {
					//向支付宝支付
					PayForAli();
				} else if (weixinPayCheckBox.isChecked()) {
					//向微信支付
					payForWeixin();
				} else {
					payForBalance();
				}
			} else {
				CommonTools.showShortToast(CommitCallTimeOrderActivity.this, object.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID) {
			try {
				api = WXAPIFactory.createWXAPI(CommitCallTimeOrderActivity.this, WEIXIN_APPID);
				WeixinGetPayIdHttp http = (WeixinGetPayIdHttp) object;
				WeiXinResultEntity entity = http.getWeixinResultEntity();
				PayReq req = new PayReq();
				//req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
				req.appId = entity.getAppid();
				req.partnerId = entity.getPartnerid();
				req.prepayId = entity.getPrepayid();
				req.nonceStr = entity.getNoncestr();
				req.timeStamp = entity.getTimestamp();
				req.packageValue = entity.getPackageX();
				req.sign = entity.getSign();
				req.extData = "app data"; // optional
				api.sendReq(req);
			} catch (Exception e) {
				Log.e("PAY_GET", "异常：" + e.getMessage());
				Toast.makeText(CommitCallTimeOrderActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}

		} else if (cmdType == HttpConfigUrl.COMTYPE_BALANCE_GETPAYID) {
			sureTextView.setEnabled(true);
			if (object.getStatus() == 1) {

				PaySuccessActivity.launch(CommitCallTimeOrderActivity.this, PaySuccessActivity.BUY_CALL_TIME, PaySuccessActivity.BALANCE,
						bean.getPrice() + "", orderEntity.getOrder().getOrderID());
                finish();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_BALANCE) {
			BalanceHttp http = (BalanceHttp) object;
			balanceFloat = http.getBalanceEntity().getAmount();
			checkBalance();
		}
	}

	private void checkBalance() {
		if (weixinPayCheckBox.isChecked()) {
			showBalanceCheckBox();
			return;
		}
		if (aliPayCheckBox.isChecked() && isAliPayClick) {
			showBalanceCheckBox();
			return;
		}
		if (bean.getPrice() < balanceFloat) {
			balanceTextView.setText(getResources().getString(R.string.balance_pay) + "(剩余￥" + balanceFloat + ")");
			balanceTextView.setEnabled(true);
			balancePayCheckBox.setVisibility(View.VISIBLE);
			balancePayCheckBox.setChecked(true);
			balancePayLienarLayout.setEnabled(true);
			aliPayCheckBox.setChecked(false);
		} else {
			balancePayCheckBox.setChecked(false);
			balanceTextView.setEnabled(false);
			balancePayCheckBox.setVisibility(View.GONE);
			balanceTextView.setText(getResources().getString(R.string.not_enough_balance));
			balancePayLienarLayout.setEnabled(false);
			aliPayCheckBox.setChecked(true);

		}
	}

	private void showBalanceCheckBox() {
		if (bean.getPrice() < balanceFloat) {
			balanceTextView.setText(getResources().getString(R.string.balance_pay) + "(剩余￥" + balanceFloat + ")");
			balanceTextView.setEnabled(true);
			balancePayCheckBox.setVisibility(View.VISIBLE);
			balancePayLienarLayout.setEnabled(true);
		} else {
			balanceTextView.setEnabled(false);
			balancePayCheckBox.setVisibility(View.GONE);
			balanceTextView.setText(getResources().getString(R.string.not_enough_balance));
			balancePayLienarLayout.setEnabled(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//查询余额
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_BALANCE);
		dismissProgress();
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {

	}

	/**
	 * 余额支付
	 */
	private void payForBalance() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_BALANCE_GETPAYID, orderEntity.getOrder().getOrderID());

	}

	/**
	 * 微信支付
	 */
	private void payForWeixin() {
		showProgress(getResources().getString(R.string.weixin_paying),true);
		SharedPreferences preferences = getSharedPreferences("order", MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("orderId", orderEntity.getOrder().getOrderID());
		editor.putString("orderAmount", orderEntity.getOrder().getTotalPrice() + "");
		editor.commit();
		createHttpRequest( HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID, orderEntity.getOrder().getOrderNum());

		sureTextView.setEnabled(true);
	}


	/**
	 * 支付宝支付
	 */
	private void PayForAli() {
		//将APPID和订单参数发送到订单信息工具
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(ALI_APPID, orderEntity);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
		String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE);
		final String orderInfo = orderParam + "&" + sign;

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(CommitCallTimeOrderActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
}
