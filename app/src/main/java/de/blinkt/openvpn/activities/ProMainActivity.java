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
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.umeng.analytics.MobclickAgent;

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
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.DBHelp;
import de.blinkt.openvpn.fragments.AccountFragment;
import de.blinkt.openvpn.fragments.AddressListFragment;
import de.blinkt.openvpn.fragments.CellPhoneFragment;
import de.blinkt.openvpn.fragments.Fragment_Phone;
import de.blinkt.openvpn.fragments.IndexFragment;
import de.blinkt.openvpn.fragments.SmsFragment;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.GetHostAndPortHttp;
import de.blinkt.openvpn.http.IsHavePacketHttp;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.IsHavePacketEntity;
import de.blinkt.openvpn.model.PreReadEntity;
import de.blinkt.openvpn.model.ServiceOperationEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.StartRegistEntity;
import de.blinkt.openvpn.model.StateChangeEntity;
import de.blinkt.openvpn.service.CallPhoneService;
import de.blinkt.openvpn.service.GrayService;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.ViewUtil;
import de.blinkt.openvpn.views.CustomViewPager;
import de.blinkt.openvpn.views.MyRadioButton;
import de.blinkt.openvpn.views.TopProgressView;

import static cn.com.aixiaoqi.R.string.index_registing;
import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;
import static de.blinkt.openvpn.constant.Constant.RETURN_POWER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCALLPHONE;

public class ProMainActivity extends BaseNetActivity implements View.OnClickListener, View.OnLongClickListener {

    public static ProMainActivity instance = null;
    @BindView(R.id.mViewPager)
    CustomViewPager mViewPager;
    @BindView(R.id.callImageView)
    ImageView callImageView;
    @BindView(R.id.rb_index)
    MyRadioButton rbIndex;
    @BindView(R.id.rb_phone)
    MyRadioButton rbPhone;
    @BindView(R.id.rb_address)
    MyRadioButton rbAddress;
    @BindView(R.id.rb_personal)
    MyRadioButton rbPersonal;
    /**
     * 拨打电话按钮
     */
    public static RelativeLayout phone_linearLayout;
    @BindView(R.id.iv_putaway)
    public ImageView iv_putaway;
    @BindView(R.id.topProgressView)
    public TopProgressView topProgressView;
    //判断是否展开了键盘
    public static boolean isDeploy = true;
    @BindView(R.id.tv_red_dot_01)
    TextView tvRedDot01;
//    @BindView(R.id.tv_red_dot_02)
//    TextView tvRedDot02;
//    @BindView(R.id.tv_red_dot_03)
//    TextView tvRedDot03;
    @BindView(R.id.tv_red_dot_04)
    TextView tvRedDot04;
    public static RadioGroup radiogroup;
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
    // public static LinearLayout bottom_bar_linearLayout;
    Intent intentCallPhone;
    public static boolean isForeground = false;
    public static final String MALL_SHOW_RED_DOT = "mall_show_red_dot";

