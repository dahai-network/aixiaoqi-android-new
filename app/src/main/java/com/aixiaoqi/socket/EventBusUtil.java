package com.aixiaoqi.socket;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.model.CanClickEntity;
import de.blinkt.openvpn.model.CancelCallService;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.GetTokenRes;
import de.blinkt.openvpn.model.ShowDeviceEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.StateChangeEntity;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class EventBusUtil {
    //卡注册状态
    public static void simRegisterStatue(int regstatues) {
        SimRegisterStatue entity = new SimRegisterStatue();
        entity.setRigsterSimStatue(regstatues);
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
}
