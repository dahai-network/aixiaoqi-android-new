package de.blinkt.openvpn.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.CommenActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BindDeviceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.IsBindHttp;
import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
import de.blinkt.openvpn.model.BluetoothModel;
import de.blinkt.openvpn.model.ServiceOperationEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.PointProgressBar;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.BASIC_MESSAGE;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER;
import static de.blinkt.openvpn.util.CommonTools.getBLETime;


public class BindDeviceActivity extends CommenActivity implements InterfaceCallback, DialogInterfaceTypeBase {

	@BindView(R.id.stopImageView)
	ImageView stopImageView;
	@BindView(R.id.connectedRelativeLayout)
	RelativeLayout connectedRelativeLayout;
	@BindView(R.id.tip_search)
	TextView tip_search;
	@BindView(R.id.search_bluetooth)
	TextView search_bluetooth;
	@BindView(R.id.pointProgressBar)
	PointProgressBar pointProgressBar;
	@BindView(R.id.findedImageView)
	ImageView findedImageView;

	private Handler mHandler;
	private Handler findDeviceHandler;
	private HashSet<BluetoothModel> deviceSet;
	private BluetoothAdapter mBluetoothAdapter;
	private static final long SCAN_PERIOD = 10000; //120 seconds
	private String deviceAddress = "";
	SharedUtils utils = SharedUtils.getInstance();
	private DialogBalance noDevicedialog;
	private String TAG = "BindDeviceActivity";
	private UartService mService = ICSOpenVPNApplication.uartService;
	private String bracelettype;
	//设备名称：类型不同名称不同，分别有【unitoys、unibox】
	private String bluetoothName = Constant.UNITOYS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {//没有发现设备
			CommonTools.showShortToast(this, getString(R.string.bluetooth_ble_not_support));
			finish();
			return;
		}

		bracelettype = getIntent().getStringExtra(MyDeviceActivity.BRACELETTYPE);

