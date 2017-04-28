package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class ShowDeviceEntity implements Serializable {

    private boolean showDevice;

    public boolean isShowDevice() {
        return showDevice;
    }

    public void setShowDevice(boolean showDevice) {
        this.showDevice = showDevice;
    }
}
