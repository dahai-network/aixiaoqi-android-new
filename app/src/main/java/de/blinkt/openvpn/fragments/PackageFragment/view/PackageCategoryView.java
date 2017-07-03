package de.blinkt.openvpn.fragments.PackageFragment.view;

import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by kim
 * on 2017/6/15.
 */

public interface PackageCategoryView extends Toast{

    void loadSuccessView(BoughtPackageEntity bean);
    void dismissProgress();
    void showProgress(int showProgressContentId);
}
