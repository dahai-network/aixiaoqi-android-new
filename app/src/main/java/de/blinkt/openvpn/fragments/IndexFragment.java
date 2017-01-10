package de.blinkt.openvpn.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.HotPackageAdapter;
import cn.com.johnson.adapter.OrderAdapter;
import cn.com.johnson.adapter.PictureAdapter;
import cn.com.johnson.model.BoughtPackageEntity;
import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.activities.CallPackageLlistActivity;
import de.blinkt.openvpn.activities.FastSetActivity;
import de.blinkt.openvpn.activities.InlandSaveActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.MyPackageActivity;
import de.blinkt.openvpn.activities.PackageMarketActivity;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.WebViewActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BannerHttp;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetHotHttp;
import de.blinkt.openvpn.http.GetSportTotalHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.SportTotalEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.DividerGridItemDecoration;
import de.blinkt.openvpn.views.FullyRecylerView;
import de.blinkt.openvpn.views.ScrollViewPager;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.xrecycler.DividerItemDecoration;

import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_GET_SPORT_TOTAL;
import static de.blinkt.openvpn.constant.UmengContant.CLICKABROADFEE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBANNER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKHOTPACKAGEMORE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKINLANDFEE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKNOTES;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSPORTTOTALDATA;
import static de.blinkt.openvpn.constant.UmengContant.CLICORDERMORE;
import static de.blinkt.openvpn.fragments.SportFragment.REFRESHSTEP;


public class IndexFragment extends Fragment implements View.OnClickListener, ScrollViewPager.OnImageItemClickListener, InterfaceCallback {

	private TextView inlandTextView;

	private TextView foreignTextView;
	private ScrollViewPager viewPager;

	private List<ImageView> pageViews;

	private TextView callPacketTextView;
	private TextView DSDSTextView;
	private FullyRecylerView hotPackageRecyclerView;
	private RecyclerView boughtPackgeRecyclerView;
	private TextView hotMessageMoreTextView;
	private TextView boughtMessageMoreTextView;
	private RelativeLayout scrollViewPagerLayout;
	private List<IndexBannerEntity> bannerData;
	//图片加载类
	private RequestManager manager;
	public ScrollView indexScrollView;
	private RelativeLayout boughtPacketLinearLayout;
	private LinearLayout sportTabLienarLayout;
	private LinearLayout scrollLinearlayout;
	private RelativeLayout hotPacketLinearLayout;
	private LinearLayout dots_layout;
	private OrderAdapter orderAdapter;
	private TextView totalStepTextView;
	private TextView totalKmTextView;
	private TextView totalDayTextView;
	private TextView totalKalTextView;
	private TitleBar title;


