package de.blinkt.openvpn.activities.UserInfo.Model;

import de.blinkt.openvpn.http.ModifyPersonInfoHttp;

/**
 * Created by kim
 * on 2017/6/9.
 */

public interface EditNameModel {
    /**
     *
     * @param realName 设置名字
     * @param modifyPersonInfoHttp 访问网络对象
     */
    void setNickName(String realName,ModifyPersonInfoHttp modifyPersonInfoHttp);

}
