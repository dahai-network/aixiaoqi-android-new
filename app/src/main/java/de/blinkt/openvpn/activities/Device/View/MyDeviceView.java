package de.blinkt.openvpn.activities.Device.View;


import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public interface MyDeviceView extends Toast{
    /**
     * 开启刷新动画
     */
    void startAnim();

    /**
     * 停止刷新动画
     */
    void stopAnim();

    /**
     * 显示或者隐藏更新红点提示
     * @param isVisible
     */
    void showOrHideVersionUpgradeHotDot(int isVisible);

    /**
     * 显示升级对话框
     */
    void showUpgradeDialog();

    /**
     * 清理数据
     */
    void clearData();

    /**
     * 结束界面
     */
    void finishView();

    /**
     * 提示是否升级
     * @param upgradeContent
     */
    void showDialogGOUpgrade(String upgradeContent);
    void showNoCardDialog();
    /**
     * 显示连接文本
     * @param contentId
     */
    void setConStatueText(int contentId);
    void setConStatueBackground(int colorId);
    void setPercentText(String text);
    void percentTextViewVisible(int isVisible);
    void registerSimStatuVisible(int isVisible);
    void scanLeDevice(boolean enable);
    void connect(String macAddress);
    String getConStatusText();

    /**
     * 取消进度条
     */
    void dismissProgress();

    /**
     * 显示进度条
     * @param progressContent 进度条内存id
     * @param isCanCancel  是否准许取消
     */
  void   showProgress(int progressContent, boolean isCanCancel);

    /**
     * 设置硬件版本号
     * @param version 版本号
     */
    void setDeviceVersionText(String version);

    /**
     * 设置电量
     * @param electricityFlost 电量
     */
    void setElectricityPercent(float electricityFlost );
}
