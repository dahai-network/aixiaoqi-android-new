package de.blinkt.openvpn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
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
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.RechargeHttp;
import de.blinkt.openvpn.http.WeixinGetPayIdHttp;
import de.blinkt.openvpn.model.PayResult;
import de.blinkt.openvpn.model.RechargeEntity;
import de.blinkt.openvpn.model.WeiXinResultEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.OrderInfoUtil2_0;
import de.blinkt.openvpn.util.ViewUtil;
import de.blinkt.openvpn.views.RadioGroup;

import static de.blinkt.openvpn.constant.Constant.ALI_APPID;
import static de.blinkt.openvpn.constant.Constant.RSA_PRIVATE;
import static de.blinkt.openvpn.constant.Constant.WEIXIN_APPID;
import static de.blinkt.openvpn.constant.UmengContant.CHARGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBINDCHARGECARD;

public class RechargeActivity extends BaseNetActivity implements InterfaceCallback, RadioGroup.OnCheckedChangeListener {

	@BindView(R.id.amountEditText)
	EditText amountEditText;
	@BindView(R.id.ll_edit)
	LinearLayout llEditText;

	@BindView(R.id.weixinPayCheckBox)
	CheckBox weixinPayCheckBox;
	@BindView(R.id.weixinPayLienarLayout)
	RelativeLayout weixinPayLienarLayout;
	@BindView(R.id.aliPayCheckBox)
	CheckBox aliPayCheckBox;
	@BindView(R.id.aliPayLienarLayout)
	RelativeLayout aliPayLienarLayout;
	@BindView(R.id.nextBtn)
	Button nextBtn;
	@BindView(R.id.amountTextView)
	TextView amountTextView;
	@BindView(R.id.moneyRadioGroup)
	RadioGroup moneyRadioGroup;
	private int WEIXIN_PAY = 2;
	private int ALI_PAY = 1;
	private int pay_way = 0;
	private final int SDK_PAY_FLAG = 1;
	private IWXAPI api;
	private float moneyAmount = 100;
	private RechargeReceiver receiver;
	private RechargeEntity orderEntity;
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
						PaySuccessActivity.launch(RechargeActivity.this, PaySuccessActivity.RECHARGE, PaySuccessActivity.ALI, orderEntity.getPayment().getAmount() + "", null);
						finish();
					} else if (TextUtils.equals(resultStatus, "6002")) {
						nextBtn.setEnabled(true);
						CommonTools.showShortToast(RechargeActivity.this, payResult.getMemo());
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
						nextBtn.setEnabled(true);
					}
					break;
				}
				default:
					break;
			}
		}

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		ButterKnife.bind(this);
		initSet();
	}



	private void initSet() {
		hasLeftViewTitle(R.string.recharge, 0);

		amountEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						moneyAmount = Float.valueOf(s.toString());
						amountEditText.setText(s);
						amountEditText.setSelection(s.length());
						return;
					}
				}
				if (s.toString().trim().startsWith(".")) {
					s = "0" + s;
					moneyAmount = Float.valueOf(s.toString());
					amountEditText.setText(s);
					amountEditText.setSelection(2);
					return;
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						moneyAmount = Float.valueOf(s.toString());
						amountEditText.setText(s.subSequence(0, 1));
						amountEditText.setSelection(1);
						return;
					}
				}

				if (!TextUtils.isEmpty(s.toString()) && !s.toString().startsWith("0")) {
					moneyAmount = Float.valueOf(s.toString());
					if (moneyAmount > 5000) {
						moneyAmount = 5000;
						amountEditText.setText(5000 + "");
						amountEditText.setSelection(4);
						CommonTools.showShortToast(RechargeActivity.this, getResources().getString(R.string.max_recharge_amount));
					}
				}
				if (!TextUtils.isEmpty(s.toString())) {
					moneyAmount = Float.valueOf(s.toString());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

		});
		moneyRadioGroup.setOnCheckedChangeListener(this);

	}

	@OnClick({R.id.nextBtn, R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout,R.id.recharge_card})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.nextBtn:
				//友盟方法统计
				HashMap<String, String> map = new HashMap<>();
				if (CommonTools.isFastDoubleClick(1000)) {
					return;
				}
				nextBtn.setEnabled(false);
				if (aliPayCheckBox.isChecked()) {
					map.put("type", ALI_PAY + "");
					MobclickAgent.onEvent(this, CHARGE, map);
					pay_way = ALI_PAY;
					showProgress(getString(R.string.ali_paying),true);
					pay(ALI_PAY);
				} else {
					if (isWXAppInstalledAndSupported()) {
						map.put("type", WEIXIN_PAY + "");
						MobclickAgent.onEvent(this, CHARGE, map);
						pay_way = WEIXIN_PAY;
						showProgress(getString(R.string.weixin_paying),true);
						pay(WEIXIN_PAY);
					} else {
						nextBtn.setEnabled(true);
						CommonTools.showShortToast(this, getResources().getString(R.string.no_weixin_yet));
					}
				}

				break;
			case R.id.weixinPayLienarLayout:
				weixinPayCheckBox.setChecked(true);
				aliPayCheckBox.setChecked(false);
				break;
			case R.id.aliPayLienarLayout:
				weixinPayCheckBox.setChecked(false);
				aliPayCheckBox.setChecked(true);
				break;
			case R.id.recharge_card:
				MobclickAgent.onEvent(this, CLICKBINDCHARGECARD);
				BindRechargeCardActivity.launch(RechargeActivity.this, BindRechargeCardActivity.RECHARGE);
				break;
		}
	}

	private void pay(int payWay) {
		if (!"".equals(moneyAmount + "")) {
			if (moneyAmount != 0) {
				createHttpRequest(HttpConfigUrl.COMTYPE_RECHARGE_ORDER, moneyAmount + "", payWay+"");
			} else {
				nextBtn.setEnabled(true);
				CommonTools.showShortToast(RechargeActivity.this, getResources().getString(R.string.input_money_0));
			}
		} else {
			CommonTools.showShortToast(RechargeActivity.this, getResources().getString(R.string.input_money_null));
			nextBtn.setEnabled(true);
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_RECHARGE_ORDER) {
			RechargeHttp http = (RechargeHttp) object;
			if (http.getRechargeEntity() != null) {
				nextBtn.setEnabled(true);
				//获取订单用于支付成功后传到充值成功界面
				orderEntity = http.getRechargeEntity();
				if (pay_way == ALI_PAY) {
					payForAli(http.getRechargeEntity());
				} else {
					payForWeixin(http.getRechargeEntity());
				}

			} else {
				CommonTools.showShortToast(this, http.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID) {
			try {
				api = WXAPIFactory.createWXAPI(RechargeActivity.this, WEIXIN_APPID);
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
				Toast.makeText(RechargeActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 支付宝支付
	 *
	 * @param rechargeEntity 充值订单类
	 */
	private void payForAli(RechargeEntity rechargeEntity) {
		//将APPID和订单参数发送到订单信息工具
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(ALI_APPID, rechargeEntity);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
		String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE);
		final String orderInfo = orderParam + "&" + sign;

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(RechargeActivity.this);
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

	private void payForWeixin(RechargeEntity rechargeEntity) {
		showProgress(getResources().getString(R.string.weixin_paying),true);
		createHttpRequest(HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID, rechargeEntity.getPayment().getPaymentNum());
		nextBtn.setEnabled(true);
		if (receiver == null) {
			receiver = new RechargeReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.recharge");
			registerReceiver(receiver, filter);
		}
	}


	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(this, getResources().getString(R.string.no_wifi));
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		RadioButton moneyButton = (RadioButton) RechargeActivity.this.findViewById(checkedId);
		if (checkedId != R.id.recharge1000Button) {
			(RechargeActivity.this.findViewById(R.id.recharge1000Button)).setVisibility(View.VISIBLE);
			amountEditText.setText("");
			amountEditText.setVisibility(View.GONE);
			moneyAmount = Float.valueOf(moneyButton.getText().toString());
			ViewUtil.hideSoftKeyboard(this);
		} else {
			moneyAmount = 0;
			(RechargeActivity.this.findViewById(R.id.recharge1000Button)).setVisibility(View.INVISIBLE);
			amountEditText.setVisibility(View.VISIBLE);
			amountEditText.setFocusable(true);
			amountEditText.setFocusableInTouchMode(true);
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(amountEditText, 0);
		}
	}

	public class RechargeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			PaySuccessActivity.launch(RechargeActivity.this, PaySuccessActivity.RECHARGE, PaySuccessActivity.WEIXIN, orderEntity.getPayment().getAmount() + "", null);
			finish();
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
	protected void onResume() {
		super.onResume();
		dismissProgress();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}
}
