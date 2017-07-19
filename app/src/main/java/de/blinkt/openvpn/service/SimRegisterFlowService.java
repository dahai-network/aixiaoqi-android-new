package de.blinkt.openvpn.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.aixiaoqi.socket.JNIUtil;
import com.aixiaoqi.socket.RadixAsciiChange;
import com.aixiaoqi.socket.ReceiveDataframSocketService;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SdkAndBluetoothDataInchange;
import com.aixiaoqi.socket.SendYiZhengService;
import com.aixiaoqi.socket.SocketConnection;
import com.aixiaoqi.socket.SocketConstant;
import com.aixiaoqi.socket.TestProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.DBHelp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.GetHostAndPortHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.CancelCallService;
import de.blinkt.openvpn.model.PreReadEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.Constant.ICCID_GET;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class SimRegisterFlowService extends Service implements InterfaceCallback{
    private UartService mService = null;
    private   ReceiveBLEMoveReceiver bleMoveReceiver;
    private String deviceAddress;
    public static SendYiZhengService sendYiZhengService = null;
    //重连时间
    private int RECONNECT_TIME = 10000;
    public static boolean isStartSdk = false;
    SocketConnection socketTcpConnection;
    public static SdkAndBluetoothDataInchange sdkAndBluetoothDataInchange = null;
    private int requestCount = 0;
    SocketConnection socketUdpConnection;
    Intent     intentCallPhone;
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        //本地是否保存有设备地址和设备类型
        if(TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))||TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))){
            //如果没有保存设备地址和设备类型则去请求
            CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
        }else{
            //有绑定过，则搜索设备，没有搜索到，就用通知栏的方式提示用户
            //搜索到设备，则连接设备。
            initUartServices();
            connectOperate();
        }

        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(CallPhoneService.class.getName())) {
                 intentCallPhone = new Intent(this, CallPhoneService.class);
            startService(intentCallPhone);
        }

    }

    public void initUartServices() {
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(UartService.class.getName())) {
            Intent bindIntent = new Intent(this, UartService.class);
            try {
                bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelCallService(CancelCallService entity) {
        if (intentCallPhone != null) {
            stopService(intentCallPhone);

        }
        unbindTcpService();
        destorySocketService();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            ICSOpenVPNApplication.uartService=  mService = ((UartService.LocalBinder) rawBinder).getService();
            //存在Application供全局使用
            Log.d("SimRegisterFlowService", "onServiceConnected: 初始化广播");
            initBrocast();
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };


    private void connectOperate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mService != null && mService.mConnectionState != UartService.STATE_CONNECTED) {
                    connDeviceFiveSecond();
                    CommonTools.delayTime(RECONNECT_TIME);
                }

            }
        }).start();
    }

//    private Handler stopHandler = null;
    //扫描五秒后提示
    private void connDeviceFiveSecond() {
        mService.connect(SharedUtils.getInstance().readString(Constant.IMEI));

//                EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.CONNECTING_DEVICE);
//                if (stopHandler == null) {
//                    stopHandler = new Handler();
//                }
//                stopHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
////                        scanLeDevice(false);
//                        if (mService != null && !mService.isConnectedBlueTooth()) {
//                            EventBusUtil.canClickEntity(CanClickEntity.JUMP_MYDEVICE);
//                        }
//                    }
//                }, 10000);

    }

