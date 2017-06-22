package de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.AppMode;
import de.blinkt.openvpn.activities.Device.Model.GetBindDeviceInfoModel;
import de.blinkt.openvpn.activities.Device.ModelImpl.GetBindDeviceInfoModelImpl;
import de.blinkt.openvpn.activities.Device.ModelImpl.UnbindDeviceModelImpl;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.BalanceModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.UserOrderUsageModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Presenter.AccountPresenter;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.AccountView;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.OrderUsageRemainHttp;
import de.blinkt.openvpn.model.BlueToothDeviceEntity;
import de.blinkt.openvpn.model.UsageRemainEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.Constant.BRACELETNAME;
import static de.blinkt.openvpn.constant.Constant.BRACELETPOWER;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class AccountPresenterImpl extends NetPresenterBaseImpl implements AccountPresenter {
    AccountView accountView;
    BalanceModelImpl balanceModel;
    GetBindDeviceInfoModelImpl getBindDeviceInfoModel;
    UnbindDeviceModelImpl unbindDeviceModel;
    UserOrderUsageModelImpl userOrderUsageModel;
    public  AccountPresenterImpl(AccountView accountView){
        this.accountView=accountView;
        balanceModel=new BalanceModelImpl(this);
        getBindDeviceInfoModel=new GetBindDeviceInfoModelImpl(this);
        unbindDeviceModel=new UnbindDeviceModelImpl(this);
        userOrderUsageModel=new UserOrderUsageModelImpl(this);
    }


    @Override
    public void requestUserPackage() {
        userOrderUsageModel.requestUserPackage();
    }

    @Override
    public void requestBalance() {
        balanceModel.requestBalance();
    }

    @Override
    public void requestGetBindInfo() {
        getBindDeviceInfoModel.getBindDeviceinfo();
    }

    @Override
    public void requestUnbindDevice() {
        unbindDeviceModel.unbindDevice();
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_BALANCE) {
            BalanceHttp http = (BalanceHttp) object;
            if (http.getBalanceEntity() != null)
                accountView.setBalanceText(http.getBalanceEntity().getAmount());
        } else if (cmdType == HttpConfigUrl.COMTYPE_UN_BIND_DEVICE) {
            unbindDevice(object);
        }else if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_ORDER_USAGE_REMAINING) {
            showPackage(object);
        } else if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
            if(object.getStatus()==1){
           GetBindDeviceHttp getBindDeviceHttp=(GetBindDeviceHttp)     object;
                if(getBindDeviceHttp.getBlueToothDeviceEntityity()==null||TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())){
                    if(isClickAddDevice)
                    accountView.toActivity();
                }else{
                    getBindDeviceInfo(object);
                }
            }

            isClickAddDevice = false;
        }
    }

    private void unbindDevice(CommonHttp object) {
        if (object.getStatus() == 1) {
            accountView.showToast(R.string.ready_remove_bind);
            accountView.showDeviceSummarized(false);
        } else {
            accountView.showToast(object.getMsg());
        }
    }
    public boolean canClick(){
        String braceletName = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
        //如果设备名没有就设置成爱小器钥匙扣
        if (TextUtils.isEmpty(braceletName)||TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
            if (NetworkUtils.isNetworkAvailable(ICSOpenVPNApplication.getContext())) {
                isClickAddDevice = true;
                requestGetBindInfo();
            } else {
                accountView.showToast(R.string.no_wifi);
            }
            return false;
        }
        return true;

    }
    private void getBindDeviceInfo(CommonHttp object) {
        GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
        if (object.getStatus() == 1) {
            if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
                accountView.showDeviceSummarized(!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI()));
                if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
                    if (isClickAddDevice) {
                        accountView.setDeviceType();
                        accountView.toMyDeviceActivity();
                    }

                }
            }
        }
    }

    public String getServiceOrderId() {
        return serviceOrderId;
    }

    private String serviceOrderId;
    public boolean isHasPackage() {
        return hasPackage;
    }
    private int SIGN_MSG_ONE=1;
    private int SIGN_MSG_TWO=2;
    private boolean hasPackage;
    private boolean isClickAddDevice;
    private void showPackage(CommonHttp object) {
        if (object.getStatus() == 1) {
            OrderUsageRemainHttp orderUsageRemainHttp = (OrderUsageRemainHttp) object;
            UsageRemainEntity.Unactivated unactivated = orderUsageRemainHttp.getUsageRemainEntity().getUnactivated();
            UsageRemainEntity.Used   used = orderUsageRemainHttp.getUsageRemainEntity().getUsed();
            if (used == null) {
                return;
            }
            serviceOrderId=used.getServiceOrderId();
            if ("0".equals(used.getTotalNum()) && !"0".equals(unactivated.getTotalNumFlow()) && "0".equals(used.getTotalNumFlow())) {//有套餐，未激活
                if (!AppMode.getInstance().isClickPackage)
                    accountView.updateRedDot(SIGN_MSG_ONE);
                hasPackage = true;
                accountView.showPackage(View.GONE,View.VISIBLE);
                accountView.addOrActivatePackageIvAndText(R.drawable.activate_device_account,R.string.activate_packet);
            } else if ("0".equals(used.getTotalNum()) && "0".equals(unactivated.getTotalNumFlow())) {//无套餐显示
                accountView.updateRedDot(SIGN_MSG_TWO);
                hasPackage = false;
                accountView.showPackage(View.GONE,View.VISIBLE);
                accountView.addOrActivatePackageIvAndText(R.drawable.add_device,R.string.add_package);
            } else {//有套餐且激活了。
                hasPackage = true;
                accountView.showPackage(View.VISIBLE,View.GONE);
                accountView.callTime(used.getTotalRemainingCallMinutes() + "分");
                //显示出有未激活套餐的提示
                if (!"0".equals(unactivated.getTotalNumFlow()) && !AppMode.getInstance().isClickPackage) {

                    accountView.updateRedDot(SIGN_MSG_ONE);
                } else {
                    accountView.updateRedDot(SIGN_MSG_TWO);
                }

                if ("0".equals(used.getTotalNumFlow())) {
                    accountView.activatePackage(R.string.no_flow_count,unactivated.getTotalNumFlow());
                } else {
                    accountView.activatePackage(R.string.flow_count,used.getTotalNumFlow());
                }
                accountView.packageAllCount(used.getTotalNum());
                String serviceName = used.getServiceName();
                if (!TextUtils.isEmpty(serviceName)) {
                    SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, true);
                    accountView.setServiceText(serviceName);
                } else {
                    SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, false);
                    accountView.setServiceText("---");
                }
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        accountView.showToast(errorMessage);
        isClickAddDevice = false;
    }

    @Override
    public void noNet() {
        isClickAddDevice = false;
        accountView.showToast(R.string.no_wifi);
    }


}
