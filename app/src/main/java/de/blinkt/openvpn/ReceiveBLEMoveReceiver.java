package de.blinkt.openvpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.aixiaoqi.socket.SocketConnection;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.ActivateActivity;
import de.blinkt.openvpn.activities.BindDeviceActivity;
import de.blinkt.openvpn.activities.MyOrderDetailActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.ActivationLocalCompletedHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.HistoryStepHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.ReportRealtimeStepHttp;
import de.blinkt.openvpn.model.SportStepEntity;
import de.blinkt.openvpn.service.UpdateStepService;
import de.blinkt.openvpn.util.BLECheckBitUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.Constant.FIND_VERSION;
import static de.blinkt.openvpn.constant.Constant.GET_NULLCARDID;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.IS_WRITE_CARD_SUCCESS;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR;
import static de.blinkt.openvpn.constant.Constant.RESTORATION;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.UP_TP_POWER_RECEIVE;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_91;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP1;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP11;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP4;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP5;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP7;
import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;

/**
 * Created by Administrator on 2016/10/5.
 */

public class ReceiveBLEMoveReceiver extends BroadcastReceiver implements InterfaceCallback {

	private UartService mService = null;
	private static final int UART_PROFILE_CONNECTED = 20;
	private static final int UART_PROFILE_DISCONNECTED = 21;
	private int mState = UART_PROFILE_DISCONNECTED;
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
	private boolean repeatReceive33 = false;

	public void onReceive(final Context context, Intent intent) {
		final String action = intent.getAction();
		mService = ICSOpenVPNApplication.uartService;
		if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
			Log.d(TAG, "UART_CONNECT_MSG");
			mState = UART_PROFILE_CONNECTED;
			ICSOpenVPNApplication.isConnect = true;

			sendStepThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Log.i("toBLue", "连接成功");
						Thread.sleep(1000);

						//测试用后删除//结束BindDeviceActivity
						Intent bindCompeleteIntent = new Intent();
						bindCompeleteIntent.setAction(BindDeviceActivity.BIND_COMPELETE);
						LocalBroadcastManager.getInstance(context).sendBroadcast(bindCompeleteIntent);
						if (!CommonTools.isFastDoubleClick(3000)) {
							sendMessageToBlueTooth(FIND_VERSION);
						}
						Thread.sleep(500);
						//测试代码
//						sendMessageToBlueTooth(UP_TO_POWER);

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
			sendStepThread.start();
		}

		if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
			isConnect = false;
			mState = UART_PROFILE_DISCONNECTED;
			if (sendStepThread != null && !sendStepThread.isInterrupted())
				sendStepThread.interrupt();
			//如果保存的IMEI没有的话，那么就是在MyDevice里面，在Mydevice里面会有连接操作
			if (!TextUtils.isEmpty(utils.readString(Constant.IMEI)) && ICSOpenVPNApplication.isConnect) {
				//多次扫描蓝牙，在华为荣耀，魅族M3 NOTE 中有的机型，会发现多次断开–扫描–断开–扫描…
				// 会扫描不到设备，此时需要在断开连接后，不能立即扫描，而是要先停止扫描后，过2秒再扫描才能扫描到设备
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mService.connect(utils.readString(Constant.IMEI));
			} else {
				Log.d(TAG, "UART_DISCONNECT_MSG");
				mService.close();
			}
		}
		if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
			mService.enableTXNotification();
		}
		if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
			final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
			String messageFromBlueTooth = HexStringExchangeBytesUtil.bytesToHexString(txValue);

			//通过SDK收发Service发送信息到SDK
			Log.i("Bluetooth", "Text:" + HexStringExchangeBytesUtil.bytesToHexString(txValue));
			//判断是否是分包（BB开头的包）
			if (txValue[0] != (byte) 0xBB && txValue[0] != (byte) 0xAA) {
				return;
			}
			switch (txValue[0]) {
				case (byte) 0xBB:
					switch (txValue[1]) {
						//获取步数
						case (byte) 0x01:
							byte[] stepBytes = new byte[2];
							stepBytes[0] = txValue[3];
							stepBytes[1] = txValue[4];
							long currentTimeLong = System.currentTimeMillis() / 1000;
							int currentStepInt = Integer.parseInt(HexStringExchangeBytesUtil.bytesToHexString(stepBytes), 16);
							Intent realTimeStepIntent = new Intent();
							realTimeStepIntent.putExtra(Constant.REAL_TIME_STEPS, currentStepInt);
							realTimeStepIntent.setAction(SportFragment.REALTIMESTEP);
							ICSOpenVPNApplication.getInstance().sendBroadcast(realTimeStepIntent);
//							saveRealTimeStep(currentTimeLong, currentStepInt);
							break;
						case (byte) 0x02:
							break;
						//获取历史步数
						case (byte) 0x03:
							messages.add(messageFromBlueTooth);
							//如果历史步数到了第四行，则要输出
							if (txValue[4] == (byte) 0x03) {
								mStrStepHistory = PacketeUtil.CombinationForHistory(messages);
								messages.clear();
								//判断是哪天的步数
								switch (txValue[3]) {
									//今天的数据
									case 0x00:
										Log.i("test", "今天的步数" + mStrStepHistory + "length:" + mStrStepHistory.length());
										ArrayList<Integer> todayList = StepStrToList(mStrStepHistory);
										entity.setTodayList(todayList);
										break;
									//昨天的数据
									case 0x01:
										Log.i("test", "昨天的步数" + mStrStepHistory + "length:" + mStrStepHistory.length());
										ArrayList<Integer> yesterdayList = StepStrToList(mStrStepHistory);
										entity.setYesterdayList(yesterdayList);
										break;
									//前天的数据
									case 0x02:
										Log.i("test", "前天的步数" + mStrStepHistory + "length:" + mStrStepHistory.length());
										ArrayList<Integer> beforeYesterdayList = StepStrToList(mStrStepHistory);
										entity.setBeforeyesterdayList(beforeYesterdayList);
										break;
								}
							}
							//如果不是记录前三天数据，那么就要判断类型是不是0x03
							else if (txValue[3] == 0x03) {
								mStrStepHistory = PacketeUtil.CombinationForHistory(messages);
								messages.clear();
								ArrayList<Integer> sixDayList = StepStrToList(mStrStepHistory);
								entity.setSixDayList(sixDayList);
								updateHistoryDate();

								//是否设备内sim卡
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										//上电测试是否SIM卡插对
										sendMessageToBlueTooth(UP_TO_POWER);
									}
								}, 1000);
							}

							break;
						//电量多少
						case (byte) 0x04:
							utils.writeInt(Constant.ELECTRICITY, Integer.parseInt(String.valueOf(txValue[3])));
							break;
						case (byte) 0x05:
							//充电状态
							Log.i("test", "充电状态");
							resetOrderStr = null;
							if (sendStepThread != null)
								sendStepThread = null;
							if (!CommonTools.isFastDoubleClick(3000)) {
								sendMessageToBlueTooth(FIND_VERSION);
							}
							if (!isOpenStepService) {
								Intent updateStepIntent = new Intent(context, UpdateStepService.class);
								context.startService(updateStepIntent);
								isOpenStepService = true;
							}
