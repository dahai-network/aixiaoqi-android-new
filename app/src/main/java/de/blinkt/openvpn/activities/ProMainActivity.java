package de.blinkt.openvpn.activities;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixiaoqi.socket.JNIUtil;
import com.aixiaoqi.socket.ReceiveDataframSocketService;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SocketConnection;
import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FragmentAdapter;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.AccountFragment;
import de.blinkt.openvpn.fragments.AddressListFragment;
import de.blinkt.openvpn.fragments.CellPhoneFragment;
import de.blinkt.openvpn.fragments.Fragment_Phone;
import de.blinkt.openvpn.fragments.IndexFragment;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.IsHavePacketHttp;
import de.blinkt.openvpn.model.IsHavePacketEntity;
import de.blinkt.openvpn.model.IsSuccessEntity;
import de.blinkt.openvpn.service.CallPhoneService;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.ViewUtil;

import static android.R.attr.type;
import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLPHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKHOMECONTACT;

public class ProMainActivity extends BaseNetActivity implements View.OnClickListener {

	private ViewPager mViewPager;
	private TextView[] tvArray = new TextView[5];
	private ImageView[] ivArray = new ImageView[5];
	int viewPagerCurrentPageIndex = 0;
	public static LinearLayout bottom_bar_linearLayout;
	public static LinearLayout phone_linearLayout;
	public static LinearLayout[] llArray = new LinearLayout[5];
	private ImageView phoneNumberImageView;
	private ImageView callImageView;
	private ImageView deleteImageView;
	//判断是否展开了键盘
	public static boolean isDeploy = true;
	private String TAG = "ProMainActivity";
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
	//重连时间
	private long RECONNECT_TIME = 180000;
	SocketConnection socketUdpConnection;
	SocketConnection socketTcpConnection;
	@Override
	public Object getLastCustomNonConfigurationInstance() {
		return super.getLastCustomNonConfigurationInstance();
	}


	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
			mService = ((UartService.LocalBinder) rawBinder).getService();
			//存在Application供全局使用
			ICSOpenVPNApplication.uartService = mService;
			bleMoveReceiver = new ReceiveBLEMoveReceiver();
			LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(bleMoveReceiver, makeGattUpdateIntentFilter());
			LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(updateIndexTitleReceiver, makeGattUpdateIntentFilter());
			Log.d(TAG, "onServiceConnected mService= " + mService);
			if (!mService.initialize()) {
				Log.d(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			//打开蓝牙服务后开始搜索
			searchBLE();
		}

		public void onServiceDisconnected(ComponentName classname) {
			////     mService.disconnect(mDevice);
			mService = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pro_main);
		findViewById();
		initFragment();
		addListener();
		setListener();
		initServices();
		socketUdpConnection = new SocketConnection();
		socketTcpConnection = new SocketConnection();
		//注册eventbus，观察goip注册问题
		EventBus.getDefault().register(this);
	}


