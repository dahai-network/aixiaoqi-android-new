package de.blinkt.openvpn.model.enentbus;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/7 0007.
 */

public class BlueConnStatue implements Serializable {
    private int connStatue;
    public int getConnStatue() {
        return connStatue;
    }

    public void setConnStatue(int connStatue) {
        this.connStatue = connStatue;
    }



}
