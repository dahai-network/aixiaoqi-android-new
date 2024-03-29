package de.blinkt.openvpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;
import com.orhanobut.logger.*;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import cn.com.johnson.model.MyDeviceEntity;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.ConnectBluetoothReceiveModel;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.DeviceBaseSystemInfoModel;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.PowerOnModel;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.SimDataInfoModel;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.WriteCardFlowModel;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.EncryptionUtil;
import de.blinkt.openvpn.util.SharedUtils;
import static de.blinkt.openvpn.activities.Device.PresenterImpl.ProMainPresenterImpl.sdkAndBluetoothDataInchange;
import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.AGREE_BIND;
import static de.blinkt.openvpn.constant.Constant.APP_CONNECT;
import static de.blinkt.openvpn.constant.Constant.BASIC_MESSAGE;
import static de.blinkt.openvpn.constant.Constant.BIND_SUCCESS;
import static de.blinkt.openvpn.constant.Constant.ICCID_GET;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_CARD_MSG;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_ELECTRICITY;
import static de.blinkt.openvpn.util.CommonTools.getBLETime;

/**
 * Created by Administrator on 2016/10/5.
 */

public class ReceiveBLEMoveReceiver extends BroadcastReceiver   {

    private UartService mService = null;

    private String TAG = "ReceiveBLEMoveReceiver";
    private String mStrSimCmdPacket;
    //分包存储ArrayList
    //写卡状态（订单状态 ，0是没有写卡，1是写卡成功，4是写卡失败）
    public static int orderStatus = 0;
    //是否获取空卡序列号，如果是则发送到广播与服务器进行处理后发给蓝牙设备
    public static String nullCardId = null;
    //重连次数
    public static int retryTime;
    ConnectBluetoothReceiveModel connectBluetoothReceiveModel;
    DeviceBaseSystemInfoModel deviceBaseSystemInfoModel;
    SimDataInfoModel simDataInfoModel;
    WriteCardFlowModel writeCardFlowModel;
    SharedUtils utils = SharedUtils.getInstance();
    public static boolean isGetnullCardid = false;//是否获取空卡数据
    public static String rechargeStatue="01";
    private void gattDisconnect() {
        if (mService != null) {
            Log.d(TAG, "断开服务gattDisconnect: ");
            mService.disconnect();
        }
        if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
            EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.DISCOONECT_DEVICE);
        }
        EventBusUtil.blueConnStatue(UartService.STATE_DISCONNECTED);
    }

    /**
     * 接受广播
     *
     * @param context
     * @param intent
     */
    public void onReceive(final Context context, Intent intent) {

        if(connectBluetoothReceiveModel==null)
            connectBluetoothReceiveModel=new ConnectBluetoothReceiveModel(context);
        final String action = intent.getAction();
        mService = ICSOpenVPNApplication.uartService;
        if (action.equals(UartService.FINDED_SERVICE)) {
            findBlueService();

        } else if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
            //断开处理
            disconnectedOption();
            ICSOpenVPNApplication.isConnect = false;
        } else if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
            EventBusUtil.blueConnStatue(UartService.STATE_CONNECTED);
            EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.CONNECTING_DEVICE);
            ICSOpenVPNApplication.isConnect = true;
        } else if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
            mService.enableTXNotification();
            //如果版本号小于android N
        } else if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
            if (hasValideData(context, intent)) return;
        }
        if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
            mService.disconnect();
