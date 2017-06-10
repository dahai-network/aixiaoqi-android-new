package de.blinkt.openvpn.activities.ShopModules.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.umeng.analytics.MobclickAgent;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.PackageDetailModel;
import de.blinkt.openvpn.activities.ShopModules.modelImpl.PackageDetailImpl;
import de.blinkt.openvpn.activities.ShopModules.ui.CommitOrderActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.PackageDetailActivity;
import de.blinkt.openvpn.activities.ShopModules.view.PackageDetailView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.PacketDtailHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKPACKAGEDETAILPURCHASE;

/**
 * Created by kim
 * on 2017/6/7.
 */

public class PackageDetailPresenter extends BaseNetActivity {

    private PackageDetailModel packageDetailModel;
    private PackageDetailView packageDetailView;
    private PackageDetailActivity instance;
    ScrollView detailScrollView;
    RelativeLayout NoNetRelativeLayout;
    TextView packageNameTextView;
    TextView priceTextView;
    ImageView packageDetailImageView;

    public PackageDetailPresenter(PackageDetailView packageDetailView) {
        this.packageDetailView = packageDetailView;
        packageDetailModel = new PackageDetailImpl();
        instance = ICSOpenVPNApplication.packageDetailInstance;
         initControlView();

    }

    /**
     * 初始化控件
     */
    private void initControlView() {
        detailScrollView = packageDetailView.getDetailScrollView();
        NoNetRelativeLayout = packageDetailView.getNoNetRelativeLayout();
        packageNameTextView = packageDetailView.getPackageNameTextView();
        priceTextView = packageDetailView.getPriceTextView();
        packageDetailImageView = packageDetailView.getPackageDetailImageView();
    }

    public void getPackageDetailData(String id) {
        packageDetailModel.getPacketDetail(id, this);
    }

    PacketDtailEntity.ListBean bean;

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        PacketDtailHttp http = (PacketDtailHttp) object;
        if (cmdType == HttpConfigUrl.COMTYPE_PACKET_DETAIL)
            if (http.getStatus() == 1) {
                NoNetRelativeLayout.setVisibility(View.GONE);
                detailScrollView.setVisibility(View.VISIBLE);
                bean = http.getPacketDtailEntity().getList();
                packageNameTextView.setText(bean.getPackageName());
                //进行本地缓存
                SharedUtils.getInstance().writeString(Constant.DETAIL_SIGN, bean.getDetails());
                SharedUtils.getInstance().writeString(Constant.FEATURES_SIGN, bean.getFeatures());
                //使用广播进行数据交互
                Log.d("PackageDetailActivity", "rightComplete:发送广播 ");
                Intent intent = new Intent(Constant.LOCALBROADCAST_INTENT_DATA);
                intent.putExtra(Constant.DETAIL_SIGN, bean.getDetails());
                intent.putExtra(Constant.FEATURES_SIGN, bean.getFeatures());
                LocalBroadcastManager.getInstance(instance).sendBroadcast(intent);
                priceTextView.setText("￥" + bean.getPrice());
                setSpan(priceTextView);
                String countryPic = instance.getIntent().getStringExtra("countryPic");
                if (countryPic == null)
                    Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getPic()).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            int imageWidth = resource.getWidth();
                            int imageHeight = resource.getHeight();
                            int height = CommonTools.getScreenWidth(instance) * imageHeight / imageWidth;
                            ViewGroup.LayoutParams para = packageDetailImageView.getLayoutParams();
                            para.height = height;
                            packageDetailImageView.setLayoutParams(para);
                            packageDetailImageView.setImageBitmap(resource);
                        }
                    });
            } else {
                packageDetailView.showToast(object.getMsg());
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

    public void buyPackageButtonEvent() {
        if (bean != null) {
            MobclickAgent.onEvent(context, CLICKPACKAGEDETAILPURCHASE);
            CommitOrderActivity.launch(instance, bean, 1);
        }

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        packageDetailView.showToast(errorMessage);
    }

    @Override
    public void noNet() {
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
        detailScrollView.setVisibility(View.GONE);
    }
}
