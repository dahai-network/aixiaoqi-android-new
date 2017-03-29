package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.DownloadSkyUpgradePackageHttp;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.GetDeviceSimRegStatuesHttp;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
import de.blinkt.openvpn.model.BlueToothDeviceEntity;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.ServiceOperationEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.UIOperatorEntity;
import de.blinkt.openvpn.service.DfuService;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.MySinkingView;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogTipUpgrade;
import de.blinkt.openvpn.views.dialog.DialogUpgrade;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static android.view.View.GONE;
import static cn.com.aixiaoqi.R.id.register_sim_statue;
import static cn.com.aixiaoqi.R.string.device;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.isGetnullCardid;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.nullCardId;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.retryTime;
import static de.blinkt.openvpn.constant.Constant.BRACELETPOWER;
import static de.blinkt.openvpn.constant.Constant.FIND_DEVICE;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RESTORATION;
import static de.blinkt.openvpn.constant.Constant.SKY_UPGRADE_ORDER;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBINDDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKDEVICEUPGRADE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKUNBINDDEVICE;

public class MyDeviceActivity extends BaseNetActivity implements DialogInterfaceTypeBase, View.OnClickListener {
	@BindView(R.id.noConnectImageView)
	ImageView noConnectImageView;
	@BindView(R.id.statueTextView)
	TextView statueTextView;
	@BindView(R.id.firmwareTextView)
	TextView firmwareTextView;
	@BindView(R.id.callPayLinearLayout)
	LinearLayout callPayLinearLayout;
	@BindView(R.id.macTextView)
	TextView macTextView;
	@BindView(R.id.flowPayLinearLayout)
	LinearLayout flowPayLinearLayout;
	@BindView(R.id.unBindButton)
	Button unBindButton;
	@BindView(R.id.sinking)
	MySinkingView sinking;
	@BindView(R.id.simStatusLinearLayout)
	LinearLayout simStatusLinearLayout;
	@BindView(R.id.findStatusLinearLayout)
	LinearLayout findStatusLinearLayout;
	@BindView(R.id.conStatusTextView)
	TextView conStatusTextView;
	@BindView(R.id.percentTextView)
	TextView percentTextView;
	@BindView(register_sim_statue)
	Button registerSimStatu;
	@BindView(R.id.alarmClockLinearLayout)
	LinearLayout alarmClockLinearLayout;
	@BindView(R.id.messageRemindLinearLayout)
	LinearLayout messageRemindLinearLayout;
	private String TAG = "MyDeviceActivity";
	private BluetoothAdapter mBtAdapter = null;
	private static final int REQUEST_ENABLE_BT = 2;
	public static final String BLUESTATUSFROMPROMAIN = "bluestatusfrompromain";
	public static String BRACELETTYPE = "bracelettype";
	public static String UNITOYS = "unitoys";
	public static String UNIBOX = "unibox";