//							//结束BindDeviceActivity
//							Intent bindCompeleteIntent = new Intent();
//							bindCompeleteIntent.setAction(BindDeviceActivity.BIND_COMPELETE);
//							LocalBroadcastManager.getInstance(context).sendBroadcast(bindCompeleteIntent);
							break;
						case (byte) 0x09:
							Log.i("test", "上一次充电时间");
							break;
						case (byte) 0x11:
							if (!IS_TEXT_SIM) {
								Intent cardBreakIntent = new Intent();
								cardBreakIntent.setAction(MyOrderDetailActivity.CARD_RULE_BREAK);
								LocalBroadcastManager.getInstance(context).sendBroadcast(cardBreakIntent);
							}
							//复位操作
							sendMessageSeparate(RESTORATION);
							break;
						case (byte) 0x33:
							//添加计时器20秒后没有回复则写卡失败
//							Timer overTimer = new Timer();
//							overTimer.schedule(new TimerTask() {
//								@Override
//								public void run() {
//									orderStatus = 4;
//									Intent intent = new Intent();
//									intent.setAction(MyOrderDetailActivity.FINISH_PROCESS);
//									ICSOpenVPNApplication.getInstance().sendBroadcast(intent);
//									repeatReceive33 = false;
//								}
//							}, 30000);
							if (!repeatReceive33) {
								//当上电完成则需要发送写卡命令
								if (!IS_TEXT_SIM) {
									sendMessageSeparate("A0A40000023F00");
									repeatReceive33 = true;
								}
							}
							break;
						case (byte) 0xDB:
						case (byte) 0xDA:
							if (IS_TEXT_SIM) {
								SocketConnection.sdkAndBluetoothDataInchange.sendToSDKAboutBluetoothInfo(messageFromBlueTooth, txValue);
							} else {
								messages.add(messageFromBlueTooth);
								if (txValue[3] == txValue[4]) {
									mStrSimCmdPacket = PacketeUtil.Combination(messages);
									// 接收到一个完整的数据包,处理信息
									ReceiveDBOperate(mStrSimCmdPacket);
									messages.clear();
								}
							}
							break;
						case (byte) 0xEE:
							if (!isOpenStepService) {
								Intent updateStepIntent = new Intent(context, UpdateStepService.class);
								context.startService(updateStepIntent);
								isOpenStepService = true;
							}
							try {
								sendMessageToBlueTooth("AADD01DDAA");
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							//更新时间操作
							sendMessageToBlueTooth(getBLETime());
							isConnect = true;
							if (sendStepThread != null)
								sendStepThread = null;
							break;
					}
					break;
				case (byte) 0xAA:
					Log.i("toBlue", "已收到重置信息：" + messageFromBlueTooth);
					resetOrderStr = messageFromBlueTooth;
					break;

				default:
					updateMessage(messageFromBlueTooth);
					break;
			}
		}
		if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
			mService.disconnect();
			mService.close();
