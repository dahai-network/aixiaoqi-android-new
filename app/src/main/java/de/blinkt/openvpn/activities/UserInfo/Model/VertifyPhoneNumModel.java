package de.blinkt.openvpn.activities.UserInfo.Model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/8.
 */

public interface VertifyPhoneNumModel {
    /**
     *
     * @param phoneNumEditText 手机号码
     * @param ICCID iccid
     */
    void confirmedICCID(String phoneNumEditText, String ICCID, BaseNetActivity baseNetActivity);
}
