package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.ActivateKingCardHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetOrderByIdHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.OrderEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogKingCard;


public class KingCardDetailActivity extends BaseActivity implements InterfaceCallback, DialogInterfaceTypeBase {

	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.countryImageView)
	ImageView countryImageView;
	@BindView(R.id.packageNameTextView)
	TextView packageNameTextView;
	@BindView(R.id.priceTextView)
	TextView priceTextView;
	@BindView(R.id.lastDateDetailTextView)
	TextView lastDateDetailTextView;
	@BindView(R.id.packetStatusDetailTextView)
	TextView packetStatusDetailTextView;
	@BindView(R.id.introduceTextView)
	TextView introduceTextView;
	@BindView(R.id.noticeTextView)
	TextView noticeTextView;
	@BindView(R.id.activateButton)
	Button activateButton;
	private boolean isCreateView;
	private OrderEntity.ListBean bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addData();
	}

	public static void launch(Context context, String id, int orderStatus) {
		Intent intent = new Intent(context, KingCardDetailActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("orderStatus", orderStatus);
		context.startActivity(intent);
	}

	private void createViews() {
		setContentView(R.layout.activity_king_card_detail);
		ButterKnife.bind(this);
		initSet();
		isCreateView = true;
	}

	//获取数据
	private void addData() {
		showDefaultProgress();
		GetOrderByIdHttp http = new GetOrderByIdHttp(this, HttpConfigUrl.COMTYPE_PACKET_DETAIL, getIntent().getStringExtra("id"));
		new Thread(http).start();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.order_detail, 0);
	}

	@OnClick({R.id.packetDetailRelativeLayout, R.id.activateButton})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.packetDetailRelativeLayout:
				break;
			case R.id.activateButton:
				showDialog();
				break;
		}
	}

	private void showDialog() {
		DialogKingCard dialog = new DialogKingCard(this, this, bean.getTotalPrice());
		dialog.changeText("" + bean.getTotalPrice(), getResources().getString(R.string.sure));
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_PACKET_DETAIL) {
			dismissProgress();
			if (!isCreateView) {
				createViews();
			}
			NoNetRelativeLayout.setVisibility(View.GONE);
			GetOrderByIdHttp http = (GetOrderByIdHttp) object;
			bean = http.getOrderEntity().getList();
			Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(countryImageView);
			packageNameTextView.setText(bean.getPackageName());
			//如果订单状态是正在使用，那么就计算时间
			String lastDateStr = DateUtils.getDateToString(bean.getLastCanActivationDate() * 1000);
			int orderStatus = bean.getOrderStatus();
			if (orderStatus == 0) {
				lastDateDetailTextView.setText(lastDateStr);
				packetStatusDetailTextView.setText("未激活");

			} else if (orderStatus == 2) {
				lastDateDetailTextView.setText(lastDateStr);
				packetStatusDetailTextView.setText("订单已过期");
				activateButton.setVisibility(View.GONE);
			} else if (orderStatus == 4) {
				lastDateDetailTextView.setText(lastDateStr);
				packetStatusDetailTextView.setText("激活失败");
				activateButton.setText("重新激活");
			} else if (orderStatus == 1) {
				lastDateDetailTextView.setText(lastDateStr);
				packetStatusDetailTextView.setText("已激活");
				packetStatusDetailTextView.setTextColor(ContextCompat.getColor(this, R.color.select_contacct));
				activateButton.setVisibility(View.GONE);
			}
			priceTextView.setText("￥" + bean.getTotalPrice());
			setSpan(priceTextView);
			introduceTextView.setText(bean.getPackageFeatures());
			noticeTextView.setText(bean.getPackageDetails());
		} else if (cmdType == HttpConfigUrl.COMTYPE_ACTIVATE_KINGCARD) {
			if (object.getStatus() == 1) {
				packetStatusDetailTextView.setText("已激活");
				packetStatusDetailTextView.setTextColor(ContextCompat.getColor(this, R.color.select_contacct));
				activateButton.setVisibility(View.GONE);
			}
			CommonTools.showShortToast(KingCardDetailActivity.this, object.getMsg());
		}
	}

	//设置大小字体
	public void setSpan(TextView textview) {
		Spannable WordtoSpan = new SpannableString(textview.getText().toString());
		int intLength = String.valueOf((int) (bean.getTotalPrice())).length();
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

	@Override
	public void dialogText(int type, String text) {
		if (type != 1) {
			CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_ACTIVATE_KINGCARD, bean.getOrderID(), text);
		}
	}
}
