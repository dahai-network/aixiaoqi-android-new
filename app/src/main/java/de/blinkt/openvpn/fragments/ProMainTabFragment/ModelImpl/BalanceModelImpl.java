package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.BalanceModel;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class BalanceModelImpl extends NetModelBaseImpl implements BalanceModel {
    public BalanceModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestBalance() {
        createHttpRequest(HttpConfigUrl.COMTYPE_GET_BALANCE);
    }


}
