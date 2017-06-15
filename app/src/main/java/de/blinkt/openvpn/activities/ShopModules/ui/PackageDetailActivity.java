package de.blinkt.openvpn.activities.ShopModules.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.ShopModules.presenter.PackageDetailPresenter;
import de.blinkt.openvpn.activities.ShopModules.view.PackageDetailView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.factory.FragmentFactory;
import de.blinkt.openvpn.http.PacketDtailHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.PagerSlidingTabStripExtends;

/**
 * 套餐详情界面
 */
public class PackageDetailActivity extends BaseActivity implements PackageDetailView {
    public static PackageDetailActivity activity;
    @BindView(R.id.packageDetailImageView)
    ImageView packageDetailImageView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.packageNameTextView)
    TextView packageNameTextView;
    @BindView(R.id.flowTextView)
    TextView flowTextView;
    @BindView(R.id.buyPackageButton)
    TextView buyPackageButton;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.detailScrollView)
    ScrollView detailScrollView;
    String[] detail_titles;
    PagerSlidingTabStripExtends mTabs;
    DisplayMetrics dm;
    private PackageDetailPresenter packageDetailPresenter;

    public static void launch(Context context, String id, String countryPic) {
        Intent intent = new Intent(context, PackageDetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("countryPic", countryPic);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_detail);
        ICSOpenVPNApplication.packageDetailInstance = this;
        ButterKnife.bind(this);
        initSet();
        dm = getResources().getDisplayMetrics();
        setTabsValue();


    }

    private void initSet() {
        //获取标题
        detail_titles = getResources().getStringArray(R.array.detail_titles);
        activity = this;
        initViews();
        packageDetailPresenter = new PackageDetailPresenter(this);
        addData();
        String countryPic = getIntent().getStringExtra("countryPic");
        if (countryPic != null) {
            Glide.with(ICSOpenVPNApplication.getContext()).load(countryPic).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int imageWidth = resource.getWidth();
                    int imageHeight = resource.getHeight();
                    int height = CommonTools.getScreenWidth(PackageDetailActivity.this) * imageHeight / imageWidth;
                    ViewGroup.LayoutParams para = packageDetailImageView.getLayoutParams();
                    para.height = height;
                    packageDetailImageView.setLayoutParams(para);
                    packageDetailImageView.setImageBitmap(resource);
                }
            });
        }
    }

    private void initViews() {
        hasLeftViewTitle(R.string.package_detail, 0);
        String paymentOfTerms = SharedUtils.getInstance().readString(IntentPutKeyConstant.PAYMENT_OF_TERMS);

        if (!TextUtils.isEmpty(paymentOfTerms))
            SharedUtils.getInstance().writeString(Constant.PAYTERMS_SIGN, paymentOfTerms);
        mTabs = (PagerSlidingTabStripExtends) findViewById(R.id.jbp_tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        DtailFragmentStatePagerAdapter adapter = new DtailFragmentStatePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        mTabs.setViewPager(pager);
    }

    private void addData() {
        packageDetailPresenter.getPackageDetailData(getIntent().getStringExtra("id"));
    }


    @OnClick({R.id.buyPackageButton, R.id.retryTextView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buyPackageButton:
                packageDetailPresenter.buyPackageButtonEvent();
                break;
            case R.id.retryTextView:
                addData();
                break;
        }
    }

    @Override
    public void showToast(String msg) {
        CommonTools.showShortToast(PackageDetailActivity.this, msg);

    }

    @Override
    public void loadSuccessShowView(PacketDtailEntity.ListBean bean, PacketDtailHttp http) {
        NoNetRelativeLayout.setVisibility(View.GONE);
        detailScrollView.setVisibility(View.VISIBLE);
        packageNameTextView.setText(bean.getPackageName());
        priceTextView.setText("￥" + bean.getPrice());
        setSpan(priceTextView, bean.getPrice());
    }

    @Override
    public void loadSuccessAndSetImage(Bitmap resource, int height) {
        ViewGroup.LayoutParams para = packageDetailImageView.getLayoutParams();
        para.height = height;
        packageDetailImageView.setLayoutParams(para);
        packageDetailImageView.setImageBitmap(resource);
    }

    @Override
    public void noNetShowView() {
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
        detailScrollView.setVisibility(View.GONE);
    }

    /**
     * 创建适配器
     */
    class DtailFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        public DtailFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = FragmentFactory.getDetailFragment(position);
            return fragment;
        }

        @Override
        public int getCount() {
            if (detail_titles != null) {
                return detail_titles.length;
            }
            return 0;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO
            return detail_titles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // container.addView(jbpTitles.get(position));
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            super.destroyItem(container, position, object);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearSPData();
    }

    /**
     * 清除sp里面的数据
     */
    public void clearSPData() {
        SharedUtils.getInstance().delete(Constant.DETAIL_SIGN);
        SharedUtils.getInstance().delete(Constant.FEATURES_SIGN);
        SharedUtils.getInstance().delete(Constant.PAYTERMS_SIGN);
    }

    //设置大小字体
    public void setSpan(TextView textview, double price) {
        Spannable WordtoSpan = new SpannableString(textview.getText().toString());
        int intLength = String.valueOf((int) (price)).length();
        WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
    }

    /**
     * 设置该PagerSlidingTabStrip的样式
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        mTabs.setShouldExpand(false);
        // 设置Tab的分割线是透明的
        mTabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度

        // 设置Tab Indicator的高度
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));

    }
}
