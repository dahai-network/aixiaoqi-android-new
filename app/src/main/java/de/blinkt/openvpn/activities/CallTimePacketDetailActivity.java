package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import de.blinkt.openvpn.activities.ShopModules.ui.CommitOrderActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.CallTimePDDetailFragment;
import de.blinkt.openvpn.http.AddReceiveHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.PacketDtailHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.PagerSlidingTabStripExtends;

public class CallTimePacketDetailActivity extends BaseNetActivity implements InterfaceCallback {
	public static CallTimePacketDetailActivity activity;
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
	@BindView(R.id.iv_purchase)
	ImageView iv_purchase;
	@BindView(R.id.tv_expirydate)
	TextView tv_expirydate;
	PagerSlidingTabStripExtends myTabs;
	ViewPager vpPager;
	private boolean isCreateView;
	private PacketDtailEntity.ListBean bean;
	Fragment callTimePDDetailFragment;
	DisplayMetrics dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		dm = getResources().getDisplayMetrics();
		addData();
	}

	CallTimePacketDetailAdapter callTimePacketDetailAdapter;

	private void initView() {
		vpPager = (ViewPager) findViewById(R.id.vp_pager);
		myTabs = (PagerSlidingTabStripExtends) findViewById(R.id.my_tabs);
		if (callTimePDDetailFragment == null || !callTimePDDetailFragment.isAdded()) {
			//判断是否已经被添加
			callTimePacketDetailAdapter = new CallTimePacketDetailAdapter(getSupportFragmentManager());
		}
		//创建适配器
		vpPager.setAdapter(callTimePacketDetailAdapter);
		myTabs.setViewPager(vpPager);
		setTabsValue();

	}

	public static void launch(Context context, String id) {
		Intent intent = new Intent(context, CallTimePacketDetailActivity.class);
		intent.putExtra("id", id);
		context.startActivity(intent);
	}

	public static void launch(Context context, String id, String sureText, boolean isCanClick) {
		Intent intent = new Intent(context, CallTimePacketDetailActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("sureText", sureText);
		intent.putExtra("isCanClick", isCanClick);
		context.startActivity(intent);
	}

	@OnClick({R.id.retryTextView, R.id.iv_purchase})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.retryTextView:
				addData();
				break;
			case R.id.iv_purchase:
				String sureText = getIntent().getStringExtra("sureText");
				if (sureText != null && sureText.equals(getString(R.string.receive_fw))) {
					createHttpRequest(HttpConfigUrl.COMTYPE_ADD_RECEIVE, getIntent().getStringExtra("id"));
				} else {
					CommitOrderActivity.launch(this, bean, 0);
				}
				break;
		}
	}

	//获取数据
	private void addData() {
		showDefaultProgress();
		createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_DETAIL, getIntent().getStringExtra("id"));
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
		initView();
		ButterKnife.bind(this);
		initSet();
		isCreateView = true;
	}

	private void initSet() {
		String sureText = getIntent().getStringExtra("sureText");
		if (sureText != null) {
			if (sureText.equals(getString(R.string.receive_fw))) {
				iv_purchase.setBackgroundResource(R.drawable.image_receive_selector);
				hasLeftViewTitle(R.string.receive_call, 0);
			}
		} else {
			hasLeftViewTitle(R.string.package_detail, 0);
		}

		boolean isCanClick = getIntent().getBooleanExtra("isCanClick", true);
//		if (!isCanClick) {
//			iv_purchase.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					CommonTools.showShortToast(CallTimePacketDetailActivity.this, getString(R.string.already_get));
//				}
//			});
//		}
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
			Log.d("aixiaoqi__", "rightComplete: " + bean.getFeatures());
			SharedUtils.getInstance().writeString(Constant.CALLTIME_FEATURES_SIGN, bean.getFeatures());
			tv_expirydate.setText("有效期：" + bean.getExpireDays() + "天");
		} else if (cmdType == HttpConfigUrl.COMTYPE_ADD_RECEIVE) {
			if (object.getStatus() == 1) {
				AddReceiveHttp http = (AddReceiveHttp) object;
				CommonTools.showShortToast(this, getString(R.string.receive_success));
				MyOrderDetailActivity.launch(this, http.getOrderEntity().getOrder().getOrderID());
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedUtils.getInstance().delete(Constant.CALLTIME_FEATURES_SIGN);
	}

	class CallTimePacketDetailAdapter extends FragmentPagerAdapter {

		public CallTimePacketDetailAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (callTimePDDetailFragment == null) {

				callTimePDDetailFragment = new CallTimePDDetailFragment();
			}
			return callTimePDDetailFragment;
		}

		@Override
		public int getCount() {

			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO
			String s = "套餐详情";
			return s;
		}
	}

	/**
	 * 设置该PagerSlidingTabStrip的样式
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		myTabs.setShouldExpand(false);
		// 设置Tab的分割线是透明的
		myTabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度

		// 设置Tab Indicator的高度
		myTabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));

	}
}
