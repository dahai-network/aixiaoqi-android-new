package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.util.Log;
import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.RadixAsciiChange;
import com.aixiaoqi.socket.SocketConstant;
import java.util.ArrayList;
import de.blinkt.openvpn.Logger;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;
import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class SimDataInfoModel extends Logger{
    public void isInsertCardOrCardType(ArrayList<String> messages) {
        Log.d(TAG, "run: " + messages.toString() + ":" + messages.get(0).substring(10, 12));
        Log.i(TAG, "接收数据：是否插卡：" + messages.toString());
        if (messages.get(0).substring(10, 12).equals("00")) {
            Log.i(TAG, "未插卡");
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
            Log.i(TAG, "已插卡");
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
                    Log.i(TAG, "移动卡！");
                    registFlowPath();
                    SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_MOBILE);
                    //卡类型是运营商则开始注册
                    break;
                case "02":
                    Log.i(TAG, "联通卡！");
                    registFlowPath();
                    SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_UNICOM);
                    //卡类型是运营商则开始注册
                    break;
                case "03":
                    Log.i(TAG, "电信卡！");
                    registFlowPath();
                    SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_TELECOM);
                    //卡类型是运营商则开始注册
                    break;
                case "04":
                    Log.i(TAG, "爱小器卡！");
                    iccid="";
                    SharedUtils.getInstance().delete(Constant.OPERATER);
                    EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.AIXIAOQI_CARD);
                    break;
            }

        }
    }

    private  String iccid="";
    private boolean isSameIccid;

    public void setIccid(ArrayList<String> messages){
        String Iccid = PacketeUtil.Combination(messages);
        Log.e("ICCID_BLUE_VALUE", Iccid);
        if(Iccid.equals(iccid)){
            isSameIccid=true;
            return ;
        }
        iccid=Iccid;
        isSameIccid=false;
        SharedUtils.getInstance().writeString(Constant.ICCID, Iccid);
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]] = RadixAsciiChange.convertStringToHex(Iccid);
    }

    private void registFlowPath(){
        if(isSameIccid){
            return ;
        }
        Log.i("Bluetooth", "进入注册流程");
        EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING, SocketConstant.VAILD_CARD);
        IS_TEXT_SIM = true;
        ReceiveBLEMoveReceiver.isGetnullCardid = false;
    }
}