	//	private SharedUtils utils = null;
	private UartService mService = ICSOpenVPNApplication.uartService;
	private String macAddressStr;
	private int SCAN_PERIOD = 10000;//原本120000毫秒
	private DialogBalance noDevicedialog;
	private DialogBalance cardRuleBreakDialog;
	Animation RegisterStatueAnim;
	public static boolean isForeground = false;
	//写卡进度
	private static int percentInt;
	//是否一次都没连上，如果是则不显示重新连接
	public static boolean isConnectOnce = false;
	//手环类型
	private String bracelettype;
	public static boolean isUpgrade = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_device);
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			finish();
			return;
		}
		initSet();
		serviceInit();
		initDialogUpgrade();
	}


	//停止动画
	public void stopAnim() {
		registerSimStatu.setEnabled(true);
		RegisterStatueAnim.reset();
		registerSimStatu.clearAnimation();
		registerSimStatu.setBackgroundResource(R.drawable.registering);
	}

	//启动动画
	public void startAnim() {
		if (!registerSimStatu.isEnabled()) return;
		registerSimStatu.setEnabled(false);
		RegisterStatueAnim.reset();
		registerSimStatu.clearAnimation();
		registerSimStatu.setBackgroundResource(R.drawable.registering);
		registerSimStatu.startAnimation(RegisterStatueAnim);
	}

	DfuProgressListener mDfuProgressListener;

	//空中升级
	private void skyUpgradeHttp() {
		Log.e(TAG, "skyUpgradeHttp");
		long beforeRequestTime = SharedUtils.getInstance().readLong(Constant.UPGRADE_INTERVAL);
		if (beforeRequestTime == 0L || System.currentTimeMillis() - beforeRequestTime > 216000000)//一小时以后再询问
		{
			int DeviceType = 0;
			String braceletname = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
			if (!TextUtils.isEmpty(braceletname)) {
				if (braceletname.contains(MyDeviceActivity.UNITOYS)) {
					DeviceType = 0;
				} else {
					DeviceType = 1;
				}
			}
			createHttpRequest(HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA, SharedUtils.getInstance().readString(Constant.BRACELETVERSION), DeviceType + "");
		}
	}

	private void initSet() {
		Log.e(TAG, "initSet");
		bracelettype = getIntent().getStringExtra(BRACELETTYPE);
		if (bracelettype != null && bracelettype.contains(MyDeviceActivity.UNIBOX)) {
			alarmClockLinearLayout.setVisibility(GONE);
			messageRemindLinearLayout.setVisibility(GONE);
			findStatusLinearLayout.setVisibility(GONE);
		}

		String blueStatus = getIntent().getStringExtra(BLUESTATUSFROMPROMAIN);
		RegisterStatueAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate_register_statue);

		hasLeftViewTitle(device, 0);
		if (mService != null && mService.mConnectionState == UartService.STATE_CONNECTED) {
			int electricityInt = SharedUtils.getInstance().readInt(BRACELETPOWER);
			noConnectImageView.setVisibility(GONE);
			unBindButton.setVisibility(View.VISIBLE);
			sinking.setVisibility(View.VISIBLE);
			if (electricityInt != 0) {
				sinking.setPercent(((float) electricityInt) / 100);
			} else {
				sinking.setPercent(0f);
			}
			statueTextView.setVisibility(GONE);
			if (blueStatus != null) {
				setConStatus(blueStatus);
			}
			skyUpgradeHttp();
		} else {
			createHttpRequest(HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
		}

		firmwareTextView.setText(SharedUtils.getInstance().readString(Constant.BRACELETVERSION));
		//如果是在注册中才能打开动画
		if ((SocketConstant.REGISTER_STATUE_CODE == 1 || SocketConstant.REGISTER_STATUE_CODE == 2)
				&& conStatusTextView.getText().toString().equals(getResources().getString(R.string.index_registing))) {
			startAnim();
		}
		if (percentInt != 0 && ICSOpenVPNApplication.uartService != null && ICSOpenVPNApplication.uartService.mConnectionState == UartService.STATE_CONNECTED) {
			percentTextView.setText(percentInt + "%");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					String deviceAddress = SharedUtils.getInstance().readString(Constant.IMEI);
					if (!TextUtils.isEmpty(deviceAddress)) {
						connDevice(deviceAddress);
					} else {
						clickFindBracelet();
					}
				} else {
					Log.d(TAG, "BT not enabled");
					Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
					finish();
				}
				break;
			default:
				Log.e(TAG, "wrong request code");
				break;
		}
	}


	@OnClick({R.id.unBindButton, R.id.callPayLinearLayout, register_sim_statue, R.id.findStatusLinearLayout, R.id.statueTextView, R.id.alarmClockLinearLayout, R.id.messageRemindLinearLayout})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.unBindButton:
				//重启Uart服务
