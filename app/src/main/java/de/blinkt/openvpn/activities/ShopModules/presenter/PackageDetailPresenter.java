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

import cn.com.aixiaoqi.R;
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


    public PackageDetailPresenter(PackageDetailView packageDetailView) {
        this.packageDetailView = packageDetailView;
        packageDetailModel = new PackageDetailImpl();
        instance = ICSOpenVPNApplication.packageDetailInstance;

    }


    public void getPackageDetailData(String id) {
        instance.showProgress(R.string.loading_data);
        packageDetailModel.getPacketDetail(id, this);
    }

    PacketDtailEntity.ListBean bean;

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        PacketDtailHttp http = (PacketDtailHttp) object;
        if (cmdType == HttpConfigUrl.COMTYPE_PACKET_DETAIL)
            if (http.getStatus() == 1) {
                bean = http.getPacketDtailEntity().getList();
                packageDetailView.loadSuccessShowView(bean, http);
                //进行本地缓存
                SharedUtils.getInstance().writeString(Constant.DETAIL_SIGN, bean.getDetails());
                SharedUtils.getInstance().writeString(Constant.FEATURES_SIGN, bean.getFeatures());
                //使用广播进行数据交互
                Log.d("PackageDetailActivity", "rightComplete:发送广播 ");
                Intent intent = new Intent(Constant.LOCALBROADCAST_INTENT_DATA);
                intent.putExtra(Constant.DETAIL_SIGN, bean.getDetails());
                intent.putExtra(Constant.FEATURES_SIGN, bean.getFeatures());
                LocalBroadcastManager.getInstance(instance).sendBroadcast(intent);


                String countryPic = instance.getIntent().getStringExtra("countryPic");
                if (countryPic == null)
                    Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getPic()).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            int imageWidth = resource.getWidth();
                            int imageHeight = resource.getHeight();
                            int height = CommonTools.getScreenWidth(instance) * imageHeight / imageWidth;
                            packageDetailView.loadSuccessAndSetImage(resource, height);
                        }
                    });
            } else {
                instance.showToast(object.getMsg());
            }
            instance.dismissProgress();
    }

    public void buyPackageButtonEvent() {
        if (bean != null) {
            MobclickAgent.onEvent(context, CLICKPACKAGEDETAILPURCHASE);
            CommitOrderActivity.launch(instance, bean, 1);
        }

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        instance.showToast(errorMessage);
    }

    @Override
    public void noNet() {
        packageDetailView.noNetShowView();
    }
}
