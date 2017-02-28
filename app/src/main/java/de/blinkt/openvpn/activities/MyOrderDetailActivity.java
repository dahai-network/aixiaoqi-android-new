package de.blinkt.openvpn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CancelOrderHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetOrderByIdHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.OrderDataHttp;
import de.blinkt.openvpn.model.OrderEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static android.view.View.GONE;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.orderStatus;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCANCELORDER;

public class MyOrderDetailActivity extends BaseActivity implements InterfaceCallback, DialogInterfaceTypeBase {

	public static String FINISH_PROCESS = "finish";
	public static String CARD_RULE_BREAK = "card_rule_break";
	@BindView(R.id.countryImageView)
	ImageView countryImageView;
	@BindView(R.id.packageNameTextView)
	TextView packageNameTextView;
	@BindView(R.id.dateTextView)
	TextView dateTextView;
	@BindView(R.id.priceTextView)
	TextView priceTextView;
	@BindView(R.id.packetCountTextView)
	TextView packetCountTextView;
	@BindView(R.id.packageStateTextView)
	TextView packageStateTextView;
	@BindView(R.id.expiryDateTextView)
	TextView expiryDateTextView;
	@BindView(R.id.activateTextView)
	TextView activateTextView;
	@BindView(R.id.orderNumberTextView)
	TextView orderNumberTextView;
	@BindView(R.id.orderTimeTextView)
	TextView orderTimeTextView;
	@BindView(R.id.payWayTextView)
	TextView payWayTextView;

	@BindView(R.id.allPriceTextView)
	TextView allPriceTextView;

