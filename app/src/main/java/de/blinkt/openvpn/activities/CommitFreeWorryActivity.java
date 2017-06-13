package de.blinkt.openvpn.activities;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.aixiaoqi.wxapi.WXPayEntryActivity;
import cn.com.johnson.adapter.CommitFreeWorryAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.ui.PaySuccessActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BalanceGetPayIdHttp;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetAttrsByIdHttp;
import de.blinkt.openvpn.http.OrderAddHttp;
import de.blinkt.openvpn.http.WeixinGetPayIdHttp;
import de.blinkt.openvpn.model.GetAttrsByIdEntity;
import de.blinkt.openvpn.model.OrderAddEntity;
import de.blinkt.openvpn.model.PayResult;
import de.blinkt.openvpn.model.WeiXinResultEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.OrderInfoUtil2_0;

import static de.blinkt.openvpn.constant.Constant.ALI_APPID;
import static de.blinkt.openvpn.constant.Constant.RSA_PRIVATE;
import static de.blinkt.openvpn.constant.Constant.WEIXIN_APPID;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSUREPAGMENT;

public class CommitFreeWorryActivity extends BaseNetActivity implements TextWatcher {

	@BindView(R.id.monthlyFeeEditText)
	EditText monthlyFeeEditText;
	/*@BindView(R.id.callTimeTextView)
	TextView callTimeTextView;*/
	@BindView(R.id.callPacketRecyclerView)
	RecyclerView callPacketRecyclerView;
	@BindView(R.id.serviceFeeTextView)
	TextView serviceFeeTextView;
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
	@BindView(R.id.originalPriceTextView)
	TextView originalPriceTextView;
	@BindView(R.id.sureTextView)
	TextView sureTextView;
	@BindView(R.id.monthCountRecyclerView)
	RecyclerView monthCountRecyclerView;
	//	@BindView(R.id.buyMonthRadioGroup)
//	RadioGroup buyMonthRadioGroup;
	private int BALANCE_PAY_METHOD = 3;
	private int WEIXIN_PAY_METHOD = 2;
	private int ALI_PAY_METHOD = 1;
	private final int SDK_PAY_FLAG = 1;
	private IWXAPI api;
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
						PaySuccessActivity.launch(CommitFreeWorryActivity.this, PaySuccessActivity.BUY, PaySuccessActivity.ALI, orderEntity.getOrder().getTotalPrice() + "", orderEntity.getOrder().getOrderID());
						finish();
					} else if (TextUtils.equals(resultStatus, "6002")) {
						sureTextView.setEnabled(true);
						CommonTools.showShortToast(CommitFreeWorryActivity.this, payResult.getMemo());
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						Toast.makeText(CommitFreeWorryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
						sureTextView.setEnabled(true);
					}
					break;
				}
				default:
					break;
			}
		}

	};
	//每月费用
	private int monthlyFeeInt;
	//	//购买月数
//	private int monthCount = 1;
	private ArrayList<String> monthData = new ArrayList<>();
	private ArrayList<String> callPacketData = new ArrayList<>();
	private List<GetAttrsByIdEntity.ListBean> allData;
	private CommitFreeWorryAdapter callPacketAdapter;
	private CommitFreeWorryAdapter monthCountAdapter;
	private String callPacket;
	private String monthCountStr;
	//当前选择的bean
	private GetAttrsByIdEntity.ListBean bean;
	//余额float
	private float balanceFloat;
	private OrderAddEntity orderEntity;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commit_free_worry);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.dredge_free_for_worry, 0);
		double price = getIntent().getDoubleExtra("price", 0);
		String priceStr = String.format(getString(R.string.price_everymonth) + "", price);
//		float originalPrice = Float.parseFloat(getIntent().getStringExtra("originalPrice"));
//		String originalPriceStr = String.format(getString(R.string.origin_price) + "", originalPrice);
//		originalPriceTextView.setText(originalPriceStr);
//		originalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		serviceFeeTextView.setText(priceStr);
		monthCountAdapter = new CommitFreeWorryAdapter(this, monthData, new CommitFreeWorryAdapter.OnClickItemLisener() {
			@Override
			public void onItemClick(String textContent, int position) {
				monthCountStr = textContent;
				monthCountAdapter.setCheck(position);
				monthCountAdapter.notifyDataSetChanged();
				checkMoney();
			}
		});
		monthCountRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		monthCountRecyclerView.setAdapter(monthCountAdapter);
		callPacketAdapter = new CommitFreeWorryAdapter(this, callPacketData, new CommitFreeWorryAdapter.OnClickItemLisener() {
			@Override
			public void onItemClick(String textContent, int position) {
				callPacket = textContent;
				callPacketAdapter.setCheck(position);
				callPacketAdapter.notifyDataSetChanged();
				checkMoney();
			}
		});
		callPacketRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		callPacketRecyclerView.setAdapter(callPacketAdapter);
//		buyMonthRadioGroup.setOnCheckedChangeListener(this);
		monthlyFeeEditText.addTextChangedListener(this);
