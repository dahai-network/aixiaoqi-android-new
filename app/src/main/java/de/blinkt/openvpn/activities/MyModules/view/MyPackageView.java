package de.blinkt.openvpn.activities.MyModules.view;

import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.johnson.adapter.OrderAdapter;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/6/9.
 */

public interface MyPackageView {

    XRecyclerView getOrderListRecylerView();
    RelativeLayout getNoNetRelativeLayout();
    OrderAdapter getOrderAdapter();
    RelativeLayout getNodataRelativeLayout();
    TextView getNoDataTextView();
    void showToast(String msg);
}
