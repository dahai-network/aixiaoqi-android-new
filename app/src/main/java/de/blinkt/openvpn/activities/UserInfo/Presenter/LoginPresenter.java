package de.blinkt.openvpn.activities.UserInfo.Presenter;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public interface LoginPresenter {
    void requestBasicConfig();
    void requestSecurityConfig();
    void requestBlackList();
    void requestLogin();
    void onDestory();
}
