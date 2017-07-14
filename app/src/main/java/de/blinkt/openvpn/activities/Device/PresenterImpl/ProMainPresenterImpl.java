package de.blinkt.openvpn.activities.Device.PresenterImpl;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SdkAndBluetoothDataInchange;
import com.aixiaoqi.socket.SendYiZhengService;
import com.aixiaoqi.socket.SocketConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.aixiaoqi.R;
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
import de.blinkt.openvpn.util.NetworkUtils;
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
    private int requestCount = 0;//判断是否有网，重发三次如果还没有网络，就注册失败
    public static   SdkAndBluetoothDataInchange sdkAndBluetoothDataInchange;
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

    public  void registerReceiveBroadcast(){
        registerBroadcastModel.registerReceiveBLEMoveReceiverBroadcast(context);
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
        e("requestGetSecurityConfig");
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
                        if(!SharedUtils.getInstance().readBoolean(Constant.HAS_DEVICE_NEED_UPGRADE)){
                            requestSkyUpdate();
                        }else{
                            proMainView.showHotDot(View.VISIBLE);
                        }

                        proMainView.blueToothOpen();
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
                          /*  if(CommonTools.isFastDoubleClick(3000)){

                                return;
                            }*/
                            if (sdkAndBluetoothDataInchange == null) {
                                sdkAndBluetoothDataInchange = new SdkAndBluetoothDataInchange();
                            }
                            sdkAndBluetoothDataInchange.isHasPreData = false;
                            //判断是否有没有iccid
                            if (!TextUtils.isEmpty(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]])) {
                                getIccidCount=0;
                                //通过iccid去本地数据库获取鉴权数据
                                DBHelp dbHelp = new DBHelp(context);
                                PreReadEntity preReadEntity = dbHelp.getPreReadEntity(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]]);
                                //判断是否有鉴权数据
                                if (preReadEntity != null) {
                                    e("有预读取数据=");
                                    hasPreDataRegisterImpl.initPreData(preReadEntity);
                                    hasPreDataRegisterImpl.registerSimPreData();
                                } else {
                                    e("没有预读取数据=");
                                    noPreDataRegister();
                                }
                            } else {
                                CommonTools.delayTime(3000);
                                if(getIccidCount<3){
                                    SendCommandToBluetooth.sendMessageToBlueTooth(ICCID_GET);
                                    getIccidCount++;
                                }else{
                                    getIccidCount=0;
                                    noPreDataRegister();
                                }
                            }
                        }
                    }).start();
                }
            }
        }else if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
            if (object.getStatus() == 1) {
                proMainView.showHotDot(View.VISIBLE);
            }
        }
    }

    /**
     * 获取不到鉴权数据
     */
    private void noPreDataRegister() {

        hasPreDataRegisterImpl.startSocketService();
        noPreDataRegisterModel.noPreDataStartSDKSimRegister();
    }

    private int getIccidCount=0;//统计请求不到ICCID的次数，如果大于3次就不在请求ICCID,直接启动UDP进行注册。如果请求到了或者直接启动udp去注册亦或换卡了，则重新置零。

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG) {
            if (requestCount < 3) {
                requestCount++;
                requestGetSecurityConfig();
            }else{
                requestCount=0;
                EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.NO_NET);
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
        e("getRigsterSimStatue="+entity.getRigsterSimStatue()+"\ngetRigsterStatueReason="+entity.getRigsterStatueReason());
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
                getIccidCount=0;
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
            //TCP失败
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
                getPortAndIp();
                break;
        }
    }


    private void getPortAndIp() {
        //如果请求三次没成功显示失败
        if(requestCount>3){
            proMainView.showToast(R.string.no_wifi);
            EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.NO_NET);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(NetworkUtils.isNetworkAvailable(context)){
                    //请求网络获取port与ip
                    requestGetSecurityConfig();
                }else{
                    CommonTools.delayTime(2000);
                    requestCount++;
                    Looper.prepare();
                    getPortAndIp();
                    Looper.loop();
                }
            }
        }).start();

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
        registerBroadcastModel.unregisterReceiveBLEMoveReceiverBroadcast(context);
        proMainView.stopCallPhoneService();
    }
}
