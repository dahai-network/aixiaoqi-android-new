package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.util.Log;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

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

public class WriteCardFlow {
    private  String lastSendMessageStr;

//    private void ReceiveDBOperate(String mStrSimCmdPacket) {
//        Log.i("test", "写卡收回：" + mStrSimCmdPacket);
//
//        if (lastSendMessageStr.contains(Constant.WRITE_NEW_SIM_STEP_A012)) {
//            lastSendMessageStr = Constant.WRITE_NEW_SIM_STEP_A012;
//        } else if (lastSendMessageStr.contains(Constant.WRITE_NEW_SIM_STEP_7)) {
//            lastSendMessageStr = Constant.WRITE_NEW_SIM_STEP_7;
//        }
//        switch (lastSendMessageStr) {
//            //获取空卡序列号第一步/新卡写卡第一步
//            case Constant.WRITE_SIM_FIRST:
//                Log.i("Bluetooth", "进入获取空卡序列号第一步:" + mStrSimCmdPacket);
//                if (mStrSimCmdPacket.contains(WRITE_CARD_STEP1)) {
//                    if (isGetnullCardid) {
//                        sendMessageSeparate(Constant.WRITE_SIM_STEP_TWO, Constant.WRITE_SIM_DATA);
//                    } else {
//                        //新卡写卡第一步
//                        sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_2, Constant.WRITE_SIM_DATA);
//                    }
//                }
//                break;
//            //获取空卡序列号第二步
//            case Constant.WRITE_SIM_STEP_TWO:
//                Log.i("Bluetooth", "进入获取空卡序列号第二步:" + mStrSimCmdPacket);
//                if (mStrSimCmdPacket.contains(GET_NULLCARDID)) {
//                    if (isGetnullCardid)
//                        sendMessageSeparate(Constant.WRITE_SIM_STEP_THREE, Constant.WRITE_SIM_DATA);
//                } else {
//                    registFlowPath();
//                }
//                break;
//            //获取空卡序列号第三部
//            case Constant.WRITE_SIM_STEP_THREE:
//                Log.i("Bluetooth", "进入获取空卡序列号第三步:" + mStrSimCmdPacket);
//                if (mStrSimCmdPacket.contains(WRITE_CARD_STEP5)
//                        && (mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR)
//                        || mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR2))) {
//                    if (isGetnullCardid) {
//                        if (mStrSimCmdPacket.length() > 20) {
//                            mStrSimCmdPacket = mStrSimCmdPacket.substring(4, 20);
//                            Log.i("Bluetooth", "空卡序列号:" + mStrSimCmdPacket);
//                            nullCardId = mStrSimCmdPacket;
//                            //重新上电清空
////							sendMessageToBlueTooth(UP_TO_POWER);
//                            if (Integer.valueOf(nullCardId.substring(8, 16)) >= 301) {
//                                Log.i(TAG, "这是新卡");
//                                utils.writeBoolean(Constant.IS_NEW_SIM_CARD, true);
//                            } else {
//                                Log.i(TAG, "这是旧卡");
//                                utils.writeBoolean(Constant.IS_NEW_SIM_CARD, false);
//                            }
//                            isGetnullCardid = false;
//                            lastSendMessageStr = "";
//                            //发送空卡序列号
//                            writeCard();
//                            //获取完空卡序列号后获取步数
//                            sendMessageToBlueTooth(Constant.HISTORICAL_STEPS);
//                            EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.AIXIAOQI_CARD);
//
//
//                        }
//                    }
//                    //异常情况重新走一遍流程
//                } else if (mStrSimCmdPacket.contains("6e00")) {
//                    sendMessageToBlueTooth(UP_TO_POWER_NO_RESPONSE);
//                } else {
//                    if (mStrSimCmdPacket.startsWith("9000")) {
//                        //新型写卡完成
//                        handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
//                        sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
//                        isGetnullCardid = false;
//                        return;
//                    } else {
//                        registFlowPath();
//                    }
//                }
////				//最后发送信息复位
////				lastSendMessageStr = "";
//                break;
//            case Constant.WRITE_NEW_SIM_STEP_2:
//                checkIs91toSend(mStrSimCmdPacket);
//                break;
//            case Constant.WRITE_NEW_SIM_STEP_A012:
//                if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP3)) {
//                    sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_4, Constant.WRITE_SIM_DATA);
//                } else if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP6)) {
//                    sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_7 + ICSOpenVPNApplication.cardData, Constant.WRITE_SIM_DATA);
//                } else if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP8)) {
//                    //新卡写卡完成
//                    handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
//                    sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
//                }
//                break;
//            case Constant.WRITE_NEW_SIM_STEP_4:
//                if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP4)) {
//                    sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_5, Constant.WRITE_SIM_DATA);
//                }
//                break;
//            case Constant.WRITE_NEW_SIM_STEP_5:
//                checkIs91toSend(mStrSimCmdPacket);
//                break;
//            case Constant.WRITE_NEW_SIM_STEP_7:
//                checkIs91toSend(mStrSimCmdPacket);
//                break;
//
//            default:
//                if (mStrSimCmdPacket.startsWith("9000") && !CommonTools.isFastDoubleClick(1000)) {
//                    //新型写卡完成
//                    handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
//                    sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
//                    isGetnullCardid = false;
//                    return;
//                    //异常情况重新走一遍流程
//                } else if (mStrSimCmdPacket.contains("6e00")) {
//                    sendMessageToBlueTooth(UP_TO_POWER_NO_RESPONSE);
//                }
//                break;
//        }
//    }
}
