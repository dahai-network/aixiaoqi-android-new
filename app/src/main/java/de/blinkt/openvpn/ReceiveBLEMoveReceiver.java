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

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.RadixAsciiChange;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.ActivateActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetDeviceSimRegStatuesHttp;
import de.blinkt.openvpn.http.HistoryStepHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
import de.blinkt.openvpn.model.SportStepEntity;
import de.blinkt.openvpn.model.StartRegistEntity;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.EncryptionUtil;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static de.blinkt.openvpn.activities.ActivateActivity.FINISH_ACTIVITY;
import static de.blinkt.openvpn.activities.MyDeviceActivity.isUpgrade;
import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.AGREE_BIND;
import static de.blinkt.openvpn.constant.Constant.APP_CONNECT;
import static de.blinkt.openvpn.constant.Constant.BASIC_MESSAGE;
import static de.blinkt.openvpn.constant.Constant.BIND_DEVICE;
import static de.blinkt.openvpn.constant.Constant.BIND_FAIL;
import static de.blinkt.openvpn.constant.Constant.BIND_SUCCESS;
import static de.blinkt.openvpn.constant.Constant.GET_NULLCARDID;
import static de.blinkt.openvpn.constant.Constant.ICCID_GET;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_CARD_MSG;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_ELECTRICITY;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR;
import static de.blinkt.openvpn.constant.Constant.RECEIVE_NULL_CARD_CHAR2;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER_NO_RESPONSE;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP1;
import static de.blinkt.openvpn.constant.Constant.WRITE_CARD_STEP5;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;
import static de.blinkt.openvpn.util.CommonTools.getBLETime;

/**
 * Created by Administrator on 2016/10/5.
 */

public class ReceiveBLEMoveReceiver extends BroadcastReceiver implements InterfaceCallback, DialogInterfaceTypeBase {

