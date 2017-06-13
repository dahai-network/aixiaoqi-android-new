package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import de.blinkt.openvpn.activities.UserInfo.Model.EditNameModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.ModifyPersonInfoHttp;

/**
 * Created by kim
 * on 2017/6/9.
 */

public class EditNameImpl implements EditNameModel {
    @Override
    public void setNickName(String realName, ModifyPersonInfoHttp modifyPersonInfoHttp) {
        modifyPersonInfoHttp.setNickName(realName, HttpConfigUrl.COMTYPE_POST_MODIFY_NICK);
        new Thread(modifyPersonInfoHttp).start();

    }
}
