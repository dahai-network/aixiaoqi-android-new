package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.PacketDtailHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.util.CommonTools;

import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.orderStatus;

public class CallTimePacketDetailActivity extends BaseActivity implements InterfaceCallback {

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
	@BindView(R.id.introduceTextView)
	TextView introduceTextView;
	@BindView(R.id.noticeTextView)
	TextView noticeTextView;
	@BindView(R.id.buyButton)
	Button buyButton;
	private boolean isCreateView;
	private PacketDtailEntity.ListBean bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addData();
	}

	public static void launch(Context context, String id) {
		Intent intent = new Intent(context, CallTimePacketDetailActivity.class);
		intent.putExtra("id", id);
		context.startActivity(intent);
	}

	@OnClick({R.id.retryTextView, R.id.buyButton})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.retryTextView:
				addData();
				break;
			case R.id.buyButton:
				CommitCallTimeOrderActivity.launch(this, bean);
				break;
		}
	}


	//获取数据
	private void addData() {
		showDefaultProgress();
		PacketDtailHttp http = new PacketDtailHttp(this, HttpConfigUrl.COMTYPE_PACKET_DETAIL, getIntent().getStringExtra("id"));
		new Thread(http).start();
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

	private void createViews() {
		setContentView(R.layout.activity_call_time_packet_detail);
		ButterKnife.bind(this);
		initSet();
		isCreateView = true;
	}

	private void initSet() {
		hasLeftViewTitle(R.string.package_detail, 0);
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_PACKET_DETAIL) {
			dismissProgress();
			if (!isCreateView) {
				createViews();
			}
			NoNetRelativeLayout.setVisibility(View.GONE);
			PacketDtailHttp http = (PacketDtailHttp) object;
			bean = http.getPacketDtailEntity().getList();
			Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(countryImageView);
			packageNameTextView.setText(bean.getPackageName());

			priceTextView.setText("￥" + bean.getPrice());
			setSpan(priceTextView);
			introduceTextView.setText(bean.getFeatures());
			noticeTextView.setText(bean.getDetails());
		} else if (cmdType == HttpConfigUrl.COMTYPE_ACTIVATE_KINGCARD) {
			if (object.getStatus() == 1) {
				orderStatus = 1;
				refresh();
			}
			CommonTools.showShortToast(this, object.getMsg());
		}
	}

	private void refresh() {
		addData();
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
}