	private UartService mService = null;
	private Context context;
	private String TAG = "ReceiveBLEMoveReceiver";
	private String mStrSimCmdPacket;
	private String mStrStepHistory;
	private SportStepEntity entity = new SportStepEntity();
	//分包存储ArrayList
//	private ArrayList<String> messages = new ArrayList<>();
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
	private int CHECK_SIGNAL = 3;
	//重连次数
	public static int retryTime;
	//	private String dataType;//发出数据以后需要把dataType重置为-1；
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == UPDATE_HISTORY_DATE) {
				//更新历史步数
				updateHistoryDate();
			} else if (msg.what == WRITE_CARD_COMPLETE) {
				activationLocalCompletedHttp();
			} else if (msg.what == CHECK_SIGNAL) {
				getDeviceSimRegStatues();
			}
		}
	};

	public void onReceive(final Context context, Intent intent) {
		this.context = context;
		final String action = intent.getAction();
		mService = ICSOpenVPNApplication.uartService;
		if (action.equals(UartService.FINDED_SERVICE)) {
			Log.d(TAG, "UART_CONNECT_MSG");
			IS_TEXT_SIM = false;

			sendStepThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
						//8880021400
						sendMessageToBlueTooth(APP_CONNECT + EncryptionUtil.random8Number());//APP专属命令
//						sendMessageToBlueTooth(APP_CONNECT);//APP专属命令
						Log.i(TAG, "发送了专属命令");
						String braceletname = utils.readString(Constant.BRACELETNAME);
						if (!BluetoothConstant.IS_BIND && braceletname != null && braceletname.contains(Constant.UNIBOX)) {
//							CommonTools.delayTime(100);
//							sendMessageToBlueTooth(BIND_DEVICE);//绑定命令
//							BluetoothMessageCallBackEntity entity = new BluetoothMessageCallBackEntity();
//							entity.setBlueType(BluetoothConstant.BLUE_BIND);
//							EventBus.getDefault().post(entity);
						} else {
							Thread.sleep(200);
							sendMessageToBlueTooth(ICCID_GET);
							Thread.sleep(200);
							sendMessageToBlueTooth(BASIC_MESSAGE);
							Log.i("toBLue", "连接成功");
							//更新时间操作
							sendMessageToBlueTooth(getBLETime());
//							CommonTools.delayTime(500);
//							sendMessageToBlueTooth(UP_TO_POWER);
						}

						Thread.sleep(20000);
						if (!isConnect && action.equals(UartService.ACTION_GATT_CONNECTED)
								&& TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
							sendMessageToBlueTooth(BIND_FAIL);
							//连接|标记请出
							isConnect = false;
							mService.disconnect();
							BluetoothMessageCallBackEntity entity = new BluetoothMessageCallBackEntity();
							entity.setBlueType(BluetoothConstant.BLUE_BIND_SUCCESS);
							entity.setSuccess(false);
							EventBus.getDefault().post(entity);
						}
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
			if (retryTime < 20 && ICSOpenVPNApplication.isConnect) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Log.d(TAG, "IMEI=" + TextUtils.isEmpty(utils.readString(Constant.IMEI)) + "\nisConnect=" + ICSOpenVPNApplication.isConnect);
						if (!TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
							if (isUpgrade) {
								return;
							}
							//多次扫描蓝牙，在华为荣耀，魅族M3 NOTE 中有的机型，会发现多次断开–扫描–断开–扫描…
							// 会扫描不到设备，此时需要在断开连接后，不能立即扫描，而是要先停止扫描后，过2秒再扫描才能扫描到设备
							mService.connect(utils.readString(Constant.IMEI));
						} else {
							Log.d(TAG, "UART_DISCONNECT_MSG");
						}
					}
				}).start();
				retryTime++;
			}
		}

		if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
			ICSOpenVPNApplication.isConnect = true;
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
			final ArrayList<String> messages = intent.getStringArrayListExtra(UartService.EXTRA_DATA);
			if (messages.size() == 0) {
				return;
			}
			retryTime = 0;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {

						Log.d(TAG, "run: 接受数据");
						String firstPackage = messages.get(0).substring(0, 2);
						String dataType = messages.get(0).substring(6, 10);

						if (messages.size() == 1) {
							Log.e(TAG, messages.get(0));
						} else {
							for (int i = 0; i < messages.size(); i++) {
								Log.e(TAG, messages.get(i));
							}
						}
						Log.e("Blue_Chanl", "dataType：" + dataType);
						switch (firstPackage) {
							case "55":
								switch (dataType) {
									//电量多少
									case RECEIVE_ELECTRICITY:
										utils.writeInt(Constant.BRACELETPOWER, Integer.parseInt(messages.get(0).substring(10, 12), 16));
										break;

									case AGREE_BIND:
										//绑定流程成功命令
										CommonTools.delayTime(500);
										//android 标记，给蓝牙设备标记是否是android设备用的
										SendCommandToBluetooth.sendMessageToBlueTooth(BIND_SUCCESS);
										isConnect = true;
										if (sendStepThread != null)
											sendStepThread = null;
										BluetoothMessageCallBackEntity entity = new BluetoothMessageCallBackEntity();
										entity.setBlueType(BluetoothConstant.BLUE_BIND_SUCCESS);
										entity.setSuccess(true);
										EventBus.getDefault().post(entity);
										break;
									//基本信息获取
									case Constant.SYSTEM_BASICE_INFO:
										String deviceVesion = Integer.parseInt(messages.get(0).substring(10, 12), 16) + "." + Integer.parseInt(messages.get(0).substring(12, 14), 16);
										Log.i(TAG, "版本号:" + deviceVesion);
										int DeviceType = 1;
										String braceletname = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
										if (!TextUtils.isEmpty(braceletname)) {
											if (braceletname.contains(MyDeviceActivity.UNITOYS)) {
												DeviceType = 0;
											} else {
												DeviceType = 1;
											}
										}

										SharedUtils.getInstance().writeInt(Constant.BRACELETTYPEINT, DeviceType);
										SharedUtils.getInstance().writeInt(Constant.BRACELETPOWER, Integer.parseInt(messages.get(0).substring(14, 16), 16));
										SharedUtils.getInstance().writeString(Constant.BRACELETVERSION, deviceVesion);
										//如果本地保存的版本号与设备中的版本号不一致则更新版本号
										if(!SharedUtils.getInstance().readString(SharedUtils.getInstance().readString(Constant.IMEI)).equals(deviceVesion)){
											updateDeviceInfo();
												}

										break;

									case Constant.RETURN_POWER:
										if (messages.get(0).substring(10, 12).equals("03")) {
											//当上电完成则需要发送写卡命令
											Log.i(TAG, "上电ReceiveBLEMove返回：IS_TEXT_SIM:" + IS_TEXT_SIM + ",nullCardId=" + nullCardId);
											if (!IS_TEXT_SIM && isGetnullCardid) {
												//空卡ID是否不为空，若不为空则
												if (nullCardId != null) {
													Log.i(TAG, "nullcardid上电返回");
													//发送旧卡空卡序列号
													WriteCardEntity writeCardEntity = new WriteCardEntity();
													writeCardEntity.setNullCardId(nullCardId);
													EventBus.getDefault().post(writeCardEntity);
												} else {
													Log.i(TAG, "发送" + Constant.WRITE_SIM_FIRST);
													sendMessageSeparate(Constant.WRITE_SIM_FIRST, Constant.WRITE_SIM_DATA);
												}
											} else {
												if (nullCardId != null) {
													//发送旧卡空卡序列号
													WriteCardEntity writeCardEntity = new WriteCardEntity();
													writeCardEntity.setNullCardId(nullCardId);
													EventBus.getDefault().post(writeCardEntity);
												}
											}

										} else if (messages.get(0).substring(10, 12).equals("13")) {
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
											ProMainActivity.sdkAndBluetoothDataInchange.sendToSDKAboutBluetoothInfo(messages);
										}
										break;
									case RECEIVE_CARD_MSG:
//										if ((Integer.parseInt(messages.get(0).substring(2, 4), 16) & 0x80) == 0x80) {
										mStrSimCmdPacket = PacketeUtil.Combination(messages);
										// 接收到一个完整的数据包,处理信息
										ReceiveDBOperate(mStrSimCmdPacket);
										messages.clear();
//										}
										break;
									case Constant.IS_INSERT_CARD:
										Log.i(TAG, "接收数据：是否插卡：" + messages.toString());
										if (messages.get(0).substring(10, 12).equals("00")) {
											Log.i(TAG, "未插卡");
											EventBusUtil.changeConnectStatus(context
													.getString(R.string.index_un_insert_card), R.drawable.index_uninsert_card);
											//未插卡（需要修改：由于没有获取ICCID无法判断所以日后需要修改，暂时这样写）
											SocketConstant.REGISTER_STATUE_CODE = 0;
											//保证程序正常所以要下电
											sendMessageToBlueTooth(OFF_TO_POWER);
											//恢复测试写卡流程
											IS_TEXT_SIM = false;
										} else if (messages.get(0).substring(10, 12).equals("04")) {

										} else {
											Log.i(TAG, "已插卡");
											SocketConstant.SIM_TYPE = Integer.parseInt(messages.get(0).substring(12, 14));
											nullCardId = null;

											switch (messages.get(0).substring(12, 14)) {
												//有卡并且上电失败，可能是无效卡/卡未插好/设备异常
												case "00":
													break;
												case "01":
													Log.i(TAG, "移动卡！");
													SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_MOBILE);
													//卡类型是运营商则开始注册
													registFlowPath();
													break;
												case "02":
													Log.i(TAG, "联通卡！");
													SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_UNICOM);
													//卡类型是运营商则开始注册
													registFlowPath();
													break;
												case "03":
													Log.i(TAG, "电信卡！");
													SharedUtils.getInstance().writeString(Constant.OPERATER, Constant.CHINA_TELECOM);
													//卡类型是运营商则开始注册
													registFlowPath();
													break;
												case "04":
													Log.i(TAG, "爱小器卡！");
													SharedUtils.getInstance().delete(Constant.OPERATER);
													EventBusUtil.changeConnectStatus(context.getString(R.string.index_aixiaoqicard), R.drawable.index_no_signal);
													break;
											}

											if (SocketConstant.REGISTER_STATUE_CODE != 0) {
												SocketConstant.REGISTER_STATUE_CODE = 1;
											}

											//TODO 处理异常
											//如没有没插卡检测插卡并且提示用户重启手环。
											//如果网络请求失败或者无套餐，刷新则从请求网络开始。如果上电不成功，读不到手环数据，还没有获取到预读取数据或者获取预读取数据错误，则重新开始注册。
											//如果是注册到GOIP的时候失败了，则从创建连接重新开始注册

											if (SocketConstant.REGISTER_STATUE_CODE == 1 || SocketConstant.REGISTER_STATUE_CODE == 0) {
												Thread.sleep(500);
//												SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER);
											} else if (SocketConstant.REGISTER_STATUE_CODE == 2) {
												if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
													//从预读取数据那里重新注册
													connectGoip();
												} else {
													EventBusUtil.simRegisterStatue(SocketConstant.RESTART_TCP);
												}

											} else if (SocketConstant.REGISTER_STATUE_CODE == 3) {
												//请求服务器，当卡在线的时候，不进行任何操作。当卡不在线的时候，重新从预读取数据注册
												handler.sendEmptyMessage(CHECK_SIGNAL);
											}
										}
										break;
									case Constant.ICCID_BLUE_VALUE:
										String Iccid = PacketeUtil.Combination(messages);
										Log.e("ICCID_BLUE_VALUE", Iccid);
										SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6] = RadixAsciiChange.convertStringToHex(Iccid);
										Log.e("ICCID_BLUE_VALUE111111", SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6]);
										break;
									case Constant.APP_CONNECT_RECEIVE:
										Log.i("Encryption", "返回加密数据：" + messages.get(0).toString());
										if (!EncryptionUtil.isPassEncrypt(messages.get(0).toString().substring(10))) {
											mService.disconnect();
											CommonTools.showShortToast(context, context.getString(R.string.legitimate_tips));
										} else {
											if (!BluetoothConstant.IS_BIND) {
												BluetoothMessageCallBackEntity bEntity = new BluetoothMessageCallBackEntity();
												bEntity.setBlueType(BluetoothConstant.BLUE_BIND);
												EventBus.getDefault().post(bEntity);
												sendMessageToBlueTooth(BIND_DEVICE);//绑定命令
											}
										}
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
		}
		if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
			mService.disconnect();
			mService.close();
