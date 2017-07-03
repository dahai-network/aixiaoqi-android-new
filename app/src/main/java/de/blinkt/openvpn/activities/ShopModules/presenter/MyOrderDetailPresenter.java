package de.blinkt.openvpn.activities.ShopModules.presenter;

import android.os.Handler;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.Log;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.WriteCardFlowModel;
import de.blinkt.openvpn.activities.ShopModules.model.MyOrderDetailModel;
import de.blinkt.openvpn.activities.ShopModules.modelImpl.MyOrderDetailImple;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.ShopModules.view.MyOrderDetailView;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CancelOrderHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetOrderByIdHttp;
import de.blinkt.openvpn.http.OrderDataHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by kim
 * on 2017/6/14.
 */

public class MyOrderDetailPresenter extends BaseNetActivity {
    private MyOrderDetailView myOrderDetailView;
    private MyOrderDetailModel myOrderDetailModel;
    private MyOrderDetailActivity instance;
    boolean flg=true;//判断网络是否超时
    private Handler mHandler = new Handler() {
    };


    public MyOrderDetailPresenter(MyOrderDetailView myOrderDetailView) {
        this.myOrderDetailView = myOrderDetailView;
        myOrderDetailModel = new MyOrderDetailImple();
        instance = ICSOpenVPNApplication.myOrderDetailActivity;
    }
    Runnable runnable;
    public void addData(String id) {
        Log.d("MyOrderDetailPresenter", "addData: ");
        instance.showDefaultProgress();
        Log.d("MyOrderDetailPresenter", "addData:id= " + id);
        myOrderDetailModel.getUserPacketById(id, this);
        runnable = new Runnable() {
            @Override
            public void run() {
                if(flg){
                    instance.dismissProgress();
                    instance.showToast("网络超时，获取套餐详情信息失败");
                    flg=false;
                }
            }
        };
        mHandler.postDelayed(runnable, 10000);

    }

    public void orderDataHttpPresenter(String orderID, String nullcardNumber) {

        if (nullcardNumber != null) {
            if (!CommonTools.isFastDoubleClick(100))
                if (SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD))
                    nullcardNumber = null;
            myOrderDetailModel.orderDataHttp(orderID, nullcardNumber, this);
        } else {
            instance.dismissProgress();
            instance.showToast(R.string.no_nullcard_id);
        }
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        Log.d("MyOrderDetailPresenter", "rightComplete: " + cmdType);

        if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID) {
            if (object.getStatus() == 1) {
                GetOrderByIdHttp http = (GetOrderByIdHttp) object;
                myOrderDetailView.loadSuccessShowView(http);
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_CANCEL_ORDER) {
            CancelOrderHttp http = (CancelOrderHttp) object;
            if (http.getStatus() == 1) {
                instance.showToast("取消订单成功！");
                onBackPressed();
            } else {
                instance.showToast(http.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_ORDER_DATA) {
            OrderDataHttp orderDataHttp = (OrderDataHttp) object;
            if (orderDataHttp.getStatus() == 1) {
                if (!SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD)) {
                    sendMessageSeparate(orderDataHttp.getOrderDataEntity().getData());
                } else {
                    ICSOpenVPNApplication.cardData = orderDataHttp.getOrderDataEntity().getData();
                    Log.i("MyOrderDetailPresenter", "卡数据：" + ICSOpenVPNApplication.cardData);
                    ReceiveBLEMoveReceiver.isGetnullCardid = false;
                    sendMessageSeparate(Constant.WRITE_SIM_FIRST);
                }
            } else {
                instance.showToast(orderDataHttp.getMsg());
            }
        }
        instance.dismissProgress();
        flg=false;
    }

    private void sendMessageSeparate(final String message) {
        String[] messages = PacketeUtil.Separate(message, "1300");
        WriteCardFlowModel.lastSendMessageStr = message;
        int length = messages.length;
        for (int i = 0; i < length; i++) {
            if (!SendCommandToBluetooth.sendMessageToBlueTooth(messages[i])) {
                instance.showToast("设备已断开，请重新连接");
                instance.dismissProgress();
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        Log.d("errorComplete", "errorComplete: " + errorMessage);
        instance.showToast(errorMessage);
        flg=false;
    }

    @Override
    public void noNet() {
        myOrderDetailView.noNetShowView();
        flg=false;
    }

    public void relaseResource(){

        mHandler.removeCallbacks(runnable);
    }
}
