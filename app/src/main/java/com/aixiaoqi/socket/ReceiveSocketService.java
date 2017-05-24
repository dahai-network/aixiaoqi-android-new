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

import de.blinkt.openvpn.activities.ProMainActivity;
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
	private long receiveConnectionTime;
	private long receivePreDataTime;
	private long sendConnectionTime;
	private long sendPreDataTime;
	private String sendConnectionType;
	private String sendPreDataType;
	private String sendConnectionContent;
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
        tcpClient.connect();
    }

    TcpClient tcpClient = new TcpClient() {
        @Override
        public void onConnect(SocketTransceiver transceiver) {
            Log.i("Blue_Chanl", "正在注册GOIP");
            SocketConstant.SESSION_ID = SocketConstant.SESSION_ID_TEMP;
            createSocketLisener.create();
            CONNECT_STATUE = CONNECT_SUCCEED;
        }

        @Override
        public void onConnectFailed() {
            if (CONNECT_STATUE == ACTIVE_DISCENNECT) {
                return;
            }
            Log.e("Blue_Chanl", "onConnectFailed");
            connectFailReconnect();
            CONNECT_STATUE = CONNECT_FAIL;
        }


		@Override
		public void onReceive(SocketTransceiver transceiver, byte[] s, int length) {
			String receiveData=HexStringExchangeBytesUtil.bytesToHexString(s, length);
			ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "\n"+receiveData);
			if(receiveData.startsWith(SocketConstant.RECEIVE_CONNECTION)){
				receiveConnectionTime=sendConnectionTime;
				sendConnectionType="";
			}
			else if(receiveData.startsWith(SocketConstant.RECEIVE_PRE_DATA)){
				if(REGISTER_STATUE_CODE!=3&&sendPreDataTime<10000000){
					receivePreDataTime=sendPreDataTime;
				}else{
					receivePreDataTime=0;
				}
				sendPreDataType="";
			}
			TlvAnalyticalUtils.builderMessagePackageList(receiveData);
			createHeartBeatPackage();
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			if (CONNECT_STATUE == ACTIVE_DISCENNECT) {
				return;
			}
			Log.e("Blue_Chanl", "断开连接 - onDisconnect");
			CONNECT_STATUE = CONNECT_FAIL;
			disConnectReconnect();
		}
	};

	//首次创建连接失败，重试三次还不成功，则断开连接，并且提示注册失败。
	private void connectFailReconnect() {
		ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "connect fail:\n");
		if (!isDisconnect) {
			CommonTools.delayTime(15000);
			if (tcpClient != null && !tcpClient.isConnected()) {
				if (REGISTER_STATUE_CODE == 3) {
					REGISTER_STATUE_CODE = 2;
					EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING,SocketConstant.TCP_DISCONNECT);
				}
				if (contactFailCount <= 3) {
					reConnect();
					contactFailCount++;
				} else {
					contactFailCount = 0;
				}
			}
		}
	}


	public void sendMessage(String s) {
		if(tcpResendTimer==null){
			tcpResendTimer=new Timer();
			tcpResendTimerTask=new TimerTask() {
				@Override
				public void run() {
					Log.e(TAG,"coming");
					if(CONNECT_STATUE==CONNECT_SUCCEED){
						Log.e(TAG,"sendConnectionType="+sendConnectionType);
						if(!TextUtils.isEmpty(sendConnectionType)){
							if(receiveConnectionTime<sendConnectionTime){
								receiveConnectionTime=System.currentTimeMillis();
								if(receiveConnectionTime-sendConnectionTime>30*1000){
									//重新创建连接
									if(!TextUtils.isEmpty(sendConnectionContent)){
										sendMessage(sendConnectionContent);
									}
								}
							}
						}
						if(!TextUtils.isEmpty(sendPreDataType)){
							if(REGISTER_STATUE_CODE!=3){
								if(receivePreDataTime<sendPreDataTime){
									receivePreDataTime=System.currentTimeMillis();
									if(receivePreDataTime-sendPreDataTime>30*1000){
										//重新发送预读取数据
										if(!TextUtils.isEmpty(sendPreDataContent)){
											sendMessage(sendPreDataContent);
										}
									}
								}
							}else{
								sendPreDataContent="";
								receivePreDataTime=0;
							}
						}
					}
				}
			};
			tcpResendTimer.schedule(tcpResendTimerTask,30*1000,30*1000);
		}

		if(s.startsWith(SocketConstant.CONNECTION)){
			sendConnectionType=SocketConstant.CONNECTION;
			sendConnectionContent=s;
			sendConnectionTime=System.currentTimeMillis();
		}
		else if(s.startsWith(SocketConstant.PRE_DATA)){
			if(REGISTER_STATUE_CODE!=3){
				sendPreDataType=SocketConstant.PRE_DATA;
				sendPreDataContent=s;
				sendPreDataTime=System.currentTimeMillis();
			}
		}
		Log.e(TAG,"sendYiZhengService="+s);
		Log.e("sendMessage", "发送到GOIPtcpClient" + (tcpClient != null));
		if (tcpClient != null && tcpClient.getTransceiver() != null) {
			tcpClient.getTransceiver().send(s);
		}
		ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "\n"+s);
	}
	public void disconnect() {
		CONNECT_STATUE = ACTIVE_DISCENNECT;//主动断开
		cancelTimer();
		tcpClient.disconnect();
	}


	private boolean isDisconnect = false;

	//断开连接，如果注册成功，需要重新注册，并且改变注册状态
	private void disConnectReconnect() {
		isDisconnect = true;
		CommonTools.delayTime(5000);
		if (tcpClient != null && !tcpClient.isConnected()) {
			if (REGISTER_STATUE_CODE == 3) {
				REGISTER_STATUE_CODE = 2;
				EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING,SocketConstant.TCP_DISCONNECT);
			}
			if (!SdkAndBluetoothDataInchange.isHasPreData)
				sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
			recordStringLog(DateUtils.getCurrentDateForFileDetail() + "restart connect :\n");
			reConnect();
		}
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

	private void createHeartBeatPackage() {
		Log.e(TAG, "count=" + count + "\nSocketConstant.SESSION_ID_TEMP" + SocketConstant.SESSION_ID_TEMP + "\nSocketConstant.SESSION_ID=" + SocketConstant.SESSION_ID + (SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID)));
		if (!SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID) && count == 0 && (am == null||mJobScheduler==null)) {
			count = count + 1;
            //5.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               Log.d("JobSchedulerService", "handleMessage: 发送心跳包1");
                jobEvent();
            } else {
				Intent intent = new Intent(ReceiveSocketService.this, AutoReceiver.class);
				intent.setAction(HEARTBEAT_PACKET_TIMER);
				sender = PendingIntent.getBroadcast(ReceiveSocketService.this, 0, intent, 0);
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),TCP_HEART_TIME*1000, sender);
//                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, TCP_HEART_TIME * 1000, sender);
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
    private void reConnect() {
        initSocket();
    }
	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy()");
		if (ProMainActivity.sdkAndBluetoothDataInchange != null)
			ProMainActivity.sdkAndBluetoothDataInchange.closeReceviceBlueData();
		if (tcpClient != null) {
			tcpClient.closeTimer();
			tcpClient.disconnect();
		}
		Log.e(TAG, "tcpClient=null" + (tcpClient == null));
		count = 0;
		SocketConstant.SESSION_ID = SocketConstant.SESSION_ID_TEMP;
		cancelTimer();
		TlvAnalyticalUtils.clearData();
		TestProvider.clearData();

        if (SocketConstant.REGISTER_STATUE_CODE != 0) {
            SocketConstant.REGISTER_STATUE_CODE = 1;
        }

        super.onDestroy();
    }

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

    public void setListener(CreateSocketLisener listener) {
        this.createSocketLisener = listener;
    }

    public interface CreateSocketLisener {
        void create();
    }

    int count = 0;
}