//			 mReceiveSocketService.closeThread();
		}


	}


	private void sendMessageSeparate(final String message, final String type) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				lastSendMessageStr = message;
				String[] messages = PacketeUtil.Separate(message, type);
				int length = messages.length;
				for (int i = 0; i < length; i++) {
					sendMessageToBlueTooth(messages[i]);
				}
			}
		}).start();
	}


	SharedUtils utils = SharedUtils.getInstance();
	public static boolean isGetnullCardid = false;//是否获取空卡数据
	// 上一条发送命令
	public static String lastSendMessageStr = "";


	//写卡流程
	private void ReceiveDBOperate(String mStrSimCmdPacket) {
		Log.i("test", "写卡收回：" + mStrSimCmdPacket);

		if (lastSendMessageStr.contains(Constant.WRITE_NEW_SIM_STEP_A012)) {
			lastSendMessageStr = Constant.WRITE_NEW_SIM_STEP_A012;
		} else if (lastSendMessageStr.contains(Constant.WRITE_NEW_SIM_STEP_7)) {
			lastSendMessageStr = Constant.WRITE_NEW_SIM_STEP_7;
		}
		switch (lastSendMessageStr) {
			//获取空卡序列号第一步/新卡写卡第一步
			case Constant.WRITE_SIM_FIRST:
				Log.i("Bluetooth", "进入获取空卡序列号第一步:" + mStrSimCmdPacket);
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
				Log.i("Bluetooth", "进入获取空卡序列号第二步:" + mStrSimCmdPacket);
				if (mStrSimCmdPacket.contains(GET_NULLCARDID)) {
					if (isGetnullCardid)
						sendMessageSeparate(Constant.WRITE_SIM_STEP_THREE, Constant.WRITE_SIM_DATA);
				} else {
					registFlowPath();
				}
				break;
			//获取空卡序列号第三部
			case Constant.WRITE_SIM_STEP_THREE:
				Log.i("Bluetooth", "进入获取空卡序列号第三步:" + mStrSimCmdPacket);
				if (mStrSimCmdPacket.contains(WRITE_CARD_STEP5)
						&& (mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR)
						|| mStrSimCmdPacket.contains(RECEIVE_NULL_CARD_CHAR2))) {
					if (isGetnullCardid) {
						if (mStrSimCmdPacket.length() > 20) {
							mStrSimCmdPacket = mStrSimCmdPacket.substring(4, 20);
							Log.i("Bluetooth", "空卡序列号:" + mStrSimCmdPacket);
							nullCardId = mStrSimCmdPacket;
							//重新上电清空
//							sendMessageToBlueTooth(UP_TO_POWER);
							if (Integer.valueOf(nullCardId.substring(8, 16)) >= 301) {
								Log.i(TAG, "这是新卡");
								utils.writeBoolean(Constant.IS_NEW_SIM_CARD, true);
							} else {
								Log.i(TAG, "这是旧卡");
								utils.writeBoolean(Constant.IS_NEW_SIM_CARD, false);
							}
							isGetnullCardid = false;
							lastSendMessageStr = "";
							//发送空卡序列号
							WriteCardEntity entity = new WriteCardEntity();
							entity.setNullCardId(nullCardId);
							EventBus.getDefault().post(entity);
							//获取完空卡序列号后获取步数
							sendMessageToBlueTooth(Constant.HISTORICAL_STEPS);
							EventBusUtil.changeConnectStatus(context.getString(R.string.index_aixiaoqicard), R.drawable.index_no_signal);
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
					} else {
						registFlowPath();
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
					sendMessageSeparate(Constant.WRITE_NEW_SIM_STEP_7 + ICSOpenVPNApplication.cardData, Constant.WRITE_SIM_DATA);
				} else if (mStrSimCmdPacket.contains(Constant.WRITE_NEW_CARD_STEP8)) {
					//新卡写卡完成
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


	private void registFlowPath() {
		Log.i("Bluetooth", "进入注册流程");

		if (SharedUtils.getInstance().readBoolean(Constant.ISHAVEORDER, false)) {
			EventBusUtil.changeConnectStatus(context.getString(R.string.index_registing), R.drawable.index_no_signal);
		} else {
			EventBusUtil.changeConnectStatus(context.getString(R.string.index_no_packet), R.drawable.index_no_packet);
		}

		IS_TEXT_SIM = true;
		isGetnullCardid = false;
		StartRegistEntity startRegistEntity = new StartRegistEntity();
		startRegistEntity.setRegist(true);
		EventBus.getDefault().post(startRegistEntity);
//		sendMessageToBlueTooth(UP_TO_POWER);
	}

	public String reverse(String str) {
		StringBuilder builder = new StringBuilder(str);
		return builder.reverse().toString();
	}


	private void activationLocalCompletedHttp() {
		if (MyOrderDetailActivity.OrderID != null) {
			CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED, MyOrderDetailActivity.OrderID);
		} else {
			CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED, ActivateActivity.orderId);
		}
	}

	/**
	 * 更新固件版本号
	 */
	//更新设备信息
	private void updateDeviceInfo() {
		//绑定完成更新设备信息
		if (utils == null)
			utils = SharedUtils.getInstance();
		CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_UPDATE_CONN_INFO, utils.readString(Constant.BRACELETVERSION),
				utils.readInt(Constant.BRACELETPOWER) + "", utils.readInt(Constant.BRACELETTYPEINT) + "");
	}
	/**
	 * 更新历史数据
	 */
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
//				CommonTools.showShortToast(context, "激活成功！");
				DialogBalance dialog = new DialogBalance(this,ProMainActivity.instance, R.layout.dialog_balance, 0);
				dialog.changeText("激活成功", "确定");
				orderStatus = 1;
				Intent intent = new Intent();
				intent.setAction(FINISH_ACTIVITY);
				intent.setAction(MyOrderDetailActivity.FINISH_PROCESS);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			} else {
				CommonTools.showShortToast(ICSOpenVPNApplication.getContext()
						, object.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES) {
			GetDeviceSimRegStatuesHttp getDeviceSimRegStatuesHttp = (GetDeviceSimRegStatuesHttp) object;
			if (getDeviceSimRegStatuesHttp.getStatus() == 1)
				if (!getDeviceSimRegStatuesHttp.getSimRegStatue().getRegStatus().equals("1")) {
					connectGoip();
				} else {
					CommonTools.showShortToast(context, context.getString(R.string.tip_high_signal));
				}

		}else if(cmdType == HttpConfigUrl.COMTYPE_UPDATE_CONN_INFO){
			if(object.getStatus()==1){
				utils.writeString(SharedUtils.getInstance().readString(Constant.IMEI),utils.readString(Constant.BRACELETVERSION) );
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

	private void connectGoip() {
		if (ProMainActivity.sendYiZhengService != null) {
			EventBusUtil.changeConnectStatus(context.getString(R.string.index_registing), R.drawable.index_no_signal);
			ProMainActivity.sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
		}
	}

	private void getDeviceSimRegStatues() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES);
	}

	@Override
	public void noNet() {
		try {
			CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), context.getResources().getString(R.string.no_wifi));
			Intent intent = new Intent();
			intent.setAction(MyOrderDetailActivity.FINISH_PROCESS_ONLY);
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void dialogText(int type, String text) {

	}
}
