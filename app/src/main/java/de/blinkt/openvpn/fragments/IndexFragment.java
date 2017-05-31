package de.blinkt.openvpn.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.HotPackageAdapter;
import cn.com.johnson.adapter.ProductsAdapter;
import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.OverseaGuideFeeActivity;
import de.blinkt.openvpn.activities.PackageMarketActivity;
import de.blinkt.openvpn.activities.WebViewActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.http.BannerHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetHotHttp;
import de.blinkt.openvpn.http.GetPakcetHttp;
import de.blinkt.openvpn.http.GetProductHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.PacketEntity;
import de.blinkt.openvpn.model.ProductEntity;
import de.blinkt.openvpn.util.GlideImageLoader;
import de.blinkt.openvpn.views.FullyRecylerView;
import de.blinkt.openvpn.views.TitleBar;
import static android.view.View.GONE;
import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_GET_PRODUCTS;
import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_PACKET_GET;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBANNER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKHOTPACKAGEMORE;

/*
*   商城
* */

public class IndexFragment extends BaseStatusFragment implements View.OnClickListener, InterfaceCallback {

	private String TAG = "IndexFragment";
	private FullyRecylerView hotPackageRecyclerView;
	private TextView hotMessageMoreTextView;
	private List<IndexBannerEntity> bannerData;
	//图片加载类
	public ScrollView indexScrollView;
	private RelativeLayout hotPacketLinearLayout;
	private RelativeLayout communicationRelativeLayout;
	private RelativeLayout leftPacketRelativeLayout;
	private RelativeLayout rightPacketRelativeLayout;
	private LinearLayout flowPackageLinearLayout;
	private RelativeLayout hardWareRelativeLayout;
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
	Banner banner;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		setLayoutId(R.layout.fragment_index);
		view = super.onCreateView(inflater, container,
				savedInstanceState);

		ButterKnife.bind(this, view);
		findById(view);
		init();
		return view;
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
		banner = (Banner) view.findViewById(R.id.banner);
		hotMessageMoreTextView = (TextView) view.findViewById(R.id.hotMessageMoreTextView);
		hardWareRecyclerView = (RecyclerView) view.findViewById(R.id.hardWareRecyclerView);
		hotPacketLinearLayout = (RelativeLayout) view.findViewById(R.id.hotPacketLinearLayout);
		guiderImageView = (ImageView) view.findViewById(R.id.guiderImageView);
		communicationRelativeLayout = (RelativeLayout) view.findViewById(R.id.communicationRelativeLayout);
		leftPacketRelativeLayout = (RelativeLayout) view.findViewById(R.id.leftPacketRelativeLayout);
		rightPacketRelativeLayout = (RelativeLayout) view.findViewById(R.id.rightPacketRelativeLayout);
		flowPackageLinearLayout = (LinearLayout) view.findViewById(R.id.flowPackageLinearLayout);
		hardWareRelativeLayout = (RelativeLayout) view.findViewById(R.id.hardWareRelativeLayout);
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
		getCallPackage();
		getHotPackage();
		getHardWare();
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
				marketIntent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE, Constant.HIDDEN);
				startActivity(marketIntent);
				break;
			case R.id.guiderImageView:
				//友盟方法统计
				Intent marketIntent1 = new Intent(getActivity(), OverseaGuideFeeActivity.class);
				startActivity(marketIntent1);
				break;
		}

	}





	private void initBanner(final List<IndexBannerEntity> infos) {
		banner.startAutoPlay();
		banner.setDelayTime(3000);
		List<String> imageList = new ArrayList<>();
		for (IndexBannerEntity indexBannerEntity : infos) {
			imageList.add(indexBannerEntity.getImage());
		}
		banner.setImages(imageList).setImageLoader(new GlideImageLoader()).start();
		banner.setOnBannerListener(new OnBannerListener() {
			@Override
			public void OnBannerClick(int position) {
				if (!TextUtils.isEmpty(infos.get(position).getUrl())) {
					//友盟方法统计
					MobclickAgent.onEvent(getActivity(), CLICKBANNER);
					WebViewActivity.launch(getActivity(), infos.get(position).getUrl(), infos.get(position).getTitle());
				}
			}
		});
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_INDEX_BANNER) {
			BannerHttp http = (BannerHttp) object;
			bannerData = http.getBannerList();
			if (bannerData != null && bannerData.size() != 0) {
				initBanner(bannerData);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_HOT) {
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

				// 进行跳转
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
			if (bean != null || bean.size() != 0) {
				Log.i(TAG, "产品信息：" + bean.toString());
				hardWareRecyclerView.setNestedScrollingEnabled(false);
				hardWareRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
				hardWareRecyclerView.setAdapter(new ProductsAdapter(bean, getActivity()));
			} else {
				hardWareRelativeLayout.setVisibility(GONE);
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
		if(banner!=null){
			if(isVisibleToUser){
				banner.start();
			}else{
				banner.stopAutoPlay();
			}
		}

	}

}