//				restartUartService();
				if (CommonTools.isFastDoubleClick(1000)) {
					return;
				}
				MobclickAgent.onEvent(context, CLICKUNBINDDEVICE);
				createHttpRequest(HttpConfigUrl.COMTYPE_UN_BIND_DEVICE);
				break;
			case R.id.callPayLinearLayout:
				if (CommonTools.isFastDoubleClick(1000)) {
					return;
				}
				if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETVERSION)) && !isUpgrade) {
					SharedUtils.getInstance().writeLong(Constant.UPGRADE_INTERVAL, 0);
					skyUpgradeHttp();
				} else if (isUpgrade) {
					showSkyUpgrade();
				}
				break;
			case R.id.findStatusLinearLayout:
				if (!CommonTools.isFastDoubleClick(3000)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							SendCommandToBluetooth.sendMessageToBlueTooth(FIND_DEVICE);
						}
					}).start();
				}
				break;
			case register_sim_statue:
				if (!CommonTools.isFastDoubleClick(3000)) {
					//如果激活卡成功后，刷新按钮点击需要将标记激活
					isGetnullCardid = true;
					nullCardId = null;
					percentInt = 0;
					//TODO 处理异常
					//如没有没插卡检测插卡并且提示用户重启手环。
					//如果网络请求失败或者无套餐，刷新则从请求网络开始。如果上电不成功，读不到手环数据，还没有获取到预读取数据或者获取预读取数据错误，则重新开始注册。
					//如果是注册到GOIP的时候失败了，则从创建连接重新开始注册

					startAnim();
					if (SocketConstant.REGISTER_STATUE_CODE == 1 || SocketConstant.REGISTER_STATUE_CODE == 0) {
						SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER);
					} else if (SocketConstant.REGISTER_STATUE_CODE == 2) {
						if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
							//从预读取数据那里重新注册
							connectGoip();
						} else {
							EventBusUtil.simRegisterStatue(SocketConstant.RESTART_TCP);
						}

					} else if (SocketConstant.REGISTER_STATUE_CODE == 3) {
						//请求服务器，当卡在线的时候，不进行任何操作。当卡不在线的时候，重新从预读取数据注册
						getDeviceSimRegStatues();
					}
				}
				break;

			case R.id.statueTextView:
				//当解绑设备，registerSimStatu会被隐藏，再寻找设备的时候需要再显示出来
				registerSimStatu.setVisibility(View.VISIBLE);
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				break;

			case R.id.alarmClockLinearLayout:
				//当解绑设备，registerSimStatu会被隐藏，再寻找设备的时候需要再显示出来
				registerSimStatu.setVisibility(View.VISIBLE);
				Intent intent = new Intent(MyDeviceActivity.this, AlarmClockActivity.class);
				startActivity(intent);
				break;
			case R.id.messageRemindLinearLayout:
				//当解绑设备，registerSimStatu会被隐藏，再寻找设备的时候需要再显示出来
				registerSimStatu.setVisibility(View.VISIBLE);
				Intent remindIntent = new Intent(MyDeviceActivity.this, TipUserOptionsActivity.class);
				startActivity(remindIntent);
				break;
		}
	}

	private void getDeviceSimRegStatues() {
		GetDeviceSimRegStatuesHttp getDeviceSimRegStatuesHttp = new GetDeviceSimRegStatuesHttp(this, HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES);
		new Thread(getDeviceSimRegStatuesHttp).start();
	}

	private void connectGoip() {
		if (ProMainActivity.sendYiZhengService != null) {
			conStatusTextView.setText(getString(R.string.index_registing));
			sendEventBusChangeBluetoothStatus(getString(R.string.index_registing));
			ProMainActivity.sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
		}
	}

	private void registFail() {
		Log.e(TAG, "registFail");
		EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL_INITIATIVE);
	}

	private void restartUartService() {
		Log.i(TAG, "restart Uart服务");
		new Thread(new Runnable() {
			@Override
			public void run() {
				//关闭UartService服务
				ServiceOperationEntity serviceOperationEntity = new ServiceOperationEntity();
				serviceOperationEntity.setServiceName(UartService.class.getName());
				serviceOperationEntity.setOperationType(ServiceOperationEntity.REMOVE_SERVICE);
				EventBus.getDefault().post(serviceOperationEntity);
				CommonTools.delayTime(200);
				serviceOperationEntity.setOperationType(ServiceOperationEntity.CREATE_SERVICE);
				EventBus.getDefault().post(serviceOperationEntity);
			}
		}).start();
	}

	private void clickFindBracelet() {
		Log.e(TAG, "clickFindBracelet");
		if (mBtAdapter != null) {
			scanLeDevice(false);
			Intent intent = new Intent(MyDeviceActivity.this, BindDeviceActivity.class);
			intent.putExtra(BRACELETTYPE, getIntent().getStringExtra(BRACELETTYPE));
			startActivity(intent);
		}
	}

	private void serviceInit() {
		Log.d(TAG, "serviceInit()");
		LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}


	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		return intentFilter;
	}

	public static int startDfuCount = 0;
	private Thread connectThread;
	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {


		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(UartService.STATE_CONNECTED)) {
				//TODO 连接成功，操作问题
				//测试代码
				unBindButton.setVisibility(View.VISIBLE);
				dismissProgress();
				sendEventBusChangeBluetoothStatus(getString(R.string.index_no_signal));
//				if(isUpgrade&&startDfuCount==0){
//					startDfuCount++;
//					CommonTools.delayTime(10000);

//				}
			}

			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
				if (mService != null) {
					if (retryTime >= 20 || !ICSOpenVPNApplication.isConnect) {
						sinking.setVisibility(GONE);
						noConnectImageView.setVisibility(View.VISIBLE);
						statueTextView.setVisibility(View.VISIBLE);
						unBindButton.setVisibility(GONE);
						SharedUtils.getInstance().delete(Constant.IMEI);
						SharedUtils.getInstance().delete(Constant.BRACELETNAME);
						macTextView.setText("");
						firmwareTextView.setText("");
						statueTextView.setText(getString(R.string.conn_bluetooth));
						CommonTools.showShortToast(MyDeviceActivity.this, "已断开");
						return;
					}
					connectThread = new Thread(new Runnable() {
						@Override
						public void run() {
							//多次扫描蓝牙，在华为荣耀，魅族M3 NOTE 中有的机型，会发现多次断开–扫描–断开–扫描…
							// 会扫描不到设备，此时需要在断开连接后，不能立即扫描，而是要先停止扫描后，过2秒再扫描才能扫描到设备
							CommonTools.delayTime(1000);
							if (isUpgrade) {
								Log.e(TAG, "空中升级重连");
								startDfuCount = 0;
								scanLeDevice(true);
							}
						}
					});
					connectThread.start();
					sendEventBusChangeBluetoothStatus(getString(R.string.index_connecting));
					//多次重连无效后关闭蓝牙重启
					if (retryTime == 6) {
						mBtAdapter.disable();
						Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					}
					if (!isUpgrade && isConnectOnce) {
						showProgress(getString(R.string.reconnecting), true);

					}
				} else {
					unBindButton.setVisibility(GONE);
					SharedUtils.getInstance().delete(Constant.IMEI);
					macTextView.setText("");
					firmwareTextView.setText("");
					statueTextView.setText(getString(R.string.conn_bluetooth));
					sinking.setVisibility(GONE);
					noConnectImageView.setVisibility(View.VISIBLE);
					statueTextView.setVisibility(View.VISIBLE);
					CommonTools.showShortToast(MyDeviceActivity.this, "已断开");
				}
			}
			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

				final ArrayList<String> messages = intent.getStringArrayListExtra(UartService.EXTRA_DATA);
