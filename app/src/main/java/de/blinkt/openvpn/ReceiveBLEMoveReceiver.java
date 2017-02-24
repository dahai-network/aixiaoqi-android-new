package de.blinkt.openvpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.aixiaoqi.socket.SocketConnection;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.ActivateActivity;
import de.blinkt.openvpn.activities.MyOrderDetailActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.ActivationLocalCompletedHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.HistoryStepHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.SportStepEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.activities.MyDeviceActivity.isUpgrade;
import static de.blinkt.openvpn.constant.Constant.BASIC_MESSAGE;
import static de.blinkt.openvpn.constant.Constant.GET_NULLCARDID;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_ELECTRICITY;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP1;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP5;
import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;

/**
 * Created by Administrator on 2016/10/5.
 */

public class ReceiveBLEMoveReceiver extends BroadcastReceiver implements InterfaceCallback {

	private UartService mService = null;
	private Context context;
	private String TAG = "ReceiveBLEMoveReceiver";
	private String mStrSimCmdPacket;
	private String mStrStepHistory;
	private SportStepEntity entity = new SportStepEntity();
	//分包存储ArrayList
	private ArrayList<String> messages = new ArrayList<>();
	//写卡状态（订单状态 ，0是没有写卡，1是写卡成功，4是写卡失败）
	public static int orderStatus = 0;
	private Thread sendStepThread;
	public static boolean isConnect = false;
	//是否打开了历史步数服务
	private boolean isOpenStepService = false;
	//复位命令存储
	private String resetOrderStr = null;
	//是否获取空卡序列号，如果是则发送到广播与服务器进行处理后发给蓝牙设备
	public static String nullCardId = null;
	private int UPDATE_HISTORY_DATE = 1;
	private int WRITE_CARD_COMPLETE = 2;
//	private String dataType;//发出数据以后需要把dataType重置为-1；
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == UPDATE_HISTORY_DATE) {
				//更新历史步数
				updateHistoryDate();
			} else if (msg.what == WRITE_CARD_COMPLETE) {
				activationLocalCompletedHttp();
			}
		}
	};
	//重连次数
	public static int retryTime;
	//单线程线程池 用于接收大量数据时候使用
	private ExecutorService pool = Executors.newSingleThreadExecutor();


	public void onReceive(final Context context, Intent intent) {
		this.context = context;
		final String action = intent.getAction();
		mService = ICSOpenVPNApplication.uartService;
		if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
			Log.d(TAG, "UART_CONNECT_MSG");
			ICSOpenVPNApplication.isConnect = true;
			IS_TEXT_SIM = false;
			isGetnullCardid = true;
			retryTime = 0;

			sendStepThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						Log.i("toBLue", "连接成功");

//						//测试代码
						SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER);
						Thread.sleep(500);
						//更新时间操作
						SendCommandToBluetooth.sendMessageToBlueTooth(getBLETime());
						Thread.sleep(500);
						//android 标记，给蓝牙设备标记是否是android设备用的
//						sendMessageToBlueTooth(ANDROID_TARGET);
						SendCommandToBluetooth.sendMessageToBlueTooth(BASIC_MESSAGE);

