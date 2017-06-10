package de.blinkt.openvpn.activities.Device.PresenterImpl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.aixiaoqi.socket.SdkAndBluetoothDataInchange;
import com.aixiaoqi.socket.SendYiZhengService;
import com.aixiaoqi.socket.SocketConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.johnson.model.ChangeViewStateEvent;
import de.blinkt.openvpn.activities.Device.ModelImpl.GetBindDeviceInfoModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.GetSecurityConfigModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.HasPreDataRegisterImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.NoPreDataRegisterModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.RegisterBroadcastModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.SkyUpgradeModelImpl;
import de.blinkt.openvpn.activities.Device.Presenter.ProMainPresenter;
import de.blinkt.openvpn.activities.Device.View.ProMainView;
import de.blinkt.openvpn.activities.Device.ui.ProMainActivity;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.BasicConfigModelImpl;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.database.DBHelp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.GetHostAndPortHttp;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
import de.blinkt.openvpn.model.CancelCallService;
import de.blinkt.openvpn.model.PreReadEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.enentbus.OptionProMainActivityView;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.Constant.ICCID_GET;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class ProMainPresenterImpl extends NetPresenterBaseImpl implements ProMainPresenter {

    ProMainView proMainView;
    Context context;
    BasicConfigModelImpl basicConfigModel;
    GetBindDeviceInfoModelImpl getBindDeviceInfoModel;
    HasPreDataRegisterImpl hasPreDataRegisterImpl;
    NoPreDataRegisterModelImpl noPreDataRegisterModel;
    GetSecurityConfigModelImpl getSecurityConfigModel;
    SkyUpgradeModelImpl skyUpgradeModel;
    RegisterBroadcastModelImpl registerBroadcastModel;
    private int requestCount = 0;
    public  ProMainPresenterImpl(ProMainView proMainView,Context context){
        this.proMainView=proMainView;
        this.context=context;
        basicConfigModel=new BasicConfigModelImpl();
        getBindDeviceInfoModel=new GetBindDeviceInfoModelImpl(this);
        hasPreDataRegisterImpl=new HasPreDataRegisterImpl(context);
        noPreDataRegisterModel=new NoPreDataRegisterModelImpl(context);
        getSecurityConfigModel=new GetSecurityConfigModelImpl(this);
        skyUpgradeModel=new SkyUpgradeModelImpl(this);
        registerBroadcastModel=new RegisterBroadcastModelImpl();
        EventBus.getDefault().register(this);

    }

public void registerBlueChangeBroadcast(){
    registerBroadcastModel.registerBlueChangeBroadcast(context);
}

    @Override
    public void requestGetBasicConfig() {
        basicConfigModel.requestBasicConfig();
    }

    @Override
    public void requestGetBindDeviceInfo() {
        getBindDeviceInfoModel.getBindDeviceinfo();
    }

    @Override
    public void requestGetSecurityConfig() {
        getSecurityConfigModel.getSecurityConfig();
    }

    @Override
    public void requestSkyUpdate() {
        skyUpgradeModel.skyUpgrade();
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE){
            if(object.getStatus()==1){
                GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
                if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
                    if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
                        requestSkyUpdate();
                    }
                }
            }
        }
        else if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG){
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
                            if (!TextUtils.isEmpty(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6])) {
                                DBHelp dbHelp = new DBHelp(context);
                                PreReadEntity preReadEntity = dbHelp.getPreReadEntity(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6]);
                                if (preReadEntity != null) {
                                    hasPreDataRegisterImpl.initPreData(preReadEntity);
                                    hasPreDataRegisterImpl.registerSimPreData();
                                } else {
                                    hasPreDataRegisterImpl.startSocketService();
                                    noPreDataRegisterModel.noPreDataStartSDKSimRegister();
                                }
                            } else {
                                CommonTools.delayTime(2000);
                                SendCommandToBluetooth.sendMessageToBlueTooth(ICCID_GET);
                            }
                        }
                    }).start();
                }
            }
        }else if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
            SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;
            if (skyUpgradeHttp.getStatus() == 1) {
                String braceletVersion = SharedUtils.getInstance().readString(Constant.BRACELETVERSION);
                if (!TextUtils.isEmpty(braceletVersion) && skyUpgradeHttp.getUpgradeEntity().getVersion() > Float.parseFloat(braceletVersion)) {
                    proMainView.showHotDot(View.VISIBLE);
                    SharedUtils.getInstance().writeBoolean(Constant.HAS_DEVICE_NEED_UPGRADE,true);
                }
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
            if (requestCount < 3) {
                requestCount++;
                requestGetBasicConfig();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void optionView(OptionProMainActivityView entity) {
        e("isVisibleToUser=" + entity.isShow());
        proMainView.bottomFragmentIsShow(entity.isShow()?View.VISIBLE:View.GONE);
    }

    /**
     * 判断是否显示红点
     */
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void checkRedIsShow(ChangeViewStateEvent event) {
        proMainView.showHotDot((event.isNewVersion() || event.isNewPackage())?View.VISIBLE:View.GONE);
    }

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
                break;
            case SocketConstant.REGISTER_FAIL://注册失败
                rigisterFail(entity.getRigsterStatueReason());
                break;
            case SocketConstant.REGISTERING://注册中
                registering(entity.getRigsterStatueReason());
                break;
            case SocketConstant.UNREGISTER:
                unregisterSim(entity.getRigsterStatueReason());
            default:

                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void cancelCallService(CancelCallService entity) {
        proMainView.stopCallPhoneService();
        hasPreDataRegisterImpl.unbindTcpService();
        destorySocketService();
    }

    private void unregisterSim(int unregisterReason) {
        switch (unregisterReason) {
            case SocketConstant.UN_INSERT_CARD:
                hasPreDataRegisterImpl.unbindTcpService();
                destorySocketService();
                break;
        }
    }

    private void rigisterFail(int failReason) {
        switch (failReason) {
            case SocketConstant.REGISTER_FAIL_INITIATIVE:
                //更改为注册中
                hasPreDataRegisterImpl.unbindTcpService();
                destorySocketService();
                break;
        }

    }

    private void registering(int registeringReason) {
        switch (registeringReason) {
            case SocketConstant.START_TCP_FAIL:
                hasPreDataRegisterImpl.unbindTcpService();
                destorySocketService();
                break;
            case SocketConstant.TCP_DISCONNECT:
                //更改为注册中
                break;
            case SocketConstant.RESTART_TCP:
                hasPreDataRegisterImpl.startSocketService();
                hasPreDataRegisterImpl.startTcpSocket();
                break;
            case SocketConstant.VAILD_CARD:
               requestGetSecurityConfig();
                break;
        }
    }
    //重新赋值
    private void destorySocketService() {
        if (SocketConstant.REGISTER_STATUE_CODE != 0) {
            SocketConstant.REGISTER_STATUE_CODE = 1;
        }
    }
    @Override
    public void onDestory() {
        EventBus.getDefault().unregister(this);
        hasPreDataRegisterImpl.unbindTcpService();
        noPreDataRegisterModel.unbindUdpService();
        registerBroadcastModel.unregisterBlueChangeBroadcast(context);
        proMainView.stopCallPhoneService();
    }
}
