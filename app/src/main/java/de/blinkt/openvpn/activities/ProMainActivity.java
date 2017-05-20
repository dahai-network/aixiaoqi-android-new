package de.blinkt.openvpn.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.JNIUtil;
import com.aixiaoqi.socket.RadixAsciiChange;
import com.aixiaoqi.socket.ReceiveDataframSocketService;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SdkAndBluetoothDataInchange;
import com.aixiaoqi.socket.SendYiZhengService;
import com.aixiaoqi.socket.SocketConnection;
import com.aixiaoqi.socket.SocketConstant;
import com.aixiaoqi.socket.TestProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FragmentAdapter;
import cn.com.johnson.model.ChangeViewStateEvent;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.DBHelp;
import de.blinkt.openvpn.fragments.AccountFragment;
import de.blinkt.openvpn.fragments.AddressListFragment;
import de.blinkt.openvpn.fragments.CellPhoneFragment;
import de.blinkt.openvpn.fragments.IndexFragment;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.CheckConfirmedHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBasicConfigHttp;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.GetHostAndPortHttp;
import de.blinkt.openvpn.http.IsHavePacketHttp;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
import de.blinkt.openvpn.model.BasicConfigEntity;
import de.blinkt.openvpn.model.CanClickEntity;
import de.blinkt.openvpn.model.CancelCallService;
import de.blinkt.openvpn.model.IsHavePacketEntity;
import de.blinkt.openvpn.model.PreReadEntity;
import de.blinkt.openvpn.model.ServiceOperationEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.enentbus.OptionProMainActivityView;
import de.blinkt.openvpn.service.CallPhoneService;
import de.blinkt.openvpn.service.GrayService;
import de.blinkt.openvpn.util.CheckAuthorityUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.PageChangeListener;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.CustomViewPager;
import de.blinkt.openvpn.views.MyRadioButton;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static de.blinkt.openvpn.constant.Constant.ICCID_GET;


public class ProMainActivity extends BaseNetActivity implements DialogInterfaceTypeBase {

	public static ProMainActivity instance = null;
	@BindView(R.id.radiogroup)
	RadioGroup radiogroup;
	@BindView(R.id.bottom_fragment)
	FrameLayout bottomFragment;
	private int REQUEST_LOCATION_PERMISSION = 3;
	@BindView(R.id.mViewPager)
	CustomViewPager mViewPager;
	@BindView(R.id.rb_index)
	MyRadioButton rbIndex;
	@BindView(R.id.rb_phone)
	MyRadioButton rbPhone;
	@BindView(R.id.rb_address)
	MyRadioButton rbAddress;
	@BindView(R.id.rb_personal)
	MyRadioButton rbPersonal;

