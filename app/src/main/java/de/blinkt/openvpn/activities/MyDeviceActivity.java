package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BindDeviceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.DownloadSkyUpgradePackageHttp;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
import de.blinkt.openvpn.http.UnBindDeviceHttp;
import de.blinkt.openvpn.model.BlueToothDeviceEntity;
import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
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

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.activities.BindDeviceActivity.FAILT;
import static de.blinkt.openvpn.constant.Constant.ELECTRICITY;
import static de.blinkt.openvpn.constant.Constant.FIND_DEVICE;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RESTORATION;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBINDDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKDEVICEUPGRADE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKUNBINDDEVICE;

public class MyDeviceActivity extends BaseActivity implements InterfaceCallback, DialogInterfaceTypeBase, View.OnClickListener {
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
	@BindView(R.id.resetDeviceTextView)
	TextView resetDeviceTextView;
	@BindView(R.id.sinking)
	MySinkingView sinking;
	//重连次数记录
	int retryTime = 0;
	@BindView(R.id.simStatusLinearLayout)
	LinearLayout simStatusLinearLayout;
	@BindView(R.id.conStatusLinearLayout)
	LinearLayout conStatusLinearLayout;
	@BindView(R.id.findStatusLinearLayout)
	LinearLayout findStatusLinearLayout;
	@BindView(R.id.conStatusTextView)
	TextView conStatusTextView;
	@BindView(R.id.percentTextView)
	TextView percentTextView;
	private String TAG = "MyDeviceActivity";
	private BluetoothAdapter mBtAdapter = null;
	private static final int REQUEST_SELECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int UART_PROFILE_CONNECTED = 20;
	private static final int UART_PROFILE_DISCONNECTED = 21;
	public static final String BLUESTATUSFROMPROMAIN = "bluestatusfrompromain";
	private static int conStatusResource = R.string.index_connecting;
	private TimerTask checkPowerTask = new TimerTask() {
		@Override
		public void run() {
			int powerInt = utils.readInt(ELECTRICITY);
			if (sinking != null) {
				sinking.setPercent(((float) powerInt) / 100);
			}
		}
	};
	private int mState = UART_PROFILE_DISCONNECTED;
	private SharedUtils utils = SharedUtils.getInstance();
	private UartService mService = ICSOpenVPNApplication.uartService;
	private Timer checkPowerTimer = new Timer();
	private String macAddressStr;
	private long SCAN_PERIOD = 10000;//原本120000毫秒
	private DialogBalance noDevicedialog;
	private DialogBalance cardRuleBreakDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_device);
		ButterKnife.bind(this);
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


	DfuProgressListener mDfuProgressListener;

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

		int blueStatus = getIntent().getIntExtra(BLUESTATUSFROMPROMAIN, R.string.index_connecting);
		checkPowerTimer.schedule(checkPowerTask, 100, 60000);
		if (mService != null)
			mState = mService.mConnectionState;
		macAddressStr = utils.readString(Constant.IMEI);
		macTextView.setText(macAddressStr);
		hasLeftViewTitle(R.string.device, 0);
		if (mState == UartService.STATE_CONNECTED) {
			int electricityInt = utils.readInt(ELECTRICITY);
			noConnectImageView.setVisibility(View.GONE);
			unBindButton.setVisibility(View.VISIBLE);
			sinking.setVisibility(View.VISIBLE);
			if (electricityInt != 0) {
				sinking.setPercent(((float) electricityInt) / 100);
			} else {
				sinking.setPercent(0f);
			}
			statueTextView.setVisibility(View.GONE);
			conStatusLinearLayout.setVisibility(View.VISIBLE);
			if (blueStatus != 0) {
				setConStatus(blueStatus);
			}
			skyUpgradeHttp();
		} else {
			GetBindDeviceHttp http = new GetBindDeviceHttp(MyDeviceActivity.this, HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
			new Thread(http).start();
		}

		firmwareTextView.setText(utils.readString(Constant.BRACELETVERSION));
		EventBus.getDefault().register(this);

	}

	private String deviceAddresstemp;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_SELECT_DEVICE:
				//When the DeviceListActivity return, with the selected device address
				if (resultCode == Activity.RESULT_OK && data != null) {
					String deviceAddress = data.getStringExtra(IntentPutKeyConstant.DEVICE_ADDRESS);
					deviceAddresstemp = deviceAddress;
					macTextView.setText(deviceAddresstemp);
					utils.writeString(Constant.IMEI, deviceAddress);
					conStatusLinearLayout.setVisibility(View.VISIBLE);
					setConStatus(conStatusResource);
					firmwareTextView.setText(utils.readString(Constant.BRACELETVERSION));

				} else if (resultCode == FAILT) {
					finish();
				}
				break;
			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					Toast.makeText(this, "蓝牙已启动", Toast.LENGTH_SHORT).show();
					clickFindBracelet();
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

	@OnClick({R.id.unBindButton, R.id.callPayLinearLayout, R.id.findStatusLinearLayout, R.id.resetDeviceTextView, R.id.statueTextView})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.unBindButton:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKUNBINDDEVICE);
				sinking.setVisibility(View.GONE);
				noConnectImageView.setVisibility(View.VISIBLE);
				statueTextView.setVisibility(View.VISIBLE);
				resetDeviceTextView.setVisibility(View.GONE);
				conStatusLinearLayout.setVisibility(View.GONE);
				firmwareTextView.setText("");
				macTextView.setText("");
				utils.delete(Constant.IMEI);
				utils.delete(Constant.BRACELETVERSION);
				sendMessageToBlueTooth("AAABCDEFAA");
				//判断是否再次重连的标记
				ICSOpenVPNApplication.isConnect = false;
				ReceiveBLEMoveReceiver.isConnect = false;
				mService.disconnect();
				//传出注册失败
				registFail();
