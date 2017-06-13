package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.util.CommonTools;

import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.nullCardId;
import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.GET_NULLCARDID;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR2;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER_NO_RESPONSE;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP1;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP5;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class WriteCardFlowModel {

    private Handler mHandler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private void writeCard() {
        WriteCardEntity writeCardEntity = new WriteCardEntity();
        writeCardEntity.setNullCardId(nullCardId);
        EventBus.getDefault().post(writeCardEntity);
    }


}
