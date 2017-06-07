package de.blinkt.openvpn.activities.Device.View;


import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public interface MyDeviceView extends Toast{
    void startAnim();
    void stopAnim();
    void showOrHideVersionUpgradeHotDot(int isVisible);
    void showUpgradeDialog();
    void clearData();
    void finishView();
    void showDialogGOUpgrade(String upgradeContent);
    void setConStatueText(int contentId);
    void setConStatueBackground(int colorId);
    void setPercentText(String text);
    void percentTextViewVisible(int isVisible);
    void registerSimStatuVisible(int isVisible);
    void scanLeDevice(boolean enable);
    void connect(String macAddress);
    String getConStatusText();
}
