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

import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
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
import de.blinkt.openvpn.http.UnBindDeviceHttp;
import de.blinkt.openvpn.http.UpdateVersionHttp;
import de.blinkt.openvpn.model.BlueToothDeviceEntity;
import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.IsSuccessEntity;
import de.blinkt.openvpn.model.PercentEntity;
import de.blinkt.openvpn.model.ServiceOperationEntity;
import de.blinkt.openvpn.service.DfuService;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.MySinkingView;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogUpgrade;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static android.view.View.GONE;
import static cn.com.aixiaoqi.R.id.register_sim_statue;
import static com.aixiaoqi.socket.EventBusUtil.registerFail;
import static com.aixiaoqi.socket.TestProvider.sendYiZhengService;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.isGetnullCardid;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.nullCardId;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.retryTime;
import static de.blinkt.openvpn.constant.Constant.ELECTRICITY;
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
	private String TAG = "MyDeviceActivity";
	private BluetoothAdapter mBtAdapter = null;
	private static final int REQUEST_ENABLE_BT = 2;
	public static final String BLUESTATUSFROMPROMAIN = "bluestatusfrompromain";
	private TimerTask checkPowerTask = new TimerTask() {
		@Override
		public void run() {
			int powerInt = utils.readInt(ELECTRICITY);
			if (sinking != null) {
				sinking.setPercent(((float) powerInt) / 100);
			}
		}
	};
	private SharedUtils utils = SharedUtils.getInstance();
	private UartService mService = ICSOpenVPNApplication.uartService;
	private Timer checkPowerTimer = new Timer();
	private String macAddressStr;
	private int SCAN_PERIOD = 10000;//原本120000毫秒
	private DialogBalance noDevicedialog;
	private DialogBalance cardRuleBreakDialog;
	Animation RegisterStatueAnim;
	//写卡进度
	private static int percentInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_device);
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
//			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
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
		long beforeRequestTime = utils.readLong(Constant.UPGRADE_INTERVAL);
		if (beforeRequestTime == 0L || System.currentTimeMillis() - beforeRequestTime > 216000000)//一小时以后再询问
		{
			utils.writeLong(Constant.UPGRADE_INTERVAL, System.currentTimeMillis());
			SkyUpgradeHttp skyUpgradeHttp = new SkyUpgradeHttp(this, HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA, utils.readString(Constant.BRACELETVERSION));
			new Thread(skyUpgradeHttp).start();
		}
	}

	private void initSet() {
		Log.e(TAG, "initSet");

		String blueStatus = getIntent().getStringExtra(BLUESTATUSFROMPROMAIN);
		RegisterStatueAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate_register_statue);
		checkPowerTimer.schedule(checkPowerTask, 100, 60000);

		macAddressStr = utils.readString(Constant.IMEI);
		if (macAddressStr != null)
			macAddressStr = macAddressStr.toUpperCase();
		macTextView.setText(macAddressStr);
		hasLeftViewTitle(R.string.device, 0);
		if (mService != null && mService.mConnectionState == UartService.STATE_CONNECTED) {
			int electricityInt = utils.readInt(ELECTRICITY);
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
			GetBindDeviceHttp http = new GetBindDeviceHttp(MyDeviceActivity.this, HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
			new Thread(http).start();
		}

		firmwareTextView.setText(utils.readString(Constant.BRACELETVERSION));
		//如果是在注册中才能打开动画
		if ((SocketConstant.REGISTER_STATUE_CODE == 1 || SocketConstant.REGISTER_STATUE_CODE == 2)
				&& conStatusTextView.getText().toString().equals(getResources().getString(R.string.index_registing))) {
			startAnim();
		}
		if (percentInt != 0) {
			percentTextView.setText(percentInt + "%");
		}
	}

	private String deviceAddresstemp;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					String deviceAddress = utils.readString(Constant.IMEI);
					if (deviceAddress != null) {
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

	public static boolean isUpgrade = false;

	@OnClick({R.id.unBindButton, R.id.callPayLinearLayout, register_sim_statue, R.id.findStatusLinearLayout, R.id.statueTextView})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.unBindButton:
				//重启Uart服务
//				restartUartService();
				if (CommonTools.isFastDoubleClick(1000)) {
					return;
				}
				MobclickAgent.onEvent(context, CLICKUNBINDDEVICE);
				UnBindDeviceHttp http = new UnBindDeviceHttp(this, HttpConfigUrl.COMTYPE_UN_BIND_DEVICE);
				new Thread(http).start();

				break;
			case R.id.callPayLinearLayout:
				if (!TextUtils.isEmpty(utils.readString(Constant.BRACELETVERSION)) && !isUpgrade) {
					utils.writeLong(Constant.UPGRADE_INTERVAL, 0);
					skyUpgradeHttp();
				} else if (isUpgrade) {
					showSkyUpgrade();
				}
				break;
			case R.id.findStatusLinearLayout:

				new Thread(new Runnable() {
					@Override
					public void run() {
						if (!CommonTools.isFastDoubleClick(3000)) {
							SendCommandToBluetooth.sendMessageToBlueTooth(FIND_DEVICE);
						}
					}
				}).start();

				break;
			case register_sim_statue:
				//如果激活卡成功后，刷新按钮点击需要将标记激活
				isGetnullCardid = true;
				nullCardId = null;
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
						registerFail(Constant.REGIST_CALLBACK_TYPE, SocketConstant.RESTART_TCP);
					}

				} else if (SocketConstant.REGISTER_STATUE_CODE == 3) {
					//请求服务器，当卡在线的时候，不进行任何操作。当卡不在线的时候，重新从预读取数据注册
					getDeviceSimRegStatues();
				}
				break;

			case R.id.statueTextView:
				//当解绑设备，registerSimStatu会被隐藏，再寻找设备的时候需要再显示出来
				registerSimStatu.setVisibility(View.VISIBLE);
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				break;
		}
	}

	private void getDeviceSimRegStatues() {
		GetDeviceSimRegStatuesHttp getDeviceSimRegStatuesHttp = new GetDeviceSimRegStatuesHttp(this, HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES);
		new Thread(getDeviceSimRegStatuesHttp).start();
	}

	private void connectGoip() {
		if (sendYiZhengService != null)
			sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
	}

	private void registFail() {
		Log.e(TAG, "registFail");
		registerFail(Constant.REGIST_CALLBACK_TYPE, SocketConstant.REGISTER_FAIL_INITIATIVE);
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


	private Thread connectThread;
	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public String dataType;

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				//TODO 连接成功，操作问题
				//测试代码
				unBindButton.setVisibility(View.VISIBLE);
				dismissProgress();
				setView();
				sendEventBusChangeBluetoothStatus(getString(R.string.index_no_signal));
				if(isUpgrade){
					CommonTools.delayTime(5000);
					uploadToBlueTooth();
				}
			}

			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
				if (mService != null) {
					Log.e(TAG, "isUpgradeSTATECONNECTED=" + isUpgrade);
					if (retryTime >= 20 || !ICSOpenVPNApplication.isConnect) {
						sinking.setVisibility(GONE);
						noConnectImageView.setVisibility(View.VISIBLE);
						statueTextView.setVisibility(View.VISIBLE);
						unBindButton.setVisibility(GONE);
						utils.delete(Constant.IMEI);
						macTextView.setText("");
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
								Log.i(TAG, "空中升级重连");
								scanLeDevice(true);
							}
						}
					});
					connectThread.start();
					sendEventBusChangeBluetoothStatus(getString(R.string.index_connecting));
					showProgress("正在重新连接");
				} else {
					unBindButton.setVisibility(GONE);
					utils.delete(Constant.IMEI);
					macTextView.setText("");
					sinking.setVisibility(GONE);
					noConnectImageView.setVisibility(View.VISIBLE);
					statueTextView.setVisibility(View.VISIBLE);
					CommonTools.showShortToast(MyDeviceActivity.this, "已断开");
				}
			}
			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

				final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
				String messageFromBlueTooth = HexStringExchangeBytesUtil.bytesToHexString(txValue);

				if (txValue[0] != (byte) 0x55) {
					return;
				}
				//判断是否是分包（0x80的包）
				if (txValue[1] != (byte) 0x80) {
					return;
				}
				dataType = messageFromBlueTooth.substring(6, 10);
				switch (dataType) {
//					case (byte) 0xBB:
//						if (txValue[1] == (byte) 0x05) {
//							setView();
//						} else if (txValue[1] == (byte) 0x04) {
//							slowSetPercent(((float) Integer.parseInt(String.valueOf(txValue[3]))) / 100);
//						}
//						break;
					case "0100":
						Log.i(TAG, "版本号:" + txValue[5]);
						firmwareTextView.setText(txValue[5] + "");
						UpdateVersionHttp http = new UpdateVersionHttp(MyDeviceActivity.this, HttpConfigUrl.COMTYPE_UPDATE_VERSION, txValue[5] + "");
						new Thread(http).start();
						if (!TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
							BluetoothMessageCallBackEntity entity = new BluetoothMessageCallBackEntity();
							entity.setBlueType(BluetoothConstant.BLUE_VERSION);
							entity.setBraceletversion(txValue[5] + "");
							entity.setSuccess(true);
							EventBus.getDefault().post(entity);
							Log.i(TAG, "进入版本号:" + txValue[5]);
						}
						break;
					case "0700":
						if (txValue[5] == 0x01) {
							if (SocketConstant.REGISTER_STATUE_CODE == 1 && SocketConstant.REGISTER_STATUE_CODE == 2) {
								sendEventBusChangeBluetoothStatus(getString(R.string.index_registing));
							}
						} else if (txValue[5] == 0x11) {
							//百分比TextView设置为0
//							percentTextView.setText("");
							showNoCardDialog();
							SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);
							sendEventBusChangeBluetoothStatus(getString(R.string.index_un_insert_card));
							stopAnim();
						}
						break;
				}
			}
		}
	};

	private void setView() {
		dismissProgress();
		int electricityInt = utils.readInt(ELECTRICITY);
		noConnectImageView.setVisibility(GONE);
		sinking.setVisibility(View.VISIBLE);
//		resetDeviceTextView.setVisibility(View.VISIBLE);
		if (electricityInt != 0) {
			sinking.setPercent(((float) electricityInt) / 100);
		} else {
			sinking.setPercent(0f);
		}
		statueTextView.setVisibility(GONE);
		if (macTextView.getText().length() == 0) {
			if (deviceAddresstemp != null && deviceAddresstemp.length() != 0) {
				macTextView.setText(deviceAddresstemp);
			} else {
				macTextView.setText(utils.readString(Constant.IMEI));
			}
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onResume");
		super.onPause();
		DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopAnim();
		Log.d(TAG, "onDestroy()");
		isUpgrade = false;
		if (isDfuServiceRunning()) {
			stopService(new Intent(this, DfuService.class));
		}
		if (checkPowerTimer != null) {
			checkPowerTimer.cancel();
			checkPowerTimer = null;
		}
		if (checkPowerTask != null) {
			checkPowerTask.cancel();
			checkPowerTask = null;
		}
		try {
			LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(UARTStatusChangeReceiver);
			EventBus.getDefault().unregister(this);
		} catch (Exception ignore) {
			Log.e(TAG, ignore.toString());
		}
		utils=null;

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
				//传出注册失败
				utils.delete(ELECTRICITY);
				firmwareTextView.setText("");
				macTextView.setText("");
				utils.delete(Constant.IMEI);
				utils.delete(Constant.BRACELETVERSION);
				//判断是否再次重连的标记
				ICSOpenVPNApplication.isConnect = false;
				ReceiveBLEMoveReceiver.isConnect = false;
				mService.disconnect();
				registFail();
				CommonTools.showShortToast(this, "已解绑设备");
				sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind));
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
			GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
			//网络获取看有没有存储IMEI设备号,如果没有绑定过则去绑定流程
			if (getBindDeviceHttp.getStatus() == 1) {
				BlueToothDeviceEntity mBluetoothDevice = getBindDeviceHttp.getBlueToothDeviceEntityity();
				firmwareTextView.setText(mBluetoothDevice.getVersion());
				statueTextView.setText(getString(R.string.blue_connecting));
				statueTextView.setEnabled(false);
				utils.writeString(Constant.IMEI, mBluetoothDevice.getIMEI());
				utils.writeString(Constant.BRACELETVERSION, mBluetoothDevice.getVersion());
				unBindButton.setVisibility(View.VISIBLE);
				//当接口调用完毕后，扫描设备，打开状态栏
//				scanLeDevice(true);
			}
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
			SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;
			if (skyUpgradeHttp.getStatus() == 1) {
				if (skyUpgradeHttp.getUpgradeEntity() != null) {
					if (skyUpgradeHttp.getUpgradeEntity().getVersion() > Integer.parseInt(utils.readString(Constant.BRACELETVERSION))) {
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
				SendCommandToBluetooth.sendMessageToBlueTooth(SKY_UPGRADE_ORDER);


			} else if (Constant.DOWNLOAD_FAIL.equals(downloadSkyUpgradePackageHttp.getDownloadStatues())) {
				CommonTools.showShortToast(this, Constant.DOWNLOAD_FAIL);
			}
			//检测是否在线
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES) {
			if (object.getStatus() != 1) {
				connectGoip();
			} else {
				stopAnim();
				CommonTools.showShortToast(this, getString(R.string.tip_high_signal));
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_UPDATE_VERSION) {
			if (object.getStatus() != 1) {
				CommonTools.showShortToast(this, object.getMsg());
			}
		}
	}


	String url;

	private void showDialogGOUpgrade(String desc) {
		Log.d(TAG, "showDialogGOUpgrade");
		//不能按返回键，只能二选其一
		DialogBalance Upgrade = new DialogBalance(this, MyDeviceActivity.this, R.layout.dialog_balance, DOWNLOAD_SKY_UPGRADE);
		Upgrade.changeText(desc, getResources().getString(R.string.upgrade), 1);
	}

	private void uploadToBlueTooth() {
		Log.d(TAG, "uploadToBlueTooth");
		if (isDfuServiceRunning()) {
			return;
		}

		showSkyUpgrade();
		Log.e(TAG, "isUpgrade=" + isUpgrade);
		final DfuServiceInitiator starter = new DfuServiceInitiator(utils.readString(Constant.IMEI));
		starter.setKeepBond(true);
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
		DialogUpgrade dialogUpgrade = new DialogUpgrade(this, MyDeviceActivity.this, R.layout.dialog_upgrade, 3);
		upgradeDialog = dialogUpgrade.getDialogUpgrade();
		mDfuProgressListener = dialogUpgrade.getDfuProgressListener();
		hideDialogUpgrade();
	}

	private void showDialogUpgrade() {
		Log.d(TAG, "showDialogUpgrade");
		isUpgrade = true;
		upgradeDialog.show();
	}

	private void hideDialogUpgrade() {
		upgradeDialog.dismiss();
	}

	private void downloadSkyUpgradePackageHttp(String path) {
		DownloadSkyUpgradePackageHttp downloadSkyUpgradePackageHttp = new DownloadSkyUpgradePackageHttp(this, HttpConfigUrl.COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE, true, path);
		new Thread(downloadSkyUpgradePackageHttp).start();
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		firmwareTextView.setText(utils.readString(Constant.BRACELETVERSION));
		macTextView.setText(utils.readString(Constant.IMEI));
		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
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
			connDevice(utils.readString(Constant.IMEI));
		} else {
			onBackPressed();
		}
	}


	private boolean isDfuServiceRunning() {
		if (ICSOpenVPNApplication.getInstance().isServiceRunning(DfuService.class.getName())) {
			return true;
		}
		return false;
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
				if (mService != null && mService.mConnectionState == UartService.STATE_CONNECTED) {
					return;
				}
				if (mBtAdapter != null) {
					if (enable) {
						// Stops scanning after a pre-defined scan period.
						mBtAdapter.startLeScan(mLeScanCallback);
						CommonTools.delayTime(SCAN_PERIOD);
						if (mService.mConnectionState != UartService.STATE_CONNECTED) {
							mBtAdapter.stopLeScan(mLeScanCallback);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showDialog();
								}
							});
						} else {
							mBtAdapter.stopLeScan(mLeScanCallback);
						}
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
				if (mService != null && mService.mConnectionState == UartService.STATE_CONNECTED) {
					return;
				}
				if (mBtAdapter != null) {
					mService.connect(deviceAddress);
					CommonTools.delayTime(SCAN_PERIOD);
					if (mService.mConnectionState != UartService.STATE_CONNECTED) {
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
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (device.getName() == null) {
										return;
									}
									Log.i(TAG, "isUpgrade:" + isUpgrade + "deviceName:" + device.getName() + "保存的IMEI地址:" + utils.readString(Constant.IMEI).replace(":", ""));
									if (isUpgrade && device.getName().contains(utils.readString(Constant.IMEI).replace(":", ""))) {
										Log.i(TAG, "device:" + device.getName() + "mac:" + device.getAddress());
										if (mService != null) {
											scanLeDevice(false);
											mService.connect(device.getAddress());
										}
									} else if (!isUpgrade && macAddressStr != null && macAddressStr.equalsIgnoreCase(device.getAddress())) {
										Log.i(TAG, "find the device:" + device.getName() + "mac:" + device.getAddress() + "macAddressStr:" + macAddressStr + ",rssi :" + rssi);
										if (mService != null) {
											scanLeDevice(false);
											utils.writeString(Constant.IMEI, macAddressStr);
											mService.connect(macAddressStr);
										}
									}
								}
							});
						}
					});
				}
			};


	private void showDialog() {
		scanLeDevice(false);
		//不能按返回键，只能二选其一
		noDevicedialog = new DialogBalance(this, this, R.layout.dialog_balance, NOT_YET_REARCH);
		noDevicedialog.setCanClickBack(false);
		noDevicedialog.changeText(getResources().getString(R.string.no_find_device), getResources().getString(R.string.retry));
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
			stopAnim();
		} else if (conStatus.equals(getString(R.string.index_no_signal))) {
			percentTextView.setText("0%");
			registerSimStatu.setVisibility(View.VISIBLE);
			startAnim();
		} else if (conStatus.equals(getString(R.string.index_regist_fail))) {
			percentTextView.setText("");
		} else if (conStatus.equals(getString(R.string.index_registing))) {
			percentTextView.setText("");
			registerSimStatu.setVisibility(View.VISIBLE);
			startAnim();
		} else if (conStatus.equals(getString(R.string.index_unbind))) {
			percentTextView.setText("");
		} else if (conStatus.equals(getString(R.string.index_no_packet))) {
			stopAnim();
			percentTextView.setText("");
			registerSimStatu.setVisibility(GONE);
		} else if (conStatus.equals(getString(R.string.index_un_insert_card))) {
			percentTextView.setText("");
		} else if (conStatus.equals(getString(R.string.index_high_signal))) {
			conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.select_contacct));
			percentTextView.setText("");
		} else if (conStatus.equals(getString(R.string.index_unconnect))) {
			percentTextView.setText("");
			percentTextView.setVisibility(GONE);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onIsSuccessEntity(IsSuccessEntity entity) {
		Log.e(TAG, "onIsSuccessEntity");
		if (entity.getType() == Constant.REGIST_CALLBACK_TYPE) {
			if (entity.isSuccess()) {
				sendEventBusChangeBluetoothStatus(getString(R.string.index_high_signal));
				percentInt = 0;
				stopAnim();
			} else {
				if (entity.getFailType() != SocketConstant.START_TCP_FAIL)
					stopAnim();
				percentTextView.setVisibility(GONE);
				sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail));
				switch (entity.getFailType()) {
					case SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA:
						CommonTools.showShortToast(this, getString(R.string.index_regist_fail));
						break;
					case SocketConstant.REGISTER_FAIL:
						CommonTools.showShortToast(this, getString(R.string.regist_fail));
						break;
					case SocketConstant.REGISTER_FAIL_IMSI_IS_NULL:
						CommonTools.showShortToast(this, getString(R.string.regist_fail_card_invalid));
						break;
					case SocketConstant.REGISTER_FAIL_IMSI_IS_ERROR:
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
						sendEventBusChangeBluetoothStatus(getString(R.string.index_registing));
						break;
					case SocketConstant.RESTART_TCP:

						break;
					default:
						if (entity.getFailType() != SocketConstant.REGISTER_FAIL_INITIATIVE) {
							sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail));
						}
						break;
				}
			}
		}
		percentTextView.setVisibility(GONE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void receiveConnectStatus(ChangeConnectStatusEntity entity) {
		setConStatus(entity.getStatus());
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onPercentEntity(PercentEntity entity) {
		if (SocketConstant.REGISTER_STATUE_CODE == 3) {
			percentTextView.setVisibility(GONE);
			return;
		} else {
			percentTextView.setVisibility(View.VISIBLE);
		}
		double percent = entity.getPercent();
		percentInt = (int) (percent / 1.6);
		Log.i(TAG, "写卡进度：" + percentInt + "%");
		if (percentInt >= 100) {
			percentInt = 98;
		}
		percentTextView.setText(percentInt + "%");
	}

	private void showNoCardDialog() {
		//不能按返回键，只能二选其一
		cardRuleBreakDialog = new DialogBalance(MyDeviceActivity.this, MyDeviceActivity.this, R.layout.dialog_balance, 3);
		cardRuleBreakDialog.setCanClickBack(false);
		cardRuleBreakDialog.changeText(getResources().getString(R.string.no_card_or_rule_break), getResources().getString(R.string.reset));
	}
}
