package de.blinkt.openvpn.fragments;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.HotPackageAdapter;
import cn.com.johnson.adapter.OrderAdapter;
import cn.com.johnson.model.BoughtPackageEntity;
import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.activities.CallPackageLlistActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.MyPackageActivity;
import de.blinkt.openvpn.activities.OrderedOutsidePurchaseActivity;
import de.blinkt.openvpn.activities.PackageMarketActivity;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.WebViewActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BannerHttp;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetHotHttp;
import de.blinkt.openvpn.http.GetSportTotalHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.SportTotalEntity;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.DividerGridItemDecoration;
import de.blinkt.openvpn.views.FullyRecylerView;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.bannerview.CycleViewPager;
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


public class IndexFragment extends Fragment implements View.OnClickListener, InterfaceCallback {

	private String TAG = "IndexFragment";
	private TextView dualSimTextView;

	private TextView foreignTextView;

	private List<ImageView> pageViews;

	private TextView callPacketTextView;

	private FullyRecylerView hotPackageRecyclerView;
	private RecyclerView boughtPackgeRecyclerView;
	private TextView hotMessageMoreTextView;
	private TextView boughtMessageMoreTextView;
	private CycleViewPager scrollViewPagerLayout;
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
	private View view;
	private TitleBar title;



	public OrderAdapter getOrderAdapter() {
		return orderAdapter;
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
		view = inflater.inflate(R.layout.fragment_index, container, false);
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
		dualSimTextView.setOnClickListener(this);
		manager = Glide.with(ICSOpenVPNApplication.getContext());
		ICSOpenVPNApplication.getInstance().registerReceiver(realStepReceiver, getFilter());
		changeBluetoothStatus(getString(R.string.index_blue_un_opne), R.drawable.index_blue_unpen);
	}