	@BindView(R.id.cancelOrderButton)
	TextView cancelOrderButton;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.textView2)
	TextView textView2;
	@BindView(R.id.textView3)
	TextView textView3;
	@BindView(R.id.orderDetailTitleRelativeLayout)
	RelativeLayout orderDetailTitleRelativeLayout;
	@BindView(R.id.line1)
	View line1;
	@BindView(R.id.line2)
	View line2;
	@BindView(R.id.line3)
	View line3;
	@BindView(R.id.dateTitleTextView)
	TextView dateTitleTextView;
	@BindView(R.id.expirytitleTextView)
	TextView expirytitleTextView;
	@BindView(R.id.statueTextView)
	TextView statueTextView;
	private OrderEntity.ListBean bean;
	private boolean isCreateView = false;
	private DialogBalance cardRuleBreakDialog;
	private boolean isActivateSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addData();
	}

	public static void launch(Context context, String id,int PackageCategory) {
		Intent intent = new Intent(context, MyOrderDetailActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("PackageCategory", PackageCategory);
		context.startActivity(intent);
	}
	public static void launch(Context context, String id ) {
		Intent intent = new Intent(context, MyOrderDetailActivity.class);
		intent.putExtra("id", id);
		context.startActivity(intent);
	}

	private void initSet() {
		if(getIntent().getIntExtra("PackageCategory",-1)==0){
			hasAllViewTitle(R.string.order_detail,R.string.standby_tutorial,0,false);
		}else{
		hasLeftViewTitle(R.string.order_detail, 0);}
	}

	@Override
	protected void onClickRightView() {
		toActivity(new Intent(this,FastSetActivity.class).putExtra(IntentPutKeyConstant.IS_SUPPORT_4G,bean.isPackageIsSupport4G()));
	}

	//获取数据
	private void addData() {
		LocalBroadcastManager.getInstance(this).registerReceiver(isWriteReceiver, setFilter());
		showDefaultProgress();
		GetOrderByIdHttp http = new GetOrderByIdHttp(this, HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, getIntent().getStringExtra("id"));
		new Thread(http).start();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID) {
			dismissProgress();
			if (!isCreateView) {
				createViews();
			}
			NoNetRelativeLayout.setVisibility(GONE);
			GetOrderByIdHttp http = (GetOrderByIdHttp) object;
			bean = http.getOrderEntity().getList();
			Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(countryImageView);
			packageNameTextView.setText(bean.getPackageName());
			//如果订单状态是正在使用，那么就计算时间
			if (bean.getOrderStatus() == 0) {
				expiryDateTextView.setText(bean.getExpireDays());
				expirytitleTextView.setText(getResources().getString(R.string.expireday));
				packageStateTextView.setText("未激活");
			} else if (bean.getOrderStatus() == 2) {
				packageStateTextView.setText("订单已过期");
				expiryDateTextView.setVisibility(GONE);
				activateTextView.setVisibility(GONE);
				cancelOrderButton.setVisibility(GONE);
				expirytitleTextView.setVisibility(GONE);
				expirytitleTextView.setVisibility(GONE);
				expiryDateTextView.setVisibility(GONE);
			} else if (bean.getOrderStatus() == 3) {
				packageStateTextView.setText("订单已经被取消");
				expiryDateTextView.setVisibility(GONE);
				activateTextView.setVisibility(GONE);
				cancelOrderButton.setVisibility(GONE);
			} else if (bean.getOrderStatus() == 4) {
				packageStateTextView.setText("激活失败");
				cancelOrderButton.setVisibility(GONE);
				expiryDateTextView.setText(bean.getExpireDays());
				activateTextView.setText("再次激活");
			} else if (bean.getOrderStatus() == 1) {
				packageStateTextView.setText("已激活");
				cancelOrderButton.setVisibility(GONE);
				expiryDateTextView.setText(bean.getExpireDays());
				activateTextView.setText("再次激活");
			}
			priceTextView.setText("￥" + bean.getUnitPrice());
			setSpan(priceTextView);
			packetCountTextView.setText("x" + bean.getQuantity());
			orderNumberTextView.setText(bean.getOrderNum());
			orderTimeTextView.setText(DateUtils.getDateToString(bean.getOrderDate() * 1000));
			allPriceTextView.setText("￥" + bean.getTotalPrice());
			payWayTextView.setText(getPaymentMethod(bean.getPaymentMethod()));
			dateTextView.setText(DateUtils.getDateToString(bean.getLastCanActivationDate() * 1000));
		} else if (cmdType == HttpConfigUrl.COMTYPE_CANCEL_ORDER) {
			CancelOrderHttp http = (CancelOrderHttp) object;
			if (http.getStatus() == 1) {
				CommonTools.showShortToast(MyOrderDetailActivity.this, "取消订单成功！");
				onBackPressed();
			} else {
				CommonTools.showShortToast(MyOrderDetailActivity.this, http.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_ORDER_DATA) {
			OrderDataHttp orderDataHttp = (OrderDataHttp) object;
			if (orderDataHttp.getStatus() == 1) {
				Log.i("cardNumber", "写卡ID:" + orderDataHttp.getOrderDataEntity().getData());
				sendMessageSeparate(orderDataHttp.getOrderDataEntity().getData());
			} else {
				CommonTools.showShortToast(MyOrderDetailActivity.this, orderDataHttp.getMsg());
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (ReceiveBLEMoveReceiver.orderStatus == 1) {
			packageStateTextView.setText("已激活");
			cancelOrderButton.setVisibility(GONE);
			expiryDateTextView.setText(bean.getExpireDays());
			activateTextView.setText("再次激活");
			ReceiveBLEMoveReceiver.orderStatus = -1;
		} else if (ReceiveBLEMoveReceiver.orderStatus == 4) {
			packageStateTextView.setText("激活失败");
			cancelOrderButton.setVisibility(GONE);
			expiryDateTextView.setText(bean.getExpireDays());
			activateTextView.setText("再次激活");
			ReceiveBLEMoveReceiver.orderStatus = -1;
		}
	}

	private BroadcastReceiver isWriteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (TextUtils.equals(intent.getAction(), CARD_RULE_BREAK)) {
				dismissProgress();
				showDialog();
			} else if (TextUtils.equals(intent.getAction(), FINISH_PROCESS)) {
				if (ReceiveBLEMoveReceiver.orderStatus == 4) {
					HashMap<String, String> map = new HashMap<>();
					map.put("statue", 0 + "");
					//友盟方法统计
					MobclickAgent.onEvent(mContext, CLICKACTIVECARD, map);
					CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), "激活失败！请检查你的SIM卡是否是爱小器SIM卡");
				} else {
					isActivateSuccess = true;
				}
				GetOrderByIdHttp http = new GetOrderByIdHttp(MyOrderDetailActivity.this, HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, getIntent().getStringExtra("id"));
				new Thread(http).start();
			}
		}
	};

	private void showDialog() {
		//不能按返回键，只能二选其一
		cardRuleBreakDialog = new DialogBalance(this, MyOrderDetailActivity.this, R.layout.dialog_balance, 2);
		cardRuleBreakDialog.setCanClickBack(false);
		cardRuleBreakDialog.changeText(getResources().getString(R.string.no_card_or_rule_break), getResources().getString(R.string.reset));
	}

	private void sendMessageSeparate(final String message) {
		String[] messages = PacketeUtil.Separate(message, "1300");
		int length = messages.length;
		for (int i = 0; i < length; i++) {
			if (!SendCommandToBluetooth.sendMessageToBlueTooth(messages[i])) {
				CommonTools.showShortToast(MyOrderDetailActivity.this, "设备已断开，请重新连接");
				dismissProgress();
			}
		}
	}


	private void createViews() {
		setContentView(R.layout.activity_myorder_detail);
		ButterKnife.bind(this);
		initSet();
		isCreateView = true;
	}

	private IntentFilter setFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyOrderDetailActivity.FINISH_PROCESS);
		filter.addAction(MyOrderDetailActivity.CARD_RULE_BREAK);
		return filter;
	}


	private String getPaymentMethod(String paymentMethod) {
		switch (paymentMethod) {
			case "1":
				return getResources().getString(R.string.ali_pay);
			case "2":
				return getResources().getString(R.string.weixin_pay);
			case "3":
				return getResources().getString(R.string.balance_pay);
			case "4":
				return getResources().getString(R.string.official_gifts);
			default:
				return "";
		}
	}

	//设置大小字体
	public void setSpan(TextView textview) {
		Spannable WordtoSpan = new SpannableString(textview.getText().toString());
		int intLength = String.valueOf((int) (bean.getUnitPrice())).length();
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		dismissProgress();
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		dismissProgress();
		createViews();
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
	}

	@OnClick({R.id.cancelOrderButton, R.id.activateTextView, R.id.retryTextView, R.id.orderDetailTitleRelativeLayout})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.cancelOrderButton:
				if (bean != null) {
					if (!CommonTools.isFastDoubleClick(1000)) {
						//友盟方法统计
						MobclickAgent.onEvent(context, CLICKCANCELORDER);
						CancelOrderHttp http = new CancelOrderHttp(this, HttpConfigUrl.COMTYPE_CANCEL_ORDER, bean.getOrderID());
						new Thread(http).start();
					}
				}
				break;
			case R.id.activateTextView:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKACTIVECARD);
				OrderID = bean.getOrderID();
				//如果订单未激活跳转到激活界面
				if (bean.getOrderStatus() == 0)
					toActivity(new Intent(this, ActivateActivity.class).putExtra(IntentPutKeyConstant.ORDER_ID, bean.getOrderID()).putExtra("ExpireDaysInt", bean.getExpireDaysInt()));
				else {
					//是否测试卡位置：否，这是写卡！
					IS_TEXT_SIM = false;
					orderStatus = 4;
					showProgress("正在激活");
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(20000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (!isActivateSuccess) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dismissProgress();
										CommonTools.showShortToast(MyOrderDetailActivity.this, getString(R.string.activate_fail));
									}
								});
							}
						}
					}).start();
					SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER);
					orderDataHttp(SharedUtils.getInstance().readString(Constant.NULLCARD_SERIALNUMBER));
				}
				break;
			case R.id.retryTextView:
				addData();
				break;
			case R.id.orderDetailTitleRelativeLayout:
				PackageDetailActivity.launch(this, bean.getPackageId(), bean.getPic());
				break;
		}
	}

	public static String OrderID;

	private void orderDataHttp(String nullcardNumber) {
		if (nullcardNumber != null) {
			OrderDataHttp orderDataHttp = new OrderDataHttp(this, HttpConfigUrl.COMTYPE_ORDER_DATA, bean.getOrderID(), nullcardNumber);
			new Thread(orderDataHttp).start();
		} else {
			CommonTools.showShortToast(this, getString(R.string.no_nullcard_id));
		}
	}

	@Override
	protected void onDestroy() {
		if (isWriteReceiver != null)
			LocalBroadcastManager.getInstance(this).unregisterReceiver(isWriteReceiver);
		super.onDestroy();
	}

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			SendCommandToBluetooth.sendMessageToBlueTooth("AA112233AA");
		}
	}
}
