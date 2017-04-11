package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.PackageDetailActivityItemFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.PacketDtailHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.PagerSlidingTabStrip;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKPACKAGEDETAILPURCHASE;

/**
 * 套餐详情界面
 */
public class PackageDetailActivity extends BaseNetActivity implements InterfaceCallback {
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
    Button buyPackageButton;
    /*  @BindView(R.id.featuresTextView)
      TextView featuresTextView;*/
  /*  @BindView(R.id.detailTextView)
    TextView detailTextView;*/
   /* @BindView(R.id.retryTextView)*/
 /*   TextView retryTextView;
    @BindView(R.id.how_to_use_tv)
    TextView howToUseTv;*/
  /*  @BindView(R.id.payment_term_text)
            TextView paymentTermText;*/
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.detailScrollView)
    ScrollView detailScrollView;
    private PacketDtailEntity.ListBean bean;


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
        ButterKnife.bind(this);
        initSet();
    }

    private void initSet() {
        activity = this;
        initViews();
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
        // if (!TextUtils.isEmpty(paymentOfTerms))
        //paymentTermText.setText(SharedUtils.getInstance().readString(IntentPutKeyConstant.PAYMENT_OF_TERMS));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MyPagerDetailAdapter adapter = new MyPagerDetailAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }


    private void addData() {
        createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_DETAIL, getIntent().getStringExtra("id"));
    }


    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        PacketDtailHttp http = (PacketDtailHttp) object;
        if (cmdType == HttpConfigUrl.COMTYPE_PACKET_DETAIL)
            if (http.getStatus() == 1) {
                NoNetRelativeLayout.setVisibility(View.GONE);
                detailScrollView.setVisibility(View.VISIBLE);
                bean = http.getPacketDtailEntity().getList();
                Log.d("aixiaoqi__", "rightComplete: " + bean.getDetails());
                //detailTextView.setText(bean.getDetails());
                //featuresTextView.setText(bean.getFeatures());
                Log.d("aixiaoqi__", "rightComplete: " + bean.getPackageName());

                packageNameTextView.setText(bean.getPackageName());

                Log.d("aixiaoqi__", "rightComplete: " + bean.getFlow());
                flowTextView.setText(bean.getFlow());
                //  howToUseTv.setText(bean.getUseDescr());
                priceTextView.setText("￥" + bean.getPrice());
                setSpan(priceTextView);
                String countryPic = getIntent().getStringExtra("countryPic");
                if (countryPic == null)
                    Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getPic()).asBitmap().into(new SimpleTarget<Bitmap>() {
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
            } else {
                CommonTools.showShortToast(PackageDetailActivity.this, object.getMsg());
            }
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

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        CommonTools.showShortToast(this, errorMessage);
    }

    @Override
    public void noNet() {
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
        detailScrollView.setVisibility(View.GONE);
    }


    @OnClick({R.id.buyPackageButton, R.id.retryTextView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buyPackageButton:
                if (bean != null) {
                    //友盟方法统计
                    MobclickAgent.onEvent(context, CLICKPACKAGEDETAILPURCHASE);
                    CommitOrderActivity.launch(PackageDetailActivity.this, bean);
                }
                break;
            case R.id.retryTextView:
                addData();
                break;
        }
    }

    class MyPagerDetailAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"套餐详情", "产品特色", "支付条款"
        };

        public MyPagerDetailAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return PackageDetailActivityItemFragment.newInstance(position);
        }

    }

}
