package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class CanClickEntity implements Serializable {

    public static final String JUMP_MYDEVICE="jump_mydevice";
    private String jumpTo;

    public String getJumpTo() {
        return jumpTo;
    }

    public void setJumpTo(String jumpTo) {
        this.jumpTo = jumpTo;
    }
}