//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            mService.mBluetoothAdapter.startLeScan(mLeScanCallback);
//        } else {
//            mService.mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//
//    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(UartService.FINDED_SERVICE);
        return intentFilter;
    }
    private void initBrocast() {
        if (bleMoveReceiver == null) {
            bleMoveReceiver = new ReceiveBLEMoveReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(bleMoveReceiver, makeGattUpdateIntentFilter());
        }
    }
    private void initPre(PreReadEntity preReadEntity) {
        SocketConstant.REGISTER_STATUE_CODE = 2;
        SocketConstant.CONNENCT_VALUE[3] = RadixAsciiChange.convertStringToHex(SharedUtils.getInstance().readString(Constant.TOKEN));
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[3]] = preReadEntity.getPreReadData();
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[2]] = preReadEntity.getDataLength();
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[1]] = preReadEntity.getImsi();
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]] = preReadEntity.getIccid();
    }

    private void registerSimPreData() {
        if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_SUCCEED) {
            sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
        } else if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_FAIL) {
            SocketConnection.mReceiveSocketService.disconnect();
            startTcp();
        } else {
            startTcp();
        }
    }
    private void startTcp() {
        startSocketService();
        startTcpSocket();
        SocketConnection.mReceiveSocketService.setListener(new ReceiveSocketService.CreateSocketLisener() {
            @Override
            public void create() {
                TestProvider.isCreate = true;
                CommonTools.delayTime(500);
               sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
            }

        });
    }

    private void startDataframService() {
        if(socketUdpConnection==null){
            socketUdpConnection = new SocketConnection();
        }
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())) {
            Intent receiveSdkIntent = new Intent(this, ReceiveDataframSocketService.class);
            bindService(receiveSdkIntent, socketUdpConnection, Context.BIND_AUTO_CREATE);
        }

    }
    private void noPreDataStartSDK() {
        isStartSdk = true;
        startDataframService();
        startSocketService();
        CommonTools.delayTime(5000);
        JNIUtil.getInstance().startSDK(1);
    }
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        //如果没有绑定过则所有的操作结束。否则继续下面的操作
        if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
            GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
            if (object.getStatus() == 1) {
                if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
                    if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
                        deviceAddress = getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI();
                        if (deviceAddress != null) {
                            deviceAddress = deviceAddress.toUpperCase();
                        }
                        SharedUtils utils = SharedUtils.getInstance();
                        utils.writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI().toUpperCase());
                        //按MAC地址保存版本号
                        if (!TextUtils.isEmpty(deviceAddress))
                            utils.writeString(deviceAddress, getBindDeviceHttp.getBlueToothDeviceEntityity().getVersion());
                        //防止返回“”或者null
                        String deviceTypeStr = getBindDeviceHttp.getBlueToothDeviceEntityity().getDeviceType();
                        if (!TextUtils.isEmpty(deviceTypeStr)) {
                            int deviceType = Integer.parseInt(deviceTypeStr);
                            if (deviceType == 0) {
                                utils.writeString(Constant.BRACELETNAME, Constant.UNITOYS);
                            } else {
                                utils.writeString(Constant.BRACELETNAME, Constant.UNIBOX);
                            }
                        }
                        initUartServices();
                    }
                }
            }
        }else if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
            GetHostAndPortHttp http = (GetHostAndPortHttp) object;
            if (http.getStatus() == 1) {
                requestCount = 0;
                
                if (http.getGetHostAndPortEntity().getVswServer().getIp() != null) {
                    SocketConstant.hostIP = http.getGetHostAndPortEntity().getVswServer().getIp();
                    SocketConstant.port = http.getGetHostAndPortEntity().getVswServer().getPort();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SdkAndBluetoothDataInchange.isHasPreData = false;
                            if (sdkAndBluetoothDataInchange == null) {
                                sdkAndBluetoothDataInchange = new SdkAndBluetoothDataInchange();
                            }
                            if (sendYiZhengService == null) {
                                sendYiZhengService = new SendYiZhengService();
                            }
                            if (!TextUtils.isEmpty(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]])) {
                                DBHelp dbHelp = new DBHelp(SimRegisterFlowService.this);
                                PreReadEntity preReadEntity = dbHelp.getPreReadEntity(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]]);
                                if (preReadEntity != null) {
                                    SdkAndBluetoothDataInchange.isHasPreData = true;
                                    SdkAndBluetoothDataInchange.PERCENT = 0;
                                    initPre(preReadEntity);
                                    registerSimPreData();
                                } else {
                                    noPreDataStartSDK();
                                }
                            } else {
                                CommonTools.delayTime(2000);
                                SendCommandToBluetooth.sendMessageToBlueTooth(ICCID_GET);
                            }
                        }
                    }).start();
                }
            } else {
                CommonTools.showShortToast(this, object.getMsg());
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
            if (requestCount < 3) {
                requestCount++;
                getConfigInfo();
            }
        }
    }

    @Override
    public void noNet() {

    }





    @Subscribe(threadMode = ThreadMode.MAIN)//ui线程
    public void onIsSuccessEntity(SimRegisterStatue entity) {
        switch (entity.getRigsterSimStatue()) {
            case SocketConstant.REGISTER_FAIL://注册失败
                rigisterFail(entity.getRigsterStatueReason());
                break;
            case SocketConstant.REGISTERING://注册中
                registering(entity.getRigsterStatueReason());
                break;

            default:

                break;
        }

    }

    private void rigisterFail(int failReason) {
        switch (failReason) {
            case SocketConstant.REGISTER_FAIL_INITIATIVE:
                //更改为注册中
//                unbindTcpService();
//                destorySocketService();
                break;
        }

    }
    //解除绑定
    private void unbindTcpService() {
        if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
            unbindService(socketTcpConnection);
            if (SocketConnection.mReceiveSocketService != null) {
                SocketConnection.mReceiveSocketService.stopSelf();
                SocketConnection.mReceiveSocketService = null;
            }
        }
    }
    //重新复制
    private void destorySocketService() {
        if (SocketConstant.REGISTER_STATUE_CODE != 0) {
            SocketConstant.REGISTER_STATUE_CODE = 1;
        }
    }
    private void startTcpSocket() {
        if (sendYiZhengService != null && SocketConnection.mReceiveSocketService != null) {
            sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);
            return;
        }
        bindTcpSucceed();
    }


    private int bindtime = 0;
    private void bindTcpSucceed() {
        if (SocketConnection.mReceiveSocketService == null) {
            CommonTools.delayTime(1000);
            if (bindtime > 15) {
                return;
            }
            bindtime++;
            startTcpSocket();
        }
        bindtime = 0;
    }
    private void startSocketService() {
        if(socketTcpConnection==null)
        socketTcpConnection = new SocketConnection();
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
            Intent receiveSdkIntent = new Intent(this, ReceiveSocketService.class);
            bindService(receiveSdkIntent, socketTcpConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void getConfigInfo() {
        CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG);
    }
    private void requestPacket() {
        getConfigInfo();
    }
    private void registering(int registeringReason) {
        switch (registeringReason) {
            case SocketConstant.START_TCP_FAIL:
                unbindTcpService();
                break;
            case SocketConstant.TCP_DISCONNECT:
                //更改为注册中
                break;
            case SocketConstant.RESTART_TCP:
                startSocketService();
                if (sendYiZhengService == null) {
                    sendYiZhengService = new SendYiZhengService();
                }
                startTcpSocket();
                break;
            case SocketConstant.VAILD_CARD:
                requestPacket();
                break;
        }
    }

}
