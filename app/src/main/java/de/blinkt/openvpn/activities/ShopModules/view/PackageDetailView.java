package de.blinkt.openvpn.activities.ShopModules.view;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by kim
 * on 2017/6/7.
 */

public interface PackageDetailView {

    RelativeLayout getNoNetRelativeLayout();
    ScrollView getDetailScrollView();
    TextView getPackageNameTextView();
    TextView getPriceTextView();
    ImageView getPackageDetailImageView();
    void showToast(String msg);
}
