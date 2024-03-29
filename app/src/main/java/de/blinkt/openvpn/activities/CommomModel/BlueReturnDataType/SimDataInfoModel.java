package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.util.Log;
import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.RadixAsciiChange;
import com.aixiaoqi.socket.SocketConstant;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.util.SharedUtils;
import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class SimDataInfoModel {

    public void isInsertCardOrCardType(ArrayList<String> messages) {
        Logger.d("run: " + messages.toString() + ":" + messages.get(0).substring(10, 12));
        Logger.d( "接收数据：是否插卡：" + messages.toString());

        if (messages.get(0).substring(10, 12).equals("00")) {
            Logger.d( "未插卡");
            iccid="";
            EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.UN_INSERT_CARD);
            //未插卡（需要修改：由于没有获取ICCID无法判断所以日后需要修改，暂时这样写）
            SocketConstant.REGISTER_STATUE_CODE = 0;
            //保证程序正常所以要下电
            sendMessageToBlueTooth(OFF_TO_POWER);
            //恢复测试写卡流程
            IS_TEXT_SIM = false;
        } else if (messages.get(0).substring(10, 12).equals("04")) {

        } else {
            Logger.d( "已插卡");
            if (SocketConstant.REGISTER_STATUE_CODE != 0) {
                SocketConstant.REGISTER_STATUE_CODE = 1;
            }
            SocketConstant.SIM_TYPE = Integer.parseInt(messages.get(0).substring(12, 14));
            ReceiveBLEMoveReceiver.nullCardId = null;

            switch (messages.get(0).substring(12, 14)){
                case "00"://有卡并且上电失败，可能是无效卡/卡未插好/设备异常 重启钥匙扣
                    sendMessageToBlueTooth(Constant.RESTORATION);
                    SharedUtils.getInstance().delete(Constant.OPERATER);
                    iccid="";
                    break;
                case "01":
                    Logger.d( "移动卡！");
                    registFlowPath();
                    SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_MOBILE);
                    //卡类型是运营商则开始注册
                    break;
                case "02":
                    Logger.d( "联通卡！");
                    registFlowPath();
                    SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_UNICOM);
                    //卡类型是运营商则开始注册
                    break;
                case "03":
                    Logger.d( "电信卡！");
                    registFlowPath();
                    SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_TELECOM);
                    //卡类型是运营商则开始注册
                    break;
                case "04":
                    Logger.d( "爱小器卡！");
                    iccid="";
                    SharedUtils.getInstance().delete(Constant.OPERATER);
                    EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.AIXIAOQI_CARD);
                    break;
            }

        }
    }

    public static  String iccid="";//
    private boolean isSameIccid;

    public void setIccid(ArrayList<String> messages){
        String Iccid = PacketeUtil.Combination(messages);
        Log.e("ICCID_BLUE_VALUE", Iccid);
        //89860115851010259736
        if(Iccid.equals(iccid)){
            isSameIccid=true;
            return ;
        }
        iccid=Iccid;
        isSameIccid=false;
        SharedUtils.getInstance().writeString(Constant.ICCID, Iccid);
        //给对应SIM卡ICCID进行赋值
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]] = RadixAsciiChange.convertStringToHex(Iccid);
    }

    private void registFlowPath(){

        Logger.d("判断Iccid是否相同"+isSameIccid +"--当前状态"+ BaseStatusFragment.bleStatus);
        if(isSameIccid&&"注册中".equals(BaseStatusFragment.bleStatus)){
            return ;
        }
        Logger.d("SimDataInfoModel"+"进入注册流程");
        EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING, SocketConstant.VAILD_CARD);
        IS_TEXT_SIM = true;
        ReceiveBLEMoveReceiver.isGetnullCardid = false;
    }
}
