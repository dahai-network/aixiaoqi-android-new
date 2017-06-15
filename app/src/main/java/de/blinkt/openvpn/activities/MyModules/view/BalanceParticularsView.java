package de.blinkt.openvpn.activities.MyModules.view;

import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.johnson.adapter.ParticularAdapter;
import de.blinkt.openvpn.http.ParticularHttp;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/6/5.
 */

public interface BalanceParticularsView {
    /**
     * 加载成功界面
     * @param http
     */
    void loadSuccessView(ParticularHttp http);
    void loadNoNetView();

    void showToast(String msg);


}
