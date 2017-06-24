package com.aixiaoqi.socket;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.service.JobSchedulerService;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;

import static com.aixiaoqi.socket.SocketConstant.HEARTBEAT_PACKET_TIMER;
import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;
import static com.aixiaoqi.socket.SocketConstant.TRAN_DATA_TO_SDK;
import static com.aixiaoqi.socket.TlvAnalyticalUtils.TCP_HEART_TIME;
import static com.aixiaoqi.socket.TlvAnalyticalUtils.sendToSdkLisener;
import static de.blinkt.openvpn.activities.Device.PresenterImpl.ProMainPresenterImpl.sdkAndBluetoothDataInchange;

/**
 * Created by Administrator on 2016/12/30 0030.
 */
public class ReceiveSocketService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private int contactFailCount = 1;
    PendingIntent sender;
    AlarmManager am;
    public static int CONNECT_SUCCEED = 0;//连接成功
    public static int CONNECT_FAIL = 1;//连接失败
    public static int CONNECT_STATUE = -1;//连接状态
    public static int ACTIVE_DISCENNECT = -2;//主动断开
    private static String TAG = "ReceiveSocketService";
    Timer tcpResendTimer;
    TimerTask tcpResendTimerTask;
    private boolean isReceiveConnection;
    private boolean isReceivePreData;
    private long sendConnectionTime;
    private long sendPreDataTime;
    private String sendConnectionType;
    private String sendPreDataType;
    private String sendConnectionContent;//记录发送的内容，以免发送失败，重新发送的
    private String sendPreDataContent;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ReceiveSocketService getService() {
            return ReceiveSocketService.this;
        }
    }


    public void initSocket() {
        Log.e("Blue_Chanl", "initSocket");
        tcpClient.connect();
    }

    TcpClient tcpClient = new TcpClient() {
        @Override
        public void onConnect(SocketTransceiver transceiver) {
            Log.i("Blue_Chanl", "正在注册GOIP");
            SocketConstant.SESSION_ID = SocketConstant.SESSION_ID_TEMP;
            createSocketLisener.create();
            contactFailCount = 0;
            CONNECT_STATUE = CONNECT_SUCCEED;
        }
        //连接失败，主动断开的不再去重连
        @Override
        public void onConnectFailed() {

            if (activeDis()) return;
            Log.e("Blue_Chanl", "onConnectFailed");
            connectFailReconnect();

        }

        //接收从服务器发送过来的数据，收到数据以后清理数据不在重新发送这条数据。
        @Override
        public void onReceive(SocketTransceiver transceiver, byte[] s, int length) {
            receiveServiceData(s, length);
        }

        //断开，如果是主动断开连接不在重连
        @Override
        public void onDisconnect(SocketTransceiver transceiver) {

            if (activeDis())
                return;
            Log.e("Blue_Chanl", "断开连接 - onDisconnect");
            disConnectReconnect();
        }
    };

    private boolean activeDis() {
        if (CONNECT_STATUE == ACTIVE_DISCENNECT) {
            return true;
        }
        CONNECT_STATUE = CONNECT_FAIL;
        return false;
    }

    private void receiveServiceData(byte[] s, int length) {
        String receiveData = HexStringExchangeBytesUtil.bytesToHexString(s, length);
        ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "\n" + receiveData);
        if (receiveData.startsWith(SocketConstant.RECEIVE_CONNECTION)) {
            isReceiveConnection =true;
            sendConnectionType = "";
        } else if (receiveData.startsWith(SocketConstant.RECEIVE_PRE_DATA)) {
            isReceivePreData =true;
            sendPreDataType = "";
        }
        TlvAnalyticalUtils.builderMessagePackageList(receiveData);
        Log.d(TAG, "onReceive: ---------------");
        createHeartBeatPackage();
    }

    //首次创建连接失败，重试三次还不成功，则断开连接，并且提示注册失败。
    private void connectFailReconnect() {
        ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "connect fail:\n");
        CommonTools.delayTime(15000);
        if (tcpClient != null && !tcpClient.isConnected()) {
            if (REGISTER_STATUE_CODE == 3) {
                REGISTER_STATUE_CODE = 2;

            }
            if (contactFailCount <= 3) {
                reConnect();
                contactFailCount++;
            } else {
                EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL, SocketConstant.NO_NET_ERROR);
            }
        }

    }


    //断开连接，如果注册成功，需要重新注册，并且改变注册状态
    private void disConnectReconnect() {

        clearResendTimer();
        CommonTools.delayTime(5000);
        if (tcpClient != null && !tcpClient.isConnected()) {
            if (REGISTER_STATUE_CODE == 3) {
                REGISTER_STATUE_CODE = 2;
            }
            if (!SdkAndBluetoothDataInchange.isHasPreData)
                sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
            recordStringLog(DateUtils.getCurrentDateForFileDetail() + "restart connect :\n");
            contactFailCount = 1;
            reConnect();
        }
    }
    //向服务器发送信息
    public void sendMessage(String s) {
        //当与设备断开连接，就不再发送创建连接。因为重新连接的时候还会发送创建连接，发送这条数据没有意义。是为了防止客户端与服务器不断创建与断开的问题
        ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "\n" + "UartService.STATE_DISCONNECTED="+( UartService.mConnectionState==UartService.STATE_DISCONNECTED));
        if(s.startsWith(SocketConstant.CONNECTION)&& UartService.mConnectionState==UartService.STATE_DISCONNECTED){
            return;
        }
        resendMessageTimer();
        if (s.startsWith(SocketConstant.CONNECTION)) {
            sendConnectionType = SocketConstant.CONNECTION;
            sendConnectionContent = s;
            isReceiveConnection=false;
            sendConnectionTime = System.currentTimeMillis();
        } else if (s.startsWith(SocketConstant.PRE_DATA)) {
            if (REGISTER_STATUE_CODE != 3) {
                isReceiveConnection=false;
                sendPreDataType = SocketConstant.PRE_DATA;
                sendPreDataContent = s;
                sendPreDataTime = System.currentTimeMillis();
            }
        }
        Log.e(TAG, "sendYiZhengService=" + s);
        Log.e("sendMessage", "发送到GOIPtcpClient" + (tcpClient != null));
        if (tcpClient != null && tcpClient.getTransceiver() != null) {
            tcpClient.getTransceiver().send(s);
        }
        ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "\n" + s);
    }

    private int resendConnectionCount=0;
    private int resendPreDataCount=0;
    //重新发送信息
    private void resendMessageTimer() {
        if(resendCount ==0){
            resendCount++;
            if (tcpResendTimer == null) {
                tcpResendTimer = new Timer();
            }
            if(tcpResendTimerTask==null)
                tcpResendTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.e(TAG, "coming");
                        if (CONNECT_STATUE == CONNECT_SUCCEED) {
                            Log.e(TAG, "sendConnectionType=" + sendConnectionType);
                            if (!TextUtils.isEmpty(sendConnectionType)) {
                                if (System.currentTimeMillis() - sendConnectionTime >=30 * 1000&&!isReceiveConnection&&resendConnectionCount<3) {
                                    //重新创建连接
                                    if (!TextUtils.isEmpty(sendConnectionContent)) {
                                        sendMessage(sendConnectionContent);
                                        resendConnectionCount++;
                                    }
                                }else{

                                    if(resendConnectionCount>=3){
                                    //关闭定时器
                                    clearResendTimer();

                                    //EventBus 通知界面提示网络异常
                                    EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.NO_NET_ERROR);
                                    }
                                    //把resendConnectionCount重置为零
                                    resendConnectionCount=0;
                                }
                            }
                            if (!TextUtils.isEmpty(sendPreDataType)) {
                                if (REGISTER_STATUE_CODE != 3) {
                                    if (System.currentTimeMillis() - sendPreDataTime >= 30 * 1000&&!isReceivePreData&&resendPreDataCount<3) {
                                        //重新发送预读取数据
                                        if (!TextUtils.isEmpty(sendPreDataContent)) {
                                            sendMessage(sendPreDataContent);
                                            resendPreDataCount++;
                                        }
                                    }else{

                                        if(resendPreDataCount>=3){
                                        //关闭定时器
                                        clearResendTimer();
                                        //EventBus 通知界面提示网络异常
                                        EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.NO_NET_ERROR);
                                            //把resendPreDataCount重置为零
                                            resendPreDataCount=0;
                                        }
                                    }
                                } else {
                                    sendPreDataContent = "";
                                    isReceivePreData = true;
                                }
                            }
                        }
                    }
                };

            tcpResendTimer.schedule(tcpResendTimerTask, 30 * 1000, 30 * 1000);

        }
    }

    private int resendCount;
    //取消重新发送闹钟
    private void clearResendTimer() {
        if(tcpResendTimer!=null){
            tcpResendTimer.cancel();
            tcpResendTimer=null;
        }
        if(tcpResendTimerTask!=null){
            tcpResendTimerTask.cancel();
            tcpResendTimerTask=null;
        }
        resendCount=0;
    }
    public void disconnect() {
        CONNECT_STATUE = ACTIVE_DISCENNECT;//主动断开
        cancelTimer();
        clearResendTimer();
        tcpClient.disconnect();
    }
    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    public static void recordStringLog(final String text) {// 新建或打开日志文件

        if (Constant.IS_DEBUG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/aixiaoqi/";
                    String fileName = "TCP" + DateUtils.getCurrentDateForFile() + ".text";
                    File file = new File(path + fileName);
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                        BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                        bufWriter.write(text);
                        bufWriter.newLine();
                        bufWriter.close();
                        filerWriter.close();
                        Log.d("行为日志写入成功", text);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    //创建心跳包
    private void createHeartBeatPackage() {
        Log.e(TAG, "count=" + count + "\nSocketConstant.SESSION_ID_TEMP" + SocketConstant.SESSION_ID_TEMP + "\nSocketConstant.SESSION_ID=" + SocketConstant.SESSION_ID + (SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID)));
        if (!SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID) && count == 0 && (am == null || mJobScheduler == null)) {
            count = count + 1;
            //5.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.d("JobSchedulerService", "handleMessage: 发送心跳包1");
                jobEvent();
            } else {
                Log.d("android4.0", "handleMessage: 发送心跳包2");
                Intent intent = new Intent(ReceiveSocketService.this, AutoReceiver.class);
                intent.setAction(HEARTBEAT_PACKET_TIMER);
                sender = PendingIntent.getBroadcast(ReceiveSocketService.this, 0, intent, 0);
                am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TCP_HEART_TIME * 1000, sender);
            }
        }
    }

    JobScheduler mJobScheduler;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void jobEvent() {
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(Constant.TYPE_ONE,
                new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
        builder.setPeriodic(TCP_HEART_TIME * 1000);
        if (mJobScheduler.schedule(builder.build()) <= 0) {
            //If something goes wrong
        }
    }

    //重新连接
    private void reConnect() {
        initSocket();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy()");
        CONNECT_STATUE = ACTIVE_DISCENNECT;
        if (sdkAndBluetoothDataInchange != null)
            sdkAndBluetoothDataInchange.closeReceviceBlueData();
        if (tcpClient != null) {
            tcpClient.disconnect();
        }
        count = 0;
        SocketConstant.SESSION_ID = SocketConstant.SESSION_ID_TEMP;
        cancelTimer();
        clearResendTimer();
        TlvAnalyticalUtils.clearData();
        TestProvider.clearData();
        if (SocketConstant.REGISTER_STATUE_CODE != 0) {
            SocketConstant.REGISTER_STATUE_CODE = 1;
        }
        super.onDestroy();
    }

    //取消定时器
    private void cancelTimer() {
        if (am != null) {
            am.cancel(sender);
            am = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mJobScheduler != null)
                mJobScheduler.cancelAll();
        }
    }

    CreateSocketLisener createSocketLisener;
    //设置创建成功以后，回调到界面创建跟服务器之间的连接
    public void setListener(CreateSocketLisener listener) {
        this.createSocketLisener = listener;
    }

    public interface CreateSocketLisener {
        void create();
    }

    int count = 0;
}
