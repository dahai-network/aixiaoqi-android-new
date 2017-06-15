package de.blinkt.openvpn.fragments.PackageFragment.presenter;

import android.os.Handler;
import android.util.Log;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.activities.MyModules.ui.PackageCategoryActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.PackageFragment.model.PackageCategoryModel;
import de.blinkt.openvpn.fragments.PackageFragment.modelImpl.PackageCategoryImpl;
import de.blinkt.openvpn.fragments.PackageFragment.view.PackageCategoryView;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by kim
 * on 2017/6/15.
 */

public class PackageCategoryPresenter implements InterfaceCallback {

    private PackageCategoryModel packageCategoryModel;
    private PackageCategoryView packageCategoryView;
    boolean isTimeOut;
    private Handler mHandler = new Handler() {
    };
    private PackageCategoryActivity instance;

    public PackageCategoryPresenter(PackageCategoryView packageCategoryView) {

        this.packageCategoryView = packageCategoryView;
        packageCategoryModel = new PackageCategoryImpl();
        instance = ICSOpenVPNApplication.packageCategoryActivity;
    }

    Runnable runnable;

    public void addData(int page, int pageSize, int type, String channel_id, boolean isLoadMore) {
        if (!isLoadMore)
            instance.showProgress(R.string.loading_data);
        packageCategoryModel.getOrder(this, page, pageSize, type, channel_id);
        isTimeOut = true;
        if (runnable != null)
            mHandler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("PackageCPresenter", "run: " + isTimeOut);
                if (isTimeOut) {
                    instance.dismissProgress();
                    instance.showToast("网络超时，请重试");
                    isTimeOut = false;
                }
            }
        };
        mHandler.postDelayed(runnable, 10000);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {

        BoughtPacketHttp http = (BoughtPacketHttp) object;
        BoughtPackageEntity bean = http.getBoughtPackageEntity();
        if (cmdType == HttpConfigUrl.COMTYPE_GET_ORDER) {
            packageCategoryView.loadSuccessView(bean);
        }
        isTimeOut = false;
        instance.dismissProgress();
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        instance.dismissProgress();
    }

    @Override
    public void noNet() {
        instance.dismissProgress();
    }

    public void releaseResource() {
        if (runnable != null)
            mHandler.removeCallbacks(runnable);
    }
}