	private void searchBLE() {
		/**
		 * 搜索蓝牙步骤：
		 * 1.通过接口询问是否绑定过蓝牙设备
		 * 2.如果有绑定过蓝牙设备，则询问打开蓝牙
		 * 3.打开后，则通过线程做扫描操作。
		 * 4.扫描到设备则连接上，没扫描到十秒后自动断开。关闭所有与之相关的东西
		 */
		GetBindDeviceHttp http = new GetBindDeviceHttp(ProMainActivity.this, HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
		new Thread(http).start();
	}


	private void initServices() {
		Intent bindIntent = new Intent(this, UartService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void startSocketService() {
		if(!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
			Intent receiveSdkIntent = new Intent(this, ReceiveSocketService.class);
			bindService(receiveSdkIntent, socketTcpConnection, Context.BIND_AUTO_CREATE);
		}
	}

	private void startDataframService() {
		if(!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())){
		Intent receiveSdkIntent = new Intent(this, ReceiveDataframSocketService.class);
		bindService(receiveSdkIntent, socketUdpConnection, Context.BIND_AUTO_CREATE);
		}

	}

	public LinearLayout getLlArrayToSport() {
		return llArray[3];
	}

	private void findViewById() {

		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		//两个底部栏
		bottom_bar_linearLayout = (LinearLayout) findViewById(R.id.bottom_bar_linearLayout);
		phone_linearLayout = (LinearLayout) findViewById(R.id.phone_linearLayout);
		//电话底部栏的三个按钮，接入功能
		phoneNumberImageView = (ImageView) findViewById(R.id.phoneNumberImageView);
		callImageView = (ImageView) findViewById(R.id.callImageView);
		deleteImageView = (ImageView) findViewById(R.id.deleteImageView);
		llArray[1] = (LinearLayout) findViewById(R.id.phoneLinearLayout);
		llArray[0] = (LinearLayout) findViewById(R.id.indexLinearLayout);
		llArray[4] = (LinearLayout) findViewById(R.id.accountLinearLayout);
		llArray[2] = (LinearLayout) findViewById(R.id.addressListLienarLayout);
		llArray[3] = (LinearLayout) findViewById(R.id.sportLinearLayout);
		tvArray[1] = (TextView) findViewById(R.id.phoneTextView);
		tvArray[0] = (TextView) findViewById(R.id.indexTextView);
		tvArray[3] = (TextView) findViewById(R.id.sportTextView);
		tvArray[4] = (TextView) findViewById(R.id.accountTextView);
		tvArray[2] = (TextView) findViewById(R.id.addressListTextView);
		ivArray[1] = (ImageView) findViewById(R.id.phoneImageView);
		ivArray[0] = (ImageView) findViewById(R.id.indexImageView);
		ivArray[4] = (ImageView) findViewById(R.id.accountImageView);
		ivArray[2] = (ImageView) findViewById(R.id.addressListImageView);
		ivArray[3] = (ImageView) findViewById(R.id.sportImageView);
		removeAllStatus();
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		return intentFilter;
	}

	private void addListener() {
		LinearLayout[] localLlArray = llArray;
		for (LinearLayout ll : localLlArray) {
			ll.setOnClickListener(this);
		}
		phoneNumberImageView.setOnClickListener(this);
		callImageView.setOnClickListener(this);
		deleteImageView.setOnClickListener(this);
		llArray[0].performClick();
	}


	private void initFragment() {
		if (phoneFragment == null) {
			phoneFragment = Fragment_Phone.newInstance();
		}
		if (indexFragment == null) {
			indexFragment = new IndexFragment();

		}
		if (cellPhoneFragment == null) {
			cellPhoneFragment = new CellPhoneFragment();
			cellPhoneFragment.setFragment_Phone(phoneFragment);

		}
		if (addressListFragment == null) {
			addressListFragment = new AddressListFragment();

		}
		if (sportFragment == null) {
			sportFragment = new SportFragment();

		}
		if (accountFragment == null) {
			accountFragment = new AccountFragment();
		}
		if (list.size() < 5) {
			list.clear();
			list.add(indexFragment);
			list.add(cellPhoneFragment);
			list.add(addressListFragment);
			list.add(sportFragment);
			list.add(accountFragment);
			FragmentAdapter adapter = new FragmentAdapter(
					getSupportFragmentManager(), list);
			mViewPager.setAdapter(adapter);
			mViewPager.setOffscreenPageLimit(5);
		}

	}

	Fragment_Phone phoneFragment;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(this, "蓝牙已启动", Toast.LENGTH_SHORT).show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						final BluetoothManager bluetoothManager =
								(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
						mBluetoothAdapter = bluetoothManager.getAdapter();
						if (mBluetoothAdapter == null) {
							return;
						}
						while (mService.mConnectionState != UartService.STATE_CONNECTED) {
							try {
								scanDeviceFiveSecond();
								Thread.sleep(RECONNECT_TIME);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					}
				}).start();
			} else {
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
				indexFragment.changeBluetoothStatus(getString(R.string.index_blue_un_opne), R.drawable.index_blue_unpen);
			}
		}
	}

	private Handler stopHandler = null;

	//扫描五秒后断连
	private void scanDeviceFiveSecond() {
		scanLeDevice(true);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				indexFragment.changeBluetoothStatus(getResources().getString(R.string.index_connecting), R.drawable.index_connecting);
				if (stopHandler == null) {
					stopHandler = new Handler();
				}
				stopHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						scanLeDevice(false);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (indexFragment.getBlutoothStatus().equals(getResources().getString(R.string.index_unconnect)))
									indexFragment.changeBluetoothStatus(getResources().getString(R.string.index_unconnect), R.drawable.index_unconnect);
							}
						});
					}
				}, 5000);
			}
		});
	}

	private boolean isClick = false;

	@Override
	public void onClick(View v) {
		removeAllStatus();
		int id = v.getId();
		switch (id) {
			case R.id.phoneLinearLayout:
				isClick = true;
				viewPagerCurrentPageIndex = 1;
				if (isDeploy) {
					//如果展开则收回
					ViewUtil.showView(phoneFragment.t9dialpadview);
					ivArray[viewPagerCurrentPageIndex].setBackgroundResource(R.drawable.phone_icon_check);
					isDeploy = false;
				} else if (!isDeploy) {
					//如果展开则收回
					ViewUtil.hideView(phoneFragment.t9dialpadview);
					ivArray[viewPagerCurrentPageIndex].setBackgroundResource(R.drawable.phone_icon_check_open);
					isDeploy = true;

				}
				break;
			case R.id.indexLinearLayout:
				viewPagerCurrentPageIndex = 0;
				break;
			case R.id.addressListLienarLayout:
				viewPagerCurrentPageIndex = 2;
				break;
			case R.id.accountLinearLayout:
				//友盟方法统计
				MobclickAgent.onEvent(this, CLICKHOMECONTACT);
				viewPagerCurrentPageIndex = 4;
				accountFragment.setBleStatus(indexFragment.getBlutoothStatus());
				break;
			case R.id.sportLinearLayout:
				viewPagerCurrentPageIndex = 3;
				break;
			case R.id.phoneNumberImageView:
				if (isDeploy) {
					phoneNumberImageView.setImageResource(R.drawable.phone_icon_check);
				} else {
					phoneNumberImageView.setImageResource(R.drawable.phone_icon_check_open);
				}

				llArray[1].performClick();
				break;
			case R.id.callImageView:
				if (phoneFragment != null) {
					//友盟方法统计
					MobclickAgent.onEvent(this, CLICKCALLPHONE);
					phoneFragment.phonecallClicked();
				}
				break;
			case R.id.deleteImageView:
				if (phoneFragment != null) {
					phoneFragment.dial_delete_btn.performClick();
				}
				break;
		}
		if (!(id == R.id.phoneNumberImageView || id == R.id.callImageView || id == R.id.deleteImageView)) {
			mViewPager.setCurrentItem(viewPagerCurrentPageIndex, false);
			tvArray[viewPagerCurrentPageIndex].setTextColor(getResources().getColor(R.color.bottom_bar_text_enable));
			if (viewPagerCurrentPageIndex != 1) {
				ivArray[viewPagerCurrentPageIndex].setEnabled(true);
			}
		}
	}

	private void removeAllStatus() {
		ivArray[1].setBackgroundResource(R.drawable.phone_icon_uncheck);
		int length = tvArray.length;
		for (int i = 0; i < length; i++) {
			if (i != 1)
				ivArray[i].setEnabled(false);
			tvArray[i].setTextColor(getResources().getColor(R.color.bottom_bar_text_normal));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!ICSOpenVPNApplication.getInstance().isServiceRunning(CallPhoneService.class.getName()))
			startService(new Intent(this, CallPhoneService.class));

	}

	public void hidePhoneBottomBar() {
		ProMainActivity.bottom_bar_linearLayout.setVisibility(View.VISIBLE);
		ProMainActivity.phone_linearLayout.setVisibility(View.GONE);
	}

	private void setListener() {
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position != 1) {
					isClick = false;
					hidePhoneBottomBar();
					llArray[position].performClick();
				} else {
					if (!isClick) {
						removeAllStatus();
						if (phoneFragment != null && phoneFragment.t9dialpadview != null && phoneFragment.t9dialpadview.getVisibility() == View.VISIBLE) {
							ivArray[1].setBackgroundResource(R.drawable.phone_icon_check);
						} else {
							if (phoneFragment == null) {
								phoneFragment = Fragment_Phone.newInstance();
							}
							ivArray[1].setBackgroundResource(R.drawable.phone_icon_check_open);
						}

						tvArray[1].setTextColor(getResources().getColor(R.color.bottom_bar_text_enable));
						mViewPager.setCurrentItem(1);
					}
				}


			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
