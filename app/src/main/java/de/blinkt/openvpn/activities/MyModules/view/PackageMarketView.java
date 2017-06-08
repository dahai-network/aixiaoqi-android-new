package de.blinkt.openvpn.activities.MyModules.view;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.blinkt.openvpn.views.addHeaderAndFooterRecyclerView.WrapRecyclerView;

/**
 * Created by kim
 * on 2017/6/7.
 */

public interface PackageMarketView {

    WrapRecyclerView getMarketRecyclerView();
    RelativeLayout getNoNetRelativeLayout();
    TextView getLeftPriceTextView();
    TextView getLeftContentTextView();
    TextView getLeftExpiryDateTextView();
    RelativeLayout getLeftPacketRelativeLayout();
    TextView getRightPriceTextView();
    TextView getRightContentTextView();
    TextView getRightExpiryDateTextView();
    RelativeLayout getRightPacketRelativeLayout();
    RelativeLayout getCommunicationRelativeLayout();
    void showToast(String msg);
}
