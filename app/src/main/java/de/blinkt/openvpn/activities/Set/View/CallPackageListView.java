package de.blinkt.openvpn.activities.Set.View;

import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.johnson.adapter.CallPacketAdapter;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/6/6.
 */

public interface CallPackageListView {


    int getPageNumber();

    String getPageSize();

    /**
     * 0流量/1通话/2大王卡/3双卡双待
     * @return
     */
    String getCategory();

    XRecyclerView getCallListRecylerView();

    RelativeLayout getNoNetRelativeLayout();

    CallPacketAdapter getCallPacketAdapter();
    RelativeLayout  getNodataRelativeLayout();
    TextView getNoDataTextView();

}
