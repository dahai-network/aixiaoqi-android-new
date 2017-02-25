package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class SimRegStatue implements Serializable {
    private String RegStatus;

    public String getRegStatus() {
        return RegStatus;
    }

    public void setRegStatus(String regStatus) {
        RegStatus = regStatus;
    }
}