//			 mReceiveSocketService.closeThread();
		}


	}

	private void sendMessageSeparate(final String message) {
		String[] messages = PacketeUtil.Separate(message);
		int length = messages.length;
		for (int i = 0; i < length; i++) {
			sendMessageToBlueTooth(messages[i]);
		}
	}

	private void sendMessageToBlueTooth(final String message) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		byte[] value;
		Log.i("toBLue", message);
		value = HexStringExchangeBytesUtil.hexStringToBytes(message);
		if (mService != null) {
			if (mService.mConnectionState == UartService.STATE_CONNECTED) {
				mService.writeRXCharacteristic(value);
			}
		}
	}

	SharedUtils utils = SharedUtils.getInstance();
	public static boolean isGetnullCardid = false;//是否获取空卡数据

	//写卡流程
	private void ReceiveDBOperate(String mStrSimCmdPacket) {
		Log.i("test", "写卡收回：" + mStrSimCmdPacket);
//		if (TextUtils.isEmpty(utils.readString(Constant.WRITE_CARD_ID))) {
//			CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), "写卡失败，没有写卡ID");
//			repeatReceive33 = false;
//		}
		if (mStrSimCmdPacket.contains(WRITE_CARD_STEP1)) {
			if (isGetnullCardid) {
				sendMessageSeparate("A0A40000022F02");
			}

		} else if (mStrSimCmdPacket.startsWith(WRITE_CARD_91)) {
			String lastTwoBytesStr = mStrSimCmdPacket.substring(2, 4);
			sendMessageSeparate("A0120000" + lastTwoBytesStr);
		} else if (mStrSimCmdPacket.contains(WRITE_CARD_STEP4)) {
			sendMessageSeparate("A01400000C810301250082028281830100");
		} else if (mStrSimCmdPacket.contains(GET_NULLCARDID)) {
			if (isGetnullCardid)
				sendMessageSeparate("A0B000000A");

		} else if (mStrSimCmdPacket.contains(WRITE_CARD_STEP7)) {
			sendMessageSeparate("A01400000C810301130082028281830100");
		} else if (mStrSimCmdPacket.contains(WRITE_CARD_STEP11)) {
			activationLocalCompletedHttp();
		} else if (mStrSimCmdPacket.contains("9000") && mStrSimCmdPacket.contains(IS_WRITE_CARD_SUCCESS)) {
			if (isGetnullCardid) {
				//新型写卡完成
				activationLocalCompletedHttp();
				sendMessageToBlueTooth(OFF_TO_POWER);//对卡下电
				isGetnullCardid = false;
				return;
			}
		} else if (mStrSimCmdPacket.contains(UP_TP_POWER_RECEIVE)) {
			//当上电完成则需要发送写卡命令
			Log.i("receiUptoPower", "收到上电命令");
		} else if (mStrSimCmdPacket.contains(WRITE_CARD_STEP5)
				&& mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR)) {
			if (isGetnullCardid) {
				if (mStrSimCmdPacket.length() > 20) {
					repeatReceive33 = false;
					mStrSimCmdPacket = mStrSimCmdPacket.substring(4, 20);
					Log.i("Bluetooth", "空卡序列号:" + mStrSimCmdPacket);
					Intent intent = new Intent();
					intent.putExtra("nullcardNumber", mStrSimCmdPacket);
					intent.setAction(MyOrderDetailActivity.FIND_NULL_CARD_ID);
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				}
			}
		}
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

	private void saveRealTimeStep(long currentTimeLong, int currentStepInt) {
		updateRealTimeStep(currentTimeLong, currentStepInt);
	}


	//上传到服务器
	private void updateRealTimeStep(long currentTimeLong, int currentStepInt) {
		ReportRealtimeStepHttp http = new ReportRealtimeStepHttp(this, HttpConfigUrl.COMTYPE_SPORT_REPORT_REALTIME_STEP, currentStepInt, currentTimeLong);
		new Thread(http).start();
	}

	private void updateMessage(final String finalMessage) {

	}

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
				repeatReceive33 = false;
				orderStatus = 1;
				Intent intent = new Intent();
				intent.setAction(ActivateActivity.FINISH_ACTIVITY);
				intent.setAction(MyOrderDetailActivity.FINISH_PROCESS);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			} else {
				CommonTools.showShortToast(ICSOpenVPNApplication.getContext()
						, object.getMsg());
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), errorMessage);
		Log.i("test", "http.getMsg:" + errorMessage);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), context.getResources().getString(R.string.no_wifi));
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


		bleTime = "AA020A" + addZero(toHex(year)) + addZero(toHex(mouth)) + addZero(toHex(day))
				+ addZero("" + week) + addZero(toHex(hour)) + addZero(toHex(minute)) + addZero(toHex(second));
		byte[] check = HexStringExchangeBytesUtil.hexStringToBytes(bleTime);
		String checkBleStr = HexStringExchangeBytesUtil.bytesToHexString(new byte[]{BLECheckBitUtil.getXor(check)});
		bleTime += addZero(checkBleStr);
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
