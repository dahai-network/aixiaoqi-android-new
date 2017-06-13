package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.MaxPhoneCallTimeModel;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class MaxPhoneCallTimeModelImpl extends NetModelBaseImpl implements MaxPhoneCallTimeModel {
    public MaxPhoneCallTimeModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestMaxPhoneCallTime() {
       createHttpRequest( HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME);
    }
}