	private void findById(View view) {
		indexScrollView = (ScrollView) view.findViewById(R.id.indexScrollView);
		scrollLinearlayout = (LinearLayout) view.findViewById(R.id.scrollLinearlayout);
		title = (TitleBar) view.findViewById(R.id.title);
		title.setTextTitle(getString(R.string.index));
		foreignTextView = (TextView) view.findViewById(R.id.foreignTextView);
		callPacketTextView = (TextView) view.findViewById(R.id.callPacketTextView);

		dualSimTextView = (TextView) view.findViewById(R.id.dualSimTextView);
		boughtPacketLinearLayout = (RelativeLayout) view.findViewById(R.id.boughtPacketLinearLayout);
		scrollViewPagerLayout = (CycleViewPager) view.findViewById(R.id.scrollViewPagerLayout);
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
		title.getLeftText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MyDeviceActivity.class);
				int status = R.string.index_connecting;
				if (getActivity().getResources().getString(R.string.index_no_signal).equals(getBlutoothStatus())) {
					status = R.string.index_no_signal;
				} else if (getActivity().getResources().getString(R.string.index_connecting).equals(getBlutoothStatus())) {
					status = R.string.index_connecting;
				} else if (getActivity().getResources().getString(R.string.index_high_signal).equals(getBlutoothStatus())) {
					status = R.string.index_high_signal;
				} else if (getActivity().getResources().getString(R.string.index_no_packet).equals(getBlutoothStatus())) {
					status = R.string.index_no_packet;
				} else if (getString(R.string.index_un_insert_card).equals(getBlutoothStatus())) {
					status = R.string.index_un_insert_card;
				} else if (getString(R.string.index_high_signal).equals(getBlutoothStatus())) {
					status = R.string.index_high_signal;
				} else if (getString(R.string.index_registing).equals(getBlutoothStatus())) {
					status = R.string.index_registing;
				} else if (getString(R.string.index_aixiaoqicard).equals(getBlutoothStatus())) {
					status = R.string.index_aixiaoqicard;
				}
				intent.putExtra(MyDeviceActivity.BLUESTATUSFROMPROMAIN, getString(status));
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
		CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_GET_HOT);
	}

	private void getBoughtPackage() {
		CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_GET_ORDER);
	}

	private void getSportTotal() {
		CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_GET_SPORT_TOTAL);
	}

	//获取banner图
	private void getIndexBanner() {
		CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_INDEX_BANNER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.foreignTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKABROADFEE);
				Intent fastSetIntent = new Intent(getActivity(), OrderedOutsidePurchaseActivity.class);
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
			case R.id.dualSimTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKINLANDFEE);
				WebViewActivity.launch(getActivity(), SharedUtils.getInstance().readString(IntentPutKeyConstant.DUALSIM_STANDBYTUTORIAL_URL), getString(R.string.dual_sim_standby_tutorial));
				break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getBoughtPackage();
		getSportTotal();
	}



	@SuppressLint("NewApi")
	private void initialize(List<IndexBannerEntity> infos) {
		// 将最后一个ImageView添加进来
		pageViews.add(getImageView(getActivity(), infos.get(infos.size() - 1).getImage()));
		for (int i = 0; i < infos.size(); i++) {
			pageViews.add(getImageView(getActivity(), infos.get(i).getImage()));
		}
		// 将第一个ImageView添加进来
		pageViews.add(getImageView(getActivity(), infos.get(0).getImage()));

		// 设置循环，在调用setData方法前调用
		scrollViewPagerLayout.setCycle(true);

		// 在加载数据前设置是否循环
		scrollViewPagerLayout.setData(pageViews, infos, mAdCycleViewListener);
		//设置轮播
		scrollViewPagerLayout.setWheel(true);
		//设置圆点指示图标组居中显示，默认靠右
		scrollViewPagerLayout.setIndicatorCenter();
	}

	public static ImageView getImageView(Context context, String url) {
		ImageView imageView = (ImageView)LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
		Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
		return imageView;
	}
	private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {

		@Override
		public void onImageClick(IndexBannerEntity info, int position, View imageView) {
			if (scrollViewPagerLayout.isCycle()) {
				if (!TextUtils.isEmpty(info.getUrl())) {
					//友盟方法统计
					MobclickAgent.onEvent(getActivity(), CLICKBANNER);
					WebViewActivity.launch(getActivity(), info.getUrl(),info.getTitle());
				}
			}

		}

	};
	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_INDEX_BANNER) {
			BannerHttp http = (BannerHttp) object;
			bannerData = http.getBannerList();
			if(bannerData!=null&&bannerData.size()!=0){
				initialize(bannerData);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_ORDER) {
			BoughtPacketHttp http = (BoughtPacketHttp) object;
			BoughtPackageEntity boughtPackageEntity = http.getBoughtPackageEntity();
			if (boughtPackageEntity != null) {
				if (boughtPackageEntity.getList().size() == 0) {
					boughtPacketLinearLayout.setVisibility(View.GONE);
					orderAdapter.clear();
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
			if (sportTotalEntity != null && !TextUtils.isEmpty(sportTotalEntity.getStepNum())) {
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
		if (scrollViewPagerLayout != null) {
			if (isVisibleToUser) {
				if (pageViews.size() >=1)
				controlWheel(true);
			} else {
				controlWheel(false);
			}
		}
	}
private void controlWheel(boolean isWheel){
	scrollViewPagerLayout.setCycle(isWheel);
	scrollViewPagerLayout.setWheel(isWheel);
}

	//修改蓝牙状态
	public void changeBluetoothStatus(String leftText, int leftIconId) {
		if (title == null)
			title = (TitleBar) view.findViewById(R.id.title);
		if (leftText != null && leftIconId != 0 && title != null) {
			Log.i("changeBluetoothStatus", "title=" + (title == null) + "\nleftText=" + leftText + "\nleftIconId=" + leftIconId);
			title.setLeftIvIconAndText(leftIconId, leftText);
		}
	}

	//获取蓝牙状态
	public String getBlutoothStatus() {
		return title != null ? title.getLeftText().getText().toString() : "";
	}
}