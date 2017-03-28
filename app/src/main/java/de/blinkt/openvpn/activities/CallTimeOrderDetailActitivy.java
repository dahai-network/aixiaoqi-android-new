package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetOrderByIdHttp;
import de.blinkt.openvpn.model.OrderEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;


public class CallTimeOrderDetailActitivy extends BaseNetActivity   {

	public static CallTimeOrderDetailActitivy actitivy ;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.packetImageView)
	ImageView packetImageView;
	@BindView(R.id.packageNameTextView)
	TextView packageNameTextView;
	@BindView(R.id.priceTextView)
	TextView priceTextView;
	@BindView(R.id.orderNumberTextView)
	TextView orderNumberTextView;
	@BindView(R.id.orderTimeTextView)
	TextView orderTimeTextView;
	@BindView(R.id.payWayTextView)
	TextView payWayTextView;
	@BindView(R.id.expiryDateTextView)
	TextView expiryDateTextView;
	@BindView(R.id.packetStatusTextView)
	TextView packetStatusTextView;
	@BindView(R.id.headBarRelativeLayout)
	RelativeLayout headBarRelativeLayout;
	@BindView(R.id.line1)
	View line1;
	@BindView(R.id.textView3)
	TextView textView3;
	@BindView(R.id.textView)
	TextView textView;
	@BindView(R.id.line2)
	View line2;
	@BindView(R.id.textView4)
	TextView textView4;
	@BindView(R.id.line3)
	View line3;
	private boolean isCreateView;
	private OrderEntity.ListBean bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actitivy = this;
		addData();
	}

	public static void launch(Context context, String id) {
		Intent intent = new Intent(context, CallTimeOrderDetailActitivy.class);
		intent.putExtra("id", id);

		context.startActivity(intent);
	}


	//获取数据
	private void addData() {
		showDefaultProgress();

		createHttpRequest(HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, getIntent().getStringExtra("id"));
	}

	@OnClick({R.id.retryTextView, R.id.headBarRelativeLayout})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.retryTextView:
				addData();
				break;
			case R.id.headBarRelativeLayout:
				CallTimePacketDetailActivity.launch(CallTimeOrderDetailActitivy.this, bean.getPackageId());
				break;
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID) {
			dismissProgress();
			if (object.getStatus() == 1) {
				dismissProgress();
				if (!isCreateView) {
					createViews();
				}
				NoNetRelativeLayout.setVisibility(View.GONE);
				GetOrderByIdHttp http = (GetOrderByIdHttp) object;
				bean = http.getOrderEntity().getList();
				Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(packetImageView);
				packageNameTextView.setText(bean.getPackageName());
				//如果订单状态是正在使用，那么就计算时间
				String expireStr = bean.getExpireDays().replace(getResources().getString(R.string.expireday), "");
				if (bean.getOrderStatus() == 0) {
					expiryDateTextView.setText(expireStr);
					packetStatusTextView.setText("未激活");

				} else if (bean.getOrderStatus() == 2) {
					packetStatusTextView.setText("订单已过期");

				} else if (bean.getOrderStatus() == 3) {
					packetStatusTextView.setText("订单已经被取消");
				} else if (bean.getOrderStatus() == 4) {
					packetStatusTextView.setText("激活失败");
					expiryDateTextView.setText(expireStr);
				} else if (bean.getOrderStatus() == 1) {
					packetStatusTextView.setTextColor(Color.BLACK);
					packetStatusTextView.setText(getResources().getString(R.string.residue) + bean.getRemainingCallMinutes() + getResources().getString(R.string.minute));
					setResidueMinueSpan(packetStatusTextView, bean.getRemainingCallMinutes());
					expiryDateTextView.setText(expireStr);
				}
				String payWayStr = getPaymentMethod(bean.getPaymentMethod());
				if (payWayStr != null) {
					payWayTextView.setText(payWayStr);
				}
				priceTextView.setText("￥" + bean.getUnitPrice());
				setSpan(priceTextView);
				orderNumberTextView.setText(bean.getOrderNum());
				orderTimeTextView.setText(DateUtils.getDateToString(bean.getOrderDate() * 1000));
			} else {
				CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), object.getMsg());
			}
		}
	}

	private void setResidueMinueSpan(TextView stateTextView, int remainingCallMinutes) {
		Spannable WordtoSpan = new SpannableString(stateTextView.getText().toString());
		WordtoSpan.setSpan(new ForegroundColorSpan(
				ContextCompat.getColor(this, R.color.select_contacct)), 2, 2 + String.valueOf(remainingCallMinutes).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		stateTextView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	private void createViews() {
		setContentView(R.layout.activity_call_time_order_detail_actitivy);
		ButterKnife.bind(this);
		initSet();
		isCreateView = true;
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

	private void initSet() {
			hasLeftViewTitle(R.string.order_detail,0);

	}




}