//						sendMessageToBlueTooth("AABBCCDDEEFF");//绑定命令
//						Thread.sleep(1000);
//						if (!isConnect) {
//							if (TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
//								sendMessageToBlueTooth("AAEEEEAA");//绑定命令
//								isConnect = true;
//							} else {
//								//更新时间操作
//								isConnect = true;
//								if (sendStepThread != null)
//									sendStepThread = null;
//								sendMessageToBlueTooth(getBLETime());
//							}
//						}
////						如果有复位命令储存在全局变量的话发送给设备
//						if (!TextUtils.isEmpty(resetOrderStr)) {
//							sendMessageToBlueTooth(resetOrderStr);
//						}
//						Thread.sleep(20000);
//						if (!isConnect && action.equals(UartService.ACTION_GATT_CONNECTED)
//								&& TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
//							sendMessageToBlueTooth("AADD00DDAA");
//							//连接|标记请出
//							isConnect = false;
//							mService.disconnect();
//						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			//五秒内不可以再次启动
			if (!CommonTools.isFastDoubleClick(1000) && !isUpgrade) {
				sendStepThread.start();
			}
		}

		if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
			isConnect = false;
			nullCardId = null;
			if (sendStepThread != null && !sendStepThread.isInterrupted())
				sendStepThread.interrupt();
			//如果保存的IMEI没有的话，那么就是在MyDevice里面，在Mydevice里面会有连接操作
			if (retryTime < 20) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "IMEI=" + TextUtils.isEmpty(utils.readString(Constant.IMEI)) + "\nisConnect=" + ICSOpenVPNApplication.isConnect);
						if (!TextUtils.isEmpty(utils.readString(Constant.IMEI)) && ICSOpenVPNApplication.isConnect) {
							if (isUpgrade) {
								return;
							}
							//多次扫描蓝牙，在华为荣耀，魅族M3 NOTE 中有的机型，会发现多次断开–扫描–断开–扫描…
							// 会扫描不到设备，此时需要在断开连接后，不能立即扫描，而是要先停止扫描后，过2秒再扫描才能扫描到设备
							CommonTools.delayTime(2000);
							mService.connect(utils.readString(Constant.IMEI));
						} else {
							Log.d(TAG, "UART_DISCONNECT_MSG");
						}
					}
				}).start();
				retryTime++;
			}
		}
		if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
			mService.enableTXNotification();
			//如果版本号小于android N
			if (Build.VERSION.SDK_INT < 24) {
				boolean isSuccess = mService.ensureServiceChangedEnabled();
				Log.i(TAG, "ensureServiceChangedEnabled:" + isSuccess);
			}
		}
		if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
			final ArrayList<String>  messages= intent.getStringArrayListExtra(UartService.EXTRA_DATA);
			if(messages.size()==0){
				return;
			}
			pool.execute(
			new Thread(new Runnable() {
				@Override
				public void run() {
//					String messageFromBlueTooth = HexStringExchangeBytesUtil.bytesToHexString(txValue);

					//通过SDK收发Service发送信息到SDK
//					Log.e("Blue_Chanl", "接收从蓝牙发出的消息：" + HexStringExchangeBytesUtil.bytesToHexString(txValue));
//					//是否第一个包，判断类型
//					int dataID = Integer.parseInt(messageFromBlueTooth.substring(2, 4) + "", 16) & 127;
//					Log.e("Blue_Chanl", "txValue[1]" + Integer.parseInt(messageFromBlueTooth.substring(2, 4) + "", 16) + "dataID：" + dataID);
//					if (dataID == 0) {
//						dataType = messageFromBlueTooth.substring(6, 10);
//					}
					String firstPackage=	messages.get(0).substring(0,2);
			String		dataType=messages.get(0).substring(6,10);

					if(messages.size()==1){
						Log.e(TAG,messages.get(0));
					}else {
						for(int i=0;i<messages.size();i++){
							Log.e(TAG,messages.get(i));
						}
					}
//					messages.add(messageFromBlueTooth);
//					int lengthData=(txValue[1]&0x7f)+1;
//					if(messages.size()<lengthData){
//						return;
//					}
//			if (isWholeDataPackage||dataStatue==0x80) {
//				isWholeDataPackage=false;
//			}

					Log.e("Blue_Chanl", "dataType：" + dataType);
					switch (firstPackage) {
						case "55":
							switch (dataType) {
								//获取步数
//								case (byte) 0x01:
//									byte[] stepBytes = new byte[2];
//									stepBytes[0] = txValue[3];
//									stepBytes[1] = txValue[4];
//									long currentTimeLong = System.currentTimeMillis() / 1000;
//									int currentStepInt = Integer.parseInt(HexStringExchangeBytesUtil.bytesToHexString(stepBytes), 16);
//									Intent realTimeStepIntent = new Intent();
//									realTimeStepIntent.putExtra(Constant.REAL_TIME_STEPS, currentStepInt);
//									realTimeStepIntent.setAction(SportFragment.REALTIMESTEP);
//									ICSOpenVPNApplication.getInstance().sendBroadcast(realTimeStepIntent);
////							saveRealTimeStep(currentTimeLong, currentStepInt);
//									break;
//								case (byte) 0x02:
//									break;
//								//获取历史步数
//								case (byte) 0x03:
//									messages.add(messageFromBlueTooth);
//									//如果历史步数到了第四行，则要输出
//									if (txValue[4] == (byte) 0x03) {
//										mStrStepHistory = PacketeUtil.CombinationForHistory(messages);
//										messages.clear();
//										//判断是哪天的步数
//										switch (txValue[3]) {
//											//今天的数据
//											case 0x00:
//												Log.i("test", "今天的步数" + mStrStepHistory + "length:" + mStrStepHistory.length());
//												ArrayList<Integer> todayList = StepStrToList(mStrStepHistory);
//												entity.setTodayList(todayList);
//												break;
//											//昨天的数据
//											case 0x01:
//												Log.i("test", "昨天的步数" + mStrStepHistory + "length:" + mStrStepHistory.length());
//												ArrayList<Integer> yesterdayList = StepStrToList(mStrStepHistory);
//												entity.setYesterdayList(yesterdayList);
//												break;
//											//前天的数据
//											case 0x02:
//												Log.i("test", "前天的步数" + mStrStepHistory + "length:" + mStrStepHistory.length());
//												ArrayList<Integer> beforeYesterdayList = StepStrToList(mStrStepHistory);
//												entity.setBeforeyesterdayList(beforeYesterdayList);
//												break;
//										}
//									}
//									//如果不是记录前三天数据，那么就要判断类型是不是0x03
//									else if (txValue[3] == 0x03) {
//										mStrStepHistory = PacketeUtil.CombinationForHistory(messages);
//										messages.clear();
//										ArrayList<Integer> sixDayList = StepStrToList(mStrStepHistory);
//										entity.setSixDayList(sixDayList);
//										//更新历史步数到UI线程
//										handler.sendEmptyMessage(UPDATE_HISTORY_DATE);
//									}
//
//									break;
								//电量多少
								case RECEIVE_ELECTRICITY:
									utils.writeInt(Constant.ELECTRICITY, Integer.parseInt(String.valueOf(messages.get(0).substring(10,12))));
									break;
//								case (byte) 0x05:
//									//充电状态
//									Log.i("test", "充电状态");
//									resetOrderStr = null;
//									if (sendStepThread != null)
//										sendStepThread = null;
//									if (!isOpenStepService) {
//										Intent updateStepIntent = new Intent(context, UpdateStepService.class);
//										context.startService(updateStepIntent);
//										isOpenStepService = true;
//									}
////							//结束BindDeviceActivity
////							Intent bindCompeleteIntent = new Intent();
////							bindCompeleteIntent.setAction(BindDeviceActivity.BIND_COMPELETE);
////							LocalBroadcastManager.getInstance(context).sendBroadcast(bindCompeleteIntent);
//									break;
//								case (byte) 0x09:
//									Log.i("test", "上一次充电时间");
//									break;
//								case (byte) 0x11:
//									if (!IS_TEXT_SIM) {
//										Intent cardBreakIntent = new Intent();
//										cardBreakIntent.setAction(MyOrderDetailActivity.CARD_RULE_BREAK);
//										LocalBroadcastManager.getInstance(context).sendBroadcast(cardBreakIntent);
//									}
//									break;
//								case (byte) 0x33:
//									//添加计时器20秒后没有回复则写卡失败
////							Timer overTimer = new Timer();
////							overTimer.schedule(new TimerTask() {
////								@Override
////								public void run() {
////									orderStatus = 4;
////									Intent intent = new Intent();
////									intent.setAction(MyOrderDetailActivity.FINISH_PROCESS);
////									ICSOpenVPNApplication.getInstance().sendBroadcast(intent);
////									repeatReceive33 = false;
////								}
////							}, 30000);
//									//当上电完成则需要发送写卡命令
//									Log.i(TAG, "上电ReceiveBLEMove返回：IS_TEXT_SIM:" + IS_TEXT_SIM + ",nullCardId=" + nullCardId);
//									if (!IS_TEXT_SIM && isGetnullCardid) {
//										//空卡ID是否不为空，若不为空则
//										if (nullCardId != null) {
//											Log.i(TAG, "nullcardid上电返回");
//										} else {
//											Log.i(TAG, "发送A0A40000023F00");
//											sendMessageSeparate("A0A40000023F00");
//										}
//									}
//									break;

//								case (byte) 0xDB:
//								case (byte) 0xDA:
//									if (IS_TEXT_SIM) {
//										if (SocketConnection.sdkAndBluetoothDataInchange != null) {
//											SocketConnection.sdkAndBluetoothDataInchange.sendToSDKAboutBluetoothInfo(messageFromBlueTooth, txValue);
//										} else {
//											Log.i(TAG,context.getString(R.string.error_to_restart_ble));
////											CommonTools.showShortToast(context, context.getString(R.string.error_to_restart_ble));
//											ICSOpenVPNApplication.uartService.disconnect();
//										}
//									} else {
//										messages.add(messageFromBlueTooth);
//										if (txValue[3] == txValue[4]) {
//											mStrSimCmdPacket = PacketeUtil.Combination(messages);
//											// 接收到一个完整的数据包,处理信息
//											ReceiveDBOperate(mStrSimCmdPacket);
//											messages.clear();
//										}
//								case (byte) 0xEE:
//									//绑定流程成功命令
//									CommonTools.delayTime(500);
//									//android 标记，给蓝牙设备标记是否是android设备用的
//									SendCommandToBluetooth.sendMessageToBlueTooth(BIND_SUCCESS);
//									CommonTools.delayTime(500);
//									//更新时间操作
////									Thread.sleep(500);
//									//android 标记，给蓝牙设备标记是否是android设备用的
//									SendCommandToBluetooth.sendMessageToBlueTooth(getBLETime());
//									isConnect = true;
//									if (sendStepThread != null)
//										sendStepThread = null;
//									break;
										case Constant.SYSTEM_BASICE_INFO:
//									if (Integer.parseInt(String.valueOf(txValue[2]), 16) < Constant.OLD_VERSION_DEVICE) {
//										Log.i(TAG,"老版本设备，修改上电命令");
//										Constant.UP_TO_POWER = "AADB040174";
//									}
									int versionFirst = Integer.parseInt(messages.get(0).substring(10,12),16);
									int versionLast = Integer.parseInt(messages.get(0).substring(12,14),16);
									Log.i(TAG, "固件版本号：" + versionFirst + "." + versionLast + "，电量：" + messages.get(0).substring(14,16));
									utils.writeString(Constant.BRACELETVERSION, versionFirst + "." + versionLast);
									utils.writeInt(Constant.ELECTRICITY, Integer.parseInt(messages.get(0).substring(14,16),16));
									break;

								case Constant.RETURN_POWER:
									if (messages.get(0).substring(10,12).equals("01")) {
										//当上电完成则需要发送写卡命令
										Log.i(TAG, "上电ReceiveBLEMove返回：IS_TEXT_SIM:" + IS_TEXT_SIM + ",nullCardId=" + nullCardId);
										if (!IS_TEXT_SIM && isGetnullCardid) {
											//空卡ID是否不为空，若不为空则
											if (nullCardId != null) {
												Log.i(TAG, "nullcardid上电返回");
											} else {
												Log.i(TAG, "发送" + Constant.WRITE_SIM_STEP_ONE);
												sendMessageSeparate(Constant.WRITE_SIM_STEP_ONE, Constant.WRITE_SIM_DATA);
											}
										}

									} else if (messages.get(0).substring(10,12).equals("11")) {
										if (!IS_TEXT_SIM) {
											Intent cardBreakIntent = new Intent();
											cardBreakIntent.setAction(MyOrderDetailActivity.CARD_RULE_BREAK);
											LocalBroadcastManager.getInstance(context).sendBroadcast(cardBreakIntent);
										}
									}
									break;
								case Constant.READ_SIM_DATA:
									Log.i(TAG, "发送给SDK");
									if (IS_TEXT_SIM) {
										SocketConnection.sdkAndBluetoothDataInchange.sendToSDKAboutBluetoothInfo(messages);
									}
									break;
								case Constant.LAST_CHARGE_POWER_TIMER:

									if ((Integer.parseInt(messages.get(0).substring(2,4),16) & 0x80) == 0x80) {
										mStrSimCmdPacket = PacketeUtil.Combination(messages);
										// 接收到一个完整的数据包,处理信息
										ReceiveDBOperate(mStrSimCmdPacket);
										messages.clear();
									}
									break;
//								case (byte) 0xDB:
//								case (byte) 0xDA:
//									if (IS_TEXT_SIM) {
//										SocketConnection.sdkAndBluetoothDataInchange.sendToSDKAboutBluetoothInfo(messageFromBlueTooth, txValue);
//									} else {
//										messages.add(messageFromBlueTooth);
//										if (txValue[3] == txValue[4]) {
//											mStrSimCmdPacket = PacketeUtil.Combination(messages);
//											// 接收到一个完整的数据包,处理信息
//											ReceiveDBOperate(mStrSimCmdPacket);
//											messages.clear();
//										}
//									}
//									break;
//						case (byte) 0xAA:
//							Log.i("toBlue", "已收到重置信息：" + messageFromBlueTooth);
//							resetOrderStr = messageFromBlueTooth;
//							break;

								default:
//							updateMessage(messageFromBlueTooth);
									break;
							}
					}
				}
			}
			));
		}
		if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
			mService.disconnect();
			mService.close();
