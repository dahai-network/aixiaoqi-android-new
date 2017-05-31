package de.blinkt.openvpn.activities.UserInfo.View;

import android.widget.Button;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public interface GetBackPswView {
    Button getSendCodeBtn();
    void sendCodeIsClick(boolean isClick);
    void sendCodeBackground(int backgroundColorId);
    String getPhoneNumberText();
    String getPswText();
    String getVerificationCode();
    void finishView();
    void showToast(String toastContent);
    void showToast(int toastContentId);

}
