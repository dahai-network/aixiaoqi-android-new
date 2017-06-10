package de.blinkt.openvpn.activities.ShopModules.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import cn.com.johnson.adapter.CountryDetailPackageAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.CountryPackageMode;
import de.blinkt.openvpn.activities.ShopModules.modelImpl.CountryPackageImpl;
import de.blinkt.openvpn.activities.ShopModules.ui.CountryPackageActivity;
import de.blinkt.openvpn.activities.ShopModules.view.CountryPackageView;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CountryPacketHttp;
import de.blinkt.openvpn.model.CountryPacketEntity;
import static android.view.View.GONE;

/**
 * Created by kim
 * on 2017/6/7.
 */

public class CountryPackagePresenter extends BaseNetActivity {

    public CountryPackageMode countryPackageMode;
    private CountryPackageView countryPackageView;
    TextView nodataTextView;
    RelativeLayout noNetRelativeLayout;
    RecyclerView packageDetailRecyclerView;
    String countryPic;
    ImageView packageImageView;
    private CountryPackageActivity instance;

    public CountryPackagePresenter(CountryPackageView countryPackageView) {
        this.countryPackageView = countryPackageView;
        countryPackageMode = new CountryPackageImpl();
        instance = CountryPackageActivity.activity;
        initControlView();
    }

    /**
     * 初始化控件
     */
    private void initControlView() {

        nodataTextView = countryPackageView.getNodataTextView();
        noNetRelativeLayout = countryPackageView.getNoNetRelativeLayout();
        packageDetailRecyclerView = countryPackageView.getPackageDetailRecyclerView();
        packageImageView = countryPackageView.getPackageImageView();
    }

    /**
     * 获取国家套餐数据
     */
    public void addCountryPackageData() {
        String countryId = countryPackageView.getCountryId();
        countryPic = countryPackageView.getCountryPic();
        if (countryId != null)
            countryPackageMode.getCountryPacketData(countryId, this);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        CountryPacketHttp http = (CountryPacketHttp) object;
        List<CountryPacketEntity> bean = http.getCountryPacketList();
        if (bean != null) {
            if (bean.size() != 0) {
                noNetRelativeLayout.setVisibility(GONE);
                packageDetailRecyclerView.setVisibility(View.VISIBLE);
                packageDetailRecyclerView.setAdapter(new CountryDetailPackageAdapter(instance, http.getCountryPacketList(), countryPic));
            } else {
                packageDetailRecyclerView.setVisibility(View.GONE);
                nodataTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void noNet() {
        noNetRelativeLayout.setVisibility(View.VISIBLE);
        packageDetailRecyclerView.setVisibility(GONE);
        packageImageView.setVisibility(GONE);
    }

}
