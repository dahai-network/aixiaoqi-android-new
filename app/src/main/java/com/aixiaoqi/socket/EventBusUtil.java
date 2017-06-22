package com.aixiaoqi.socket;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.model.BluetoothMessageCallBackEntity;
import de.blinkt.openvpn.model.CanClickEntity;
import de.blinkt.openvpn.model.CancelCallService;
import de.blinkt.openvpn.model.GetTokenRes;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.StateChangeEntity;
import de.blinkt.openvpn.model.enentbus.BlueConnStatue;
import de.blinkt.openvpn.model.enentbus.BlueReturnData;
import de.blinkt.openvpn.model.enentbus.OptionCellPhoneFragmentView;
import de.blinkt.openvpn.model.enentbus.OptionProMainActivityView;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class EventBusUtil {

    /**
     *卡注册状态
     * @param regstatues  注册状态
     */
    public static void simRegisterStatue(int regstatues) {
        SimRegisterStatue entity = new SimRegisterStatue();
        entity.setRigsterSimStatue(regstatues);
        EventBus.getDefault().post(entity);
    }
    /**
     * 卡注册状态
     * @param regstatues  注册状态
     * @param regstatuesreason  注册状态过程中的原因
     */
    public static void simRegisterStatue(int regstatues,int regstatuesreason) {
        SimRegisterStatue entity = new SimRegisterStatue();
        entity.setRigsterSimStatue(regstatues);
        entity.setRigsterStatueReason(regstatuesreason);
        EventBus.getDefault().post(entity);
    }
    /**
     * 卡注册状态
     * @param regstatues  注册状态
     * @param regstatuesreason  注册状态过程中的原因
     * @param percent 进度条
     */
    public static void simRegisterStatue(int regstatues,int regstatuesreason,int percent) {
        SimRegisterStatue entity = new SimRegisterStatue();
        entity.setRigsterSimStatue(regstatues);
        entity.setRigsterStatueReason(regstatuesreason);
        entity.setProgressCount(percent);
        EventBus.getDefault().post(entity);
    }
    //网络状态改变
    public static void simStateChange(String registerType ) {
        StateChangeEntity entity = new StateChangeEntity();
        entity.setStateType(registerType);
        EventBus.getDefault().post(entity);
    }

    /**
     * 当退出登录或者挤下线的时候，取消关闭电话服务
     */
    public static void cancelCallService( ) {
        CancelCallService entity = new CancelCallService();
        EventBus.getDefault().post(entity);
    }

    /**
     * 能够点击头部
     * @param jumpTo
     */
    public static void canClickEntity(String jumpTo ) {
        CanClickEntity entity = new CanClickEntity();
        entity.setJumpTo(jumpTo);
        EventBus.getDefault().post(entity);
    }

    /**
     * 如果创建电话服务因为网络问题失败，则等有网络以后重新获取
     */
    public static void getTokenRes() {
        GetTokenRes entity = new GetTokenRes();
        EventBus.getDefault().post(entity);
    }

    public static void optionView(boolean isShow) {
        OptionProMainActivityView entity = new OptionProMainActivityView();
        entity.setShow(isShow);
        EventBus.getDefault().post(entity);
    }

    public static void optionView(String  textChange) {
        OptionCellPhoneFragmentView entity = new OptionCellPhoneFragmentView();
        entity.setTextChange(textChange);
        EventBus.getDefault().post(entity);
    }

    public static void blueConnStatue(int connStatue){
        BlueConnStatue blueConnStatue=new BlueConnStatue();
        blueConnStatue.setConnStatue(connStatue);
        EventBus.getDefault().post(blueConnStatue);
    }

    /**
     *
     * @param dataType
     * @param responeStatue
     * @param valideData
     */
    public  static void blueReturnData(String dataType,String responeStatue,String valideData){
        BlueReturnData blueReturnData=new BlueReturnData();
        blueReturnData.setDataType(dataType);
        blueReturnData.setResponeStatue(responeStatue);
        blueReturnData.setValideData(valideData);
        EventBus.getDefault().post(blueReturnData);
    }

    /**
     *
     * @param bindStatue 绑定状态参数，是成功的还是失败的。
     */
    public  static void bingDeviceStep(String bindStatue){
        BluetoothMessageCallBackEntity bluetoothMessageCallBackEntity=new BluetoothMessageCallBackEntity();
        bluetoothMessageCallBackEntity.setBlueType(bindStatue);
        EventBus.getDefault().post(bluetoothMessageCallBackEntity);
    }


}
