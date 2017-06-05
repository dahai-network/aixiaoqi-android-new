package de.blinkt.openvpn.activities.Set.View;

import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.johnson.adapter.ParticularAdapter;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/6/5.
 */

public interface BalanceParticularsView {

    XRecyclerView getParticularsRecyclerView();

    RelativeLayout getNoNetRelativeLayout();

    int getPageNumber();

    ParticularAdapter getParticularAdapter();

    RelativeLayout getNodataRelativeLayout();

    TextView getNoDataTextView();

    void showToast(String msg);


}
