package de.blinkt.openvpn.model.enentbus;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/13 0013.
 */

public class OptionProMainActivityView implements Serializable{

    private boolean isShow;
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
