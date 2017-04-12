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
import cn.com.johnson.adapter.ProductsAdapter;
import cn.com.johnson.model.BoughtPackageEntity;
import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.activities.CallPackageLlistActivity;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.ChoiceDeviceTypeActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.MyPackageActivity;
import de.blinkt.openvpn.activities.OrderedOutsidePurchaseActivity;
import de.blinkt.openvpn.activities.PackageMarketActivity;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.WebViewActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BannerHttp;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetHotHttp;
import de.blinkt.openvpn.http.GetPakcetHttp;
import de.blinkt.openvpn.http.GetProductHttp;
import de.blinkt.openvpn.http.GetSportTotalHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.PacketEntity;
import de.blinkt.openvpn.model.ProductEntity;
import de.blinkt.openvpn.model.SportTotalEntity;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.FullyRecylerView;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.bannerview.CycleViewPager;
import de.blinkt.openvpn.views.xrecycler.DividerItemDecoration;

import static android.view.View.GONE;
import static de.blinkt.openvpn.constant.Constant.BRACELETNAME;
import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_GET_PRODUCTS;
import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_GET_SPORT_TOTAL;
import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_PACKET_GET;
import static de.blinkt.openvpn.constant.UmengContant.CLICKABROADFEE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBANNER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKHOTPACKAGEMORE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKINLANDFEE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSPORTTOTALDATA;
import static de.blinkt.openvpn.constant.UmengContant.CLICORDERMORE;
import static de.blinkt.openvpn.fragments.SportFragment.REFRESHSTEP;

