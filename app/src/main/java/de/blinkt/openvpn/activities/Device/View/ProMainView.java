package de.blinkt.openvpn.activities.Device.View;

import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public interface ProMainView extends Toast{
    void blueToothOpen();
    void showHotDot(int isVisible);
    void bottomFragmentIsShow(int isVisible);
    void stopCallPhoneService();
}
