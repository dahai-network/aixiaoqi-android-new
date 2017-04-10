package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class BlackListEntity implements Serializable {

    private String BlackNum;

    public String getBlackNum() {
        return BlackNum;
    }

    public void setBlackNum(String blackNum) {
        BlackNum = blackNum;
    }
}