/*
*   商城
* */

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
	private RelativeLayout hotPacketLinearLayout;
	private RelativeLayout communicationRelativeLayout;
	private RelativeLayout leftPacketRelativeLayout;
	private RelativeLayout rightPacketRelativeLayout;
	private LinearLayout flowPackageLinearLayout;
	private TextView leftPriceTextView;
	private TextView leftContentTextView;
	private TextView leftExpiryDateTextView;
	private TextView rightPriceTextView;
	private TextView rightContentTextView;
	private TextView rightExpiryDateTextView;
	private RecyclerView hardWareRecyclerView;
	private OrderAdapter orderAdapter;
	private TextView totalStepTextView;
	private TextView totalKmTextView;
	private TextView totalDayTextView;
	private TextView totalKalTextView;
	private ImageView guiderImageView;
	private View view;
	private TitleBar title;

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
		guiderImageView.setOnClickListener(this);
		manager = Glide.with(ICSOpenVPNApplication.getContext());
		ICSOpenVPNApplication.getInstance().registerReceiver(realStepReceiver, getFilter());
		changeBluetoothStatus(getString(R.string.index_unconnect), R.drawable.index_unconnect);
	}

	private void findById(View view) {
		indexScrollView = (ScrollView) view.findViewById(R.id.indexScrollView);
		title = (TitleBar) view.findViewById(R.id.title);
		title.setTextTitle(getString(R.string.shop));
		foreignTextView = (TextView) view.findViewById(R.id.foreignTextView);
		callPacketTextView = (TextView) view.findViewById(R.id.callPacketTextView);

		dualSimTextView = (TextView) view.findViewById(R.id.dualSimTextView);
		boughtPacketLinearLayout = (RelativeLayout) view.findViewById(R.id.boughtPacketLinearLayout);
		scrollViewPagerLayout = (CycleViewPager) view.findViewById(R.id.scrollViewPagerLayout);
		hotMessageMoreTextView = (TextView) view.findViewById(R.id.hotMessageMoreTextView);
		boughtMessageMoreTextView = (TextView) view.findViewById(R.id.boughtMessageMoreTextView);
		sportTabLienarLayout = (LinearLayout) view.findViewById(R.id.sportTabLienarLayout);
		boughtPackgeRecyclerView = (RecyclerView) view.findViewById(R.id.boughtPackgeRecyclerView);
		hardWareRecyclerView = (RecyclerView) view.findViewById(R.id.hardWareRecyclerView);
		hotPacketLinearLayout = (RelativeLayout) view.findViewById(R.id.hotPacketLinearLayout);
		totalStepTextView = (TextView) view.findViewById(R.id.totalStepTextView);
		totalKmTextView = (TextView) view.findViewById(R.id.totalKmTextView);
		totalDayTextView = (TextView) view.findViewById(R.id.totalDayTextView);
		totalKalTextView = (TextView) view.findViewById(R.id.totalKalTextView);
		guiderImageView = (ImageView) view.findViewById(R.id.guiderImageView);
		orderAdapter = new OrderAdapter(getActivity(), null, R.layout.item_order_index);
		boughtPackgeRecyclerView.setNestedScrollingEnabled(false);
//		FullyLinearLayoutManager linearManager = new FullyLinearLayoutManager(getActivity());
		boughtPackgeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		boughtPackgeRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
		boughtPackgeRecyclerView.setAdapter(orderAdapter);
		communicationRelativeLayout = (RelativeLayout) view.findViewById(R.id.communicationRelativeLayout);
		leftPacketRelativeLayout = (RelativeLayout) view.findViewById(R.id.leftPacketRelativeLayout);
		rightPacketRelativeLayout = (RelativeLayout) view.findViewById(R.id.rightPacketRelativeLayout);
		flowPackageLinearLayout = (LinearLayout) view.findViewById(R.id.flowPackageLinearLayout);
		leftPriceTextView = (TextView) view.findViewById(R.id.leftPriceTextView);
		leftContentTextView = (TextView) view.findViewById(R.id.leftContentTextView);
		leftExpiryDateTextView = (TextView) view.findViewById(R.id.leftExpiryDateTextView);
		rightPriceTextView = (TextView) view.findViewById(R.id.rightPriceTextView);
		rightContentTextView = (TextView) view.findViewById(R.id.rightContentTextView);
		rightExpiryDateTextView = (TextView) view.findViewById(R.id.rightExpiryDateTextView);
		title.getLeftText().setVisibility(View.INVISIBLE);
		title.getLeftText().setOnClickListener(new View.OnClickListener() {
			public Intent intent;

			@Override
			public void onClick(View v) {
				//判断是否绑定过设备
				if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {

					intent = new Intent(getActivity(), ChoiceDeviceTypeActivity.class);// 选择设备界面

				} else {

					intent = new Intent(getActivity(), MyDeviceActivity.class);
					//手环的名字
					String braceleName = SharedUtils.getInstance().readString(BRACELETNAME, "");

					intent.putExtra(MyDeviceActivity.BRACELETTYPE, braceleName);

					SharedUtils.getInstance().writeString(MyDeviceActivity.BRACELETTYPE, braceleName);
				}
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
				} else if (getString(R.string.index_regist_fail).equals(getBlutoothStatus())) {
					status = R.string.index_regist_fail;
				}
				intent.putExtra(MyDeviceActivity.BLUESTATUSFROMPROMAIN, getString(status));
				startActivity(intent);
			}
		});
	}


	/**
	 * 增加轮播图
	 */
	private void addData() {
		getIndexBanner();
		pageViews = new ArrayList<>();
		getHotPackage();
	}

	/**
	 * 获取热门套餐
	 */
	private void getHotPackage() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_HOT, 3 + "");
	}

	private void getBoughtPackage() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_ORDER, "1", "3", "-1");
	}

	private void getCallPackage() {
		CreateHttpFactory.instanceHttp(this, COMTYPE_PACKET_GET, 1 + "", 2 + "", 1 + "");
	}

	/**
	 * 获取硬件购买信息
	 */
	private void getHardWare() {
		CreateHttpFactory.instanceHttp(this, COMTYPE_GET_PRODUCTS);
	}

	private void getSportTotal() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_SPORT_TOTAL);
	}

	//获取banner图
	private void getIndexBanner() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_INDEX_BANNER);
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
                marketIntent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE,Constant.HIDDEN);
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
            case R.id.guiderImageView:

				break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getBoughtPackage();
		getCallPackage();
		getHardWare();
		controlWheel(true);
