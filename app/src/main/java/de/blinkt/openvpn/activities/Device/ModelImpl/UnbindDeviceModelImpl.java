package de.blinkt.openvpn.activities.Device.ModelImpl;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;

import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.UnbindeDeviceModel;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.model.enentbus.BindStatue;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.Constant.BRACELETNAME;
import static de.blinkt.openvpn.constant.Constant.BRACELETPOWER;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public class UnbindDeviceModelImpl extends NetModelBaseImpl implements UnbindeDeviceModel {
    public UnbindDeviceModelImpl(OnLoadFinishListener onLoadFinishListener) {
        super(onLoadFinishListener);
    }

    @Override
    public void unbindDevice() {
        createHttpRequest(HttpConfigUrl.COMTYPE_UN_BIND_DEVICE);
    }


    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
       if(cmdType==HttpConfigUrl.COMTYPE_UN_BIND_DEVICE){
           if(object.getStatus()==1){
           SharedUtils.getInstance().delete(BRACELETPOWER);
           SharedUtils.getInstance().delete(Constant.IMEI);
           SharedUtils.getInstance().delete(BRACELETNAME);
           SharedUtils.getInstance().delete(Constant.BRACELETVERSION);
           //判断是否再次重连的标记
           ICSOpenVPNApplication.isConnect = false;
           // 解除绑定，注册失败不显示
               EventBusUtil.bindStatue(BindStatue.UNBIND_DEVICE);
//           EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL, SocketConstant.REGISTER_FAIL_INITIATIVE);
           ICSOpenVPNApplication.uartService.disconnect();
           }
       }
       if(onLoadFinishListener!=null){
           onLoadFinishListener.rightLoad( cmdType,object);
       }
    }
}