//		buyMonthRadioGroup.check(R.id.month1RadioButton);
		addUpTextView.setText("￥0.0");
		setSpan(addUpTextView);
		addData();
	}


	//比对结算金额
	private void checkMoney() {
		if (callPacket != null && monthCountStr != null) {
			for (GetAttrsByIdEntity.ListBean bean : allData) {
				Log.i("compare", "比对：bean.getCallMinutesDescr():" + bean.getCallMinutesDescr()
						+ ",callPacket:" + callPacket + "   bean.getExpireDaysDescr():"
						+ bean.getExpireDaysDescr() + " , monthCountStr:" + monthCountStr);
				if (bean.getCallMinutesDescr().equals(callPacket) && bean.getExpireDaysDescr().equals(monthCountStr)) {
					float allMoney = (Float.valueOf(bean.getPrice()) + (monthlyFeeInt * Float.valueOf(bean.getExpireDays())));
					float originalAllMoney = (Float.valueOf(bean.getOriginalPrice()) + (monthlyFeeInt * Float.valueOf(bean.getExpireDays())));
					addUpTextView.setText("￥" + allMoney);
					originalPriceTextView.setText("￥" + originalAllMoney);
					originalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
					setSpan(addUpTextView);
					this.bean = bean;
					checkBalance();
				}
			}
		} else {
			CommonTools.showShortToast(this, "callPacket/monthCountStr null!");
		}
	}

	//获取数据
	private void addData() {
		createHttpRequest(HttpConfigUrl.COMTYPE_GET_ATTRS_BY_ID, getIntent().getStringExtra("id"));
	}

	@OnClick({R.id.balancePayLienarLayout, R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout, R.id.sureTextView})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.balancePayLienarLayout:
				weixinPayCheckBox.setChecked(false);
				aliPayCheckBox.setChecked(false);
				balancePayCheckBox.setChecked(true);
				break;
			case R.id.weixinPayLienarLayout:
				balancePayCheckBox.setChecked(false);
				weixinPayCheckBox.setChecked(true);
				aliPayCheckBox.setChecked(false);
				break;
			case R.id.aliPayLienarLayout:
				balancePayCheckBox.setChecked(false);
				weixinPayCheckBox.setChecked(false);
				aliPayCheckBox.setChecked(true);
				break;
			case R.id.sureTextView:
				HashMap<String, String> map = new HashMap<>();
				sureTextView.setEnabled(false);
				String packetId = getIntent().getStringExtra("id");
				String orderid = bean.getID();
				if (weixinPayCheckBox.isChecked()) {
					if (isWXAppInstalledAndSupported()) {
						map.put("type", WEIXIN_PAY_METHOD + "");
						//友盟方法统计
						MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
						createHttpRequest(HttpConfigUrl.COMTYPE_CREATE_ORDER, packetId
								, 1 + "", WEIXIN_PAY_METHOD + "", monthlyFeeInt + "", orderid);
					} else {
						CommonTools.showShortToast(this, getResources().getString(R.string.no_weixin_yet));
						sureTextView.setEnabled(true);
					}
				} else if (aliPayCheckBox.isChecked()) {
					map.put("type", ALI_PAY_METHOD + "");
					//友盟方法统计
					MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
					createHttpRequest(HttpConfigUrl.COMTYPE_CREATE_ORDER, packetId, 1 + "", ALI_PAY_METHOD + "", monthlyFeeInt + "", orderid);
				} else {
					map.put("type", BALANCE_PAY_METHOD + "");
					//友盟方法统计
					MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
					createHttpRequest(HttpConfigUrl.COMTYPE_CREATE_ORDER, packetId, 1 + "", BALANCE_PAY_METHOD + "", monthlyFeeInt + "", orderid);
				}
				break;
		}
	}

	private void checkBalance() {
		if (bean == null) return;
		if (weixinPayCheckBox.isChecked()) {
			showBalanceCheckBox();
			return;
		}
		if ((Float.valueOf(bean.getPrice()) + (monthlyFeeInt * Float.valueOf(bean.getExpireDays()))) < balanceFloat) {
			balanceTextView.setText(getResources().getString(R.string.balance_pay) + "(剩余￥" + balanceFloat + ")");
			balanceTextView.setEnabled(true);
			setBalanceSpan(balanceTextView, balanceFloat);
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

	//设置余额span
	private void setBalanceSpan(TextView balanceTextView, float balanceFloat) {
		Spannable WordtoSpan = new SpannableString(balanceTextView.getText().toString());
		WordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.order_detail_orange)), 7, 8 + (balanceFloat + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		balanceTextView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	private void showBalanceCheckBox() {
		if ((Float.valueOf(bean.getPrice()) + (monthlyFeeInt * Float.valueOf(bean.getExpireDays()))) < balanceFloat) {
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

	private boolean isWXAppInstalledAndSupported() {
		IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
		msgApi.registerApp(WEIXIN_APPID);
		boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
				&& msgApi.isWXAppSupportAPI();
		return sIsWXAppInstalledAndSupported;
	}

	//设置大小字体
	public void setSpan(TextView textview) {
		String moneyStr = textview.getText().toString();
		String moneyIntergerStr = moneyStr.split("\\.")[0];
		Spannable WordtoSpan = new SpannableString(moneyStr);
		int intLength = moneyIntergerStr.length();
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() != 0) {
			monthlyFeeInt = Integer.parseInt(s.toString());
			checkMoney();
		} else {
			monthlyFeeInt = 0;
			checkMoney();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}


	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_ATTRS_BY_ID) {
			GetAttrsByIdHttp http = (GetAttrsByIdHttp) object;
			if (http.getStatus() == 1) {
				for (GetAttrsByIdEntity.ListBean entity : http.getAttrsList()) {
					monthData.add(entity.getExpireDaysDescr());
					callPacketData.add(entity.getCallMinutesDescr());
				}
				allData = http.getAttrsList();
				removeDuplicate();
				callPacket = callPacketData.get(0);
				monthCountStr = monthData.get(0);
				monthCountAdapter.notifyDataSetChanged();
				callPacketAdapter.notifyDataSetChanged();
				checkMoney();
				//查询余额
				createHttpRequest(HttpConfigUrl.COMTYPE_GET_BALANCE);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_BALANCE) {
			BalanceHttp http = (BalanceHttp) object;
			balanceFloat = http.getBalanceEntity().getAmount();
			checkBalance();
		} else if (cmdType == HttpConfigUrl.COMTYPE_CREATE_ORDER) {
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
				CommonTools.showShortToast(this, object.getMsg());
				sureTextView.setEnabled(true);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_BALANCE_GETPAYID) {
			sureTextView.setEnabled(true);
			if (object.getStatus() == 1) {
//				PaySuccessActivity.launch(CommitOrderActivity.this, PaySuccessActivity.BUY, PaySuccessActivity.BALANCE,
//						orderEntity.getOrder().getTotalPrice() + "", orderEntity.getOrder().getOrderID());
				MyOrderDetailActivity.launch(this, orderEntity.getOrder().getOrderID());
				ICSOpenVPNApplication.getInstance().finishOtherActivity();
//				finish();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID) {
			try {
				WXPayEntryActivity.PAY_PURPOSE = WXPayEntryActivity.PAY_ORDER;
				api = WXAPIFactory.createWXAPI(CommitFreeWorryActivity.this, WEIXIN_APPID);
				WeixinGetPayIdHttp http = (WeixinGetPayIdHttp) object;
				WeiXinResultEntity entity = http.getWeixinResultEntity();
				PayReq req = new PayReq();
				req.appId = entity.getAppid();
				req.partnerId = entity.getPartnerid();
				req.prepayId = entity.getPrepayid();
				req.nonceStr = entity.getNoncestr();
				req.timeStamp = entity.getTimestamp();
				req.packageValue = entity.getPackageX();
				req.sign = entity.getSign();
				req.extData = "app data"; // optional
				api.sendReq(req);
				dismissProgress();
				sureTextView.setEnabled(true);
			} catch (Exception e) {
				Log.e("PAY_GET", "异常：" + e.getMessage());
				Toast.makeText(CommitFreeWorryActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
			}

		}
	}


	/**
	 * 余额支付
	 */
	private void payForBalance() {
		BalanceGetPayIdHttp http = new BalanceGetPayIdHttp(this, HttpConfigUrl.COMTYPE_BALANCE_GETPAYID, orderEntity.getOrder().getOrderID());
		new Thread(http).start();
	}

	/**
	 * 微信支付
	 */
	private void payForWeixin() {
		showProgress(getResources().getString(R.string.weixin_paying), true);
		SharedPreferences preferences = getSharedPreferences("order", MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("orderId", orderEntity.getOrder().getOrderID());
		editor.putString("orderAmount", orderEntity.getOrder().getTotalPrice() + "");
		editor.commit();
		createHttpRequest(HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID, orderEntity.getOrder().getOrderNum());
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
				PayTask alipay = new PayTask(CommitFreeWorryActivity.this);
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


	//去重
	private void removeDuplicate() {
		for (int i = 0; i < monthData.size() - 1; i++) {
			for (int j = monthData.size() - 1; j > i; j--) {
				if (monthData.get(j).equals(monthData.get(i))) {
					monthData.remove(j);
				}
			}
		}
		for (int i = 0; i < callPacketData.size() - 1; i++) {
			for (int j = callPacketData.size() - 1; j > i; j--) {
				if (callPacketData.get(j).equals(callPacketData.get(i))) {
					callPacketData.remove(j);
				}
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//查询余额
		createHttpRequest(HttpConfigUrl.COMTYPE_GET_BALANCE);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		monthData.clear();
		monthData = null;
		callPacketData.clear();
		callPacketData = null;
	}

}
