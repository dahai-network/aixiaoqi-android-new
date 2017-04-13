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

	private List<ImageView> pageViews;
	private FullyRecylerView hotPackageRecyclerView;

	private TextView hotMessageMoreTextView;

	private CycleViewPager scrollViewPagerLayout;
	private List<IndexBannerEntity> bannerData;
	//图片加载类

	public ScrollView indexScrollView;

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
	private ImageView guiderImageView;
	private View view;
	private TitleBar title;

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
		hotMessageMoreTextView.setOnClickListener(this);
		guiderImageView.setOnClickListener(this);
	}

	private void findById(View view) {
		indexScrollView = (ScrollView) view.findViewById(R.id.indexScrollView);
		title = (TitleBar) view.findViewById(R.id.title);
		title.setTextTitle(getString(R.string.shop));

		scrollViewPagerLayout = (CycleViewPager) view.findViewById(R.id.scrollViewPagerLayout);
		hotMessageMoreTextView = (TextView) view.findViewById(R.id.hotMessageMoreTextView);
		hardWareRecyclerView = (RecyclerView) view.findViewById(R.id.hardWareRecyclerView);
		hotPacketLinearLayout = (RelativeLayout) view.findViewById(R.id.hotPacketLinearLayout);
		guiderImageView = (ImageView) view.findViewById(R.id.guiderImageView);
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



	private void getCallPackage() {
		CreateHttpFactory.instanceHttp(this, COMTYPE_PACKET_GET, 1 + "", 2 + "", 1 + "");
	}

	/**
	 * 获取硬件购买信息
	 */
	private void getHardWare() {
		CreateHttpFactory.instanceHttp(this, COMTYPE_GET_PRODUCTS);
	}

	//获取banner图
	private void getIndexBanner() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_INDEX_BANNER);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hotMessageMoreTextView:
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKHOTPACKAGEMORE);
                Intent marketIntent = new Intent(getActivity(), PackageMarketActivity.class);
                marketIntent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE,Constant.HIDDEN);
                startActivity(marketIntent);
                break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getCallPackage();
		getHardWare();
		controlWheel(true);
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

	public  ImageView getImageView(Context context, String url) {
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
		}  else if (cmdType == HttpConfigUrl.COMTYPE_GET_HOT) {
			GetHotHttp http = (GetHotHttp) object;
			List<HotPackageEntity> hotList = http.getHotPackageEntityList();
			if (hotList.size() != 0) {
				hotPackageRecyclerView = new FullyRecylerView(getActivity());
				hotPackageRecyclerView.setNestedScrollingEnabled(false);
				hotPackageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
				hotPackageRecyclerView.setAdapter(new HotPackageAdapter(hotList, getActivity(), true));
				flowPackageLinearLayout.addView(hotPackageRecyclerView);
			} else {
				hotPacketLinearLayout.setVisibility(GONE);
			}
		}
		else if (cmdType == COMTYPE_PACKET_GET) {
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


}