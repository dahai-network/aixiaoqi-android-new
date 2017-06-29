package de.blinkt.openvpn.activities.ShopModules.modelImpl;

import com.aixiaoqi.socket.SocketConstant;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class EquipmentActivateModelImpl {

    public void equipmentActivate(){
        ReceiveBLEMoveReceiver.isGetnullCardid = true;
        SendCommandToBluetooth.sendMessageToBlueTooth(Constant.UP_TO_POWER_NO_RESPONSE);
    }
    public boolean isConnect(){
        if(ICSOpenVPNApplication.uartService!=null||ICSOpenVPNApplication.uartService.isConnectedBlueTooth()){
            return true;
        }
        return false;
    }

    public boolean isAiXiaoQiCard(){
        if(SocketConstant.SIM_TYPE!=4){
            return false;
        }
        return true;
    }

}
