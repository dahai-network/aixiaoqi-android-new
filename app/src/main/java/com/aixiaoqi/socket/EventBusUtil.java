package com.aixiaoqi.socket;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.IsSuccessEntity;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class EventBusUtil {

    public static void registerFail(int callbacktype,int regstatues) {
        IsSuccessEntity entity = new IsSuccessEntity();
        entity.setType(callbacktype);
        entity.setFailType(regstatues);
        entity.setSuccess(false);
        EventBus.getDefault().post(entity);
    }
}
