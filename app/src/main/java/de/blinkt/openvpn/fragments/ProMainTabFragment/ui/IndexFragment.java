package de.blinkt.openvpn.fragments.ProMainTabFragment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.HotPackageAdapter;
import cn.com.johnson.adapter.ProductsAdapter;
import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.activities.MyModules.ui.PackageMarketActivity;
import de.blinkt.openvpn.activities.SetFlowCard.OverseaGuideFeeActivity;
import de.blinkt.openvpn.activities.WebViewActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl.IndexPresenterImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.IndexView;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.model.ProductEntity;
import de.blinkt.openvpn.util.GlideImageLoader;
import de.blinkt.openvpn.views.FullyRecylerView;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.TopProgressView;

import static android.view.View.GONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBANNER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKHOTPACKAGEMORE;

/*
*   商城
* */

public class IndexFragment extends BaseStatusFragment implements IndexView {

    @BindView(R.id.title)
    TitleBar title;
    @BindView(R.id.top_view)
    TopProgressView topView;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.communicationTextView)
    TextView communicationTextView;
    @BindView(R.id.communicationRelativeLayout)
    RelativeLayout communicationRelativeLayout;
    @BindView(R.id.hotMessageMoreTextView)
    TextView hotMessageMoreTextView;
    @BindView(R.id.hotPacketLinearLayout)
    RelativeLayout hotPacketLinearLayout;
    @BindView(R.id.flowPackageLinearLayout)
    LinearLayout flowPackageLinearLayout;
    @BindView(R.id.guiderImageView)
    ImageView guiderImageView;
    @BindView(R.id.hardWareMoreTextView)
    TextView hardWareMoreTextView;
    @BindView(R.id.hardWareLine)
    View hardWareLine;
    @BindView(R.id.hardWareRelativeLayout)
    RelativeLayout hardWareRelativeLayout;
    @BindView(R.id.hardWareRecyclerView)
    RecyclerView hardWareRecyclerView;
    @BindView(R.id.scrollLinearlayout)
    LinearLayout scrollLinearlayout;
    @BindView(R.id.indexScrollView)
    ScrollView indexScrollView;
    private String TAG = "IndexFragment";
    //图片加载类
    private View view;
Unbinder unbinder;
    IndexPresenterImpl indexPresenter;
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
        unbinder= ButterKnife.bind(this, view);
        indexPresenter=new IndexPresenterImpl(this);
        init();
        return view;
    }


    private void init() {
        title.setTextTitle(getString(R.string.shop));
        addData();
    }
    /**
     * 增加轮播图
     */
    private void addData() {
        //获取banner图
        indexPresenter.requestBanner();
        /**
         * 获取硬件购买信息
         */
        indexPresenter.requestHardWare();
        /**
         * 获取热门套餐
         */
        indexPresenter.requestHotPackageModel(3 + "");
    }

    @OnClick({R.id.hotMessageMoreTextView, R.id.guiderImageView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hotMessageMoreTextView:
                //友盟方法统计
                Log.d(TAG, "onClick:hotMessageMoreTextView ");
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

    @Override
    public void initBanner(final List<IndexBannerEntity> infos) {
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
    public void initHardWareProduct(List<ProductEntity> productEntityList) {
        if (productEntityList != null || productEntityList.size() != 0) {
            Log.i(TAG, "产品信息：" + productEntityList.toString());
            hardWareRecyclerView.setNestedScrollingEnabled(false);
            hardWareRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            hardWareRecyclerView.setAdapter(new ProductsAdapter(productEntityList, getActivity()));
        } else {
            hardWareRelativeLayout.setVisibility(GONE);
        }
    }

    @Override
    public void initHotPackage(List<HotPackageEntity> hotPackageEntityList) {
        if (hotPackageEntityList.size() != 0) {
            FullyRecylerView hotPackageRecyclerView = new FullyRecylerView(getActivity());
            hotPackageRecyclerView.setNestedScrollingEnabled(false);
            hotPackageRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            hotPackageRecyclerView.setAdapter(new HotPackageAdapter(hotPackageEntityList, getActivity(), true));
            flowPackageLinearLayout.addView(hotPackageRecyclerView);
        } else {
            hotPacketLinearLayout.setVisibility(GONE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //当页面跳出的时候停止轮播
        if (banner != null) {
            if (isVisibleToUser) {
                banner.start();
            } else {
                banner.stopAutoPlay();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        indexPresenter.onDestroy();
        unbinder.unbind();
    }
}