package de.blinkt.openvpn.activities.ShopModules.view;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/6/7.
 */

public interface CountryPackageView {

    String getCountryId();

    RelativeLayout getNoNetRelativeLayout();

    RecyclerView getPackageDetailRecyclerView();

    TextView getNodataTextView();

    String getCountryPic();
    ImageView getPackageImageView();
}
