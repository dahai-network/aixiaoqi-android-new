package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;
import java.util.ArrayList;
import de.blinkt.openvpn.Logger;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.constant.Constant;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.isGetnullCardid;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.nullCardId;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;

/**
 * Created by Administrator on 2017/6/20 0020.
 * 上电命令
 */

public class PowerOnModel extends Logger{
    WriteCardFlowModel writeCardFlowModel;
    public void returnPower(ArrayList<String> messages, Context context) {
        if (messages.get(0).substring(10, 12).equals("03")) {
            //当上电完成则需要发送写卡命令
            Log.i(TAG, "上电ReceiveBLEMove返回：IS_TEXT_SIM:" + IS_TEXT_SIM + ",nullCardId=" + nullCardId);
            if (SocketConstant.REGISTER_STATUE_CODE == 1 && SocketConstant.REGISTER_STATUE_CODE == 2) {
                EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING, SocketConstant.REGISTERING);
            }
            initWriteCardFlowModel(context);
            //空卡ID是否不为空，若不为空则
            if (nullCardId != null) {
                Log.i(TAG, "nullcardid上电返回");
                //发送旧卡空卡序列号
                writeCardFlowModel.writeCard();
            }
            if (!IS_TEXT_SIM && isGetnullCardid&&nullCardId == null) {
                    Log.i(TAG, "发送" + Constant.WRITE_SIM_FIRST);
                    writeCardFlowModel.sendMessageSeparate(Constant.WRITE_SIM_FIRST, Constant.WRITE_SIM_DATA);
            }

        } else if (messages.get(0).substring(10, 12).equals("13")) {
            if (!IS_TEXT_SIM) {
                Intent cardBreakIntent = new Intent();
                cardBreakIntent.setAction(MyOrderDetailActivity.CARD_RULE_BREAK);
                LocalBroadcastManager.getInstance(context).sendBroadcast(cardBreakIntent);
            }
            EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.UN_INSERT_CARD);
        }
    }

    private void initWriteCardFlowModel(Context context) {
        if(writeCardFlowModel==null){
            writeCardFlowModel=new WriteCardFlowModel(context);
        }
    }
}