//
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);

		}
		return true;
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(bleMoveReceiver);
		LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(updateIndexTitleReceiver);
		bleMoveReceiver = null;
		stopService(new Intent(this, CallPhoneService.class));
		//关闭服务并设置为null

		destorySocketService();
		if (mService != null)
			mService.stopSelf();
		mService = null;
		bottom_bar_linearLayout = null;
		phone_linearLayout = null;
		list.clear();
		indexFragment = null;
		cellPhoneFragment = null;
		accountFragment = null;
		addressListFragment = null;
		sportFragment = null;
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private void destorySocketService() {
		if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName()))
			unbindService(socketTcpConnection);
//		if (SocketConnection.mReceiveDataframSocketService != null) {
//			SocketConnection.mReceiveDataframSocketService.stopSelf();
//		}
		if (SocketConnection.mReceiveSocketService != null) {
			SocketConnection.mReceiveSocketService.stopSelf();
		}
		if(SocketConstant.REGISTER_STATUE_CODE!=0){
			SocketConstant.REGISTER_STATUE_CODE=1;
		}
	}

	@Override
	public void rightComplete(int cmdType, final CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
			GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
			if (object.getStatus() == 1) {
				if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
					deviceAddress = getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI();
					SharedUtils utils = SharedUtils.getInstance();
					utils.writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI());
					utils.writeString(Constant.BRACELETVERSION, getBindDeviceHttp.getBlueToothDeviceEntityity().getVersion());
					Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				}
			} else {
				indexFragment.changeBluetoothStatus(getString(R.string.index_unbind), R.drawable.index_unbind);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET) {
			if (object.getStatus() == 1) {
				IsHavePacketHttp isHavePacketHttp = (IsHavePacketHttp) object;
				IsHavePacketEntity entity = isHavePacketHttp.getOrderDataEntity();
				if (entity.getUsed() == 1) {
					indexFragment.changeBluetoothStatus(getString(R.string.index_registing), R.drawable.index_no_signal);
				} else {
					//检测是否有套餐，没有责显示新状态
					SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, true);
					indexFragment.changeBluetoothStatus(getString(R.string.index_no_packet), R.drawable.index_no_packet);
				}
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		}
	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}

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
//									addDevice(device,rssi);
									if (device.getName() == null) {
										return;
									}
									Log.i("test", "deviceName:" + device.getName());
									if (deviceAddress.equals(device.getAddress())) {
										mService.connect(deviceAddress);
									}
								}
							});

						}
					});
				}
			};


	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onIsSuccessEntity(IsSuccessEntity entity) {
		if (entity.getType() == Constant.REGIST_CALLBACK_TYPE) {
			if (entity.isSuccess()) {
				indexFragment.changeBluetoothStatus(getString(R.string.index_high_signal), R.drawable.index_high_signal);
			} else {
				destorySocketService();
				switch (type) {
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
			if(entity.getFailType()!=SocketConstant.REGISTER_FAIL_INITIATIVE) {
				indexFragment.changeBluetoothStatus(getString(R.string.index_regist_fail), R.drawable.index_no_signal);
				CommonTools.showShortToast(this, getString(R.string.regist_fail_tips));
			}
			destorySocketService();
		}
	}


	//用于改变indexFragment状态的Receiver
	private BroadcastReceiver updateIndexTitleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				//当有通话套餐的时候才允许注册操作
				IsHavePacketHttp http = new IsHavePacketHttp(ProMainActivity.this, HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET, "1");
				new Thread(http).start();

				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//测试：当刚连接的时候，因为测试阶段没有连接流程所以连通上就等于连接上。
				startDataframService();
				startSocketService();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Log.e("phoneAddress", "main.start()");
						JNIUtil.getInstance().startSDK(SharedUtils.getInstance().readString(Constant.USER_NAME));
					}
				}).start();
			} else if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
				indexFragment.changeBluetoothStatus(getString(R.string.index_unconnect), R.drawable.index_unconnect);
			} else if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
				final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);
				//判断是否是分包（BB开头的包）
				if (txValue[0] != (byte) 0xBB && txValue[0] != (byte) 0xAA) {
					return;
				}
				if (txValue[0] == (byte) 0xBB) {
					if (txValue[1] == (byte) 0x01) {
						if (txValue[3] == (byte) 0x03) {
						}
					} else if (txValue[1] == (byte) 0x33) {
						checkRegisterStatuGoIp();
					} else if (txValue[1] == (byte) 0x11) {
						indexFragment.changeBluetoothStatus(getString(R.string.index_un_insert_card), R.drawable.index_uninsert_card);
					} else if (txValue[1] == (byte) 0xEE) {
						if (SharedUtils.getInstance().readBoolean(Constant.ISHAVEORDER)) {
							checkRegisterStatuGoIp();
						} else {
							indexFragment.changeBluetoothStatus(getString(R.string.index_no_packet), R.drawable.index_no_packet);
						}
					}
				}
			}
		}
	};

	//是否注册成功，如果是则信号强，反之则信号弱
	private void checkRegisterStatuGoIp() {
		if (REGISTER_STATUE_CODE == 1) {
			indexFragment.changeBluetoothStatus(getString(R.string.index_registing), R.drawable.index_no_signal);
		} else if (REGISTER_STATUE_CODE != 3) {
			indexFragment.changeBluetoothStatus(getString(R.string.index_no_signal), R.drawable.index_no_signal);
		} else {
			indexFragment.changeBluetoothStatus(getString(R.string.index_high_signal), R.drawable.index_high_signal);
		}
	}
}
