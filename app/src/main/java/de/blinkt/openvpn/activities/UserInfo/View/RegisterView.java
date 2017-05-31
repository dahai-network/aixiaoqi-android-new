package de.blinkt.openvpn.activities.UserInfo.View;

import android.widget.Button;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public interface RegisterView {
    Button getSendCodeBtn();
    void sendCodeIsClick(boolean isClick);
    void registerIsClick(boolean isClick);
    void sendCodeBackground(int backgroundColorId);
    String getPhoneNumberText();
    String getPswText();
    String getVerificationCode();
    void finishView();
    void showToast(String toastContent);
    void showToast(int toastContentId);
    void showProgress(int progressContentId);
    void dismissProgress();
    boolean isAgree();
    void toProMainActivity();

}