	@BindView(R.id.tv_red_dot_01)
	TextView tvRedDot01;
	@BindView(R.id.tv_red_dot_04)
	TextView tvRedDot04;
	private ReceiveBLEMoveReceiver bleMoveReceiver;
	private UartService mService = null;
	//进入主页后打开蓝牙设备搜索绑定过的设备
	private BluetoothAdapter mBluetoothAdapter;
	private int REQUEST_ENABLE_BT = 2;
	private String deviceAddress = "";
	ArrayList<Fragment> list = new ArrayList<>();
	CellPhoneFragment cellPhoneFragment;
	AccountFragment accountFragment;
	AddressListFragment addressListFragment;
	SportFragment sportFragment;
	IndexFragment indexFragment;
	Intent intentCallPhone;
	public static boolean isForeground = false;
	public static final String MALL_SHOW_RED_DOT = "mall_show_red_dot";
	//重连时间
	private int RECONNECT_TIME = 180000;
	SocketConnection socketUdpConnection;
	SocketConnection socketTcpConnection;
	public static boolean isStartSdk = false;
	public static SdkAndBluetoothDataInchange sdkAndBluetoothDataInchange = null;
	public static SendYiZhengService sendYiZhengService = null;
	public static String confirmedPhoneNum;
	Intent intent = new Intent("Notic");
	//红点控制
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					tvRedDot04.setVisibility(View.VISIBLE);
					intent.putExtra("flg", true);
					break;
				case 2:
					tvRedDot04.setVisibility(View.GONE);
					intent.putExtra("flg", false);
					break;
			}
			LocalBroadcastManager.getInstance(ProMainActivity.this).sendBroadcast(intent);
		}
	};
	//位置权限提示DIALOG
	private DialogBalance noLocationPermissionDialog;


	@Override
	public Object getLastCustomNonConfigurationInstance() {
		return super.getLastCustomNonConfigurationInstance();
	}

	//绑定UartService服务
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
			mService = ((UartService.LocalBinder) rawBinder).getService();
			//存在Application供全局使用
			ICSOpenVPNApplication.uartService = mService;
			d("onServiceConnected mService= " + mService);
			if (!mService.initialize()) {
				d("Unable to initialize Bluetooth");
				finish();
			}
			initBrocast();
		}

		public void onServiceDisconnected(ComponentName classname) {
			mService = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		instance = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pro_main);
		ButterKnife.bind(this);
		initSet();
		initFragment();
		initView();
		setListener();
		initServices();
		socketUdpConnection = new SocketConnection();
		socketTcpConnection = new SocketConnection();
		//注册eventbus，观察goip注册问题
		EventBus.getDefault().register(this);
	}


	/**
	 * android 6.0以上需要位置信息动态获取
	 */
	private void initSet() {
		if (Build.VERSION.SDK_INT >= 23 && !NetworkUtils.isLocationOpen(getApplicationContext())) {
			//不能按返回键，只能二选其一
			noLocationPermissionDialog = new DialogBalance(this, this, R.layout.dialog_balance, 2);
			noLocationPermissionDialog.changeText(getResources().getString(R.string.no_location_permission), getResources().getString(R.string.sure));
		}
		//如果没有保存过推送每日推荐的日期，则为第一次推送,如果
		String recommandStr = SharedUtils.getInstance().readString(Constant.RECOMMAND_DATE);
		String todayStr = DateUtils.getCurrentDate();
		Intent intent = new Intent(this, EveryDayRecomActivity.class);
		if (recommandStr == null || !recommandStr.equals(todayStr)) {
			startActivity(intent);
			SharedUtils.getInstance().writeString(Constant.RECOMMAND_DATE, todayStr);
		}
		CheckAuthorityUtil.checkPermissions(this, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG);
	}


	/**
	 * \初始化界面
	 */
	private void initView() {
		radiogroup.check(R.id.rb_phone);
		radiogroup.setOnCheckedChangeListener(new MyRadioGroupListener());
	}

	/**
	 * 判断是否显示红点
	 */
	@Subscribe
	public void checkRedIsShow(ChangeViewStateEvent event) {

		if (AccountFragment.tvNewPackagetAction.getVisibility() == View.VISIBLE || AccountFragment.tvNewVersion.getVisibility() == View.VISIBLE)
			tvRedDot04.setVisibility(View.VISIBLE);
		else
			tvRedDot04.setVisibility(View.GONE);
	}

	//初始化广播
	private void initBrocast() {
		LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(showRedDotReceiver, showRedDotIntentFilter());
		if (bleMoveReceiver == null) {
			bleMoveReceiver = new ReceiveBLEMoveReceiver();
			LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(bleMoveReceiver, makeGattUpdateIntentFilter());
//			LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(updateIndexTitleReceiver, makeGattUpdateIntentFilter());
			registerReceiver(screenoffReceive, screenoffIntentFilter());
			//打开蓝牙服务后开始搜索
			searchBLE();
		}
	}

	//
	private void searchBLE() {
		/**
		 * 搜索蓝牙步骤：
		 * 1.通过接口询问是否绑定过蓝牙设备
		 * 2.如果有绑定过蓝牙设备，则询问打开蓝牙
		 * 3.打开后，则通过线程做扫描操作。
		 * 4.扫描到设备则连接上，没扫描到十秒后自动断开。关闭所有与之相关的东西
		 */
		if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI)) || TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))) {
			createHttpRequest(HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
		} else {
			skyUpgradeHttp();
			BluetoothConstant.IS_BIND = true;
//			accountFragment.showDeviceSummarized(true);
//			EventBusUtil.showDevice(true);
//			String typeText = "";
//			String deviceType = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
//			if (!TextUtils.isEmpty(deviceType)) {
//				//0是手环，1是钥匙扣
//				if (deviceType.contains(MyDeviceActivity.UNITOYS)) {
//					typeText = getString(R.string.device) + ": " + getString(R.string.unitoy);
//				} else if (deviceType.contains(MyDeviceActivity.UNIBOX)) {
//					typeText = getString(R.string.device) + ": " + getString(R.string.unibox_key);
//				}
//				accountFragment.setSummarized(typeText, null, false);
//			}
			blueToothOpen();
		}
	}

	private void blueToothOpen() {
		if (mService != null && !mService.isOpenBlueTooth()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			connectOperate();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
		}

		return true;
	}

	//实例化UartService
	public void initServices() {

		if (!ICSOpenVPNApplication.getInstance().isServiceRunning(UartService.class.getName())) {
			i("开启UartService");
			Intent bindIntent = new Intent(this, UartService.class);
			try {
				bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
			} catch (Exception e) {
				initBrocast();
				e.printStackTrace();
			}
		}
		//启动常驻服务
		if (!ICSOpenVPNApplication.getInstance().isServiceRunning(GrayService.class.getName())) {
			startService(new Intent(this, GrayService.class));
		}

	}

	private void startSocketService() {
		if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
			Intent receiveSdkIntent = new Intent(this, ReceiveSocketService.class);
			bindService(receiveSdkIntent, socketTcpConnection, Context.BIND_AUTO_CREATE);
		}
	}

	private void startDataframService() {
		if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())) {
			Intent receiveSdkIntent = new Intent(this, ReceiveDataframSocketService.class);
			bindService(receiveSdkIntent, socketUdpConnection, Context.BIND_AUTO_CREATE);
		}

	}


	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		intentFilter.addAction(UartService.FINDED_SERVICE);
		return intentFilter;
	}

	private static IntentFilter screenoffIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		return intentFilter;
	}

	private static IntentFilter showRedDotIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MALL_SHOW_RED_DOT);
		return intentFilter;
	}

	//初始化Fragment
	private void initFragment() {

		if (indexFragment == null) {
			indexFragment = new IndexFragment();
		}
		if (cellPhoneFragment == null) {
			cellPhoneFragment = new CellPhoneFragment();
		}
		if (addressListFragment == null) {
			addressListFragment = new AddressListFragment();
		}
		if (accountFragment == null) {
			accountFragment = new AccountFragment();
		}
		if (list.size() < 4) {
			list.clear();
			list.add(indexFragment);
			list.add(cellPhoneFragment);
			list.add(addressListFragment);
			list.add(accountFragment);
			FragmentAdapter adapter = new FragmentAdapter(
					getSupportFragmentManager(), list);
			mViewPager.setAdapter(adapter);
			mViewPager.setOffscreenPageLimit(4);
			mViewPager.setCurrentItem(1);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				CommonTools.showShortToast(this, "蓝牙已启动");
				//连接操作
				connectOperate();
			} else {
				EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.BLUETOOTH_CLOSE);
			}
		} else if (requestCode == REQUEST_LOCATION_PERMISSION) {
			if (NetworkUtils.isLocationOpen(getApplicationContext())) {
				Log.i(TAG, "打开位置权限");
				//Android6.0需要动态申请权限
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {
					//请求权限
					ActivityCompat.requestPermissions(this,
							new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
									Manifest.permission.ACCESS_FINE_LOCATION},
							REQUEST_LOCATION_PERMISSION);
					if (ActivityCompat.shouldShowRequestPermissionRationale(this,
							Manifest.permission.ACCESS_COARSE_LOCATION)) {
						//判断是否需要解释
//							DialogUtils.shortT(getApplicationContext(), "需要蓝牙权限");
					}
				}

			} else {
				CommonTools.showShortToast(this, getString(R.string.no_location_tips));
			}
		} else {
			if (cellPhoneFragment != null) {
				cellPhoneFragment.onActivityResult(requestCode, resultCode, data);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//如果蓝牙开启后，之前已绑定的设备会重新连接上
	private void connectOperate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final BluetoothManager bluetoothManager =
						(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
				mBluetoothAdapter = bluetoothManager.getAdapter();
				if (mBluetoothAdapter == null) {
					return;
				}
				while (mService != null && mService.mConnectionState != UartService.STATE_CONNECTED) {
					connDeviceFiveSecond();
					CommonTools.delayTime(RECONNECT_TIME);
				}

			}
		}).start();
	}

	private Handler stopHandler = null;

	//扫描五秒后提示
	private void connDeviceFiveSecond() {
		mService.connect(SharedUtils.getInstance().readString(Constant.IMEI));
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.CONNECTING_DEVICE);
				if (stopHandler == null) {
					stopHandler = new Handler();
				}
				stopHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						scanLeDevice(false);
						if (mService != null && !mService.isConnectedBlueTooth()) {
							EventBusUtil.canClickEntity(CanClickEntity.JUMP_MYDEVICE);
						}
					}
				}, 10000);
			}
		});
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
		if (!ICSOpenVPNApplication.getInstance().isServiceRunning(CallPhoneService.class.getName())) {
			e("onResume");
			intentCallPhone = new Intent(this, CallPhoneService.class);
			startService(intentCallPhone);
		}

		if (SharedUtils.getInstance().readBoolean(IntentPutKeyConstant.CLICK_MALL)) {
			e("onResume " + SharedUtils.getInstance().readBoolean(IntentPutKeyConstant.CLICK_MALL));
			tvRedDot01.setVisibility(View.VISIBLE);
		} else {
			tvRedDot01.setVisibility(View.INVISIBLE);
		}

		basicConfigHttp();
	}

	private void basicConfigHttp() {
		if (TextUtils.isEmpty(SharedUtils.getInstance().readString(IntentPutKeyConstant.USER_AGREEMENT_URL))) {
			createHttpRequest(HttpConfigUrl.COMTYPE_GET_BASIC_CONFIG);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		isForeground = false;
	}

	//ViewPager添加监听
	private void setListener() {
		new PageChangeListener(mViewPager) {
			@Override
			public void pageSelected(int position) {
				switch (position) {
					case 0:
						radiogroup.check(R.id.rb_index);
						SharedUtils.getInstance().writeBoolean(IntentPutKeyConstant.CLICK_MALL, false);
						tvRedDot01.setVisibility(View.INVISIBLE);
						break;
					case 1:
						radiogroup.check(R.id.rb_phone);
						break;
					case 2:
						radiogroup.check(R.id.rb_address);
						break;
					case 3:
						radiogroup.check(R.id.rb_personal);
						break;
				}
			}
		};


	}

	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(enableLocate, REQUEST_LOCATION_PERMISSION);
		}
	}

	private class MyRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.rb_index:
					mViewPager.setCurrentItem(0);
					break;
				case R.id.rb_phone:
					mViewPager.setCurrentItem(1);
					break;
				case R.id.rb_address:
					mViewPager.setCurrentItem(2);
					break;
				case R.id.rb_personal:
					mViewPager.setCurrentItem(3);
					break;
			}
		}
	}

	//销毁数据
	@Override
	protected void onDestroy() {

		LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(bleMoveReceiver);
//		LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(updateIndexTitleReceiver);
		unregisterReceiver(screenoffReceive);
		bleMoveReceiver = null;
		radiogroup = null;
		screenoffReceive = null;
		if (intentCallPhone != null)
			stopService(intentCallPhone);
		if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())) {
			unbindService(socketUdpConnection);
			if (SocketConnection.mReceiveDataframSocketService != null) {
				SocketConnection.mReceiveDataframSocketService.stopSelf();
			}
		}
		unbindTcpService();
		if (mService != null)
			mService.stopSelf();
		mService = null;
		radiogroup = null;
		list.clear();
		indexFragment = null;
		cellPhoneFragment = null;
		accountFragment = null;
		addressListFragment = null;
		sportFragment = null;
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	//解除绑定
	private void unbindTcpService() {
		if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
			unbindService(socketTcpConnection);
			if (SocketConnection.mReceiveSocketService != null) {
				SocketConnection.mReceiveSocketService.stopSelf();
				SocketConnection.mReceiveSocketService = null;
			}
		}
	}

	//重新复制
	private void destorySocketService() {
		if (SocketConstant.REGISTER_STATUE_CODE != 0) {
			SocketConstant.REGISTER_STATUE_CODE = 1;
		}
	}

	//网路请求
	@Override
	public void rightComplete(int cmdType, final CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
			GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
			if (object.getStatus() == 1) {
				if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
					SharedUtils utils = SharedUtils.getInstance();

					if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
						deviceAddress = getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI();
						if (deviceAddress != null) {
							deviceAddress = deviceAddress.toUpperCase();
							BluetoothConstant.IS_BIND = true;
							skyUpgradeHttp();
//							accountFragment.showDeviceSummarized(true);
//							EventBusUtil.showDevice(true);
							//按MAC地址保存版本号
							utils.writeString(deviceAddress, getBindDeviceHttp.getBlueToothDeviceEntityity().getVersion());
//							utils.writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI().toUpperCase());
						}
						//防止返回“”或者null
//						String deviceTypeStr = getBindDeviceHttp.getBlueToothDeviceEntityity().getDeviceType();
//						if (!TextUtils.isEmpty(deviceTypeStr)) {
//							int deviceType = Integer.parseInt(deviceTypeStr);
//							String typeText;
//							//0是手环，1是钥匙扣
//							if (deviceType == 0) {
//								utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNITOYS);
//								typeText = getString(R.string.device) + ": " + getString(R.string.unitoy);
//							} else {
//								utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNIBOX);
//								typeText = getString(R.string.device) + ": " + getString(R.string.unibox_key);
//							}
//							accountFragment.setSummarized(typeText, null, false);
//						}
						blueToothOpen();
					}
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET) {
			if (object.getStatus() == 1) {
				requestCount = 0;
				IsHavePacketHttp isHavePacketHttp = (IsHavePacketHttp) object;
				IsHavePacketEntity entity = isHavePacketHttp.getOrderDataEntity();
				if (entity.getUsed() == 1) {
					e("有套餐");
					SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, true);

				} else {
					//TODO 没有通知到设备界面
					//如果是没有套餐，则通知我的设备界面更新状态并且停止转动
					SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, false);
//				EventBusUtil.changeConnectStatus(getString(R.string.index_no_packet), R.drawable.index_no_packet);
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
			GetHostAndPortHttp http = (GetHostAndPortHttp) object;
			if (http.getStatus() == 1) {
				requestCount = 0;
				if (http.getGetHostAndPortEntity().getVswServer().getIp() != null) {
					SocketConstant.hostIP = http.getGetHostAndPortEntity().getVswServer().getIp();
					SocketConstant.port = http.getGetHostAndPortEntity().getVswServer().getPort();
					new Thread(new Runnable() {
						@Override
						public void run() {
							e("开启线程=");
							SdkAndBluetoothDataInchange.isHasPreData = false;
							if (sdkAndBluetoothDataInchange == null) {
								sdkAndBluetoothDataInchange = new SdkAndBluetoothDataInchange();
							}
							if (sendYiZhengService == null) {
								sendYiZhengService = new SendYiZhengService();
							}
							if (!TextUtils.isEmpty(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6])) {
								DBHelp dbHelp = new DBHelp(ProMainActivity.instance);
								PreReadEntity preReadEntity = dbHelp.getPreReadEntity(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6]);
								if (preReadEntity != null) {
									SdkAndBluetoothDataInchange.isHasPreData = true;
									SdkAndBluetoothDataInchange.PERCENT = 0;
									initPre(preReadEntity);
									registerSimPreData();
								} else {
									noPreDataStartSDK();
								}
							} else {
								CommonTools.delayTime(2000);
								SendCommandToBluetooth.sendMessageToBlueTooth(ICCID_GET);
							}
						}
					}).start();
				}
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
			SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;
			if (skyUpgradeHttp.getStatus() == 1) {
				String braceletVersion = SharedUtils.getInstance().readString(Constant.BRACELETVERSION);
				if (TextUtils.isEmpty(braceletVersion)) {
					braceletVersion = "0";
				}
				if (braceletVersion != null && skyUpgradeHttp.getUpgradeEntity().getVersion() > Float.parseFloat(braceletVersion)) {
					mHandler.sendEmptyMessage(1);
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_BASIC_CONFIG) {
			GetBasicConfigHttp getBasicConfigHttp = (GetBasicConfigHttp) object;
			if (getBasicConfigHttp.getStatus() == 1) {
				BasicConfigEntity basicConfigEntity = getBasicConfigHttp.getBasicConfigEntity();
				SharedUtils.getInstance().writeString(IntentPutKeyConstant.USER_AGREEMENT_URL, basicConfigEntity.getUserAgreementUrl());
				SharedUtils.getInstance().writeString(IntentPutKeyConstant.DUALSIM_STANDBYTUTORIAL_URL, basicConfigEntity.getDualSimStandbyTutorialUrl());
				SharedUtils.getInstance().writeString(IntentPutKeyConstant.BEFORE_GOING_ABROAD_TUTORIAL_URL, basicConfigEntity.getBeforeGoingAbroadTutorialUrl());
				SharedUtils.getInstance().writeString(IntentPutKeyConstant.PAYMENT_OF_TERMS, basicConfigEntity.getPaymentOfTerms());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_CHECK_CONFIRMED) {
			CheckConfirmedHttp http = (CheckConfirmedHttp) object;
			if (http.getStatus() == 1) {
				if (!http.getEntity().isIsConfirmed()) {
//				if (http.getEntity().isIsConfirmed()) {
					Intent intent = new Intent(this, VertifyPhoneNumActivity.class);
					startActivity(intent);
				} else {
					confirmedPhoneNum = http.getEntity().getTel();
				}
			}
		}
	}


	private void initPre(PreReadEntity preReadEntity) {
		SocketConstant.REGISTER_STATUE_CODE = 2;
		SocketConstant.CONNENCT_VALUE[3] = RadixAsciiChange.convertStringToHex(SharedUtils.getInstance().readString(Constant.TOKEN));
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 1] = preReadEntity.getPreReadData();
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 2] = preReadEntity.getDataLength();
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5] = preReadEntity.getImsi();
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6] = preReadEntity.getIccid();
	}


	private void getConfigInfo() {
		createHttpRequest(HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG);
	}

	private int requestCount = 0;

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		super.errorComplete(cmdType, errorMessage);
		if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
			if (requestCount < 3) {
				requestCount++;
				getConfigInfo();
			}
		}
	}

	//控制扫描设备
	private void scanLeDevice(final boolean enable) {
		e("scanLeDevice");
		if (enable) {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}

	}

	//扫描结果监听
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {
				@Override
				public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (device.getName() == null) {
								return;
							}
							i("deviceName:" + device.getName());
							if (deviceAddress.equalsIgnoreCase(device.getAddress())) {
								scanLeDevice(false);
								mService.connect(deviceAddress);

							}
						}
					});
				}
			};


	/**
	 * 接收到到卡注册状态作出相应的操作
	 * 连接TCP失败，要做操作。
	 * 断开连接要进行操作。
	 * SocketService关闭要重新开启TCP。
	 * 解除绑定需要销毁TCP
	 *
	 * @param entity
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onIsSuccessEntity(SimRegisterStatue entity) {
		switch (entity.getRigsterSimStatue()) {
			case SocketConstant.REGISTER_SUCCESS:
				createHttpRequest(HttpConfigUrl.COMTYPE_CHECK_CONFIRMED, SharedUtils.getInstance().readString(Constant.ICCID));
				break;
			case SocketConstant.REGISTER_FAIL://注册失败
				rigisterFail(entity.getRigsterStatueReason());
				break;
			case SocketConstant.REGISTERING://注册中
				registering(entity.getRigsterStatueReason());
				break;

			default:

				break;
		}

	}

	private void rigisterFail(int failReason) {
		switch (failReason) {
			case SocketConstant.REGISTER_FAIL_INITIATIVE:
				//更改为注册中
				unbindTcpService();
				destorySocketService();
				break;
		}

	}

	private void registering(int registeringReason) {
		switch (registeringReason) {
			case SocketConstant.START_TCP_FAIL:
				unbindTcpService();
				break;
			case SocketConstant.TCP_DISCONNECT:
				//更改为注册中
				break;
			case SocketConstant.RESTART_TCP:
				startSocketService();
				if (ProMainActivity.sendYiZhengService == null) {
					ProMainActivity.sendYiZhengService = new SendYiZhengService();
				}
				startTcpSocket();
				break;
			case SocketConstant.VAILD_CARD:
				requestPacket();
				break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (actionBar != null)
			actionBar.hide();
		else {
			actionBar = getActionBar();
			if (actionBar != null)
				actionBar.hide();
		}
	}

	private void noPreDataStartSDK() {
		isStartSdk = true;
		startDataframService();
		startSocketService();
		CommonTools.delayTime(5000);
		e("main.start()");
		JNIUtil.getInstance().startSDK(1);
	}

	private void registerSimPreData() {
		if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_SUCCEED) {
			ProMainActivity.sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
		} else if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_FAIL) {
			SocketConnection.mReceiveSocketService.disconnect();
			startTcp();
		} else {
			startTcp();
		}
	}


	private void startTcp() {
		startSocketService();
		startTcpSocket();
		SocketConnection.mReceiveSocketService.setListener(new ReceiveSocketService.CreateSocketLisener() {
			@Override
			public void create() {
				TestProvider.isCreate = true;
				CommonTools.delayTime(500);

				ProMainActivity.sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
			}

		});
	}

	private int bindtime = 0;

	private void startTcpSocket() {
		if (sendYiZhengService != null && SocketConnection.mReceiveSocketService != null) {
			sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);
			return;
		}
		bindTcpSucceed();
	}

	private void bindTcpSucceed() {
		if (SocketConnection.mReceiveSocketService == null) {
			CommonTools.delayTime(1000);
			if (bindtime > 15) {
				return;
			}
			bindtime++;
			startTcpSocket();
		}
		bindtime = 0;
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void cancelCallService(CancelCallService entity) {
		if (intentCallPhone != null) {
			stopService(intentCallPhone);

		}
		unbindTcpService();
		destorySocketService();

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void optionView(OptionProMainActivityView entity) {
		e("isVisibleToUser=" + entity.isShow());
		if (entity.isShow()) {
			bottomFragment.setVisibility(View.VISIBLE);
		} else {
			bottomFragment.setVisibility(View.GONE);
		}
	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)//非UI线程
	public void onServiceOperation(ServiceOperationEntity entity) {
		switch (entity.getOperationType()) {
			case ServiceOperationEntity.REMOVE_SERVICE:
				if (UartService.class.getName().equals(entity.getServiceName())) {
					i("关闭UartService");
					unbindService(mServiceConnection);
				}
				break;
			case ServiceOperationEntity.CREATE_SERVICE:
				if (UartService.class.getName().equals(entity.getServiceName())) {
					initServices();
				}
				break;
		}
	}


	//用于改变indexFragment状态的Receiver
	private BroadcastReceiver showRedDotReceiver = new BroadcastReceiver() {


		@Override
		public void onReceive(final Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(MALL_SHOW_RED_DOT)) {
				e("showRedDotReceiver");
				tvRedDot01.setVisibility(View.VISIBLE);
			}

		}
	};


//	private BroadcastReceiver updateIndexTitleReceiver = new BroadcastReceiver() {
//
//
//		@Override
//		public void onReceive(final Context context, Intent intent) {
//			final String action = intent.getAction();
//			if (action.equals(UartService.FINDED_SERVICE)) {
//				EventBusUtil.showDevice(true);
//			} else if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
//				if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
//					EventBusUtil.showDevice(false);
//				}
//				//判断IMEI是否存在，如果不在了表明已解除绑定，否则就是未连接
//			} else if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
//				ArrayList<String> message = intent.getStringArrayListExtra(UartService.EXTRA_DATA);
//				if (message != null && message.size() == 0 || !message.get(0).substring(0, 2).equals("55")) {
//					return;
//				}
//				//判断是否是分包（0x80的包）
//				if (message != null && message.size() == 0 || !message.get(0).substring(2, 4).equals("80")) {
//					return;
//				}
//				try {
//					String dataType = message.get(0).substring(6, 10);
//					switch (dataType) {
//						case RETURN_POWER:
//							break;
//						case Constant.SYSTEM_BASICE_INFO:
//							//返回基本信息就更新account的仪表盘栏
//							int powerText;
//							powerText = Integer.parseInt(message.get(0).substring(14, 16), 16);
//							String typeText = "";
//							String bracelettype = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
//							if (bracelettype != null) {
//								if (bracelettype.contains(MyDeviceActivity.UNITOYS)) {
//									typeText = getString(R.string.device) + ": " + getString(R.string.unitoy);
//								} else if (bracelettype.contains(MyDeviceActivity.UNIBOX)) {
//									typeText = getString(R.string.device) + ": " + getString(R.string.unibox_key);
//
//								}
//							}
//							accountFragment.setSummarized(typeText, powerText + "", false);
//
//							break;
//						case Constant.RECEIVE_ELECTRICITY:
//							powerText = Integer.parseInt(message.get(0).substring(10, 12), 16);
//							accountFragment.setPowerPercent(powerText + "");
//							break;
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					return;
//				}
//			}
//
//		}
//	};


	private void requestPacket() {
		getConfigInfo();
	}

	//接收蓝牙是否开启
	private BroadcastReceiver screenoffReceive = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR);
				switch (state) {
					case BluetoothAdapter.STATE_OFF:
						d("STATE_OFF 手机蓝牙关闭");
						EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.BLUETOOTH_CLOSE);
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						d("STATE_TURNING_OFF 手机蓝牙正在关闭");
						break;
					case BluetoothAdapter.STATE_ON:
						d("STATE_ON 手机蓝牙开启");
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						d("STATE_TURNING_ON 手机蓝牙正在开启");
						break;
				}
			}
		}
	};


	//空中升级
	private void skyUpgradeHttp() {
		Log.e(TAG, "skyUpgradeHttp");
		int DeviceType;
		String braceletname = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
		if (!TextUtils.isEmpty(braceletname)) {
			if (braceletname.contains(MyDeviceActivity.UNITOYS)) {
				DeviceType = 0;
			} else {
				DeviceType = 1;
			}
			createHttpRequest(HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA, SharedUtils.getInstance().readString(Constant.BRACELETVERSION), DeviceType + "");
		}

	}


}
