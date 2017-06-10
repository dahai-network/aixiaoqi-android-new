package de.blinkt.openvpn.activities.Device.Model;

import de.blinkt.openvpn.model.PreReadEntity;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public interface HasPreDataRegisterModel {
    void initPreData(PreReadEntity preReadEntity);
   void  registerSimPreData();
}
