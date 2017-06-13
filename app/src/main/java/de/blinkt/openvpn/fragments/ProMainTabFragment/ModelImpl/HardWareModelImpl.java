package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.HardWareModel;
import de.blinkt.openvpn.http.CreateHttpFactory;

import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_GET_PRODUCTS;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class HardWareModelImpl extends NetModelBaseImpl implements HardWareModel {
    public  HardWareModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestHardWare()
        {
            createHttpRequest(COMTYPE_GET_PRODUCTS);
    }

}
