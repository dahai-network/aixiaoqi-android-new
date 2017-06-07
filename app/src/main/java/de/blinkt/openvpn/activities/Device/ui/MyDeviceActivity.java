package de.blinkt.openvpn.activities.Device.ui;

import android.Manifest;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import de.blinkt.openvpn.activities.AlarmClockActivity;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.Device.PresenterImpl.MyDevicePresenterImpl;
import de.blinkt.openvpn.activities.Device.View.MyDeviceView;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.TipUserOptionsActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.DownloadSkyUpgradePackageHttp;
import de.blinkt.openvpn.http.GetDeviceSimRegStatuesHttp;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.UIOperatorEntity;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.service.DfuService;
import de.blinkt.openvpn.util.CheckAuthorityUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.MySinkingView;
import de.blinkt.openvpn.views.TitleBar;
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
import static de.blinkt.openvpn.constant.Constant.BRACELETPOWER;
import static de.blinkt.openvpn.constant.Constant.FIND_DEVICE;
import static de.blinkt.openvpn.constant.Constant.ICCID_GET;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.RESTORATION;
import static de.blinkt.openvpn.constant.Constant.SKY_UPGRADE_ORDER;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER_NO_RESPONSE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBINDDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKDEVICEUPGRADE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKUNBINDDEVICE;

public class MyDeviceActivity extends BluetoothBaseActivity implements MyDeviceView,DialogInterfaceTypeBase {
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
    @BindView(R.id.deviceNameTextView)
    TextView deviceNameTextView;
    @BindView(R.id.findStatusView)
    View findStatusView;
    @BindView(R.id.alarmClockView)
    View alarmClockView;
    @BindView(R.id.messageRemindView)
    View messageRemindView;
    @BindView(R.id.title)
    TitleBar title;
    @BindView(R.id.version_upgrade_tip)
    ImageView versionUpgradeTip;
    private String TAG = "MyDeviceActivity";
    private static final int REQUEST_ENABLE_BT = 2;

    private String macAddressStr;
    private DialogBalance cardRuleBreakDialog;
    Animation RegisterStatueAnim;
    public static boolean isForeground = false;
    //写卡进度
    public static int percentInt;
    //手环类型
   private String braceletname;

