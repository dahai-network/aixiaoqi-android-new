package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import com.aixiaoqi.socket.EventBusUtil;

import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.util.CommonTools;

import static de.blinkt.openvpn.constant.Constant.BIND_SUCCESS;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class BindDeviceCommandModel {

    public void agreeBind(){
        CommonTools.delayTime(500);
        //android 标记，给蓝牙设备标记是否是android设备用的
        SendCommandToBluetooth.sendMessageToBlueTooth(BIND_SUCCESS);
        EventBusUtil.bingDeviceStep(BluetoothConstant.BLUE_BIND_SUCCESS);
    }
}
