package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class BlackListLOcalSQLEntity implements Serializable{
    private String userPhone;
    private String blackListPhone;

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getBlackListPhone() {
        return blackListPhone;
    }

    public void setBlackListPhone(String blackListPhone) {
        this.blackListPhone = blackListPhone;
    }
}
