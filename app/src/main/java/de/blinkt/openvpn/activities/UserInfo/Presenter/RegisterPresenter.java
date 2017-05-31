package de.blinkt.openvpn.activities.UserInfo.Presenter;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public interface RegisterPresenter {
    void getVerificationCode();
    void requestRegister();
    void requestLogin();
    void requestSecurityConfig();
    void requestBlackList();
    void onDestory();
}