    //重连时间
    private int RECONNECT_TIME = 180000;
    SocketConnection socketUdpConnection;
    SocketConnection socketTcpConnection;
    public static String STOP_CELL_PHONE_SERVICE = "stopservice";
    public static boolean isStartSdk = false;
    public static SdkAndBluetoothDataInchange sdkAndBluetoothDataInchange = null;
    public static SendYiZhengService sendYiZhengService = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    tvRedDot04.setVisibility(View.VISIBLE);
                    noticeNewVersion(3);
                    break;
                case 2:
                    tvRedDot04.setVisibility(View.GONE);
                    noticeNewVersion(4);
                    break;


            }

        }
    };

    @Override
    public Object getLastCustomNonConfigurationInstance() {
        return super.getLastCustomNonConfigurationInstance();
    }

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
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_main);
        ButterKnife.bind(this);
        findViewById();
        initFragment();
        initView();
        addListener();
        setListener();
        initBrocast();
        initServices();
       // initData();
        socketUdpConnection = new SocketConnection();
        socketTcpConnection = new SocketConnection();
        //注册eventbus，观察goip注册问题
        EventBus.getDefault().register(this);
    }

    private void initData() {
    }


    /**
     * \初始化界面
     */
    private void initView() {
        radiogroup.check(R.id.rb_index);
        radiogroup.setOnCheckedChangeListener(new MyRadioGroupListener());
        //无网络时候提醒
        if (!NetworkUtils.isNetworkAvailable(this)) {
            topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
        }
//        initRedDotView();
    }

    private void findViewsById() {
        phone_linearLayout = (RelativeLayout) findViewById(R.id.phone_linearLayout);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
    }




    /**
     * 通知我的界面是否有新的固件包
     */
    public void noticeNewVersion(int state) {

        Log.d("__aixiaoqi", "noticeNewVersion: ");
        EventBus.getDefault().post(new ChangeViewStateEvent(state));
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


    private void initBrocast() {
        LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(showRedDotReceiver, showRedDotIntentFilter());
        if (bleMoveReceiver == null) {
            bleMoveReceiver = new ReceiveBLEMoveReceiver();
            LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(bleMoveReceiver, makeGattUpdateIntentFilter());
            LocalBroadcastManager.getInstance(ProMainActivity.this).registerReceiver(updateIndexTitleReceiver, makeGattUpdateIntentFilter());
            registerReceiver(screenoffReceive, screenoffIntentFilter());
            //打开蓝牙服务后开始搜索
            searchBLE();
        }
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
        }
        return true;
    }

    public void initServices() {
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(UartService.class.getName())) {
            i("开启UartService");
            Intent bindIntent = new Intent(this, UartService.class);
            bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
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


    private void findViewById() {
        //主界面下栏
        // bottom_bar_linearLayout = (LinearLayout) findViewById(R.id.bottom_bar_linearLayout);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        //拨打电话下栏
        phone_linearLayout = (RelativeLayout) findViewById(R.id.phone_linearLayout);
        //隐藏拨号界面控件
        iv_putaway = (ImageView) findViewById(R.id.iv_putaway);
        topProgressView = (TopProgressView) findViewById(R.id.topProgressView);
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(ProMainActivity.STOP_CELL_PHONE_SERVICE);
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

    private void addListener() {
        callImageView.setOnClickListener(this);
        iv_putaway.setOnClickListener(this);
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
        if (accountFragment == null) {
            accountFragment = new AccountFragment();
        }
        if (list.size() < 5) {
            list.clear();
            list.add(indexFragment);
            list.add(cellPhoneFragment);
            list.add(addressListFragment);
            list.add(accountFragment);
            FragmentAdapter adapter = new FragmentAdapter(
                    getSupportFragmentManager(), list);
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(4);
        }

    }

    Fragment_Phone phoneFragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                CommonTools.showShortToast(this, "蓝牙已启动");
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
            } else {
                sendEventBusChangeBluetoothStatus(getString(R.string.index_blue_un_opne), R.drawable.index_blue_unpen);
            }
        }
    }

    private Handler stopHandler = null;

    //扫描五秒后提示
    private void connDeviceFiveSecond() {
        mService.connect(SharedUtils.getInstance().readString(Constant.IMEI));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendEventBusChangeBluetoothStatus(getResources().getString(R.string.index_connecting), R.drawable.index_connecting);
                if (stopHandler == null) {
                    stopHandler = new Handler();
                }
                stopHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanLeDevice(false);
                        if (mService != null && !mService.isConnectedBlueTooth()) {
                            topProgressView.showTopProgressView(getString(R.string.un_connect_tip), -1, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String braceletName = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
                                    if (braceletName != null) {
                                        Intent intent = new Intent(ProMainActivity.this, MyDeviceActivity.class);
                                        intent.putExtra(MyDeviceActivity.BRACELETTYPE, braceletName);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                }, 10000);
            }
        });
    }

    private int clickCount = 0;
    private int scrollCount = 0;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.phoneLinearLayout:
                if (CellPhoneFragment.floatingActionButton.getVisibility() != View.VISIBLE && phoneFragment.t9dialpadview.getVisibility() != View.VISIBLE) {
                    if (SmsFragment.editSmsImageView != null) {
                        if (SmsFragment.editSmsImageView.getVisibility() != View.VISIBLE) {
                            ViewUtil.hideView(phoneFragment.t9dialpadview);
                            CellPhoneFragment.floatingActionButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
            //拨打电话
            case R.id.callImageView:
                CellPhoneFragment.floatingActionButton.setVisibility(View.VISIBLE);
                ViewUtil.hideView(phoneFragment.t9dialpadview);
                hidePhoneBottomBar();
                if (phoneFragment != null) {
                    //友盟方法统计
                    MobclickAgent.onEvent(this, CLICKCALLPHONE);
                    phoneFragment.phonecallClicked();
                }
                break;
            case R.id.iv_putaway:
                CellPhoneFragment.floatingActionButton.setVisibility(View.VISIBLE);
                ViewUtil.hideView(phoneFragment.t9dialpadview);
                hidePhoneBottomBar();
                break;
        }

    }


    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(CallPhoneService.class.getName())) {
            intentCallPhone = new Intent(this, CallPhoneService.class);
            startService(intentCallPhone);
        }


        if (!SharedUtils.getInstance().readBoolean(IntentPutKeyConstant.CLICK_MALL, true)) {
			tvRedDot01.setVisibility(View.VISIBLE);
        }else{
			tvRedDot01.setVisibility(View.INVISIBLE);
		}
    }

    public void hidePhoneBottomBar() {
        ProMainActivity.radiogroup.setVisibility(View.VISIBLE);
        ProMainActivity.phone_linearLayout.setVisibility(View.GONE);

    }


    @Override
    protected void onStop() {
        super.onStop();
        isForeground = false;
    }

    public int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void setListener() {

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //对切换的状态进行保存
                setPosition(position);
                topProgressView.setWhiteBack(false);
                topProgressView.invalidate();
                if (phoneFragment != null && phoneFragment.t9dialpadview != null && phoneFragment.t9dialpadview.getVisibility() == View.VISIBLE) {
                    phoneFragment.t9dialpadview.clearT9Input();
                }

                hidePhoneBottomBar();
                switch (position) {
                    case 0:
                        radiogroup.check(R.id.rb_index);
						SharedUtils.getInstance().writeBoolean(IntentPutKeyConstant.CLICK_MALL,true);
						tvRedDot01.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        radiogroup.check(R.id.rb_phone);

                        if (phoneFragment != null && phoneFragment.t9dialpadview != null && phoneFragment.t9dialpadview.getVisibility() == View.VISIBLE) {
                            //隐藏键盘，清理数据

                        } else {
                            if (phoneFragment == null) {
                                phoneFragment = Fragment_Phone.newInstance();
                            }
                        }
                        if (clickCount == 0 && scrollCount == 0) {
                            scrollCount++;
                        }
                        if (phoneFragment != null && phoneFragment.t9dialpadview != null && phoneFragment.t9dialpadview.getVisibility() == View.VISIBLE) {
                            //隐藏键盘
                            ViewUtil.hideView(phoneFragment.t9dialpadview);
                        }
                        break;
                    case 2:
                        radiogroup.check(R.id.rb_address);
                        // MobclickAgent.onEvent(this, CLICKHOMECONTACT);
                        break;
                    case 3:
                        topProgressView.setWhiteBack(true);
                        topProgressView.invalidate();
                        radiogroup.check(R.id.rb_personal);
                        //  MobclickAgent.onEvent(this, CLICKHOMECONTACT);
                        break;


                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

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


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(bleMoveReceiver);
        LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(updateIndexTitleReceiver);
        unregisterReceiver(screenoffReceive);
        bleMoveReceiver = null;
        //bottom_bar_linearLayout = null;
        radiogroup = null;
        screenoffReceive = null;
        if (intentCallPhone != null)
            stopService(intentCallPhone);
        //关闭服务并设置为null
//		if (isDfuServiceRunning()) {
//			stopService(new Intent(this, DfuService.class));
//		}

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

    private void unbindTcpService() {
        if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
            unbindService(socketTcpConnection);
            if (SocketConnection.mReceiveSocketService != null) {
                SocketConnection.mReceiveSocketService.stopSelf();
                SocketConnection.mReceiveSocketService = null;
            }
        }
    }

    private void destorySocketService() {
        if (SocketConstant.REGISTER_STATUE_CODE != 0) {
            SocketConstant.REGISTER_STATUE_CODE = 1;
        }
    }

    @Override
    public void rightComplete(int cmdType, final CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
            GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
            if (object.getStatus() == 1) {
                if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
                    if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
                        deviceAddress = getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI();
                        if (deviceAddress != null) {
                            deviceAddress = deviceAddress.toUpperCase();
                            BluetoothConstant.IS_BIND = true;
                            accountFragment.showDeviceSummarized(true);
                            skyUpgradeHttp();
                        }
                        SharedUtils utils = SharedUtils.getInstance();

                        utils.writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI().toUpperCase());
                        utils.writeString(Constant.BRACELETVERSION, getBindDeviceHttp.getBlueToothDeviceEntityity().getVersion());
                        //防止返回“”或者null
                        String deviceTypeStr = getBindDeviceHttp.getBlueToothDeviceEntityity().getDeviceType();
                        if (!TextUtils.isEmpty(deviceTypeStr)) {
                            int deviceType = Integer.parseInt(deviceTypeStr);
                            if (deviceType == 0) {
                                utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNITOYS);
                            } else {
                                utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNIBOX);
                            }
                        }
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    } else {
                        sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind), R.drawable.index_unbind);
