package de.blinkt.openvpn.activities.MyModules.presenter;

import android.util.Log;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.BalanceParticularsMode;
import de.blinkt.openvpn.activities.MyModules.modelImple.BalanceParticularsImpl;
import de.blinkt.openvpn.activities.MyModules.ui.BalanceParticularsActivity;
import de.blinkt.openvpn.activities.MyModules.view.BalanceParticularsView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ParticularHttp;

/**
 * Created by kim
 * on 2017/6/5.
 */

public class BalanceParticularsPresenter extends BaseNetActivity {

    private BalanceParticularsMode balanceParticularsMode;
    private BalanceParticularsView balanceParticularsView;

    public BalanceParticularsPresenter(BalanceParticularsView balanceParticularsView) {
        this.balanceParticularsView = balanceParticularsView;
        balanceParticularsMode = new BalanceParticularsImpl();
    }


    /**
     * 加载订单数据
     */
    public void addData(int pageNumber) {

        balanceParticularsMode.getAccountData(pageNumber, Constant.PAGESIZE, this);

    }


    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        Log.e("Presenter", "rightComplete: " + cmdType);
        ParticularHttp http = (ParticularHttp) object;
        balanceParticularsView.loadSuccessView(http);


    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        balanceParticularsView.showToast(errorMessage);
    }

    @Override
    public void noNet() {
        balanceParticularsView.loadNoNetView();
    }
}