//				String messageFromBlueTooth = HexStringExchangeBytesUtil.bytesToHexString(txValue);


				try {
					if (messages == null || messages.size() == 0 || !messages.get(0).substring(0, 2).equals("55")) {
						return;
					}
					//判断是否是分包（0x80的包）
					if (messages == null || messages.size() == 0 || !messages.get(0).substring(2, 4).equals("80")) {
						return;
					}
					String dataType = messages.get(0).substring(6, 10);
					switch (dataType) {
						case Constant.SYSTEM_BASICE_INFO:
							String deviceVesion = Integer.parseInt(messages.get(0).substring(10, 12), 16) + "." + Integer.parseInt(messages.get(0).substring(12, 14), 16);
							firmwareTextView.setText(deviceVesion);
							dismissProgress();
							//不让无设备dialog弹出
							if (noDevicedialog != null)
								noDevicedialog.getDialog().dismiss();

							slowSetPercent(((float) Integer.parseInt(messages.get(0).substring(14, 16), 16)) / 100);
							break;
						case Constant.RETURN_POWER:
							if (messages.get(0).substring(10, 12).equals("01")) {
								if (SocketConstant.REGISTER_STATUE_CODE == 1 && SocketConstant.REGISTER_STATUE_CODE == 2) {
									conStatusTextView.setText(getString(R.string.index_registing));
									sendEventBusChangeBluetoothStatus(getString(R.string.index_registing));
								}
							} else if (messages.get(0).substring(10, 12).equals("11")) {
								//百分比TextView设置为0
//							percentTextView.setText("");
								showNoCardDialog();
								SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);
								sendEventBusChangeBluetoothStatus(getString(R.string.index_un_insert_card));
								stopAnim();
							}
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

		}
	};

	private void setView() {
		dismissProgress();
		int electricityInt = SharedUtils.getInstance().readInt(BRACELETPOWER);
		noConnectImageView.setVisibility(GONE);
		sinking.setVisibility(View.VISIBLE);
//		resetDeviceTextView.setVisibility(View.VISIBLE);
		macAddressStr = SharedUtils.getInstance().readString(Constant.IMEI);
		if (macAddressStr != null)
			macAddressStr = macAddressStr.toUpperCase();
		macTextView.setText(macAddressStr);

		if (electricityInt != 0) {
			sinking.setPercent(((float) electricityInt) / 100);
		} else {
			sinking.setPercent(0f);
		}
		statueTextView.setVisibility(GONE);
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		isForeground = true;
		setView();
		DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		sinking.setStatus(MySinkingView.Status.NONE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopAnim();
		isForeground = false;
		DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
		Log.d(TAG, "onDestroy()");
		isUpgrade = false;
		if (isDfuServiceRunning()) {
			stopService(new Intent(this, DfuService.class));
		}
		try {
			LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(UARTStatusChangeReceiver);
			EventBus.getDefault().unregister(this);
		} catch (Exception ignore) {
			Log.e(TAG, ignore.toString());
		}

	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		Log.d(TAG, "rightComplete");
		if (cmdType == HttpConfigUrl.COMTYPE_UN_BIND_DEVICE) {
			if (object.getStatus() == 1) {
				stopAnim();
				sinking.setVisibility(GONE);
				unBindButton.setVisibility(GONE);
				noConnectImageView.setVisibility(View.VISIBLE);
				statueTextView.setVisibility(View.VISIBLE);
				registerSimStatu.setVisibility(GONE);
				statueTextView.setText(getString(R.string.conn_bluetooth));
				statueTextView.setEnabled(true);
				firmwareTextView.setText("");
				percentTextView.setText("");
				macTextView.setText("");
				SharedUtils.getInstance().delete(BRACELETPOWER);
				SharedUtils.getInstance().delete(Constant.IMEI);
				SharedUtils.getInstance().delete(Constant.BRACELETNAME);
				SharedUtils.getInstance().delete(Constant.BRACELETVERSION);
				BluetoothConstant.IS_BIND = false;
				//判断是否再次重连的标记
				ICSOpenVPNApplication.isConnect = false;
				ReceiveBLEMoveReceiver.isConnect = false;
				registFail();
				sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind));
				CommonTools.showShortToast(this, "已解绑设备");
				mService.disconnect();
				finish();
			} else {
				CommonTools.showShortToast(this, object.getMsg());
				Log.i(TAG, object.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
			GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
			//网络获取看有没有存储IMEI设备号,如果没有绑定过则去绑定流程
			if (getBindDeviceHttp.getStatus() == 1) {
				BlueToothDeviceEntity mBluetoothDevice = getBindDeviceHttp.getBlueToothDeviceEntityity();
				if (mBluetoothDevice != null) {
					if (!TextUtils.isEmpty(mBluetoothDevice.getVersion())) {
						firmwareTextView.setText(mBluetoothDevice.getVersion());
						SharedUtils.getInstance().writeString(Constant.BRACELETVERSION, mBluetoothDevice.getVersion());
					} else {
						Log.i(TAG, "mBluetoothDevice.getVersion()为空");
					}
					if (!TextUtils.isEmpty(mBluetoothDevice.getIMEI())) {
						SharedUtils.getInstance().writeString(Constant.IMEI, mBluetoothDevice.getIMEI());
					} else {
						Log.i(TAG, "mBluetoothDevice.getIMEI()为空");
					}
					statueTextView.setText(getString(R.string.blue_connecting));
					statueTextView.setEnabled(false);
					unBindButton.setVisibility(View.VISIBLE);
					//当接口调用完毕后，扫描设备，打开状态栏
//				scanLeDevice(true);
				}
				//如果有设备，则开启重连机制，重连需要该参数为true。
				ICSOpenVPNApplication.isConnect = true;
			}
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
			SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;
			SharedUtils.getInstance().writeLong(Constant.UPGRADE_INTERVAL, System.currentTimeMillis());
			if (skyUpgradeHttp.getStatus() == 1) {
				if (skyUpgradeHttp.getUpgradeEntity() != null) {
					if (skyUpgradeHttp.getUpgradeEntity().getVersion() > Float.parseFloat(SharedUtils.getInstance().readString(Constant.BRACELETVERSION))) {
						url = skyUpgradeHttp.getUpgradeEntity().getUrl();
						showDialogGOUpgrade(skyUpgradeHttp.getUpgradeEntity().getDescr());
					} else {
						CommonTools.showShortToast(this, getString(R.string.last_version));
						stopAnim();
					}
				}
			}

		} else if (cmdType == HttpConfigUrl.COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE) {
			DownloadSkyUpgradePackageHttp downloadSkyUpgradePackageHttp = (DownloadSkyUpgradePackageHttp) object;
			if (Constant.DOWNLOAD_SUCCEED.equals(downloadSkyUpgradePackageHttp.getDownloadStatues())) {
				isUpgrade = true;
				SendCommandToBluetooth.sendMessageToBlueTooth(Constant.OFF_TO_POWER);
				SendCommandToBluetooth.sendMessageToBlueTooth(SKY_UPGRADE_ORDER);
				showSkyUpgrade();

			} else if (Constant.DOWNLOAD_FAIL.equals(downloadSkyUpgradePackageHttp.getDownloadStatues())) {
				CommonTools.showShortToast(this, Constant.DOWNLOAD_FAIL);
			}
			//检测是否在线
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES) {
			GetDeviceSimRegStatuesHttp getDeviceSimRegStatuesHttp = (GetDeviceSimRegStatuesHttp) object;
			if (getDeviceSimRegStatuesHttp.getStatus() == 1)
				if (!getDeviceSimRegStatuesHttp.getSimRegStatue().getRegStatus().equals("1")) {
					connectGoip();
				} else {
					stopAnim();
					CommonTools.showShortToast(this, getString(R.string.tip_high_signal));
				}

		} else if (cmdType == HttpConfigUrl.COMTYPE_UPDATE_CONN_INFO) {
			if (object.getStatus() != 1) {
				CommonTools.showShortToast(this, object.getMsg());
			}
		}
	}


	String url;
//	DialogBalance Upgrade;

	private void showDialogGOUpgrade(String desc) {
		Log.d(TAG, "showDialogGOUpgrade");
		//不能按返回键，只能二选其一

		DialogTipUpgrade upgrade = new DialogTipUpgrade(this, this, R.layout.dialog_tip_upgrade, DOWNLOAD_SKY_UPGRADE);

		upgrade.changeText(desc);
	}

	private void uploadToBlueTooth(String deviceName, String deviceAddress) {
		Log.e(TAG, "uploadToBlueTooth");
		if (isDfuServiceRunning()) {
			return;
		}


		Log.e(TAG, "isUpgrade=" + isUpgrade);
		final DfuServiceInitiator starter = new DfuServiceInitiator(deviceAddress)
				.setDeviceName(deviceName).setKeepBond(true).setDisableNotification(true);
		Log.e(TAG, "deviceAddress:" + deviceAddress + "deviceName:" + deviceName);

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File filePath = Environment.getExternalStorageDirectory();
			String path = filePath.getPath();
			String abo = path + Constant.UPLOAD_PATH;
			starter.setZip(abo);
			starter.start(this, DfuService.class);
		}

	}

	private void showSkyUpgrade() {
		if (upgradeDialog != null && !upgradeDialog.isShowing()) {
			showDialogUpgrade();
		}
	}

	Dialog upgradeDialog;


	private void initDialogUpgrade() {
		Log.d(TAG, "initDialogUpgrade");
		DialogUpgrade dialogUpgrade = new DialogUpgrade(this, this, R.layout.dialog_upgrade, 3);
		upgradeDialog = dialogUpgrade.getDialogUpgrade();
		mDfuProgressListener = dialogUpgrade.getDfuProgressListener();
		hideDialogUpgrade();
	}

	private void showDialogUpgrade() {
		Log.d(TAG, "showDialogUpgrade");
		isUpgrade = true;
		if (upgradeDialog != null)
			upgradeDialog.show();
	}

	private void hideDialogUpgrade() {
		if (upgradeDialog != null)
			upgradeDialog.dismiss();
	}

	private void downloadSkyUpgradePackageHttp(String path) {
		DownloadSkyUpgradePackageHttp downloadSkyUpgradePackageHttp = new DownloadSkyUpgradePackageHttp(this, HttpConfigUrl.COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE, true, path);
		new Thread(downloadSkyUpgradePackageHttp).start();
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
		if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
			SharedUtils.getInstance().writeLong(Constant.UPGRADE_INTERVAL, System.currentTimeMillis());
		}
	}

	@Override
	public void noNet() {

		CommonTools.showShortToast(this, getString(R.string.no_wifi));
	}

	public static final int DOWNLOAD_SKY_UPGRADE = 5;
	public static final int NOT_YET_REARCH = 6;

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			//友盟方法统计
			MobclickAgent.onEvent(context, CLICKBINDDEVICE);
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else if (type == 3) {
			SendCommandToBluetooth.sendMessageToBlueTooth(RESTORATION);
		} else if (type == DOWNLOAD_SKY_UPGRADE) {
			if (!TextUtils.isEmpty(url)) {
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKDEVICEUPGRADE);
				downloadSkyUpgradePackageHttp(url);
			}
		} else if (type == NOT_YET_REARCH) {
			retryTime = 0;
			connDevice(SharedUtils.getInstance().readString(Constant.IMEI));
		} else {
			onBackPressed();
		}
	}

	private boolean isDfuServiceRunning() {
		return ICSOpenVPNApplication.getInstance().isServiceRunning(DfuService.class.getName());
	}

	private void slowSetPercent(final float percent) {
		final float[] slowPercent = {0};
		if (sinking.getmPercent() != 0f) {
			sinking.setPercent(percent);
			return;
		}
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (percent >= slowPercent[0]) {
					sinking.setPercent(slowPercent[0]);
					slowPercent[0] += 0.01f;
					CommonTools.delayTime(40);
				}
				slowPercent[0] = percent;
				sinking.setPercent(slowPercent[0]);
				// mSinkingView.clear();
			}
		});
		thread.start();
	}

	//扫描新设备
	private void scanLeDevice(final boolean enable) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mService == null || mService.mConnectionState == UartService.STATE_CONNECTED) {
					return;
				}
				//重置
				startDfuCount = 0;
				if (mBtAdapter != null) {
					if (enable) {
						// Stops scanning after a pre-defined scan period.
						mBtAdapter.startLeScan(mLeScanCallback);
					} else {
						mBtAdapter.stopLeScan(mLeScanCallback);
					}
				}
			}
		}).start();
	}

	//连接旧设备
	private void connDevice(final String deviceAddress) {
		if (deviceAddress == null) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mService == null || mService.mConnectionState == UartService.STATE_CONNECTED) {
					return;
				}
				//防止连接后却显示不出来的问题
				mService.disconnect();
				if (mBtAdapter != null) {
					mService.connect(deviceAddress);
					CommonTools.delayTime(SCAN_PERIOD);
					if (CommonTools.isFastDoubleClick(5000) && mService.mConnectionState != UartService.STATE_CONNECTED) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showDialog();
							}
						});
					}
				}
			}
		}).start();

	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {

				@Override
				public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {

					if (device.getName() == null) {
						return;
					}
					Log.e(TAG, "isUpgrade:" + isUpgrade + "deviceName:" + device.getName() + "保存的IMEI地址:" + SharedUtils.getInstance().readString(Constant.IMEI).replace(":", ""));
					if (isUpgrade && device.getName().contains(SharedUtils.getInstance().readString(Constant.IMEI).replace(":", ""))) {
						Log.e(TAG, "device:" + device.getName() + "mac:" + device.getAddress());
						if (mService != null) {
							scanLeDevice(false);
							Log.i(TAG, "startDfuCount:" + startDfuCount);
							if (startDfuCount == 0) {
								Log.i(TAG, "startDfuCount:" + startDfuCount);
								startDfuCount++;
								CommonTools.delayTime(1000);
								uploadToBlueTooth(device.getName(), device.getAddress());
							}
						}
					} else if (!isUpgrade && macAddressStr != null && macAddressStr.equalsIgnoreCase(device.getAddress())) {
						Log.e(TAG, "find the device:" + device.getName() + "mac:" + device.getAddress() + "macAddressStr:" + macAddressStr + ",rssi :" + rssi);
						if (mService != null) {
							scanLeDevice(false);
							SharedUtils.getInstance().writeString(Constant.IMEI, macAddressStr);
							mService.connect(macAddressStr);
						}
					}
				}
			};


	private void showDialog() {
		scanLeDevice(false);
		dismissProgress();
		if (noDevicedialog != null) noDevicedialog.getDialog().dismiss();
		//不能按返回键，只能二选其一
		noDevicedialog = new DialogBalance(this, this, R.layout.dialog_balance, NOT_YET_REARCH);
		noDevicedialog.setCanClickBack(false);
		if (bracelettype != null && bracelettype.contains(MyDeviceActivity.UNIBOX)) {
			noDevicedialog.changeText(getResources().getString(R.string.no_find_unibox), getResources().getString(R.string.retry));
		} else if (bracelettype != null && bracelettype.contains(MyDeviceActivity.UNITOYS)) {
			noDevicedialog.changeText(getResources().getString(R.string.no_find_unitoys), getResources().getString(R.string.retry));
		} else {
			noDevicedialog.getDialog().dismiss();
		}

	}

	/**
	 * 修改蓝牙连接状态，通过EVENTBUS发送到各个页面。
	 */
	private void sendEventBusChangeBluetoothStatus(String status) {
		int statusDrawable = R.drawable.index_connecting;
		if (status.equals(getString(R.string.index_connecting))) {
		} else if (status.equals(getString(R.string.index_aixiaoqicard))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_no_signal))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_regist_fail))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_registing))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_unbind))) {
			statusDrawable = R.drawable.index_unbind;
		} else if (status.equals(getString(R.string.index_no_packet))) {
			statusDrawable = R.drawable.index_no_packet;
		} else if (status.equals(getString(R.string.index_un_insert_card))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_high_signal))) {
			statusDrawable = R.drawable.index_high_signal;
		} else if (status.equals(getString(R.string.index_blue_un_opne))) {
			statusDrawable = R.drawable.index_blue_unpen;
		}
		ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
		entity.setStatus(status);
		entity.setStatusDrawableInt(statusDrawable);
		EventBus.getDefault().post(entity);
	}

	public void setConStatus(String conStatus) {
		Log.i(TAG, "状态：" + conStatus);
		conStatusTextView.setText(conStatus);
		conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
		if (conStatus.equals(getString(R.string.index_connecting))) {
			percentTextView.setText("");
		} else if (conStatus.equals(getString(R.string.index_aixiaoqicard))) {
			percentTextView.setText("");
			noDevicedialog.getDialog().dismiss();
			stopAnim();
			//重新上电清空
			SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);
		} else if (conStatus.equals(getString(R.string.index_no_signal))) {
			percentTextView.setText("0%");
			registerSimStatu.setVisibility(View.VISIBLE);
			startAnim();
		} else if (conStatus.equals(getString(R.string.index_regist_fail))) {
			percentTextView.setText("");
			percentTextView.setVisibility(GONE);
			percentInt = 0;
			stopAnim();
		} else if (conStatus.equals(getString(R.string.index_registing))) {
			percentTextView.setText("");
			registerSimStatu.setVisibility(View.VISIBLE);
			noDevicedialog.getDialog().dismiss();
			startAnim();
		} else if (conStatus.equals(getString(R.string.index_unbind))) {
			percentTextView.setText("");
		} else if (conStatus.equals(getString(R.string.index_no_packet))) {
			stopAnim();
			percentTextView.setText("");
			registerSimStatu.setVisibility(GONE);
		} else if (conStatus.equals(getString(R.string.index_un_insert_card))) {
			percentTextView.setText("");
			registerSimStatu.setVisibility(View.VISIBLE);
			stopAnim();
		} else if (conStatus.equals(getString(R.string.index_high_signal))) {
			conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.select_contacct));
			percentTextView.setVisibility(GONE);
			percentInt = 0;
			stopAnim();
		} else if (conStatus.equals(getString(R.string.index_unconnect))) {
			percentTextView.setText("");
			percentTextView.setVisibility(GONE);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onIsSuccessEntity(SimRegisterStatue entity) {
		synchronized (MyDeviceActivity.this) {
			switch (entity.getRigsterSimStatue()) {
				case SocketConstant.REGISTER_SUCCESS:
					conStatusTextView.setText(getString(R.string.index_high_signal));
					conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.select_contacct));
					sendEventBusChangeBluetoothStatus(getString(R.string.index_high_signal));
					percentInt = 0;
					percentTextView.setVisibility(GONE);
					stopAnim();
					break;
				case SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA:
					stopAnim();
					percentTextView.setVisibility(GONE);
					conStatusTextView.setText(getString(R.string.index_regist_fail));
					sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail));
					CommonTools.showShortToast(this, getString(R.string.index_regist_fail));
					break;
				case SocketConstant.REGISTER_FAIL:
					stopAnim();
					percentTextView.setVisibility(GONE);
					conStatusTextView.setText(getString(R.string.index_regist_fail));
					CommonTools.showShortToast(this, getString(R.string.index_regist_fail));
					break;
				case SocketConstant.REGISTER_FAIL_IMSI_IS_NULL:
					stopAnim();
					percentTextView.setVisibility(GONE);
					conStatusTextView.setText(getString(R.string.index_regist_fail));
					sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail));
					CommonTools.showShortToast(this, getString(R.string.regist_fail_card_invalid));
					break;
				case SocketConstant.REGISTER_FAIL_IMSI_IS_ERROR:
					stopAnim();
					percentTextView.setVisibility(GONE);
					conStatusTextView.setText(getString(R.string.index_regist_fail));
					sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail));
					CommonTools.showShortToast(this, getString(R.string.regist_fail_card_operators));
					break;
				case SocketConstant.NOT_NETWORK:
					CommonTools.showShortToast(this, getString(R.string.check_net_work_reconnect));
					break;
				case SocketConstant.START_TCP_FAIL:
					CommonTools.showShortToast(this, getString(R.string.check_net_work_reconnect));
					break;
				case SocketConstant.TCP_DISCONNECT:
					startAnim();
					conStatusTextView.setText(getString(R.string.index_registing));
					sendEventBusChangeBluetoothStatus(getString(R.string.index_registing));
					break;
				case SocketConstant.RESTART_TCP:
					startAnim();
					conStatusTextView.setText(getString(R.string.index_registing));
					sendEventBusChangeBluetoothStatus(getString(R.string.index_registing));
					break;
				case SocketConstant.REG_STATUE_CHANGE:
					startAnim();
					conStatusTextView.setText(getString(R.string.index_registing));
					sendEventBusChangeBluetoothStatus(getString(R.string.index_registing));
					break;
				case SocketConstant.REGISTER_CHANGING:
					if (SocketConstant.REGISTER_STATUE_CODE == 3) {
						percentTextView.setVisibility(GONE);
						return;
					} else {
						percentTextView.setVisibility(View.VISIBLE);
					}
					double percent = entity.getProgressCount();
					conStatusTextView.setText(getString(R.string.index_registing));
					percentInt = (int) (percent / 1.6);
					Log.i(TAG, "写卡进度：" + percentInt + "%");
					if (percentInt >= 100) {
						percentInt = 98;
					}
					percentTextView.setText(percentInt + "%");
					break;
				default:
