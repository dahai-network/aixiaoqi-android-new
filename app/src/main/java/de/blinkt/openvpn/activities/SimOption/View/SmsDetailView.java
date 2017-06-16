package de.blinkt.openvpn.activities.SimOption.View;

import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public interface SmsDetailView extends Toast{
    void showResendDialog();
    void rlSmsImageViewVisible(int isVisible);
    void llSendSmsVisible(int isVisible);
    void finishView();
    void recyclerViewBottom();
    void stopRefresh();
    void noNetRelativeLayoutVisible(int isVisible);
    String getSendSmsContent();
    String getSendSmsPhone();
    void  combinePhoneNumber();
    String getPhoneNumbers();
    void setSwipeRefresh();
    void setTitleBar();
    void setSmsContent(String text);
    void consigneeLl(int isVisible);
   void addMap(String phoneNumber,String realName);
}
