package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.HotPackageModel;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class HotPackageModelImpl extends NetModelBaseImpl implements HotPackageModel {
    public  HotPackageModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestHotPackageModel(String hotCount) {
        createHttpRequest( HttpConfigUrl.COMTYPE_GET_HOT,hotCount);
    }
}
