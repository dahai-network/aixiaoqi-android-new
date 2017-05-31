package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.Model.GetBackPswModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class GetBackPswModelImpl extends NetModelBaseImpl implements GetBackPswModel {

    public GetBackPswModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestGetBackPsw(String phoneStr,String pswStr,String vertificationStr) {
        createHttpRequest(HttpConfigUrl.COMTYPE_FORGET_PSW,
                phoneStr,
                pswStr,
                vertificationStr);
    }
}