//				//重启Uart服务
//				restartUartService();
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
							sendMessageToBlueTooth(FIND_DEVICE);
						}
					}
				}).start();

				break;
			case R.id.resetDeviceTextView:
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (!CommonTools.isFastDoubleClick(5000)) {
							sendMessageToBlueTooth(RESTORATION);
						}
					}
				}).start();
				break;
			case R.id.statueTextView:
				clickFindBracelet();
				break;
		}
	}

	private void registFail() {
		Log.e(TAG, "registFail");
		IsSuccessEntity entity = new IsSuccessEntity();
		entity.setType(Constant.REGIST_TYPE);
		entity.setSuccess(false);
		entity.setFailType(SocketConstant.REGISTER_FAIL_INITIATIVE);
		EventBus.getDefault().post(entity);
	}

	private void restartUartService() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				//关闭UartService服务
				ServiceOperationEntity serviceOperationEntity = new ServiceOperationEntity();
				serviceOperationEntity.setServiceName(UartService.class.getName());
				serviceOperationEntity.setOperationType(ServiceOperationEntity.REMOVE_SERVICE);
				EventBus.getDefault().post(serviceOperationEntity);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
			startActivityForResult(intent, REQUEST_SELECT_DEVICE);
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

	private void sendMessageToBlueTooth(final String message) {
		byte[] value;
		Log.i("TAG", "sendMessageToBlueTooth=" + message);
		value = HexStringExchangeBytesUtil.hexStringToBytes(message);
		mService.writeRXCharacteristic(value);
	}

	private Thread connectThread;
	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				mState = UART_PROFILE_CONNECTED;
				//测试代码
				unBindButton.setVisibility(View.VISIBLE);
				IS_TEXT_SIM = true;
				dismissProgress();
				setView();
				if (utils.readBoolean(Constant.ISHAVEORDER, true)) {
					setConStatus(R.string.index_no_signal);
				} else {
					setConStatus(R.string.index_no_packet);
				}
				if (retryTime != 0) {
//							//测试：当刚连接的时候，因为测试阶段没有连接流程所以连通上就等于连接上。
//							new Thread(new Runnable() {
//								@Override
//								public void run() {
//									IsSuccessEntity entity = new IsSuccessEntity();
//									entity.setType(Constant.BLUE_CONNECTED_INT);
//									entity.setSuccess(true);
//									EventBus.getDefault().post(entity);
//									try {
//										Thread.sleep(5000);
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//									Log.e("phoneAddress", "main.start()");
//									JNIUtil.getInstance().startSDK(SharedUtils.getInstance().readString(Constant.USER_NAME));
//								}
//							}).start();
					retryTime = 0;
				}
			}

			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {

				mState = UART_PROFILE_DISCONNECTED;
				if (ICSOpenVPNApplication.isConnect) {
					retryTime++;
					if (retryTime > 20) {
						sinking.setVisibility(View.GONE);
						noConnectImageView.setVisibility(View.VISIBLE);
						statueTextView.setVisibility(View.VISIBLE);
						unBindButton.setVisibility(View.GONE);
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
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							mService.connect(deviceAddresstemp);
						}
					});
					connectThread.start();
					setConStatus(R.string.index_connecting);
					showProgress("正在重新连接");
				} else {
					unBindButton.setVisibility(View.GONE);
					utils.delete(Constant.IMEI);
					macTextView.setText("");
					sinking.setVisibility(View.GONE);
					noConnectImageView.setVisibility(View.VISIBLE);
					statueTextView.setVisibility(View.VISIBLE);
					CommonTools.showShortToast(MyDeviceActivity.this, "已断开");
				}

			}
			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

				final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
				//判断是否是分包（BB开头的包）
				if (txValue[0] != (byte) 0xBB) {
					return;
				}
				switch (txValue[0]) {
					case (byte) 0xBB:
						//如果收到
						if (txValue[1] == (byte) 0xEE) {
							utils.writeString(Constant.IMEI, deviceAddresstemp);
							setView();
						} else if (txValue[1] == (byte) 0x05) {
							setView();
						} else if (txValue[1] == (byte) 0x0A) {
							utils.writeString(Constant.BRACELETVERSION, Integer.parseInt(String.valueOf(txValue[2]), 16) + "");
							firmwareTextView.setText(Integer.parseInt(String.valueOf(txValue[2]), 16) + "");
							if (!TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
								//收到版本号后获取历史步数
								sendMessageToBlueTooth(Constant.HISTORICAL_STEPS);
								BluetoothMessageCallBackEntity entity = new BluetoothMessageCallBackEntity();
								entity.setBlueType(BluetoothConstant.BLUE_VERSION);
								entity.setSuccess(true);
								EventBus.getDefault().post(entity);
							}
						} else if (txValue[1] == (byte) 0x04) {
							slowSetPercent(((float) Integer.parseInt(String.valueOf(txValue[3]))) / 100);
						} else if (txValue[1] == (byte) 0x33) {
							if (SocketConstant.REGISTER_STATUE_CODE == 1) {
								setConStatus(R.string.index_registing);
							}
						} else if (txValue[1] == (byte) 0x11) {
							//百分比TextView设置为0
//							percentTextView.setText("");
							showNoCardDialog();
							sendMessageToBlueTooth(OFF_TO_POWER);
							setConStatus(R.string.index_un_insert_card);
						}
						break;
				}
			}
		}
	};

	private void setView() {
		dismissProgress();
		int electricityInt = utils.readInt(ELECTRICITY);
		noConnectImageView.setVisibility(View.GONE);
		sinking.setVisibility(View.VISIBLE);
		resetDeviceTextView.setVisibility(View.VISIBLE);
		if (electricityInt != 0) {
			sinking.setPercent(((float) electricityInt) / 100);
		} else {
			sinking.setPercent(0f);
		}
		statueTextView.setVisibility(View.GONE);
		if (macTextView.getText().length() == 0) {
			macTextView.setText(deviceAddresstemp);
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
		if (cmdType == HttpConfigUrl.COMTYPE_BIND_DEVICE) {
			BindDeviceHttp http = (BindDeviceHttp) object;
			if (http.getStatus() == 1) {
				Log.i("test", "保存设备名成功");
				//保存设备后在本地保存mac地址
				if (!TextUtils.isEmpty(deviceAddresstemp)) {
					utils.writeString(Constant.IMEI, deviceAddresstemp);
					macTextView.setText(deviceAddresstemp);
				} else {
					macTextView.setText(utils.readString(Constant.IMEI));
				}
			} else {
				CommonTools.showShortToast(this, http.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_UN_BIND_DEVICE) {
			utils.delete(Constant.IMEI);
			utils.delete(Constant.BRACELETVERSION);
			utils.delete(ELECTRICITY);
			CommonTools.showShortToast(this, "已解绑设备");
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
			GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
			//网络获取看有没有存储IMEI设备号,如果没有绑定过则去绑定流程
			if (getBindDeviceHttp.getStatus() == 1) {
				BlueToothDeviceEntity mBluetoothDevice = getBindDeviceHttp.getBlueToothDeviceEntityity();
				firmwareTextView.setText(mBluetoothDevice.getVersion());
				utils.writeString(Constant.IMEI, mBluetoothDevice.getIMEI());
				utils.writeString(Constant.BRACELETVERSION, mBluetoothDevice.getVersion());
				unBindButton.setVisibility(View.VISIBLE);
				//当接口调用完毕后，扫描设备，打开状态栏
				conStatusLinearLayout.setVisibility(View.VISIBLE);
				scanLeDevice(true);
			} else {
				clickFindBracelet();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
			SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;
			if (skyUpgradeHttp.getStatus() == 1) {
				if (skyUpgradeHttp.getUpgradeEntity() != null) {
					if (skyUpgradeHttp.getUpgradeEntity().getVersion() > Integer.parseInt(utils.readString(Constant.BRACELETVERSION))) {
						url = skyUpgradeHttp.getUpgradeEntity().getUrl();
						showDialogGOUpgrade(skyUpgradeHttp.getUpgradeEntity().getDescr());
					} else {
						CommonTools.showShortToast(this, getString(R.string.last_version));
					}
				}
			}

		} else if (cmdType == HttpConfigUrl.COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE) {
			DownloadSkyUpgradePackageHttp downloadSkyUpgradePackageHttp = (DownloadSkyUpgradePackageHttp) object;
			if (Constant.DOWNLOAD_SUCCEED.equals(downloadSkyUpgradePackageHttp.getDownloadStatues())) {
				sendMessageToBlueTooth("AA080401A7");
				try {
					Thread.sleep(1000);
				} catch (Exception e) {

				}
				uploadToBlueTooth();
			} else if (Constant.DOWNLOAD_FAIL.equals(downloadSkyUpgradePackageHttp.getDownloadStatues())) {
				CommonTools.showShortToast(this, Constant.DOWNLOAD_FAIL);
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
		isUpgrade = true;
		showSkyUpgrade();

		final DfuServiceInitiator starter = new DfuServiceInitiator(utils.readString(Constant.IMEI));
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File filePath = Environment.getExternalStorageDirectory();
			String path = filePath.getPath();
			String abo = path + Constant.UPLOAD_PATH;
			starter.setZip(null, abo);
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
		clickFindBracelet();
	}

	public static final int DOWNLOAD_SKY_UPGRADE = 5;
	public static final int NOT_YET_REARCH = 6;

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			//友盟方法统计
			MobclickAgent.onEvent(context, CLICKBINDDEVICE);
			clickFindBracelet();
		} else if (type == 3) {
			sendMessageToBlueTooth(RESTORATION);
		} else if (type == DOWNLOAD_SKY_UPGRADE) {
			if (!TextUtils.isEmpty(url)) {
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKDEVICEUPGRADE);
				downloadSkyUpgradePackageHttp(url);
			}
		} else if (type == NOT_YET_REARCH) {
			scanLeDevice(true);
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
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				slowPercent[0] = percent;
				sinking.setPercent(slowPercent[0]);
				// mSinkingView.clear();
			}
		});
		thread.start();
	}

	//我的设备内如果有已储存设备的话，那么开始扫描已有设备进行连接，不用进入绑定流程啦！
	private void scanLeDevice(final boolean enable) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (mService.mConnectionState == UartService.STATE_CONNECTED) {
					return;
				}
				if (enable) {
					// Stops scanning after a pre-defined scan period.
					mBtAdapter.startLeScan(mLeScanCallback);
					try {
						Thread.sleep(SCAN_PERIOD);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
									Log.i("test", "find the device:" + device.getName() + "mac:" + device.getAddress() +"macAddressStr:" + macAddressStr+ ",rssi :" + rssi);
									if (macAddressStr != null) {
										if (macAddressStr.equalsIgnoreCase(device.getAddress())) {
											scanLeDevice(false);
											Intent result = new Intent();
											result.putExtra(IntentPutKeyConstant.DEVICE_ADDRESS, device.getAddress());
//										checkIsBindDevie(device);
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

	private void checkIsBindDevie(BluetoothDevice device) {
		try {
			// 连接建立之前的先配对
			if (device.getBondState() == BluetoothDevice.BOND_NONE) {
				Method creMethod = BluetoothDevice.class
						.getMethod("createBond");
				Log.e("TAG", "开始配对");
				creMethod.invoke(device);
			} else {
			}
		} catch (Exception e) {
			// TODO: handle exception
			//DisplayMessage("无法配对！");
			e.printStackTrace();
		}

	}

	private void showDialog() {
		scanLeDevice(false);
		//不能按返回键，只能二选其一
		noDevicedialog = new DialogBalance(this, this, R.layout.dialog_balance, NOT_YET_REARCH);
		noDevicedialog.setCanClickBack(false);
		noDevicedialog.changeText(getResources().getString(R.string.no_find_device), getResources().getString(R.string.retry));
	}

	public void setConStatus(int conStatus) {
		conStatusTextView.setText(getResources().getString(conStatus));
		conStatusResource = conStatus;
		conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
		switch (conStatus) {
			case R.string.index_connecting:
				setLeftDrawable(-1);
				percentTextView.setText("");
				break;
			case R.string.index_no_signal:
				setLeftDrawable(R.drawable.device_no_signal);
				percentTextView.setText("0%");
				break;
			case R.string.index_regist_fail:
				setLeftDrawable(R.drawable.device_no_signal);
				percentTextView.setText("");
				break;
			case R.string.index_registing:
				setLeftDrawable(R.drawable.device_no_signal);
				percentTextView.setText("");
				break;
			case R.string.index_no_packet:
				setLeftDrawable(R.drawable.device_no_packet);
				percentTextView.setText("");
				break;
			case R.string.index_un_insert_card:
				setLeftDrawable(R.drawable.device_no_packet);
				percentTextView.setText("");
				break;
			case R.string.index_high_signal:
				setLeftDrawable(R.drawable.device_high_signal);
				conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.select_contacct));
				break;
		}
	}

	private void setLeftDrawable(int resId) {
		if (resId != -1) {
			Drawable leftDrawable = getResources().getDrawable(resId);
			if (leftDrawable != null) {
				leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
				conStatusTextView.setCompoundDrawables(null, null, leftDrawable, null);
			}
		} else {
			conStatusTextView.setCompoundDrawables(null, null, null, null);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onIsSuccessEntity(IsSuccessEntity entity) {
		Log.e(TAG, "onIsSuccessEntity");
		if (entity.getType() == Constant.REGIST_CALLBACK_TYPE) {
			if (entity.isSuccess()) {
				setConStatus(R.string.index_high_signal);
			} else {
				switch (entity.getFailType()) {
					case SocketConstant.REGISTER_FAIL:
						CommonTools.showShortToast(this, getString(R.string.regist_fail));
						break;
					case SocketConstant.REGISTER_FAIL_IMSI_IS_NULL:
						CommonTools.showShortToast(this, getString(R.string.regist_fail_card_invalid));
						break;
					case SocketConstant.REGISTER_FAIL_IMSI_IS_ERROR:
						CommonTools.showShortToast(this, getString(R.string.regist_fail_card_operators));
						break;
				}
			}
		} else if (entity.getType() == Constant.REGIST_TYPE) {
			if (entity.getFailType() != SocketConstant.REGISTER_FAIL_INITIATIVE) {
				setConStatus(R.string.index_regist_fail);
			}
		}
		percentTextView.setVisibility(View.GONE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onPercentEntity(PercentEntity entity) {
		if (SocketConstant.REGISTER_STATUE_CODE == 3) {
			percentTextView.setVisibility(View.GONE);
			return;
		} else {
			percentTextView.setVisibility(View.VISIBLE);
		}
		double percent = entity.getPercent();
		int percentInt = (int) (percent / 1.6);
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
