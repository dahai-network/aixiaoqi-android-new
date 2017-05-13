package com.aixiaoqi.socket;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.model.CanClickEntity;
import de.blinkt.openvpn.model.CancelCallService;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.GetTokenRes;
import de.blinkt.openvpn.model.ShowDeviceEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.StateChangeEntity;
import de.blinkt.openvpn.model.enentbus.OptionCellPhoneFragmentView;
import de.blinkt.openvpn.model.enentbus.OptionProMainActivityView;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class EventBusUtil {

    /**
     *卡注册状态
     * @param regstatues  注册状态
     */
    public static void simRegisterStatue(int regstatues) {
        SimRegisterStatue entity = new SimRegisterStatue();
        entity.setRigsterSimStatue(regstatues);
        EventBus.getDefault().post(entity);
    }
    /**
     * 卡注册状态
     * @param regstatues  注册状态
     * @param regstatuesreason  注册状态过程中的原因
     */
    public static void simRegisterStatue(int regstatues,int regstatuesreason) {
        SimRegisterStatue entity = new SimRegisterStatue();
        entity.setRigsterSimStatue(regstatues);
        entity.setRigsterStatueReason(regstatuesreason);
        EventBus.getDefault().post(entity);
    }
    /**
     * 卡注册状态
     * @param regstatues  注册状态
     * @param regstatuesreason  注册状态过程中的原因
     * @param percent 进度条
     */
    public static void simRegisterStatue(int regstatues,int regstatuesreason,int percent) {
        SimRegisterStatue entity = new SimRegisterStatue();
        entity.setRigsterSimStatue(regstatues);
        entity.setRigsterStatueReason(regstatuesreason);
        entity.setProgressCount(percent);
        EventBus.getDefault().post(entity);
    }
    //网络状态改变
    public static void simStateChange(String registerType,boolean isopen) {
        StateChangeEntity entity = new StateChangeEntity();
        entity.setStateType(registerType);
        entity.setIsopen(isopen);
        EventBus.getDefault().post(entity);
    }
    public static void changeConnectStatus(String status, int statusDrawableInt) {
        ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
        entity.setStatus(status);
        entity.setStatusDrawableInt(statusDrawableInt);
        EventBus.getDefault().post(entity);
    }

    public static void showDevice(boolean showDevice) {
        ShowDeviceEntity entity = new ShowDeviceEntity();
        entity.setShowDevice(showDevice);
        EventBus.getDefault().post(entity);
    }
    public static void cancelCallService( ) {
        CancelCallService entity = new CancelCallService();
        EventBus.getDefault().post(entity);
    }

    public static void canClickEntity(String jumpTo ) {
        CanClickEntity entity = new CanClickEntity();
        entity.setJumpTo(jumpTo);
        EventBus.getDefault().post(entity);
    }

    public static void getTokenRes() {
        GetTokenRes entity = new GetTokenRes();
        EventBus.getDefault().post(entity);
    }

    public static void optionView(boolean isShow) {
        OptionProMainActivityView entity = new OptionProMainActivityView();
        entity.setShow(isShow);
        EventBus.getDefault().post(entity);
    }

    public static void optionView(String  textChange) {
        OptionCellPhoneFragmentView entity = new OptionCellPhoneFragmentView();
        entity.setTextChange(textChange);
        EventBus.getDefault().post(entity);
    }
}
