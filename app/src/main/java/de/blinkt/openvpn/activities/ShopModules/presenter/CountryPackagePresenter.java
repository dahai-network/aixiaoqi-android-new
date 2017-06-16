package de.blinkt.openvpn.activities.ShopModules.presenter;

import android.util.Log;

import java.util.List;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.CountryPackageMode;
import de.blinkt.openvpn.activities.ShopModules.modelImpl.CountryPackageImpl;
import de.blinkt.openvpn.activities.ShopModules.ui.CountryPackageActivity;
import de.blinkt.openvpn.activities.ShopModules.view.CountryPackageView;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CountryPacketHttp;
import de.blinkt.openvpn.model.CountryPacketEntity;

/**
 * Created by kim
 * on 2017/6/7.
 */

public class CountryPackagePresenter extends BaseNetActivity {

    public CountryPackageMode countryPackageMode;
    private CountryPackageView countryPackageView;
    private CountryPackageActivity instance;

    public CountryPackagePresenter(CountryPackageView countryPackageView) {
        this.countryPackageView = countryPackageView;
        countryPackageMode = new CountryPackageImpl();
        instance = CountryPackageActivity.activity;
    }

    /**
     * 获取国家套餐数据
     */
    public void addCountryPackageData(String countryId) {
        if (countryId != null) {
            instance.showProgress(R.string.loading_data);
            countryPackageMode.getCountryPacketData(countryId, this);
        }
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        CountryPacketHttp http = (CountryPacketHttp) object;
        List<CountryPacketEntity> bean = http.getCountryPacketList();
        if (bean != null) {
            Log.d("CountryPackagePresenter", "rightComplete: ");
            countryPackageView.loadSuccessShowView(bean, http);
        }
        instance.dismissProgress();
    }

    @Override
    public void noNet() {
        countryPackageView.noNetShowView();
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        instance.showToast(errorMessage);
    }
}
