package de.blinkt.openvpn.activities.Set.View;

import android.widget.TextView;

/**
 * Created by kim
 * on 2017/6/5.
 */

public interface ActivateView {

    void showToast(String msg);

    String getOrderId();

    String getDataTime();

    TextView getSureTextView();

    TextView getPayWayTextView();



}
