package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;
import org.greenrobot.eventbus.EventBus;
import java.util.HashMap;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CommomModel.BlueHttpModelImpl.OrderActivationLocalCompletedModelImpl;
import de.blinkt.openvpn.activities.MyModules.ui.ActivateActivity;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.ShopModules.presenter.AiXiaoQiWherePresenter;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.isGetnullCardid;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.nullCardId;
import static de.blinkt.openvpn.activities.MyModules.ui.ActivateActivity.FINISH_ACTIVITY;
import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.GET_NULLCARDID;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR2;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER_NO_RESPONSE;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP1;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP5;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class WriteCardFlowModel extends NetPresenterBaseImpl  {
    // 上一条发送命令
    public static String lastSendMessageStr = "";
    private static final int WRITE_CARD_COMPLETE = 2;
    OrderActivationLocalCompletedModelImpl orderActivationLocalCompletedModel;
    Context context;
    public static int orderStatus = 0;
    public  WriteCardFlowModel(Context context){
        this.context=context;
    }

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WRITE_CARD_COMPLETE:
                    if(orderActivationLocalCompletedModel==null)
                    orderActivationLocalCompletedModel=new OrderActivationLocalCompletedModelImpl(WriteCardFlowModel.this);
                    orderActivationLocalCompletedModel.requestOrderActivationLocalCompleted(AiXiaoQiWherePresenter.orderDetailId);
                    break;
            }
        }
    };

    public void writeCard() {
        WriteCardEntity writeCardEntity = new WriteCardEntity();
        writeCardEntity.setNullCardId(nullCardId);
        EventBus.getDefault().post(writeCardEntity);
    }

    public void sendMessageSeparate(final String message, final String type) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                lastSendMessageStr = message;
                SendCommandToBluetooth.sendToBlue(message,type);
            }
        }).start();
    }


    //写卡流程
    public void ReceiveDBOperate(String mStrSimCmdPacket) {
        Log.i("WriteCard", "写卡收回：" + mStrSimCmdPacket);

        if (lastSendMessageStr.contains(Constant.WRITE_NEW_SIM_STEP_A012)) {
            lastSendMessageStr = Constant.WRITE_NEW_SIM_STEP_A012;
        } else if (lastSendMessageStr.contains(Constant.WRITE_NEW_SIM_STEP_7)) {
            lastSendMessageStr = Constant.WRITE_NEW_SIM_STEP_7;
        }

        Log.i("WriteCard", "写卡收回 lastSendMessageStr：" + lastSendMessageStr);
        switch (lastSendMessageStr) {
            //获取空卡序列号第一步/新卡写卡第一步
            case Constant.WRITE_SIM_FIRST:
                Log.i("WriteCard", "进入获取空卡序列号第一步:" + mStrSimCmdPacket+"\nisGetnullCardid="+isGetnullCardid);
                if (mStrSimCmdPacket.contains(WRITE_CARD_STEP1)) {
                    if (isGetnullCardid) {
                        sendMessageSeparate(Constant.WRITE_SIM_STEP_TWO, Constant.WRITE_SIM_DATA);
                    } else {
                        //新卡写卡第一步
                        sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_2, Constant.WRITE_SIM_DATA);
                    }
                }
                break;
            //获取空卡序列号第二步
            case Constant.WRITE_SIM_STEP_TWO:
                Log.i("WriteCard", "进入获取空卡序列号第二步:" + mStrSimCmdPacket);
                if (mStrSimCmdPacket.contains(GET_NULLCARDID)) {
                    if (isGetnullCardid)
                        sendMessageSeparate(Constant.WRITE_SIM_STEP_THREE, Constant.WRITE_SIM_DATA);
                }
                break;
            //获取空卡序列号第三部
            case Constant.WRITE_SIM_STEP_THREE:
                Log.i("WriteCard", "进入获取空卡序列号第三步:" + mStrSimCmdPacket);
                if (mStrSimCmdPacket.contains(WRITE_CARD_STEP5)
                        && (mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR)
                        || mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR2))) {
                    if (isGetnullCardid) {
                        if (mStrSimCmdPacket.length() > 20) {
                            mStrSimCmdPacket = mStrSimCmdPacket.substring(4, 20);
                            Log.i("WriteCard", "空卡序列号:" + mStrSimCmdPacket);
                            nullCardId = mStrSimCmdPacket;
                            //重新上电清空
//							sendMessageToBlueTooth(UP_TO_POWER);
                            if (Integer.valueOf(nullCardId.substring(8, 16)) >= 301) {
                                Log.i("WriteCard", "这是新卡");
                                SharedUtils.getInstance().writeBoolean(Constant.IS_NEW_SIM_CARD, true);
                                writeCard();
                            } else {
                                Log.i("WriteCard", "这是旧卡");
                                SharedUtils.getInstance().writeBoolean(Constant.IS_NEW_SIM_CARD, false);
                            }
                            isGetnullCardid = false;
                            lastSendMessageStr = "";
                            //发送空卡序列号
//                            EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.AIXIAOQI_CARD);


                        }
                    }
                    //异常情况重新走一遍流程
                } else if (mStrSimCmdPacket.contains("6e00")) {
                    sendMessageToBlueTooth(UP_TO_POWER_NO_RESPONSE);
                } else {
                    if (mStrSimCmdPacket.startsWith("9000")) {
                        //新型写卡完成
                        handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
                        sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
                        isGetnullCardid = false;
                        return;
                    }
                }
