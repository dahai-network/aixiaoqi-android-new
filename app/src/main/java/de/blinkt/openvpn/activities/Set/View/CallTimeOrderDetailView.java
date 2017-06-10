package de.blinkt.openvpn.activities.Set.View;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by kim
 * on 2017/6/6.
 */

public interface CallTimeOrderDetailView {

    String getOrderId();

    RelativeLayout getNoNetRelativeLayout();

    TextView getPackageNameTextView();

    ImageView getPacketImageView();

    TextView getExpiryDateTextView();

    TextView getPacketStatusTextView();

    TextView getPriceTextView();

    TextView getOrderNumberTextView();

    TextView getOrderTimeTextView();

    TextView getPayWayTextView();

    boolean getIsCreateView();

    void showToast(String msg);
}
