package de.blinkt.openvpn.activities.ShopModules.view;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by kim
 * on 2017/6/7.
 */

public interface CommitOrderView {

    CheckBox getAliPayCheckBox();

    CheckBox getWeixinPayCheckBox();

    TextView getSureTextView();

    void showToast(String msg);
}
