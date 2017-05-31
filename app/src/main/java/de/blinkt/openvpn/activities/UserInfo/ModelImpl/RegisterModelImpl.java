package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.Model.RegisterModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class RegisterModelImpl extends NetModelBaseImpl implements RegisterModel {
    public  RegisterModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }

    @Override
    public void requestRegister(String phone, String password, String verification) {
        createHttpRequest(HttpConfigUrl.COMTYPE_REGIST, phone,
                password, verification);
    }
}
