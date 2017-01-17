package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.DeviceAdapter;
import de.blinkt.openvpn.activities.Base.CommenActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BindDeviceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.IsBindHttp;
import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;


public class BindDeviceActivity extends CommenActivity implements InterfaceCallback, DialogInterfaceTypeBase {

	public static String BIND_COMPELETE = "BIND_COMPELETE";
	public static int FAILT = 4;
	@BindView(R.id.stopImageView)
	ImageView stopImageView;
	@BindView(R.id.all_device_rv)
	RecyclerView allDeviceRv;
	@BindView(R.id.connectedRelativeLayout)
	RelativeLayout connectedRelativeLayout;
	private Handler mHandler;
	private BluetoothAdapter mBluetoothAdapter;
	List<BluetoothDevice> deviceList;

	private DeviceAdapter deviceAdapter;
	private static final long SCAN_PERIOD = 10000; //120 seconds
	private String deviceAddress = "";
	SharedUtils utils = SharedUtils.getInstance();
	private DialogBalance noDevicedialog;
	private int REQUEST_ENABLE_BT = 2;
	private String TAG = "BindDeviceActivity";
	private UartService mService = ICSOpenVPNApplication.uartService;
	private BroadcastReceiver bindCompeleteReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			connectedRelativeLayout.setVisibility(View.VISIBLE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent result = new Intent();
					result.putExtra(IntentPutKeyConstant.DEVICE_ADDRESS, deviceAddress);
					BindDeviceActivity.this.setResult(Activity.RESULT_OK, result);
					BindDeviceActivity.this.finish();
				}
			}, 2000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_device);
		ButterKnife.bind(this);
		initSet();
		mHandler = new Handler();
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {//没有发现设备

		}
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			finish();
			return;
		}
		initList();
	}

	private void initSet() {
		//初始化广播，用于蓝牙操作后跳出界面
		LocalBroadcastManager.getInstance(this).registerReceiver(bindCompeleteReceiver, getFilter());
		EventBus.getDefault().register(this);
	}

	private void initList() {
		if (!TextUtils.isEmpty(utils.readString(Constant.IMEI))) {
			deviceAddress = utils.readString(Constant.IMEI);
		}
		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	}


	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mService != null && mService.mConnectionState != UartService.STATE_CONNECTED) {
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
		noDevicedialog.changeText(getResources().getString(R.string.no_find_device), getResources().getString(R.string.retry));
	}

	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		if (noDevicedialog != null && noDevicedialog.getDialog() != null) {
			noDevicedialog.getDialog().dismiss();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(bindCompeleteReceiver);
		EventBus.getDefault().unregister(this);
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
									Log.i("test", "find the device:" + device.getName() + ",rssi :" + rssi);
									if (device.getName().contains(Constant.BLUETOOTH_NAME)) {
//									  if (device.getName().contains("unitoys")) {
										//如果信号强度绝对值大于这个值（距离\）,则配对
										if (Math.abs(rssi) < 75) {
											mBluetoothAdapter.stopLeScan(mLeScanCallback);
											deviceAddress = device.getAddress();
											utils.writeString(Constant.IMEI, deviceAddress);
											IsBindHttp http = new IsBindHttp(BindDeviceActivity.this, HttpConfigUrl.COMTYPE_ISBIND_DEVICE, device.getAddress());
											new Thread(http).start();
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

	@OnClick(R.id.stopImageView)
	public void onClick() {
		mService.disconnect();
		scanLeDevice(false);
		finish();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_ISBIND_DEVICE) {
			IsBindHttp http = (IsBindHttp) object;
			if (http.getIsBindEntity().getBindStatus() == 0) {
				if (mService != null)
					mService.connect(deviceAddress);
			} else {
				CommonTools.showShortToast(this, "该设备已经绑定过了！");
			}
		}
		//测试用代码
		else if (cmdType == HttpConfigUrl.COMTYPE_BIND_DEVICE) {
			if (object.getStatus() == 1) {
				Log.i("test", "保存设备名成功");
				utils.writeString(Constant.IMEI, deviceAddress);
				Intent result = new Intent();
				result.putExtra(IntentPutKeyConstant.DEVICE_ADDRESS, deviceAddress);
				BindDeviceActivity.this.setResult(Activity.RESULT_OK, result);
				BindDeviceActivity.this.finish();
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		}
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
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			onBackPressed();
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
//				Toast.makeText(this, "重新搜索", Toast.LENGTH_SHORT).show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final BluetoothManager bluetoothManager =
								(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
						mBluetoothAdapter = bluetoothManager.getAdapter();
						if (mBluetoothAdapter == null) {
							return;
						}
						scanLeDevice(true);
					}
				}).start();
			} else {
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	public IntentFilter getFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BIND_COMPELETE);
		return filter;
	}

	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onVersionEntity(BluetoothMessageCallBackEntity entity) {
		String type = entity.getBlueType();
		if (type == BluetoothConstant.BLUE_VERSION) {
			BindDeviceHttp bindDevicehttp = new BindDeviceHttp(BindDeviceActivity.this, HttpConfigUrl.COMTYPE_BIND_DEVICE, deviceAddress, utils.readString(Constant.BRACELETVERSION));
			new Thread(bindDevicehttp).start();
		}
	}

}
