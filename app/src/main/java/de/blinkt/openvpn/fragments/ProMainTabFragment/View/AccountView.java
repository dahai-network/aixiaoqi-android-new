package de.blinkt.openvpn.fragments.ProMainTabFragment.View;

import android.content.Intent;

import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface AccountView  extends Toast{
    void setBalanceText(float balanceCount);
    void showDeviceSummarized(boolean isShow);
    void setDeviceType();
    void setServiceText(String textContent);
    void showPackage(int hasPackageVisible,int noPackageVisible);
    void addOrActivatePackageIvAndText(int drawableId,int textId);
    void updateRedDot(int isVisiable);
    void activatePackage(int activateStatue,String activateCount);
    void packageAllCount(String allCount);
    void callTime(String time);
    void toMyDeviceActivity();
    void toActivity( );
}
