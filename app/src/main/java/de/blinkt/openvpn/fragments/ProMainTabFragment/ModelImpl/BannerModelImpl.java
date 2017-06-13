package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.BannerModel;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class BannerModelImpl extends NetModelBaseImpl implements BannerModel {
    public BannerModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestBanner() {
       createHttpRequest( HttpConfigUrl.COMTYPE_INDEX_BANNER);
    }
}
