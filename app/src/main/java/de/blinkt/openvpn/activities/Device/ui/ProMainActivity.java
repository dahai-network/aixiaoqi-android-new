package de.blinkt.openvpn.activities.Device.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
import android.os.Looper;
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
import com.aixiaoqi.socket.SocketConstant;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FragmentAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.CommomModel.JPush.ModelImpl.JPushSetAliaModelImpl;
import de.blinkt.openvpn.activities.Device.PresenterImpl.ProMainPresenterImpl;
import de.blinkt.openvpn.activities.Device.View.ProMainView;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ui.AccountFragment;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ui.AddressListFragment;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ui.CellPhoneFragment;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ui.IndexFragment;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.model.CanClickEntity;
import de.blinkt.openvpn.service.CallPhoneService;
import de.blinkt.openvpn.service.GrayService;
import de.blinkt.openvpn.util.CheckAuthorityUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.PageChangeListener;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.CustomViewPager;
import de.blinkt.openvpn.views.MyRadioButton;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;


public class ProMainActivity extends BaseActivity implements ProMainView, DialogInterfaceTypeBase {

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
	private UartService mService = null;
	private int REQUEST_ENABLE_BT = 2;
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
	ProMainPresenterImpl proMainPresenter;
	//位置权限提示DIALOG
	private DialogBalance noLocationPermissionDialog;

	@Override
	public void showHotDot(int isVisible) {
		tvRedDot04.setVisibility(isVisible);
	}

	@Override
	public void showToast(String showContent) {
		super.showToast(showContent);
	}

	@Override
	public void showToast(int showContentId) {
		super.showToast(showContentId);
	}

	@Override
	public Object getLastCustomNonConfigurationInstance() {
		return super.getLastCustomNonConfigurationInstance();
	}

	//绑定UartService服务
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
			d("onServiceConnected mService= " + mService);
			mService = ((UartService.LocalBinder) rawBinder).getService();
			//存在Application供全局使用
			ICSOpenVPNApplication.uartService = mService;

			if (!mService.initialize()) {
				finish();
			}
			searchBLE();

		}
		public void onServiceDisconnected(ComponentName classname) {
			d("onServiceDisconnected mService= " + mService);
			mService = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		instance = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pro_main);
		if(!Constant.JPUSH_ALIAS_SUCCESS.equals(SharedUtils.getInstance().readString(Constant.JPUSH_ALIAS))){
			new JPushSetAliaModelImpl().setJPushAlia("aixiaoqi" + SharedUtils.getInstance().readString(Constant.TOKEN));
		}
		ButterKnife.bind(this);
		initServices();
		proMainPresenter=new ProMainPresenterImpl(this,this);
		initBrocast();
		initSet();
		initFragment();
		initView();
		setListener();

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
		CheckAuthorityUtil.checkPermissions(this, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG);
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		radiogroup.check(R.id.rb_phone);
		radiogroup.setOnCheckedChangeListener(new MyRadioGroupListener());
	}

	//初始化广播
	private void initBrocast() {
		//打开蓝牙服务后开始搜索
		LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(showRedDotReceiver, showRedDotIntentFilter());
		proMainPresenter.registerBlueChangeBroadcast();
		proMainPresenter.registerReceiveBroadcast();
	}

	/**
	 * 搜索蓝牙步骤：
	 * 1.通过接口询问是否绑定过蓝牙设备
	 * 2.如果有绑定过蓝牙设备，则询问打开蓝牙
	 * 3.打开后，则通过线程做扫描操作。
	 * 4.扫描到设备则连接上，没扫描到十秒后自动断开。关闭所有与之相关的东西
	 */
	private void searchBLE() {
		if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI)) || TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))) {
			proMainPresenter.requestGetBindDeviceInfo();
		} else {
			skyUpgradeHttp();
			blueToothOpen();
		}
	}

	@Override
	public void blueToothOpen() {
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
			bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		}else{
			if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI)) && !TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))&&mService!=null&&mService.isDisconnectedBlueTooth()) {
				blueToothOpen();
			}
		}
		//启动常驻服务
		if (!ICSOpenVPNApplication.getInstance().isServiceRunning(GrayService.class.getName())) {
			startService(new Intent(this, GrayService.class));
		}

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
	//要求用户打开蓝牙和GPS
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
	//30秒钟内没有连接成功则提示用户，可能设备不在周边
	private void connectOperate() {
		if(mService == null||!mService.initialize()){
			return;
		}
		final String imeiStr = SharedUtils.getInstance().readString(Constant.IMEI);
		if(TextUtils.isEmpty(imeiStr)){
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (mService != null && mService.mConnectionState != UartService.STATE_CONNECTED) {
					connDeviceFiveSecond(imeiStr);
					CommonTools.delayTime(RECONNECT_TIME);
				}
			}
		}).start();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mService != null && !mService.isConnectedBlueTooth()) {
					EventBusUtil.canClickEntity(CanClickEntity.JUMP_MYDEVICE);
				}
			}
		}, 30000);
	}

	//如果没有连上，每隔10秒重连一次
	private void connDeviceFiveSecond(String imeiStr) {
			mService.connect(imeiStr);
			EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.CONNECTING_DEVICE);
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
		e("onResume " + SharedUtils.getInstance().readBoolean(IntentPutKeyConstant.CLICK_MALL));
		tvRedDot01.setVisibility(SharedUtils.getInstance().readBoolean(IntentPutKeyConstant.CLICK_MALL)?View.VISIBLE:View.INVISIBLE);
		basicConfigHttp();
	}

	private void basicConfigHttp() {
		proMainPresenter.requestGetBasicConfig();
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
		proMainPresenter.onDestory();
		radiogroup = null;
		if (mService != null)
			mService.stopSelf();
		mService = null;
		radiogroup = null;
		list.clear();
		super.onDestroy();
	}
	@Override
	public void stopCallPhoneService() {
		if (intentCallPhone != null){
			stopService(intentCallPhone);
			intentCallPhone=null;
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

	@Override
	public void bottomFragmentIsShow(int isVisible) {
		bottomFragment.setVisibility(isVisible);
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

	//空中升级
	private void skyUpgradeHttp() {
		proMainPresenter.requestSkyUpdate();
	}


}