//						if (entity.getFailType() != SocketConstant.REGISTER_FAIL_INITIATIVE) {
//							sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail));
//						}
					break;
			}
		}
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void receiveConnectStatus(ChangeConnectStatusEntity entity) {
		setConStatus(entity.getStatus());
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//如果没有IMEI就退出页面
		if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
			finish();
		}
	}


	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onUIOperatorEntity(UIOperatorEntity entity) {
		if (entity.getType() == UIOperatorEntity.onError) {
			CommonTools.showShortToast(MyDeviceActivity.this, MyDeviceActivity.this.getString(R.string.update_fail_retry));
		} else if (entity.getType() == UIOperatorEntity.onCompelete) {
			CommonTools.showShortToast(MyDeviceActivity.this, MyDeviceActivity.this.getString(R.string.dfu_status_completed));
		}
	}


	private void showNoCardDialog() {
		//不能按返回键，只能二选其一
		if (cardRuleBreakDialog != null) cardRuleBreakDialog.getDialog().dismiss();
		cardRuleBreakDialog = new DialogBalance(this, this, R.layout.dialog_balance, 3);
		cardRuleBreakDialog.setCanClickBack(false);
		cardRuleBreakDialog.changeText(getResources().getString(R.string.no_card_or_rule_break), getResources().getString(R.string.reset));
	}
}
