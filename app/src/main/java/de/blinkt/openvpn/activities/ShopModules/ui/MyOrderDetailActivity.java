package de.blinkt.openvpn.activities.ShopModules.ui;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.FreeWorryIntroActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ActivateActivity;
import de.blinkt.openvpn.activities.OutsideFirstStepActivity;
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
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.BuySucceedDialog;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static android.view.View.GONE;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.orderStatus;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;

public class MyOrderDetailActivity extends BaseNetActivity implements InterfaceCallback, DialogInterfaceTypeBase {

	public static String FINISH_PROCESS = "finish";
	public static String FINISH_PROCESS_ONLY = "finish_process_only";
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
	@BindView(R.id.aboard_how_to_use)
	Button aboardHowToUse;
	@BindView(R.id.inland_reset)
	Button inlandReset;
	private OrderEntity.ListBean bean;
	private boolean isCreateView = false;
	private DialogBalance cardRuleBreakDialog;
	private boolean isActivateSuccess = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		addData();
	}

	public static void launch(Context context, String id, int PackageCategory) {
		Intent intent = new Intent(context, MyOrderDetailActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("PackageCategory", PackageCategory);
		context.startActivity(intent);
	}

	public static void launch(Context context, String id) {
		Intent intent = new Intent(context, MyOrderDetailActivity.class);
		intent.putExtra("id", id);
		context.startActivity(intent);
	}

	private void initSet() {
		hasLeftViewTitle(R.string.order_detail, 0);
		if (getIntent().getIntExtra("PackageCategory", -1) != 0) {
			aboardHowToUse.setVisibility(GONE);
			inlandReset.setVisibility(GONE);
		} else {
			cancelOrderButton.setVisibility(GONE);
		}
	}


	//获取数据
	private void addData() {
		LocalBroadcastManager.getInstance(this).registerReceiver(isWriteReceiver, setFilter());
		showDefaultProgress();
		createHttpRequest(HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, getIntent().getStringExtra("id"));
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID) {
			dismissProgress();
			if (object.getStatus() == 1) {
				if (!isCreateView) {
					createViews();
				}
				NoNetRelativeLayout.setVisibility(GONE);
				GetOrderByIdHttp http = (GetOrderByIdHttp) object;
				bean = http.getOrderEntity().getList();
				if (bean != null) {
					if (bean.getLogoPic() != null)
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
						aboardHowToUse.setVisibility(GONE);
						inlandReset.setVisibility(GONE);
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


					if ("1".equals(bean.getPackageCategory())) {
						activateTextView.setVisibility(GONE);
						aboardHowToUse.setVisibility(GONE);
						inlandReset.setVisibility(GONE);
						dateTitleTextView.setVisibility(GONE);
						dateTextView.setVisibility(GONE);
					} else if ("4".equals(bean.getPackageCategory()) || "5".equals(bean.getPackageCategory())) {
						activateTextView.setVisibility(GONE);
						aboardHowToUse.setVisibility(GONE);
						inlandReset.setVisibility(GONE);
						dateTitleTextView.setVisibility(GONE);
						dateTextView.setVisibility(GONE);
						statueTextView.setVisibility(GONE);
						packageStateTextView.setVisibility(GONE);
					} else {
						if (getIntent().getIntExtra("PackageCategory", -1) != 0 && !isActivateSuccess) {
							showBuySucceedDialog();
						}
					}
					priceTextView.setText("￥" + bean.getUnitPrice());
					setSpan(priceTextView);
					packetCountTextView.setText("x" + bean.getQuantity());
					orderNumberTextView.setText(bean.getOrderNum());
					orderTimeTextView.setText(DateUtils.getDateToString(bean.getOrderDate() * 1000));
					allPriceTextView.setText("￥" + bean.getTotalPrice());
					payWayTextView.setText(getPaymentMethod(bean.getPaymentMethod()));
					dateTextView.setText(DateUtils.getDateToString(bean.getLastCanActivationDate() * 1000));
				}
			}
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
				if (!SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD)) {
					sendMessageSeparate(orderDataHttp.getOrderDataEntity().getData());
				} else {
					ICSOpenVPNApplication.cardData = orderDataHttp.getOrderDataEntity().getData();
					Log.i(TAG, "卡数据：" + ICSOpenVPNApplication.cardData);
					ReceiveBLEMoveReceiver.isGetnullCardid = false;
					sendMessageSeparate(Constant.WRITE_SIM_FIRST);
				}
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
					CommonTools.showShortToast(MyOrderDetailActivity.this, "激活失败！请检查你的SIM卡是否是爱小器SIM卡");
				} else {
					isActivateSuccess = true;
				}
				GetOrderByIdHttp http = new GetOrderByIdHttp(MyOrderDetailActivity.this, HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, getIntent().getStringExtra("id"));
				new Thread(http).start();
			} else if (TextUtils.equals(intent.getAction(), FINISH_PROCESS_ONLY)) {
				dismissProgress();
			}
		}
	};

	private void showDialog() {
		//不能按返回键，只能二选其一
		cardRuleBreakDialog = new DialogBalance(this, MyOrderDetailActivity.this, R.layout.dialog_balance, 2);
		cardRuleBreakDialog.setCanClickBack(false);
		cardRuleBreakDialog.changeText(getResources().getString(R.string.no_aixiaoqi_or_rule_break), getResources().getString(R.string.reset));
	}


	private void showBuySucceedDialog() {
		//不能按返回键，只能二选其一
		BuySucceedDialog buySucceedDialog = new BuySucceedDialog(this, MyOrderDetailActivity.this, R.layout.dialog_balance, 3);
//        cardRuleBreakDialog.setCanClickBack(false);
		buySucceedDialog.changeText(getResources().getString(R.string.tip_buy_succeed), getResources().getString(R.string.activating), getResources().getString(R.string.wait_activate));
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void receiveWriteCardIdEntity(WriteCardEntity entity) {
		String nullcardId = entity.getNullCardId();
		orderDataHttp(nullcardId);
	}


	private void sendMessageSeparate(final String message) {
		String[] messages = PacketeUtil.Separate(message, "1300");
		ReceiveBLEMoveReceiver.lastSendMessageStr = message;
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
		filter.addAction(MyOrderDetailActivity.FINISH_PROCESS_ONLY);
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

	@OnClick({R.id.cancelOrderButton, R.id.activateTextView, R.id.retryTextView, R.id.orderDetailTitleRelativeLayout, R.id.aboard_how_to_use, R.id.inland_reset})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.cancelOrderButton:
				if (bean != null) {
					ICSOpenVPNApplication.getInstance().finishOtherActivity();
				}
				break;
			case R.id.activateTextView:
				activatePackage();
				break;
			case R.id.retryTextView:
				addData();
				break;
			case R.id.orderDetailTitleRelativeLayout:
				if ("1".equals(bean.getPackageCategory())) {
					CallTimePacketDetailActivity.launch(this, bean.getPackageId());
				} else if ("4".equals(bean.getPackageCategory())) {
					CallTimePacketDetailActivity.launch(this, bean.getPackageId(), this.getString(R.string.receive_fw), bean.getOrderStatus() == 2);
				} else if ("5".equals(bean.getPackageCategory())) {
					FreeWorryIntroActivity.launch(this, bean.getPackageId());
				} else {
					PackageDetailActivity.launch(this, bean.getPackageId(), bean.getPic());
				}
				break;
			case R.id.inland_reset:
				Constant.isOutsideSecondStepClick = false;
				Constant.isOutsideThirdStepClick = false;
				toActivity(new Intent(this, OutsideFirstStepActivity.class)
						.putExtra(IntentPutKeyConstant.OUTSIDE, IntentPutKeyConstant.AFTER_GOING_ABROAD)
						.putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, bean.isPackageIsSupport4G())
						.putExtra(IntentPutKeyConstant.APN_NAME, bean.getPackageApnName())
				);
				break;
			case R.id.aboard_how_to_use:
				Constant.isOutsideSecondStepClick = false;
				Constant.isOutsideThirdStepClick = false;
				toActivity(new Intent(this, OutsideFirstStepActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE, IntentPutKeyConstant.OUTSIDE)
						.putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, bean.isPackageIsSupport4G())
						.putExtra(IntentPutKeyConstant.APN_NAME, bean.getPackageApnName())
				);

				break;
		}
	}

	private void activatePackage() {
		String operator = SharedUtils.getInstance().readString(Constant.OPERATER);
		UartService uartService = ICSOpenVPNApplication.uartService;
		if (!TextUtils.isEmpty(operator)
				&& uartService != null
				&& uartService.isConnectedBlueTooth()) {
			showDialog();
			return;
		}
		if (!CommonTools.isFastDoubleClick(3000)) {
			//友盟方法统计
			MobclickAgent.onEvent(context, CLICKACTIVECARD);
			OrderID = bean.getOrderID();
			//如果订单未激活跳转到激活界面
			if (bean.getOrderStatus() == 0)
				toActivity(new Intent(this, ActivateActivity.class).putExtra(IntentPutKeyConstant.ORDER_ID, bean.getOrderID()).putExtra("ExpireDaysInt", bean.getExpireDaysInt())
						.putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, bean.isPackageIsSupport4G())
						.putExtra(IntentPutKeyConstant.COUNTRY_NAME, bean.getCountryName())
						.putExtra(IntentPutKeyConstant.APN_NAME, bean.getPackageApnName())
				);
			else {
				IS_TEXT_SIM = false;
				orderStatus = 4;
				showProgress("正在激活", false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							ReceiveBLEMoveReceiver.isGetnullCardid = true;
							SendCommandToBluetooth.sendMessageToBlueTooth(Constant.UP_TO_POWER_NO_RESPONSE);
							Thread.sleep(20000);
						} catch (InterruptedException e) {
							dismissProgress();
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
			}
//					}
		}
	}

	public static String OrderID;

	private void orderDataHttp(String nullcardNumber) {
		if (nullcardNumber != null) {
			if (!CommonTools.isFastDoubleClick(100))
				if (SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD))
					nullcardNumber = null;
			createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_DATA, bean.getOrderID(), nullcardNumber);
		} else {
			dismissProgress();
			CommonTools.showShortToast(this, getString(R.string.no_nullcard_id));
		}
	}

	@Override
	protected void onDestroy() {
		if (isWriteReceiver != null)
			LocalBroadcastManager.getInstance(this).unregisterReceiver(isWriteReceiver);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			SendCommandToBluetooth.sendMessageToBlueTooth(Constant.RESTORATION);
		} else if (type == 3) {
			if (TextUtils.isEmpty(text)) {
				activatePackage();
			} else {
				ICSOpenVPNApplication.getInstance().finishOtherActivity();
			}


		}
	}
}