		if (MyDeviceActivity.UNIBOX.equals(bracelettype)) {
			bluetoothName = Constant.UNIBOX;
		} else if (MyDeviceActivity.UNITOYS.equals(bracelettype)) {
			bluetoothName = Constant.UNITOYS;
		}

		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			finish();
			return;
		}
		setContentView(R.layout.activity_bind_device);
		EventBus.getDefault().register(this);
		ButterKnife.bind(this);
		deviceSet = new HashSet<>();
		mHandler = new Handler();
		findDeviceHandler = new Handler();
		scanLeDevice(true);
	}

	//查看选择设备类型
	private void afterConnDevice() {
		if (MyDeviceActivity.UNIBOX.equals(bracelettype)) {
			showIsBindLayout();
		} else if (MyDeviceActivity.UNITOYS.equals(bracelettype)) {
			finish();
		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mService != null && mService.mConnectionState != UartService.STATE_CONNECTED && !isStartFindDeviceDelay) {
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						showDialog();
					}
				}
			}, SCAN_PERIOD);

			mBluetoothAdapter.startLeScan(mLeScanCallback);

		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}

	}

	private void showDialog() {
		//不能按返回键，只能二选其一
		noDevicedialog = new DialogBalance(BindDeviceActivity.this, BindDeviceActivity.this, R.layout.dialog_balance, 2);
		noDevicedialog.setCanClickBack(false);
		if (MyDeviceActivity.UNITOYS.equals(bracelettype)) {
			noDevicedialog.changeText(getResources().getString(R.string.no_find_unitoys), getResources().getString(R.string.retry));
		} else if (MyDeviceActivity.UNIBOX.equals(bracelettype)) {
			noDevicedialog.changeText(getString(R.string.no_find_unibox), getResources().getString(R.string.retry));
		}
	}

	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		if (noDevicedialog != null && noDevicedialog.getDialog() != null && noDevicedialog.getDialog().isShowing()) {
			noDevicedialog.getDialog().dismiss();
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		deviceSet.clear();
		EventBus.getDefault().unregister(this);
	}

	//是否打开找到设备的计时器
	private boolean isStartFindDeviceDelay;
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
									Log.i("test", "find the device:" + device.getName() + ",rssi :" + rssi);
									if (device.getName().contains(bluetoothName)) {
										BluetoothModel model = new BluetoothModel();
										model.setAddress(device.getAddress());
										model.setDiviceName(device.getName());
										model.setRssi(rssi);
										deviceSet.add(model);
										if (!isStartFindDeviceDelay) {
											findDeviceHandler.postDelayed(new Runnable() {
												@Override
												public void run() {
													List<BluetoothModel> infos =
															new ArrayList<>(deviceSet);
													Collections.sort(infos, new Comparator<BluetoothModel>() {
														@Override
														public int compare(BluetoothModel lhs, BluetoothModel rhs) {
															return rhs.getRssi() - lhs.getRssi();
														}
													});
													for (int i = 0; i < infos.size(); i++) {
														String id = infos.get(i).toString();
														Log.i(TAG, "排序后：" + id);
													}
													//排序后连接操作
													scanLeDevice(false);
													if (infos.size() == 0) {
														CommonTools.showShortToast(BindDeviceActivity.this, getString(R.string.no_device_around));
														finish();
														return;
													}
													deviceAddress = infos.get(0).getAddress();
													utils.writeString(Constant.BRACELETNAME, infos.get(0).getDiviceName());
													IsBindHttp http = new IsBindHttp(BindDeviceActivity.this, HttpConfigUrl.COMTYPE_ISBIND_DEVICE, deviceAddress);
													new Thread(http).start();
													isStartFindDeviceDelay = false;
													deviceSet.clear();
												}
											}, 5000);
											isStartFindDeviceDelay = true;
										}
									}
								}
							});
						}
					});
				}
			};


	@OnClick(R.id.stopImageView)
	public void onClick() {
		scanLeDevice(false);
		mService.disconnect();
		ICSOpenVPNApplication.isConnect = false;
		utils.delete(Constant.IMEI);
		utils.delete(Constant.BRACELETNAME);
		finish();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_ISBIND_DEVICE) {
			IsBindHttp http = (IsBindHttp) object;
			if (http.getIsBindEntity().getBindStatus() == 0 && http.getStatus() == 1) {
				if (mService != null) {
					int DeviceType = 0;
					String braceletname = utils.readString(Constant.BRACELETNAME);
					if (!TextUtils.isEmpty(braceletname)) {
						if (braceletname.contains(MyDeviceActivity.UNITOYS)) {
							DeviceType = 0;
						} else {
							DeviceType = 1;
						}
					}
					BindDeviceHttp bindDevicehttp = new BindDeviceHttp(BindDeviceActivity.this, HttpConfigUrl.COMTYPE_BIND_DEVICE
							, deviceAddress, "0", DeviceType);
					new Thread(bindDevicehttp).start();
				} else {
					CommonTools.showShortToast(BindDeviceActivity.this, getString(R.string.connect_failure));
					restartUartService();
					finish();
				}
			} else {
				CommonTools.showShortToast(this, "该设备已经绑定过了！");
				scanLeDevice(false);
				mService.disconnect();
				utils.delete(Constant.IMEI);
				utils.delete(Constant.BRACELETNAME);
				finish();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_BIND_DEVICE) {
			Log.i(TAG, "绑定设备返回：" + object.getMsg() + ",返回码：" + object.getStatus());
			if (object.getStatus() == 1) {
				Log.i("test", "保存设备名成功");
				utils.writeString(Constant.IMEI, deviceAddress);
				mService.connect(deviceAddress);
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
//			finish();
		}
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

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(mContext, errorMessage);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(mContext, getString(R.string.no_wifi));
	}

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					scanLeDevice(true);
				}
			}).start();
		} else {
			stopImageView.performClick();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onVersionEntity(BluetoothMessageCallBackEntity entity) {
		String type = entity.getBlueType();
		if (BluetoothConstant.BLUE_BIND_SUCCESS.equals(type)) {
			if (entity.isSuccess()) {
				Log.i(TAG, "蓝牙注册返回:" + entity.getBlueType() + ",参数：MEI：" + utils.readString(Constant.IMEI) + ",版本号：" + utils.readString(Constant.BRACELETVERSION));
				connectedRelativeLayout.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					@Override
					public void run() {
						BluetoothConstant.IS_BIND = true;
						//测试代码
						sendMessageToBlueTooth(UP_TO_POWER);
						CommonTools.delayTime(500);
						//更新时间操作
						sendMessageToBlueTooth(getBLETime());
						CommonTools.delayTime(500);
						//android 标记，给蓝牙设备标记是否是android设备用的
//						sendMessageToBlueTooth(ANDROID_TARGET);
						sendMessageToBlueTooth(BASIC_MESSAGE);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								finish();
							}
						});
					}
				}).start();
			} else {
				finish();
			}

		} else if (BluetoothConstant.BLUE_BIND.equals(type)) {
			afterConnDevice();
		}
	}

	private void showIsBindLayout() {
		tip_search.setText(getString(R.string.finded_bracelet));
		search_bluetooth.setText(getString(R.string.click_bracelet_sure_bind));
		pointProgressBar.stop();
		if (bracelettype != null) {
			pointProgressBar.setVisibility(View.GONE);
			findedImageView.setVisibility(View.VISIBLE);
			if (bracelettype.equals(MyDeviceActivity.UNITOYS)) {
				findedImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.unitoy_finded));
			} else {
				findedImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.unibox_finded));
			}
		}
	}

}