//						setTipsOnNoBind();
                    }
                } else {
                    sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind), R.drawable.index_unbind);
//					setTipsOnNoBind();
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
                    if (SocketConstant.REGISTER_STATUE_CODE != 3) {
                        getConfigInfo();
                        sendEventBusChangeBluetoothStatus(getString(R.string.index_no_signal), R.drawable.index_no_signal);
                    } else {
                        sendEventBusChangeBluetoothStatus(getString(R.string.index_high_signal), R.drawable.index_high_signal);
                    }
                } else {
                    //TODO 没有通知到设备界面
                    //如果是没有套餐，则通知我的设备界面更新状态并且停止转动
                    SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, false);
                    sendEventBusChangeBluetoothStatus(getString(R.string.index_no_packet), R.drawable.index_no_packet);
                }
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
            GetHostAndPortHttp http = (GetHostAndPortHttp) object;
            if (http.getStatus() == 1) {
                e("端口号");
                requestCount = 0;
                if (http.getGetHostAndPortEntity().getVswServer().getIp() != null) {
                    SocketConstant.hostIP = http.getGetHostAndPortEntity().getVswServer().getIp();
                    SocketConstant.port = http.getGetHostAndPortEntity().getVswServer().getPort();
                    if (SocketConstant.REGISTER_STATUE_CODE == 2) {
                        sendEventBusChangeBluetoothStatus(getString(R.string.index_registing), R.drawable.index_no_signal);
//						return;
                    } else if (SocketConstant.REGISTER_STATUE_CODE == 3) {
                        sendEventBusChangeBluetoothStatus(getString(R.string.index_high_signal), R.drawable.index_high_signal);
//						return;
                    }
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
                                    initPre(preReadEntity);
                                    registerSimPreData();
                                } else {
                                    noPreDataStartSDK();
                                }
                            }
                        }
                    }).start();
                }
            } else {
                CommonTools.showShortToast(this, object.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
            SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;

            Log.d("__aixiaoqi", "rightComplete: "+skyUpgradeHttp);


            if (skyUpgradeHttp.getUpgradeEntity().getVersion() > Float.parseFloat(SharedUtils.getInstance().readString(Constant.BRACELETVERSION))) {
                Log.d("__aixiaoqi", "rightComplete: " + "有新的版本");
                mHandler.sendEmptyMessage(1);

            } else {
                Log.d("__aixiaoqi", "rightComplete: " + "已经是最新的");
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

    //没有绑定提示
    private void setTipsOnNoBind() {
        topProgressView.showTopProgressView(getString(R.string.unbind_device_tips), -1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProMainActivity.this, ChoiceDeviceTypeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getConfigInfo() {
        createHttpRequest(HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG);
    }

    private int requestCount = 0;

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        super.errorComplete(cmdType, errorMessage);
        if (cmdType == HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET) {
            if (requestCount < 3) {
                requestCount++;
                requestPacket();
            } else {
                EventBusUtil.simRegisterStatue(SocketConstant.NOT_NETWORK);
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
            if (requestCount < 3) {
                requestCount++;
                getConfigInfo();
            } else {
                EventBusUtil.simRegisterStatue(SocketConstant.NOT_NETWORK);
            }
        }
    }


    private void scanLeDevice(final boolean enable) {
        e("scanLeDevice");
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


    @Subscribe(threadMode = ThreadMode.MAIN)//ui线程
    public void onIsSuccessEntity(SimRegisterStatue entity) {

//			if (entity.isSuccess()) {

//			} else {
//				sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail), R.drawable.index_no_signal);
        switch (entity.getRigsterSimStatue()) {
            case SocketConstant.REGISTER_SUCCESS:
                sendEventBusChangeBluetoothStatus(getString(R.string.index_high_signal), R.drawable.index_high_signal);
                topProgressGone();
                accountFragment.setRegisted(true);
                break;
            case SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA:
                CommonTools.showShortToast(this, getString(R.string.index_regist_fail));
                topProgressGone();
                break;
            case SocketConstant.REGISTER_FAIL:
                CommonTools.showShortToast(this, getString(R.string.regist_fail));
                topProgressGone();
                break;
            case SocketConstant.REGISTER_FAIL_IMSI_IS_NULL:
                CommonTools.showShortToast(this, getString(R.string.regist_fail_card_invalid));
                topProgressGone();
                break;
            case SocketConstant.REGISTER_FAIL_IMSI_IS_ERROR:
                CommonTools.showShortToast(this, getString(R.string.regist_fail_card_operators));
                topProgressGone();
                break;
            case SocketConstant.NOT_NETWORK:
                CommonTools.showShortToast(this, getString(R.string.check_net_work_reconnect));
                break;
            case SocketConstant.START_TCP_FAIL:
                unbindTcpService();
                CommonTools.showShortToast(this, getString(R.string.check_net_work_reconnect));
                break;
            case SocketConstant.TCP_DISCONNECT:
                //更改为注册中
                sendEventBusChangeBluetoothStatus(getString(index_registing), R.drawable.index_no_signal);

                break;
            case SocketConstant.REGISTER_FAIL_INITIATIVE:
                //更改为注册中
                unbindTcpService();
                destorySocketService();
                sendEventBusChangeBluetoothStatus(getString(R.string.index_unconnect), R.drawable.index_unconnect);
                break;
            case SocketConstant.RESTART_TCP:
                sendEventBusChangeBluetoothStatus(getString(index_registing), R.drawable.index_no_signal);
                startSocketService();
                if (ProMainActivity.sendYiZhengService == null) {
                    ProMainActivity.sendYiZhengService = new SendYiZhengService();
                }
                startTcpSocket();
                break;
            case SocketConstant.REG_STATUE_CHANGE:
                sendEventBusChangeBluetoothStatus(getString(index_registing), R.drawable.index_no_signal);
                break;
            case SocketConstant.REGISTER_CHANGING:
                double percent = entity.getProgressCount();
                if (topProgressView.getVisibility() != View.VISIBLE) {
                    topProgressView.setVisibility(View.VISIBLE);
                    topProgressView.setContent(getString(R.string.registing));
                    topProgressView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String braceletName = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
                            if (braceletName != null) {
                                Intent intent = new Intent(ProMainActivity.this, MyDeviceActivity.class);
                                intent.putExtra(MyDeviceActivity.BRACELETTYPE, braceletName);
                                startActivity(intent);
                            }
                        }
                    });
                }
                int percentInt = (int) (percent / 1.6);
                if (percentInt >= 100) {
                    percentInt = 98;
                }
                topProgressView.setProgress(percentInt);
                break;
            default:
//						if (entity 355.getRigsterSimStatue() != SocketConstant.REGISTER_FAIL_INITIATIVE) {
//							sendEventBusChangeBluetoothStatus(getString(R.string.index_regist_fail), R.drawable.index_no_signal);
//							CommonTools.showShortToast(this, getString(R.string.regist_fail_tips));
//						}
                break;
        }

    }

    private void topProgressGone() {
        topProgressView.setVisibility(View.GONE);
        topProgressView.setProgress(0);
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

    /**
     * 修改蓝牙连接状态，通过EVENTBUS发送到各个页面。
     */
    private void sendEventBusChangeBluetoothStatus(String status, int statusDrawableInt) {
        ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
        entity.setStatus(status);
        entity.setStatusDrawableInt(statusDrawableInt);
        EventBus.getDefault().post(entity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveConnectStatus(ChangeConnectStatusEntity entity) {
        accountFragment.setBleStatus(entity.getStatus());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveConnectStatus(StartRegistEntity entity) {
        if (entity.isRegist()) {
            //当有通话套餐的时候才允许注册操作
            requestPacket();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveStateChangeEntity(StateChangeEntity entity) {
        switch (entity.getStateType()) {
            case StateChangeEntity.BLUETOOTH_STATE:
                if (entity.isopen() && getString(R.string.bluetooth_unopen).equals(topProgressView.getContent())) {
                    if (checkNetWorkAndBlueIsOpen())
                        topProgressView.setVisibility(View.GONE);
                } else {
                    topProgressView.showTopProgressView(getString(R.string.bluetooth_unopen), -1, null);
                }
                break;
            case StateChangeEntity.NET_STATE:
                if (entity.isopen() && getString(R.string.no_wifi).equals(topProgressView.getContent())) {
                    if (checkNetWorkAndBlueIsOpen())
                        topProgressView.setVisibility(View.GONE);
                } else {
                    topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
                }
                break;
        }

    }

    //打开一个开关的同时，检查是否有别的开关是否关闭
    private boolean checkNetWorkAndBlueIsOpen() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
            return false;
        } else if (!mService.isOpenBlueTooth()) {
            topProgressView.showTopProgressView(getString(R.string.bluetooth_unopen), -1, null);
            return false;
        }
        return true;
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
				tvRedDot01.setVisibility(View.VISIBLE);
            }

        }
    };


    private BroadcastReceiver updateIndexTitleReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(UartService.FINDED_SERVICE)) {
                MyDeviceActivity.isConnectOnce = true;
                accountFragment.showDeviceSummarized(true);
            } else if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                if (!ICSOpenVPNApplication.isConnect) {
                    accountFragment.showDeviceSummarized(false);
                    accountFragment.setRegisted(false);
                    topProgressGone();
                } else {
                    mService.connect(SharedUtils.getInstance().readString(Constant.IMEI));
                }
                i("被主动断掉连接！");
                //判断IMEI是否存在，如果不在了表明已解除绑定，否则就是未连接
                if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
                    sendEventBusChangeBluetoothStatus(getString(R.string.index_unconnect), R.drawable.index_unconnect);
                } else {
                    sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind), R.drawable.index_unbind);
                }
            } else if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                ArrayList<String> message = intent.getStringArrayListExtra(UartService.EXTRA_DATA);
                if (message != null && message.size() == 0 || !message.get(0).substring(0, 2).equals("55")) {
                    return;
                }
                //判断是否是分包（0x80的包）
                if (message != null && message.size() == 0 || !message.get(0).substring(2, 4).equals("80")) {
                    return;
                }
                try {
                    String dataType = message.get(0).substring(6, 10);
                    switch (dataType) {
                        case RETURN_POWER:
//							e("进入0700 ProMainActivity");
//							if (message.get(0).substring(10, 12).equals("03")) {
//
//								if (IS_TEXT_SIM && !CommonTools.isFastDoubleClick(300)) {
//									//当有通话套餐的时候才允许注册操作
//									requestPacket();
//								}
//							} else if (message.get(0).substring(10, 12).equals("13")) {
//								sendEventBusChangeBluetoothStatus(getString(R.string.index_un_insert_card), R.drawable.index_uninsert_card);
//							}
                            break;
                        case Constant.SYSTEM_BASICE_INFO:
                            //返回基本信息就更新account的仪表盘栏
                            String typeText;
                            int powerText;
                            powerText = Integer.parseInt(message.get(0).substring(14, 16), 16);
                            String bracelettype = SharedUtils.getInstance().readString(MyDeviceActivity.BRACELETTYPE);
                            if (MyDeviceActivity.UNIBOX.equals(bracelettype)) {
                                typeText = getString(R.string.device) + ": " + getString(R.string.unibox_key);
                            } else {
                                typeText = getString(R.string.device) + ": " + getString(R.string.unitoy);
                            }
                            accountFragment.setSummarized(typeText, powerText + "", false);
                            break;
                        case Constant.RECEIVE_ELECTRICITY:
                            powerText = Integer.parseInt(message.get(0).substring(10, 12), 16);
                            accountFragment.setPowerPercent(powerText + "");
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (action.equals(ProMainActivity.STOP_CELL_PHONE_SERVICE)) {
                stopService(intentCallPhone);
                unbindTcpService();
                destorySocketService();
            }
        }
    };


    private void requestPacket() {
        CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET, "3");
        checkRegisterStatuGoIp();
    }

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
                        sendEventBusChangeBluetoothStatus(getString(R.string.index_blue_un_opne), R.drawable.index_blue_unpen);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        d("STATE_TURNING_OFF 手机蓝牙正在关闭");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        d("STATE_ON 手机蓝牙开启");
                        if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
                            sendEventBusChangeBluetoothStatus(getString(R.string.index_unconnect), R.drawable.index_unconnect);
                        } else {
                            sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind), R.drawable.index_unbind);
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        d("STATE_TURNING_ON 手机蓝牙正在开启");
                        break;
                }
            }
        }
    };

    //是否注册成功，如果是则信号强，反之则信号弱
    private void checkRegisterStatuGoIp() {
        if (REGISTER_STATUE_CODE == 1) {
            sendEventBusChangeBluetoothStatus(getString(index_registing), R.drawable.index_no_signal);
        } else if (REGISTER_STATUE_CODE != 3) {
            sendEventBusChangeBluetoothStatus(getString(R.string.index_no_signal), R.drawable.index_no_signal);
        } else {
            sendEventBusChangeBluetoothStatus(getString(R.string.index_high_signal), R.drawable.index_high_signal);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        phoneFragment.clearInputEdit();
        return false;
    }

    //空中升级
    private void skyUpgradeHttp() {
        Log.e(TAG, "skyUpgradeHttp");
        int DeviceType = 0;
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
