package de.blinkt.openvpn.activities.SimOption.Presenter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public interface SmsDetailPresenter {
    void requestDeleteSms(ArrayList<String> ids);
    void requestGetSmsDetail(String phoneNumber);
    void requestOnceSendSms(String smsID);
    void requestSendSmsMessage(String phoneNumber, String content);
}