//			 mReceiveSocketService.closeThread();
		}


	}

	private void sendMessageSeparate(final String message, String type) {
		lastSendMessageStr = message;
		String[] messages = PacketeUtil.Separate(message, type);
		int length = messages.length;
		for (int i = 0; i < length; i++) {
			SendCommandToBluetooth.sendMessageToBlueTooth(messages[i]);
		}
	}


	SharedUtils utils = SharedUtils.getInstance();
	public static boolean isGetnullCardid = false;//是否获取空卡数据
	// 上一条发送命令
	private String lastSendMessageStr = "";


	//写卡流程
	private void ReceiveDBOperate(String mStrSimCmdPacket) {
		Log.i("test", "写卡收回：" + mStrSimCmdPacket);

		switch (lastSendMessageStr) {
			case "":
				sendMessageSeparate(Constant.WRITE_SIM_STEP_ONE, Constant.WRITE_SIM_DATA);
				break;
			//获取空卡序列号第一步
			case Constant.WRITE_SIM_STEP_ONE:
				if (mStrSimCmdPacket.contains(WRITE_CARD_STEP1)) {
					if (isGetnullCardid) {
						sendMessageSeparate(Constant.WRITE_SIM_STEP_TWO, Constant.WRITE_SIM_DATA);
					} else {
						registFlowPath();
					}
				}
				break;
			//获取空卡序列号第二步
			case Constant.WRITE_SIM_STEP_TWO:
				if (mStrSimCmdPacket.contains(GET_NULLCARDID)) {
					if (isGetnullCardid)
						sendMessageSeparate(Constant.WRITE_SIM_STEP_THREE, Constant.WRITE_SIM_DATA);
				} else {
					registFlowPath();
				}
				break;
			//获取空卡序列号第三部
			case Constant.WRITE_SIM_STEP_THREE:
				if (mStrSimCmdPacket.contains(WRITE_CARD_STEP5)
						&& mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR)) {
					if (isGetnullCardid) {
						if (mStrSimCmdPacket.length() > 20) {
							mStrSimCmdPacket = mStrSimCmdPacket.substring(4, 20);
							Log.i("Bluetooth", "空卡序列号:" + mStrSimCmdPacket);
							nullCardId = mStrSimCmdPacket;
							//重新上电清空
							SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER);
							utils.writeString(Constant.NULLCARD_SERIALNUMBER, nullCardId);
							//获取完空卡序列号后获取步数
							SendCommandToBluetooth.sendMessageToBlueTooth(Constant.HISTORICAL_STEPS);
							ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
							entity.setStatus(context.getString(R.string.index_aixiaoqicard));
							entity.setStatusDrawableInt(R.drawable.index_no_signal);
							EventBus.getDefault().post(entity);
						}
					}
				} else {
					if (mStrSimCmdPacket.startsWith("9000") && !CommonTools.isFastDoubleClick(1000)) {
						//新型写卡完成
						handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
						SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
						isGetnullCardid = false;
						nullCardId = null;
						return;
					}
					registFlowPath();
				}
				break;
			default:
				if (mStrSimCmdPacket.startsWith("9000") && !CommonTools.isFastDoubleClick(1000)) {
					//新型写卡完成
					handler.sendEmptyMessage(WRITE_CARD_COMPLETE);
					SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
					isGetnullCardid = false;
					nullCardId = null;
					return;
				}
				break;
		}
	}

	private void registFlowPath() {
		Log.i("Bluetooth", "进入注册流程");
		ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
		if (SharedUtils.getInstance().readBoolean(Constant.ISHAVEORDER, false)) {
			entity.setStatus(context.getString(R.string.index_registing));
			entity.setStatusDrawableInt(R.drawable.index_no_signal);
		} else {
			entity.setStatus(context.getString(R.string.index_no_packet));
			entity.setStatusDrawableInt(R.drawable.index_no_packet);
		}
		EventBus.getDefault().post(entity);
		IS_TEXT_SIM = true;
		isGetnullCardid = false;
		SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER);
	}

	public String reverse(String str) {
		StringBuilder builder = new StringBuilder(str);
		return builder.reverse().toString();
	}


	private void activationLocalCompletedHttp() {
		ActivationLocalCompletedHttp http = new ActivationLocalCompletedHttp(this, COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED, MyOrderDetailActivity.OrderID);
		new Thread(http).start();
	}

	//更新历史数据
	private void updateHistoryDate() {
		HistoryStepHttp http = new HistoryStepHttp(this, HttpConfigUrl.COMTYPE_SPORT_REPORT_HISTORY_STEP, entity);
		new Thread(http).start();
	}

	private ArrayList<Integer> StepStrToList(String mStrStepHistory) {
		ArrayList<Integer> dayList = new ArrayList<>();
		String arrayStr = "";
		int length = mStrStepHistory.length();
		for (int i = 0; i < length; i++) {
			arrayStr += mStrStepHistory.substring(i, i + 1);
			if (arrayStr.length() == 4) {
				dayList.add(Integer.parseInt(arrayStr, 16));
				arrayStr = "";
			}
		}
		return dayList;
	}