//		getSportTotal();
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
		ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
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
					WebViewActivity.launch(getActivity(), info.getUrl(), info.getTitle());
				}
			}

		}

	};

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_INDEX_BANNER) {
			BannerHttp http = (BannerHttp) object;
			bannerData = http.getBannerList();
			if (bannerData != null && bannerData.size() != 0) {
				initialize(bannerData);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_ORDER) {
			BoughtPacketHttp http = (BoughtPacketHttp) object;
			BoughtPackageEntity boughtPackageEntity = http.getBoughtPackageEntity();
			if (boughtPackageEntity != null) {
				if (boughtPackageEntity.getList().size() == 0) {
					boughtPacketLinearLayout.setVisibility(GONE);
					orderAdapter.clear();
					return;
				} else {
//					boughtPacketLinearLayout.setVisibility(View.VISIBLE);
					List<BoughtPackageEntity.ListBean> getDataList = http.getBoughtPackageEntity().getList();
					orderAdapter.addAll(getDataList);
				}
			} else {
				boughtPacketLinearLayout.setVisibility(GONE);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_HOT) {
			GetHotHttp http = (GetHotHttp) object;
			List<HotPackageEntity> hotList = http.getHotPackageEntityList();
			if (hotList.size() != 0) {
				hotPackageRecyclerView = new FullyRecylerView(getActivity());
//				hotPackageRecyclerView.setBackgroundResource(R.color.white);
				hotPackageRecyclerView.setNestedScrollingEnabled(false);
//				FullyGridLayoutManager GridManager = new FullyGridLayoutManager(getActivity(), 4);
				hotPackageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//				hotPackageRecyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
				hotPackageRecyclerView.setAdapter(new HotPackageAdapter(hotList, getActivity(), true));
				flowPackageLinearLayout.addView(hotPackageRecyclerView);
			} else {
				hotPacketLinearLayout.setVisibility(GONE);
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
		} else if (cmdType == COMTYPE_PACKET_GET) {
			GetPakcetHttp http = (GetPakcetHttp) object;
			PacketEntity bean = http.getPacketEntity();
			if (bean != null) {
				List<PacketEntity.ListBean> list = bean.getList();
				if (list.size() == 0) return;

				final PacketEntity.ListBean listBean = list.get(0);
				leftPriceTextView.setText(listBean.getPrice() + getString(R.string.yuan));
				leftContentTextView.setText(listBean.getPackageName());
				leftExpiryDateTextView.setText(getString(R.string.expiry_date) + listBean.getExpireDays() + getString(R.string.day));
				leftPacketRelativeLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CallTimePacketDetailActivity.launch(getActivity(), listBean.getPackageId());
					}
				});
				if (list.size() == 1) return;
				final PacketEntity.ListBean list2Bean = list.get(1);
				rightPriceTextView.setText(list2Bean.getPrice() + getString(R.string.yuan));
				rightContentTextView.setText(list2Bean.getPackageName());
				rightExpiryDateTextView.setText(getString(R.string.expiry_date) + list2Bean.getExpireDays() + getString(R.string.day));
				rightPacketRelativeLayout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CallTimePacketDetailActivity.launch(getActivity(), list2Bean.getPackageId());
					}
				});
			} else {
				communicationRelativeLayout.setVisibility(GONE);
			}
		} else if (cmdType == COMTYPE_GET_PRODUCTS) {
			GetProductHttp http = (GetProductHttp) object;
			List<ProductEntity> bean = http.getProductEntity();
			if (bean != null) {
				Log.i(TAG, "产品信息：" + bean.toString());
				hardWareRecyclerView.setNestedScrollingEnabled(false);
				hardWareRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
				hardWareRecyclerView.setAdapter(new ProductsAdapter(bean, getActivity()));
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
//				getSportTotal();
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
				if (pageViews.size() >= 1) {
					controlWheel(true);
				}
			} else {
				controlWheel(false);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		controlWheel(false);
	}

	private void controlWheel(boolean isWheel) {
		Log.e("controlWheel", "isWheel:" + isWheel);
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