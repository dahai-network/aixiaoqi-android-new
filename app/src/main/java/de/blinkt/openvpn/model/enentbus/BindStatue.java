package de.blinkt.openvpn.model.enentbus;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/15 0015.
 */

public class BindStatue implements Serializable {
    public static final int UNBIND_DEVICE=0;
    public static final int BIND_DEVICE=1;
    private int bindStatues;//0,表示解除绑定。1表示绑定。

    public int getBindStatues() {
        return bindStatues;
    }

    public void setBindStatues(int bindStatues) {
        this.bindStatues = bindStatues;
    }
}
