package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.text.TextUtils;
import android.util.Log;

import com.aixiaoqi.socket.EventBusUtil;

import java.util.ArrayList;

import de.blinkt.openvpn.Logger;
import de.blinkt.openvpn.activities.Device.ModelImpl.UpdateDeviceInfoModelImpl;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class DeviceBaseSystemInfoModel extends Logger{
    UpdateDeviceInfoModelImpl updateDeviceInfoModel;
   public  void returnBaseSystemInfo(ArrayList<String> messages){
        String deviceVesion = Integer.parseInt(messages.get(0).substring(10, 12), 16) + "." + Integer.parseInt(messages.get(0).substring(12, 14), 16);
        Log.i(TAG, "版本号:" + deviceVesion);
        int DeviceType = 1;
        String braceletname = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
        if (!TextUtils.isEmpty(braceletname)) {
            if (braceletname.contains(Constant.UNITOYS)) {
                DeviceType = 0;
            } else {
                DeviceType = 1;
            }
        }
        SharedUtils.getInstance().writeInt(Constant.BRACELETTYPEINT, DeviceType);
        SharedUtils.getInstance().writeInt(Constant.BRACELETPOWER, Integer.parseInt(messages.get(0).substring(14, 16), 16));
        SharedUtils.getInstance().writeString(Constant.BRACELETVERSION, deviceVesion);
        EventBusUtil.blueReturnData(Constant.SYSTEM_BASICE_INFO,"","");
       updateDeviceInfo();
    }
    /**
     * 更新固件版本号
     */
    //更新设备信息
    private void updateDeviceInfo() {
        if(updateDeviceInfoModel==null)
            updateDeviceInfoModel=new UpdateDeviceInfoModelImpl();
        updateDeviceInfoModel.updateDeviceInfo();
    }
}
