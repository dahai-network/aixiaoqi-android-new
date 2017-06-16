package de.blinkt.openvpn.activities.SimOption.View;

import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public interface CallDetailView extends Toast{
    void setUserNameText(String textContent);
    void setUserNameVisible(int isVisible);
    void setPhoneNameText(String phoneNumber);
    void loadMoreComplete();
    void noMoreLoading();
    void callRecordRvIsVisible(int isVisible);
    void lastCallTimeText(String callTime);
    void setBlackList();
    void noCallTime();

}