//			 mReceiveSocketService.closeThread();
        }
    }

    private boolean hasValideData(final Context context, Intent intent) {
        final ArrayList<String> messages = intent.getStringArrayListExtra(UartService.EXTRA_DATA);
        if (messages.size() == 0) {
            return true;
        }

        retryTime = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String firstPackage = null;
                    String dataType = null;
                    Log.d(TAG, "run: 接受数据");
                    if(messages.size()!=0) {
                        firstPackage = messages.get(0).substring(0, 2);
                        dataType = messages.get(0).substring(6, 10);
                    }
                    for (int i = 0; i < messages.size(); i++) {
                        Log.e(TAG, messages.get(i));
                    }
                    Logger.d("dataType：" + dataType);
                    Logger.d("firstPackage：" + firstPackage);
                    if(firstPackage==null){
                        Logger.d("run: 蓝牙没有回数据");
                        return;
                    }
                    switch (firstPackage) {
                        case "55":
                            switch (dataType) {
                                //电量多少
                                case RECEIVE_ELECTRICITY:
                                    int braceletPower;
                                    if(Integer.parseInt(messages.get(0).substring(10, 12), 16)>100){
                                        braceletPower=100;
                                    }else{
                                        braceletPower=Integer.parseInt(messages.get(0).substring(10, 12), 16);
                                    }
                                    utils.writeInt(Constant.BRACELETPOWER, braceletPower);
                                    EventBusUtil.blueReturnData(Constant.RECEIVE_ELECTRICITY,"","");
                                    break;
                                case AGREE_BIND:
                                    //绑定流程成功命令
                                    CommonTools.delayTime(500);
                                    //android 标记，给蓝牙设备标记是否是android设备用的
                                    Logger.d("接收到绑定命令");
                                    SendCommandToBluetooth.sendMessageToBlueTooth(BIND_SUCCESS);
                                    EventBusUtil.bingDeviceStep(BluetoothConstant.BLUE_BIND_SUCCESS);
                                    break;
                                //基本信息获取
                                case Constant.SYSTEM_BASICE_INFO:
                                    if(deviceBaseSystemInfoModel==null){
                                        deviceBaseSystemInfoModel=new DeviceBaseSystemInfoModel();
                                    }
                                    deviceBaseSystemInfoModel.returnBaseSystemInfo(messages);
                                    break;
                                case Constant.RECHARGE_STATE:
                                    if(messages!=null&&messages.get(0)!=null){
                                        rechargeStatue=messages.get(0).substring(10,12);
                                        EventBusUtil.blueReturnData(Constant.RECHARGE_STATE,rechargeStatue,"");
                                    }
                                    break;
                                case Constant.RETURN_POWER:
                                    Log.e("Blue_Chanl", "Constant.RETURN_POWER：" + firstPackage);
                                    PowerOnModel powerOnModel=new PowerOnModel();
                                    powerOnModel.returnPower(messages, context);
                                    break;
                                case Constant.READ_SIM_DATA:
                                    Log.i(TAG, "发送给SDK");
                                    if (IS_TEXT_SIM) {
                                        sdkAndBluetoothDataInchange.sendToSDKAboutBluetoothInfo(messages);
                                    }
                                    break;
                                case RECEIVE_CARD_MSG:
//										if ((Integer.parseInt(messages.get(0).substring(2, 4), 16) & 0x80) == 0x80) {
                                    Log.e("WriteCard", "RECEIVE_CARD_MSG：" + firstPackage);
                                    mStrSimCmdPacket = PacketeUtil.Combination(messages);
                                    // 接收到一个完整的数据包,处理信息
                                    initWriteCardFlowModel(context);
                                    writeCardFlowModel.ReceiveDBOperate(mStrSimCmdPacket);
                                    messages.clear();
//										}
                                    break;
                                case Constant.IS_INSERT_CARD:
                                    //5580040c000102
                                    initSimDataInfo();
                                    simDataInfoModel.isInsertCardOrCardType(messages);
                                    break;
                                case Constant.ICCID_BLUE_VALUE:
                                    initSimDataInfo();
                                    simDataInfoModel.setIccid(messages);
                                    break;
                                case Constant.APP_CONNECT_RECEIVE:
                                    Logger.d("接收到专属命令返回");
                                    connectBluetoothReceiveModel.appConnectReceive(messages);
                                    break;
                                default:
                                    break;
                            }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();
        return false;
    }

    private void disconnectedOption() {
        nullCardId = null;
        //如果保存的IMEI没有的话，那么就是在MyDevice里面，在Mydevice里面会有连接操作
        Log.d(TAG, "onReceive: retryTime=" + retryTime + "---ICSOpenVPNApplication.isConnect=" + ICSOpenVPNApplication.isConnect);
        if (retryTime < 20 && ICSOpenVPNApplication.isConnect) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "IMEI=" + TextUtils.isEmpty(utils.readString(Constant.IMEI)) + "\nisConnect=" + ICSOpenVPNApplication.isConnect);
                    if (!TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
                        //多次扫描蓝牙，在华为荣耀，魅族M3 NOTE 中有的机型，会发现多次断开–扫描–断开–扫描…
                        // 会扫描不到设备，此时需要在断开连接后，不能立即扫描，而是要先停止扫描后，过2秒再扫描才能扫描到设备
                        mService.connect(utils.readString(Constant.IMEI));
                    } else {
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                    }
                }
            }).start();
            retryTime++;
        } else {
            gattDisconnect();
            retryTime=0;
        }
    }

    private void findBlueService() {
        Log.d(TAG, "UART_CONNECT_MSG");
        IS_TEXT_SIM = false;
        CommonTools.delayTime(100);

        if(ICSOpenVPNApplication.random8NumberString==null){
            ICSOpenVPNApplication.random8NumberString= EncryptionUtil.random8Number();
        }
        String random8NumberString=ICSOpenVPNApplication.random8NumberString;

        sendMessageToBlueTooth(APP_CONNECT + random8NumberString);//APP专属命令

        //把日志保存到本地文件中
        Log.d(TAG,"发送指令=" + APP_CONNECT + random8NumberString + "----随机数" + random8NumberString);
        String braceletname = utils.readString(Constant.BRACELETNAME);


        if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI)) || braceletname == null ) {

        } else {
            CommonTools.delayTime(200);
            //获取蓝牙基本信息
            sendMessageToBlueTooth(BASIC_MESSAGE);
            CommonTools.delayTime(200);
            Log.d(TAG,"onReceive: 获取ICCID_GET");
            sendMessageToBlueTooth(ICCID_GET);
            //更新时间操作
            sendMessageToBlueTooth(getBLETime());
        }
    }

    private void initSimDataInfo() {
        if(simDataInfoModel==null){
            simDataInfoModel=new SimDataInfoModel();
        }
    }

    private void initWriteCardFlowModel(Context context) {
        if(writeCardFlowModel==null){
            writeCardFlowModel=new WriteCardFlowModel(context);
        }
    }




}
