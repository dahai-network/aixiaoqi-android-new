package de.blinkt.openvpn.activities.Device.PresenterImpl;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SocketConstant;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.AppMode;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Device.Model.DownloadUpgradePackageModel;
import de.blinkt.openvpn.activities.Device.ModelImpl.CheckDeviceIsOnlineModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.DownloadUpgradePackageModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.SkyUpgradeModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.UnbindDeviceModelImpl;
import de.blinkt.openvpn.activities.Device.View.MyDeviceView;
import de.blinkt.openvpn.activities.Device.ui.MyDeviceActivity;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.Device.Presenter.MyDevicePresenter;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.DownloadSkyUpgradePackageHttp;
import de.blinkt.openvpn.http.GetDeviceSimRegStatuesHttp;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.UIOperatorEntity;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.model.enentbus.BlueConnStatue;
import de.blinkt.openvpn.model.enentbus.BlueReturnData;
import de.blinkt.openvpn.service.DfuService;
import de.blinkt.openvpn.util.CheckAuthorityUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import no.nordicsemi.android.dfu.DfuServiceInitiator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.isGetnullCardid;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.nullCardId;
import static de.blinkt.openvpn.activities.Device.ModelImpl.HasPreDataRegisterImpl.sendYiZhengService;
import static de.blinkt.openvpn.constant.Constant.ICCID_GET;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.Constant.OFF_TO_POWER;
import static de.blinkt.openvpn.constant.Constant.SKY_UPGRADE_ORDER;
import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER_NO_RESPONSE;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public class MyDevicePresenterImpl extends NetPresenterBaseImpl implements MyDevicePresenter {
    MyDeviceView myDeviceView;
    CheckDeviceIsOnlineModelImpl checkDeviceIsOnlineModel;
    DownloadUpgradePackageModelImpl downloadUpgradePackageModel;
    SkyUpgradeModelImpl skyUpgradeModel;
    UnbindDeviceModelImpl unbindDeviceModel;
    Context context;
    public String url;
    public MyDevicePresenterImpl(MyDeviceView myDeviceView,Context context){
        this.myDeviceView=myDeviceView;
        EventBus.getDefault().register(this);
        this.context=context;
        checkDeviceIsOnlineModel=new CheckDeviceIsOnlineModelImpl(this);
        downloadUpgradePackageModel=new DownloadUpgradePackageModelImpl(this);
        skyUpgradeModel=new SkyUpgradeModelImpl(this);
        unbindDeviceModel=new UnbindDeviceModelImpl(this);
    }

    @Override
    public void requestCheckDeviceIsOnline() {
        checkDeviceIsOnlineModel.checkDeviceIsOnline();
    }


    @Override
    public void requestSkyUpgrade() {
        CheckAuthorityUtil.checkPermissions((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        skyUpgradeModel.skyUpgrade();
    }

    @Override
    public void requestDownloadUpgradePackage(String downloadUrl) {
        downloadUpgradePackageModel.downloadUpgradePackage(downloadUrl);
    }

    @Override
    public void requestUnbindDevice() {
        unbindDeviceModel.unbindDevice();
    }


    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if(cmdType==HttpConfigUrl.COMTYPE_UN_BIND_DEVICE){
            myDeviceView.showToast(object.getMsg());
            if(object.getStatus()==1){
                SharedUtils.getInstance().writeBoolean(Constant.HAS_DEVICE_NEED_UPGRADE,false);
                AppMode.getInstance().isClickAddDevice=false;
                myDeviceView.clearData();
                myDeviceView.finishView();
            }
            ;
        }else if(cmdType==HttpConfigUrl.COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE){
            if(object instanceof DownloadSkyUpgradePackageHttp){
                DownloadSkyUpgradePackageHttp downloadSkyUpgradePackageHttp = (DownloadSkyUpgradePackageHttp) object;
                if (Constant.DOWNLOAD_SUCCEED.equals(downloadSkyUpgradePackageHttp.getDownloadStatues())) {
                    MyDeviceActivity.isUpgrade = true;
                    SendCommandToBluetooth.sendMessageToBlueTooth(Constant.OFF_TO_POWER);
                    ICSOpenVPNApplication.isConnect=false;
                    SendCommandToBluetooth.sendMessageToBlueTooth(SKY_UPGRADE_ORDER);
                    myDeviceView.startAnim();
                    myDeviceView.showUpgradeDialog();
                    CommonTools.delayTime(1000);
                    skyUpgradeScan();
                } else if (Constant.DOWNLOAD_FAIL.equals(downloadSkyUpgradePackageHttp.getDownloadStatues())) {
                    myDeviceView.showToast(R.string.download_upgrade_package_fail);
                }
            }

        }else if(cmdType==HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA){
            SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;
            if (skyUpgradeHttp.getStatus() == 1) {
                if (skyUpgradeHttp.getUpgradeEntity() != null) {
                    String versionStr = SharedUtils.getInstance().readString(Constant.BRACELETVERSION);
                    if (versionStr != null) {
                        if (skyUpgradeHttp.getUpgradeEntity().getVersion() > Float.parseFloat(versionStr)) {
                            url = skyUpgradeHttp.getUpgradeEntity().getUrl();
                            myDeviceView.showDialogGOUpgrade(skyUpgradeHttp.getUpgradeEntity().getDescr());
                            myDeviceView.showOrHideVersionUpgradeHotDot(VISIBLE);
                        } else {
                            myDeviceView.showToast(R.string.last_version);
                            myDeviceView.showOrHideVersionUpgradeHotDot(GONE);
                        }
                    }
                }
            }
        }else if(cmdType==HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES){
            GetDeviceSimRegStatuesHttp getDeviceSimRegStatuesHttp = (GetDeviceSimRegStatuesHttp) object;
            if (getDeviceSimRegStatuesHttp.getStatus() == 1)
                if (!getDeviceSimRegStatuesHttp.getSimRegStatue().getRegStatus().equals("1")) {
                    connectGoip();
                } else {
                    myDeviceView.stopAnim();
                    myDeviceView.showToast(R.string.tip_high_signal);
                }
        }
    }

    private void connectGoip() {
        if (sendYiZhengService != null) {
            SocketConstant.REGISTER_STATUE_CODE = 2;
            myDeviceView.setConStatueText(R.string.index_registing);
            myDeviceView.setConStatueBackground(R.color.gray_text);
            EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING, SocketConstant.REGISTER_CHANGING);
            sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
        }
    }

    //如没有没插卡检测插卡并且提示用户重启手环。
    //如果网络请求失败或者无套餐，刷新则从请求网络开始。如果上电不成功，读不到手环数据，还没有获取到预读取数据或者获取预读取数据错误，则重新开始注册。
    //如果是注册到GOIP的时候失败了，则从创建连接重新开始注册
    //如果重连失败再进入我的设备就清空重连次数重新进入连接流程
    public void refreshStatue(){
        if(4==SocketConstant.SIM_TYPE){
            return;
        }
        if (!CommonTools.isFastDoubleClick(3000)) {
            //如果激活卡成功后，刷新按钮点击需要将标记激活
            isGetnullCardid = true;
            nullCardId = null;
            MyDeviceActivity.percentInt = 0;
            IS_TEXT_SIM = false;
            //TODO 处理异常
            myDeviceView.startAnim();
            String macStr = SharedUtils.getInstance().readString(Constant.IMEI);
            String operater = SharedUtils.getInstance().readString(Constant.OPERATER);
            if (ICSOpenVPNApplication.uartService != null && !ICSOpenVPNApplication.uartService.isConnectedBlueTooth() && !TextUtils.isEmpty(macStr)) {
                ReceiveBLEMoveReceiver.retryTime = 0;
                myDeviceView.connect(macStr);
                Log.i(TAG, "重新连接");
            } else if (operater == null) {
                SendCommandToBluetooth.sendMessageToBlueTooth(ICCID_GET);
            } else if (!myDeviceView.getConStatusText().equals(context.getString(R.string.index_high_signal)) || SocketConstant.REGISTER_STATUE_CODE == 1 || SocketConstant.REGISTER_STATUE_CODE == 0) {
                SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER_NO_RESPONSE);
            } else if (SocketConstant.REGISTER_STATUE_CODE == 2) {
                if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
                    //从预读取数据那里重新注册
                    connectGoip();
                } else {
                    //如果TCP服务关闭了，则通知主界面重新开启
                    EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING, SocketConstant.RESTART_TCP);
                }
            } else if (SocketConstant.REGISTER_STATUE_CODE == 3) {
                //请求服务器，当卡在线的时候，不进行任何操作。当卡不在线的时候，重新从预读取数据注册
                requestCheckDeviceIsOnline();
            }
        }
    }

    @Override
    public void noNet() {
        myDeviceView.showToast(R.string.no_wifi);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)//ui线程
    public void onIsSuccessEntity(SimRegisterStatue entity) {
        Logger.d("RigsterSimStatue="+entity.getRigsterSimStatue() );
        Logger.d("rigsterStatueReason="+entity.getRigsterStatueReason() );
        Logger.d("SocketConstant.REGISTER_STATUE_CODE 1未注册失败，3为注册成功="+SocketConstant.REGISTER_STATUE_CODE );

        synchronized (this) {
            myDeviceView.setConStatueBackground(R.color.gray_text);
            switch (entity.getRigsterSimStatue()) {
                case SocketConstant.REGISTER_SUCCESS://注册成功
                    myDeviceView.setConStatueText(R.string.index_high_signal);
                    myDeviceView.setConStatueBackground(R.color.select_contacct);
                    MyDeviceActivity.percentInt = 0;
                    myDeviceView.percentTextViewVisible(GONE);
                    myDeviceView.stopAnim();
                    break;
                case SocketConstant.REGISTER_FAIL://注册失败
                    myDeviceView.stopAnim();
                    myDeviceView.setConStatueText(R.string.index_regist_fail);
                    myDeviceView.percentTextViewVisible(GONE);
                    registerFail(entity.getRigsterStatueReason());

                    break;
                case SocketConstant.UNREGISTER://未注册
                    myDeviceView.stopAnim();
                    myDeviceView.percentTextViewVisible(GONE);
                    unregister(entity.getRigsterStatueReason());
                    break;
                case SocketConstant.REGISTERING://注册中
                    Logger.d("当前为注册中");
                    if (SocketConstant.REGISTER_STATUE_CODE == 3) {
                        myDeviceView.setConStatueBackground(R.color.select_contacct);
                        myDeviceView.percentTextViewVisible(GONE);
                        return;
                    }
                    myDeviceView.startAnim();
                    myDeviceView.setConStatueText(R.string.index_registing);
                    registering(entity);
                    break;
            }
        }
    }

    private void registerFail(int failReason) {
        switch (failReason) {
            case SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA:

                break;
            case SocketConstant.REGISTER_FAIL_IMSI_IS_NULL:

                break;
            case SocketConstant.REGISTER_FAIL_IMSI_IS_ERROR:

                break;
            case SocketConstant.SERVER_IS_ERROR:

                break;
            case SocketConstant.NO_NET:
                myDeviceView.setConStatueText(R.string.no_net_error);
                myDeviceView.showToast(R.string.no_wifi);
                break;
            case SocketConstant.NO_NET_ERROR:
                myDeviceView.showToast(R.string.no_wifi);
                myDeviceView.setConStatueText(R.string.no_net_error);
                break;
        }

    }


    private void registering(SimRegisterStatue entity) {
        switch (entity.getRigsterStatueReason()) {
            case SocketConstant.UPDATE_PERCENT:
                if (SocketConstant.REGISTER_STATUE_CODE != 3) {
                    myDeviceView.percentTextViewVisible(VISIBLE);
                    double percent = entity.getProgressCount();
                    myDeviceView.setConStatueText(R.string.index_registing);
                    if (percent > MyDeviceActivity.percentInt)
                        MyDeviceActivity.percentInt = (int) (percent / 1.6);
                    Log.i(TAG, "写卡进度：" + MyDeviceActivity.percentInt + "%");
                    if (MyDeviceActivity.percentInt >= 100) {
                        MyDeviceActivity.percentInt = 98;
                    }
                    myDeviceView.setPercentText( MyDeviceActivity.percentInt+"%");
                }
                break;
            case SocketConstant.REG_STATUE_CHANGE:
                myDeviceView.setConStatueText(R.string.index_registing);
                break;
            case SocketConstant.RESTART_TCP:
                myDeviceView.setConStatueText(R.string.index_registing);
                break;
            case SocketConstant.TCP_DISCONNECT:
                myDeviceView.setConStatueText(R.string.index_registing);
            case SocketConstant.START_TCP_FAIL:
                myDeviceView.showToast(R.string.check_net_work_reconnect);
                break;
        }
    }

    private void unregister(int unregisterReason) {
        switch (unregisterReason) {
            case SocketConstant.AIXIAOQI_CARD:
                myDeviceView.setConStatueText(R.string.index_aixiaoqicard);
                myDeviceView.stopAnim();
                //重新上电清空
                SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);
                break;

            case SocketConstant.CONNECTING_DEVICE:
                myDeviceView.setConStatueText(R.string.index_connecting);
                myDeviceView.percentTextViewVisible(GONE);
                break;
            case SocketConstant.DISCOONECT_DEVICE:
                myDeviceView.setConStatueText(R.string.index_unconnect);
                myDeviceView.percentTextViewVisible(GONE);
                break;
            case SocketConstant.UN_INSERT_CARD:
                myDeviceView.setConStatueText(R.string.index_un_insert_card);
                myDeviceView.percentTextViewVisible(GONE);
                myDeviceView.registerSimStatuVisible(VISIBLE);
                myDeviceView.showNoCardDialog();
                SendCommandToBluetooth.sendMessageToBlueTooth(OFF_TO_POWER);
                myDeviceView.stopAnim();
                break;


        }
    }

    //通过eventbus接收从蓝牙那边传回来的数据，并进行相应的操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void blueReturnData(BlueReturnData blueReturnData){
        e("blueReturnData:" + blueReturnData );
        switch (blueReturnData.getDataType()){
            case Constant.SYSTEM_BASICE_INFO:
                myDeviceView.setDeviceVersionText(SharedUtils.getInstance().readString(Constant.BRACELETVERSION));
                myDeviceView.dismissProgress();
                if(!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETPOWER)))
                myDeviceView.setElectricityPercent(((float) Integer.parseInt(SharedUtils.getInstance().readString(Constant.BRACELETPOWER)))/100);
                break;
        }
    }
    //实时监测连接状态，并进行相应的操作
    @Subscribe(threadMode = ThreadMode.MAIN)
    private void blueConnStatue(BlueConnStatue blueConnStatue){
        Log.e(TAG,"blueConnStatue="+blueConnStatue.getConnStatue());
        switch (blueConnStatue.getConnStatue()){
            case UartService.STATE_DISCONNECTED:
                myDeviceView.stopAnim();
                //多次扫描蓝牙，在华为荣耀，魅族M3 NOTE 中有的机型，会发现多次断开–扫描–断开–扫描…
                // 会扫描不到设备，此时需要在断开连接后，不能立即扫描，而是要先停止扫描后，过2秒再扫描才能扫描到设备
                if (!MyDeviceActivity.isUpgrade) {
                    myDeviceView.showProgress(R.string.reconnecting, true);
                }
                myDeviceView.percentTextViewVisible(GONE);
                break;
            case UartService.STATE_CONNECTED:
                myDeviceView.dismissProgress();
                break;
        }
    }
    //升级状态提示
    @Subscribe(threadMode = ThreadMode.MAIN)//ui线程
    public void onUIOperatorEntity(UIOperatorEntity entity) {
        startDfuCount = 0;
        if (entity.getType() == UIOperatorEntity.onError) {
            myDeviceView.showToast(R.string.update_fail_retry);
        } else if (entity.getType() == UIOperatorEntity.onCompelete) {
            myDeviceView.showToast(R.string.dfu_status_completed);
            myDeviceView.showOrHideVersionUpgradeHotDot(GONE);
        }
    }

    private  int startDfuCount=0;

    public    void findDevices(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device.getName() == null) {
            return;
        }
        Log.e(TAG, "isUpgrade:" + MyDeviceActivity.isUpgrade + "deviceName:" + device.getName() + "保存的IMEI地址:" + SharedUtils.getInstance().readString(Constant.IMEI).replace(":", ""));
        if (MyDeviceActivity.isUpgrade && device.getName().contains(SharedUtils.getInstance().readString(Constant.IMEI).replace(":", ""))) {
            Log.e(TAG, "device:" + device.getName() + "mac:" + device.getAddress());
            if (ICSOpenVPNApplication.uartService != null) {
                myDeviceView.scanLeDevice(false);
                Log.i(TAG, "startDfuCount:" + startDfuCount);
                synchronized (this){
                    if (startDfuCount == 0) {
                        Log.i(TAG, "startDfuCount:" + startDfuCount);
                        startDfuCount++;
                        CommonTools.delayTime(1000);
                        uploadToBlueTooth(device.getName(), device.getAddress());//升级服务会自动去连接
                    }
                }
            }
        } else if (!MyDeviceActivity.isUpgrade && SharedUtils.getInstance().readString(Constant.IMEI)!= null && SharedUtils.getInstance().readString(Constant.IMEI).equalsIgnoreCase(device.getAddress())) {
            Log.e(TAG, "find the device:" + device.getName() + "mac:" + device.getAddress() + "macAddressStr:" + SharedUtils.getInstance().readString(Constant.IMEI) + ",rssi :" + rssi);
            if (ICSOpenVPNApplication.uartService != null) {
                myDeviceView.scanLeDevice(false);
                myDeviceView.connect(SharedUtils.getInstance().readString(Constant.IMEI));
            }
        }
    }


    private boolean isDfuServiceRunning() {
        return ICSOpenVPNApplication.getInstance().isServiceRunning(DfuService.class.getName());
    }

    private void skyUpgradeScan(){
        startDfuCount = 0;
        myDeviceView.scanLeDevice(true);
    }


    //空中升级
    private void uploadToBlueTooth(String deviceName, String deviceAddress) {
        Log.e(TAG, "uploadToBlueTooth");
        if (isDfuServiceRunning()) {
            return;
        }
        final DfuServiceInitiator starter = new DfuServiceInitiator(deviceAddress)
                .setDeviceName(deviceName).setKeepBond(true).setDisableNotification(true);
        Log.e(TAG, "deviceAddress:" + deviceAddress + "deviceName:" + deviceName);

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File filePath = Environment.getExternalStorageDirectory();
            String path = filePath.getPath();
            String abo = path + Constant.UPLOAD_PATH;
            starter.setZip(abo);
            starter.start(context, DfuService.class);
        }

    }


    @Override
    public void onDestory() {
        if(unbindDeviceModel!=null){
            unbindDeviceModel=null;
        }
        if(downloadUpgradePackageModel!=null){
            downloadUpgradePackageModel=null;
        }
        if(skyUpgradeModel!=null){
            skyUpgradeModel=null;
        }
        if(checkDeviceIsOnlineModel!=null){
            checkDeviceIsOnlineModel=null;
        }
        EventBus.getDefault().unregister(this);
        if (isDfuServiceRunning()) {
            context.stopService(new Intent(context, DfuService.class));
        }
    }
}
