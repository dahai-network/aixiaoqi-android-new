package de.blinkt.openvpn.fragments.ProMainTabFragment.View;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface AccountView {
    void setBalanceText(float balanceCount);
    void showDeviceSummarized(boolean isShow);
    void setDeviceType();
    void showToast(int toastId);
    void showToast(String toastContent);
    void setServiceText(String textContent);
    void showPackage(int hasPackageVisible,int noPackageVisible);
    void addOrActivatePackageIvAndText(int drawableId,int textId);
    void updateRedDot(int isVisiable);
    void activatePackage(int activateStatue,String activateCount);
    void packageAllCount(String allCount);
    void callTime(String time);
    void toActivity();
}