//	//上传到服务器
//	private void updateRealTimeStep(long currentTimeLong, int currentStepInt) {
//		ReportRealtimeStepHttp http = new ReportRealtimeStepHttp(this, HttpConfigUrl.COMTYPE_SPORT_REPORT_REALTIME_STEP, currentStepInt, currentTimeLong);
//		new Thread(http).start();
//	}
//
//	private void updateMessage(final String finalMessage) {
//
//	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_SPORT_REPORT_HISTORY_STEP) {
			HistoryStepHttp http = (HistoryStepHttp) object;
			if (http.getStatus() == 1) {
				Log.i("test", "上传成功");
				Intent intent = new Intent();
				intent.setAction(SportFragment.REFRESHSTEP);
				ICSOpenVPNApplication.getInstance().sendBroadcast(intent);
			} else {
				Log.i("test", "上传失败");
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED) {
			if (object.getStatus() == 1) {
				HashMap<String, String> map = new HashMap<>();
				map.put("statue", 1 + "");
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKACTIVECARD, map);
				CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), "激活成功！");
				orderStatus = 1;
				Intent intent = new Intent();
				intent.setAction(ActivateActivity.FINISH_ACTIVITY);
				intent.setAction(MyOrderDetailActivity.FINISH_PROCESS);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);
			} else {
				CommonTools.showShortToast(ICSOpenVPNApplication.getContext()
						, object.getMsg());
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		try {
			CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), errorMessage);
			Log.i("test", "http.getMsg:" + errorMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void noNet() {
		try {
			CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), context.getResources().getString(R.string.no_wifi));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getBLETime() {
		String bleTime = "";
		Calendar calendar = Calendar.getInstance();
		//年
		int year = calendar.get(Calendar.YEAR);
		year = year - 2000;
		//月
		int mouth = calendar.get(Calendar.MONTH);
		mouth++;
		//日
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		//周
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		if (week == 1) {
			week = 7;
		} else {
			week--;
		}
		//时
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		//分
		int minute = calendar.get(Calendar.MINUTE);
		//秒
		int second = calendar.get(Calendar.SECOND);


		bleTime = "8880090500" + addZero(toHex(year)) + addZero(toHex(mouth)) + addZero(toHex(day))
				+ addZero(toHex(hour)) + addZero(toHex(minute)) + addZero(toHex(second)) + addZero("" + week);
//		byte[] check = HexStringExchangeBytesUtil.hexStringToBytes(bleTime);
//		String checkBleStr = HexStringExchangeBytesUtil.bytesToHexString(new byte[]{BLECheckBitUtil.getXor(check)});
//		bleTime += addZero(checkBleStr);
		return bleTime;
	}

	private String toHex(int num) {
		return Integer.toHexString(num);
	}

	//为发送的数据添加0，如果小于15
	private String addZero(String date) {
		date = date.toUpperCase();
		if (date.equals("A") || date.equals("B") || date.equals("C")
				|| date.equals("D") || date.equals("E") || date.equals("F")
				|| date.equals("0") || date.equals("1") || date.equals("2")
				|| date.equals("3") || date.equals("4") || date.equals("5") ||
				date.equals("6") || date.equals("7") || date.equals("8") || date.equals("9")) {
			return "0" + date;
		} else {
			return "" + date;
		}
	}

}
