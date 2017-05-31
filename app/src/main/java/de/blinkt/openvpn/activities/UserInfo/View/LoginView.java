package de.blinkt.openvpn.activities.UserInfo.View;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public interface LoginView {

    void showProgress(int progressContent);
    void dismissProgress();
    void showToast(String toastContent);
    void showToast(int toastContentId);
    String getUserPhone();
    String getUserPassword();
    void toProMainActivity();
    void finishView();

}