	private void addHeader() {
		View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.viewpager_round_layout, null);
		LinearLayout images_layout = (LinearLayout) headerView.findViewById(R.id.carousel_image_layout);
		dots_layout = (LinearLayout) headerView.findViewById(R.id.image_round_layout);
		int width = CommonTools.getScreenWidth(getActivity());
		ViewGroup.LayoutParams params = images_layout.getLayoutParams();
		params.width = width;
		params.height = scrollViewPagerLayout.getHeight();
		images_layout.setLayoutParams(params);
		viewPager = new ScrollViewPager(getActivity());
		initImageRounds();
		viewPager.setImages(pageViews);
		viewPager.setAdapter(new PictureAdapter(pageViews));
		viewPager.setCurrentItem(Integer.MAX_VALUE / 2);
		images_layout.addView(viewPager);
		viewPager.setOnImageItemClickListener(this);
		scrollViewPagerLayout.addView(headerView);
	}

	public OrderAdapter getOrderAdapter() {
		return orderAdapter;
	}

	private void initImageRounds() {
		List<ImageView> dots = new ArrayList<>();
		dots_layout.removeAllViews();

		/**
		 *当轮播图大于1张时小圆点显示
		 * */
		if (pageViews.size() > 1) {
			dots_layout.setVisibility(View.VISIBLE);
		} else {
			dots_layout.setVisibility(View.INVISIBLE);
		}
		int size = pageViews.size();
		for (int i = 0; i < size; i++) {
			ImageView round = new ImageView(getActivity());
			/**
			 * 默认让第一张图片显示深颜色的圆点
			 * */
			if (i == 0) {
				round.setImageResource(R.drawable.face_float_icon);
			} else {
				round.setImageResource(R.drawable.face_float_icon_on);
			}
			dots.add(round);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, -2);
			params.leftMargin = 20;
			params.width = CommonTools.dip2px(getActivity(), 5);
			params.height = CommonTools.dip2px(getActivity(), 5);
			dots_layout.addView(round, params);
		}
		viewPager.setDots(dots);
	}

	public IndexFragment() {
		// Required empty public constructor
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_index, container, false);
		ButterKnife.bind(this, view);
		findById(view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		addData();
		callPacketTextView.setOnClickListener(this);
		foreignTextView.setOnClickListener(this);
		hotMessageMoreTextView.setOnClickListener(this);
		boughtMessageMoreTextView.setOnClickListener(this);
		sportTabLienarLayout.setOnClickListener(this);
		inlandTextView.setOnClickListener(this);
		DSDSTextView.setOnClickListener(this);
		manager = Glide.with(ICSOpenVPNApplication.getContext());
		ICSOpenVPNApplication.getInstance().registerReceiver(realStepReceiver, getFilter());
	}

	private void findById(View view) {
		indexScrollView = (ScrollView) view.findViewById(R.id.indexScrollView);
		scrollLinearlayout = (LinearLayout) view.findViewById(R.id.scrollLinearlayout);
		title = (TitleBar) view.findViewById(R.id.title);
		title.setTextTitle(getString(R.string.index));
		/*title.setRightBtnText(R.string.videotutorial);
		title.getRightText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKVOIDECOURSE);
				Intent marketIntent = new Intent(getActivity(), PackageMarketActivity.class);
				startActivity(marketIntent);
			}
		});*/
		foreignTextView = (TextView) view.findViewById(R.id.foreignTextView);
		callPacketTextView = (TextView) view.findViewById(R.id.callPacketTextView);
		DSDSTextView = (TextView) view.findViewById(R.id.DSDSTextView);
		inlandTextView = (TextView) view.findViewById(R.id.inlandTextView);
		boughtPacketLinearLayout = (RelativeLayout) view.findViewById(R.id.boughtPacketLinearLayout);
		scrollViewPagerLayout = (RelativeLayout) view.findViewById(R.id.scrollViewPagerLayout);
		hotMessageMoreTextView = (TextView) view.findViewById(R.id.hotMessageMoreTextView);
		boughtMessageMoreTextView = (TextView) view.findViewById(R.id.boughtMessageMoreTextView);
		sportTabLienarLayout = (LinearLayout) view.findViewById(R.id.sportTabLienarLayout);
		boughtPackgeRecyclerView = (RecyclerView) view.findViewById(R.id.boughtPackgeRecyclerView);
		hotPacketLinearLayout = (RelativeLayout) view.findViewById(R.id.hotPacketLinearLayout);
		totalStepTextView = (TextView) view.findViewById(R.id.totalStepTextView);
		totalKmTextView = (TextView) view.findViewById(R.id.totalKmTextView);
		totalDayTextView = (TextView) view.findViewById(R.id.totalDayTextView);
		totalKalTextView = (TextView) view.findViewById(R.id.totalKalTextView);
		orderAdapter = new OrderAdapter(getActivity(), null, false, R.layout.item_order_index);
		boughtPackgeRecyclerView.setNestedScrollingEnabled(false);
//		FullyLinearLayoutManager linearManager = new FullyLinearLayoutManager(getActivity());
		boughtPackgeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		boughtPackgeRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
		boughtPackgeRecyclerView.setAdapter(orderAdapter);
		changeBluetoothStatus(getString(R.string.index_blue_un_opne), R.drawable.index_blue_unpen);
		title.getLeftText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyDeviceActivity.class);
				int status = 0;
				if (getActivity().getResources().getString(R.string.index_no_signal).equals(getBlutoothStatus())) {
					status = R.string.index_no_signal;
				} else if (getActivity().getResources().getString(R.string.index_connecting).equals(getBlutoothStatus())) {
					status = R.string.index_connecting;
				} else if (getActivity().getResources().getString(R.string.index_high_signal).equals(getBlutoothStatus())) {
					status = R.string.index_high_signal;
				} else if (getActivity().getResources().getString(R.string.index_no_packet).equals(getBlutoothStatus())) {
					status = R.string.index_no_packet;
				} else if (getString(R.string.index_un_insert_card).equals(getBlutoothStatus())) {
					status = R.string.index_no_signal;
				} else if (getString(R.string.index_high_signal).equals(getBlutoothStatus())) {
					status = R.string.index_high_signal;
				}
				intent.putExtra(MyDeviceActivity.BLUESTATUSFROMPROMAIN, status);
				startActivity(intent);
			}
		});
	}


	private void addData() {
		getIndexBanner();
		pageViews = new ArrayList<>();
		getHotPackage();
	}

	private void getHotPackage() {
		GetHotHttp http = new GetHotHttp(this, HttpConfigUrl.COMTYPE_GET_HOT, 12);
		new Thread(http).start();
	}

	private void getBoughtPackage() {
		BoughtPacketHttp http = new BoughtPacketHttp(this, HttpConfigUrl.COMTYPE_GET_ORDER, 1, 3);
		new Thread(http).start();
	}

	private void getSportTotal() {
		GetSportTotalHttp http = new GetSportTotalHttp(this, COMTYPE_GET_SPORT_TOTAL);
		new Thread(http).start();
	}

	//获取banner图
	private void getIndexBanner() {
		BannerHttp http = new BannerHttp(this, HttpConfigUrl.COMTYPE_INDEX_BANNER);
		new Thread(http).start();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.foreignTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKABROADFEE);
				Intent fastSetIntent = new Intent(getActivity(), FastSetActivity.class);
				startActivity(fastSetIntent);
				break;
			case R.id.callPacketTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKDEVICE);
				Intent callPacketIntent = new Intent(getActivity(), CallPackageLlistActivity.class);
				startActivity(callPacketIntent);
				break;
			case R.id.hotMessageMoreTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKHOTPACKAGEMORE);
				Intent marketIntent = new Intent(getActivity(), PackageMarketActivity.class);
				startActivity(marketIntent);
				break;
			case R.id.boughtMessageMoreTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICORDERMORE);
				Intent boughtIntent = new Intent(getActivity(), MyPackageActivity.class);
				startActivity(boughtIntent);
				break;
			case R.id.sportTabLienarLayout:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKSPORTTOTALDATA);
				((ProMainActivity) getActivity()).getLlArrayToSport().performClick();
				break;
			case R.id.inlandTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKINLANDFEE);
				Intent inlandIntent = new Intent(getActivity(), InlandSaveActivity.class);
				startActivity(inlandIntent);
				break;
			case R.id.DSDSTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKNOTES);
				break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getBoughtPackage();
		getSportTotal();
	}

	@Override
	public void onItemClick(int itemPosition) {
		String urlStr = bannerData.get(itemPosition).getUrl();
		if (!TextUtils.isEmpty(urlStr)) {
			//友盟方法统计
			MobclickAgent.onEvent(getActivity(), CLICKBANNER);
			WebViewActivity.launch(getActivity(), urlStr, bannerData.get(itemPosition).getTitle());
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_INDEX_BANNER) {
			BannerHttp http = (BannerHttp) object;
			bannerData = http.getBannerList();
			int size = bannerData.size();
			for (int i = 0; i < size; i++) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				manager.load(bannerData.get(i).getImage()).into(imageView);
				pageViews.add(imageView);
			}
			addHeader();
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_ORDER) {
			BoughtPacketHttp http = (BoughtPacketHttp) object;
			BoughtPackageEntity boughtPackageEntity = http.getBoughtPackageEntity();
			if (boughtPackageEntity != null) {
				if (boughtPackageEntity.getList().size() == 0) {
					boughtPacketLinearLayout.setVisibility(View.GONE);
					orderAdapter.clear();
					if (ICSOpenVPNApplication.uartService.mConnectionState == UartService.STATE_CONNECTED) {
						changeBluetoothStatus(getString(R.string.index_no_packet), R.drawable.index_no_packet);
					}
					return;
				} else {
					boughtPacketLinearLayout.setVisibility(View.VISIBLE);
					List<BoughtPackageEntity.ListBean> getDataList = http.getBoughtPackageEntity().getList();
					orderAdapter.addAll(getDataList);
				}
			} else {
				boughtPacketLinearLayout.setVisibility(View.GONE);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_HOT) {
			GetHotHttp http = (GetHotHttp) object;
			List<HotPackageEntity> hotList = http.getHotPackageEntityList();
			if (hotList.size() != 0) {
				hotPackageRecyclerView = new FullyRecylerView(getActivity());
//				hotPackageRecyclerView.setBackgroundResource(R.color.white);
				hotPackageRecyclerView.setNestedScrollingEnabled(false);
//				FullyGridLayoutManager GridManager = new FullyGridLayoutManager(getActivity(), 4);
				hotPackageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
				hotPackageRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
				hotPackageRecyclerView.setAdapter(new HotPackageAdapter(hotList, getActivity()));
				scrollLinearlayout.addView(hotPackageRecyclerView);
			} else {
				hotPacketLinearLayout.setVisibility(View.GONE);
			}
		} else if (cmdType == COMTYPE_GET_SPORT_TOTAL) {
			GetSportTotalHttp http = (GetSportTotalHttp) object;
			SportTotalEntity sportTotalEntity = http.getSportTotalEntity();
			//如果获取总步数为空，那么其他数据也是空。
			if (!TextUtils.isEmpty(sportTotalEntity.getStepNum())) {
				totalStepTextView.setText(sportTotalEntity.getStepNum());
				totalKmTextView.setText(sportTotalEntity.getKM());
				totalDayTextView.setText(sportTotalEntity.getDate());
				totalKalTextView.setText(sportTotalEntity.getKcal());
			} else {
				totalStepTextView.setText("0");
				totalKmTextView.setText("0");
				totalDayTextView.setText("0");
				totalKalTextView.setText("--");
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {

	}

	BroadcastReceiver realStepReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (REFRESHSTEP.equals(intent.getAction())) {
				getSportTotal();
			}
		}
	};

	public IntentFilter getFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(REFRESHSTEP);
		return filter;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ICSOpenVPNApplication.getInstance().unregisterReceiver(realStepReceiver);
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		//当页面跳出的时候停止轮播
		if (viewPager != null) {
			if (isVisibleToUser) {
				viewPager.openLoop();
			} else {
				viewPager.stopLoop();
			}
		}
	}

	//修改蓝牙状态
	public void changeBluetoothStatus(String leftText, int leftIconId) {
		title.setLeftIvIconAndText(leftIconId, leftText);
	}

	//获取蓝牙状态
	public String getBlutoothStatus() {
		return title.getLeftText().getText().toString();
	}
}