//				//最后发送信息复位
//				lastSendMessageStr = "";
                break;
            case Constant.WRITE_NEW_SIM_STEP_2:
                checkIs91toSend(mStrSimCmdPacket);
                break;
            case Constant.WRITE_NEW_SIM_STEP_A012:
                if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP3)) {
                    sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_4, Constant.WRITE_SIM_DATA);
                } else if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP6)) {
                    Log.i("WriteCard", "ICSOpenVPNApplication.cardData:" + ICSOpenVPNApplication.cardData);
                    sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_7 + ICSOpenVPNApplication.cardData, Constant.WRITE_SIM_DATA);
                } else if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP8)) {
                    //新卡写卡完成
                    Log.i("WriteCard", "WRITE_CARD_COMPLETE:" );
                    handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
                    sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
                }
                break;
            case Constant.WRITE_NEW_SIM_STEP_4:
                if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP4)) {
                    sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_5, Constant.WRITE_SIM_DATA);
                }
                break;
            case Constant.WRITE_NEW_SIM_STEP_5:
                checkIs91toSend(mStrSimCmdPacket);
                break;
            case Constant.WRITE_NEW_SIM_STEP_7:
                checkIs91toSend(mStrSimCmdPacket);
                break;

            default:
                if (mStrSimCmdPacket.startsWith("9000") && !CommonTools.isFastDoubleClick(1000)) {
                    //新型写卡完成
                    handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
                    sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
                    isGetnullCardid = false;
                    return;
                    //异常情况重新走一遍流程
                } else if (mStrSimCmdPacket.contains("6e00")) {
                    sendMessageToBlueTooth(UP_TO_POWER_NO_RESPONSE);
                }
                break;
        }
    }

    private void checkIs91toSend(String mStrSimCmdPacket) {
        if (mStrSimCmdPacket.contains(Constant.WRITE_CARD_91)) {
            sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_A012 +
                    mStrSimCmdPacket.substring(mStrSimCmdPacket.length() - 2), Constant.WRITE_SIM_DATA);
        }
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED) {
            if (object.getStatus() == 1) {
                HashMap<String, String> map = new HashMap<>();
                map.put("statue", 1 + "");
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKACTIVECARD, map);
                CommonTools.showShortToast(context, "激活成功！");
                orderStatus = 1;
                Intent intent = new Intent();
                intent.setAction(FINISH_ACTIVITY);
                intent.setAction(MyOrderDetailActivity.FINISH_PROCESS);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } else {
                CommonTools.showShortToast(context
                        , object.getMsg());
            }
        }
    }


    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        try {
            CommonTools.showShortToast(context, errorMessage);
            Log.i("WriteCard", "http.getMsg:" + errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void noNet() {
        try {
            CommonTools.showShortToast(context, context.getResources().getString(R.string.no_wifi));
            Intent intent = new Intent();
            intent.setAction(MyOrderDetailActivity.FINISH_PROCESS_ONLY);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