    public static boolean isUpgrade = false;
    public static final int DOWNLOAD_SKY_UPGRADE = 5;
    public static final int UNBIND = 7;
    MyDevicePresenterImpl myDevicePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);
        ButterKnife.bind(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            finish();
            return;
        }
        initSet();
        serviceInit();
        initDialogUpgrade();
        myDevicePresenter=new MyDevicePresenterImpl(this,this);

    }

    @Override
    public void startAnim() {
        if (!registerSimStatu.isEnabled()) return;
        registerSimStatu.setEnabled(false);
        RegisterStatueAnim.reset();
        registerSimStatu.clearAnimation();
        registerSimStatu.setBackgroundResource(R.drawable.registering);
        registerSimStatu.startAnimation(RegisterStatueAnim);
    }

    @Override
    public void stopAnim() {
        registerSimStatu.setEnabled(true);
        RegisterStatueAnim.reset();
        registerSimStatu.clearAnimation();
        registerSimStatu.setBackgroundResource(R.drawable.registering);
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
    public void showOrHideVersionUpgradeHotDot(int isVisible) {
        versionUpgradeTip.setVisibility(isVisible);
    }

    @Override
    public void showUpgradeDialog() {
        showSkyUpgrade();
    }

    @Override
    public void clearData() {
        registerSimStatu.setText("");
        conStatusTextView.setText("");
        firmwareTextView.setText("");
        percentTextView.setText("");
        macTextView.setText("");
    }

    @Override
    public void finishView() {
        finish();
    }


    @Override
    public void setPercentText(String text) {
        percentTextView.setText(text);
    }

    @Override
    public void percentTextViewVisible(int isVisible) {
        percentTextView.setVisibility(isVisible);
    }

    @Override
    public void registerSimStatuVisible(int isVisible) {
        registerSimStatu.setVisibility(isVisible);
    }

    @Override
    public void setConStatueText(int contentId) {
        conStatusTextView.setText(getString(contentId));
    }

    @Override
    public void setConStatueBackground(int colorId) {
        conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
    }

    DialogTipUpgrade upgrade;
    @Override
    public void showDialogGOUpgrade(String upgradeContent) {
        if(upgrade!=null){
            upgrade.show();
        }
        if(upgrade==null){
            upgrade = new DialogTipUpgrade(this, this, R.layout.dialog_tip_upgrade, DOWNLOAD_SKY_UPGRADE);

            if (braceletname != null && braceletname.contains(Constant.UNITOYS)) {
                upgrade.changeText(getString(R.string.dfu_upgrade), upgradeContent);
            } else {
                upgrade.changeText(getString(R.string.dfu_unibox_upgrade), upgradeContent);
            }
        }


    }

    //空中升级监听
    DfuProgressListener mDfuProgressListener;
    //空中升级,如果没有设备类型就不升级，如果有的话再去升级。
    private void skyUpgradeHttp() {
        CheckAuthorityUtil.checkPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        long beforeRequestTime = SharedUtils.getInstance().readLong(Constant.UPGRADE_INTERVAL);
        if (beforeRequestTime == 0L || System.currentTimeMillis() - beforeRequestTime > 216000000)//一小时以后再询问
        {
            myDevicePresenter.requestSkyUpgrade();
        }
    }

    //初始化设备界面与设备类型
    private void initSet() {
        actionBar.hide();
        braceletname = SharedUtils.getInstance().readString(Constant.BRACELETNAME);

        if (braceletname != null && braceletname.contains(Constant.UNIBOX)) {
            deviceNameTextView.setText(getString(R.string.unibox_key));
        } else if (braceletname != null && braceletname.contains(Constant.UNITOYS)) {
            initUnitoy();
        }
        RegisterStatueAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate_register_statue);
        titleSet();
        //初始化状态和电量
        if (mService != null && mService.mConnectionState == UartService.STATE_CONNECTED) {
            //获取电量
            initData();
        }
//显示固件版本
        firmwareTextView.setText(SharedUtils.getInstance().readString(Constant.BRACELETVERSION));

    }

    private void initData() {
        String blueStatus = BaseStatusFragment.bleStatus;
        int electricityInt = SharedUtils.getInstance().readInt(BRACELETPOWER);
        sinking.setPercent(((float) electricityInt) / 100);
        if (!TextUtils.isEmpty(blueStatus)) {
            conStatusTextView.setText(blueStatus);//初始化状态
            if (getString(R.string.index_high_signal).equals(blueStatus)) {
                percentTextView.setVisibility(GONE);
                conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.select_contacct));
            } else {
                if (getString(R.string.index_no_signal).equals(blueStatus)) {
                    startAnim();
                    if (percentInt != 0) {
                        percentTextView.setText(percentInt + "%");
                    }
                } else {
                    percentTextView.setVisibility(GONE);
                }
                conStatusTextView.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
            }

        }
        skyUpgradeHttp();
    }

    private void initUnitoy() {
        alarmClockLinearLayout.setVisibility(View.VISIBLE);
        messageRemindLinearLayout.setVisibility(View.VISIBLE);
        findStatusLinearLayout.setVisibility(View.VISIBLE);
        findStatusView.setVisibility(View.VISIBLE);
        alarmClockView.setVisibility(View.VISIBLE);
        messageRemindView.setVisibility(View.VISIBLE);
        deviceNameTextView.setText(getString(R.string.unitoy));
    }

    private void titleSet() {
        title.setTextTitle(getString(device));
        title.setLeftBtnIcon(R.drawable.btn_top_back);
        title.getLeftText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        title.setBackground(R.color.color_0F93FE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    String macStr = SharedUtils.getInstance().readString(Constant.IMEI);
                    if (mService != null && !mService.isConnecttingBlueTooth() && !TextUtils.isEmpty(macStr)) {
                        ReceiveBLEMoveReceiver.retryTime = 0;
                        mService.connect(macStr);
                        Log.i(TAG, "重连重新触发");
                    }
                } else {
                    Log.d(TAG, "蓝牙未打开");
                    finish();
                }
                break;

        }
    }


    @OnClick({R.id.unBindButton, R.id.callPayLinearLayout, R.id.register_sim_statue, R.id.findStatusLinearLayout, R.id.alarmClockLinearLayout, R.id.messageRemindLinearLayout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unBindButton://解除绑定，解绑以后，删除设备信息
                if (CommonTools.isFastDoubleClick(1000)) {
                    return;
                }
                MobclickAgent.onEvent(context, CLICKUNBINDDEVICE);
                showUnBindDialog();
                break;
            case R.id.callPayLinearLayout://空中升级，如果没有连接成功就不升级
                if (CommonTools.isFastDoubleClick(1000)) {
                    return;
                }
                if (mService != null && mService.mConnectionState == UartService.STATE_CONNECTED) {
                    if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETVERSION)) && !isUpgrade) {
                        SharedUtils.getInstance().writeLong(Constant.UPGRADE_INTERVAL, 0);
                        skyUpgradeHttp();
                    } else if (isUpgrade) {
                        showSkyUpgrade();
                    }
                } else {
                    CommonTools.showShortToast(this, getString(R.string.unconnection_device));
                }

                break;
            case R.id.findStatusLinearLayout://查找设备
                if (!CommonTools.isFastDoubleClick(3000)) {
                            SendCommandToBluetooth.sendMessageToBlueTooth(FIND_DEVICE);
                }
                break;
            case register_sim_statue:
               myDevicePresenter.refreshStatue();
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

    @Override
    public String getConStatusText() {
        return conStatusTextView.getText().toString().trim();
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
                dismissProgress();
                skyUpgradeHttp();
            }

         else   if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                if (mService != null) {
                    if (!ICSOpenVPNApplication.isConnect) {
                        CommonTools.showShortToast(MyDeviceActivity.this, "已断开");
                        stopAnim();
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
                    percentTextView.setVisibility(GONE);
                    //多次重连无效后关闭蓝牙重启
                    if (!isUpgrade) {
                        showProgress(getString(R.string.reconnecting), true);
                    }
                } else {
                    CommonTools.showShortToast(MyDeviceActivity.this, "已断开");
                }
            }
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                final ArrayList<String> messages = intent.getStringArrayListExtra(UartService.EXTRA_DATA);
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
                            if (!TextUtils.isEmpty(deviceVesion))
                                firmwareTextView.setText(deviceVesion);
                            dismissProgress();

                            slowSetPercent(((float) Integer.parseInt(messages.get(0).substring(14, 16), 16)) / 100);
                            break;
                        case Constant.RETURN_POWER:
                            if (messages.get(0).substring(10, 12).equals("03")) {

                            } else if (messages.get(0).substring(10, 12).equals("13")) {
                                //百分比TextView设置为0
                                showNoCardDialog();
                                SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);
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
        macAddressStr = SharedUtils.getInstance().readString(Constant.IMEI);
        macTextView.setText(macAddressStr);
        sinking.setPercent(((float) electricityInt) / 100);
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
    protected void onStop() {
        super.onStop();
        sinking.setStatus(MySinkingView.Status.NONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        stopAnim();
        isForeground = false;
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        mBluetoothAdapter = null;
        isUpgrade = false;
        registerSimStatu = null;
        RegisterStatueAnim = null;
        if (sinking != null)
            sinking.clear();
        sinking = null;

        try {
            LocalBroadcastManager.getInstance(ICSOpenVPNApplication.getContext()).unregisterReceiver(UARTStatusChangeReceiver);

        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
    }

    private void showSkyUpgrade() {
        if (upgradeDialog != null && !upgradeDialog.isShowing()) {
            showDialogUpgrade();
        }
    }

    Dialog upgradeDialog;
    DialogUpgrade dialogUpgrade;

    private void initDialogUpgrade() {
        Log.d(TAG, "initDialogUpgrade");
        dialogUpgrade = new DialogUpgrade(this, this, R.layout.dialog_upgrade, 3);
        upgradeDialog = dialogUpgrade.getDialogUpgrade();
        mDfuProgressListener = dialogUpgrade.getDfuProgressListener();
        hideDialogUpgrade();
    }

    private void showDialogUpgrade() {
        Log.d(TAG, "showDialogUpgrade");
        isUpgrade = true;
        if (upgradeDialog != null) {
            dialogUpgrade.setProgressBar();
            upgradeDialog.show();
        }
    }

    private void hideDialogUpgrade() {
        if (upgradeDialog != null)
            upgradeDialog.dismiss();
    }

    private void downloadSkyUpgradePackageHttp(String path) {
        myDevicePresenter.requestDownloadUpgradePackage(path);
    }

    @Override
    public void dialogText(int type, String text) {
     if (type == 3) {
            SendCommandToBluetooth.sendMessageToBlueTooth(RESTORATION);
        } else if (type == DOWNLOAD_SKY_UPGRADE) {
            if (!TextUtils.isEmpty(myDevicePresenter.url)) {
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKDEVICEUPGRADE);
                downloadSkyUpgradePackageHttp(myDevicePresenter.url);
            }
        } else if (type == UNBIND) {
            myDevicePresenter.requestUnbindDevice();
        } else {
            onBackPressed();
        }
    }

    private void slowSetPercent(final float percent) {
        sinking.setPercent(percent);
    }
    @Override
    public void scanLeDevice(boolean enable) {
        super.scanLeDevice(enable);
    }

    @Override
    public void connect(String macAddress) {
        super.connect(macAddress);
    }

    @Override
    protected   void findDevices(BluetoothDevice device, int rssi, byte[] scanRecord) {
        myDevicePresenter.findDevices(device,rssi,scanRecord);
    }

    private void showUnBindDialog() {
        //不能按返回键，只能二选其一
        DialogBalance cardRuleBreakDialog = new DialogBalance(this, this, R.layout.dialog_balance, UNBIND);
        cardRuleBreakDialog.setCanClickBack(false);
        cardRuleBreakDialog.changeText(getResources().getString(R.string.are_you_sure_unbind), getResources().getString(R.string.sure));
    }

    //没卡弹对话框
    private void showNoCardDialog() {
        //不能按返回键，只能二选其一
        if (cardRuleBreakDialog != null) cardRuleBreakDialog.show();
        if(cardRuleBreakDialog==null){
            cardRuleBreakDialog = new DialogBalance(this, this, R.layout.dialog_balance, 3);
            cardRuleBreakDialog.setCanClickBack(false);
            cardRuleBreakDialog.changeText(getResources().getString(R.string.no_card_or_rule_break), getResources().getString(R.string.reset));
        }
    }
}
