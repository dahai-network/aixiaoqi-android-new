package de.blinkt.openvpn.activities.ShopModules.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.WriteCardFlowModel;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.ShopModules.modelImpl.CardDataModelImpl;
import de.blinkt.openvpn.activities.ShopModules.modelImpl.EquipmentActivateModelImpl;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.ShopModules.view.AiXiaoQiWhereView;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.OrderDataHttp;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.SimActivateHelper;

import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.orderStatus;
import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.CARD_RULE_BREAK;
import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.FINISH_PROCESS;
import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.FINISH_PROCESS_ONLY;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class AiXiaoQiWherePresenter  extends NetPresenterBaseImpl{
    AiXiaoQiWhereView aiXiaoQiWhereView;

    EquipmentActivateModelImpl equipmentActivateModel;
    CardDataModelImpl cardDataModel;
    Context aiXiaoQiWhereContext;
    String  activateType;
    private static String PHONE_ACTIVATE="phone";
    private static String EQUIPMENT_ACTIVATE="equipment";
    public boolean isActivateSuccess() {
        return isActivateSuccess;
    }
    public static String orderDetailId;
    private boolean isActivateSuccess = false;

    public AiXiaoQiWherePresenter(AiXiaoQiWhereView aiXiaoQiWhereView,Context context ){
        this.aiXiaoQiWhereView=aiXiaoQiWhereView;
        this.aiXiaoQiWhereContext=context;
        equipmentActivateModel=new EquipmentActivateModelImpl(context);
        cardDataModel=new CardDataModelImpl(this);
        EventBus.getDefault().register(this);
        registerBroadcast();
        orderDetailId=((Activity)context).getIntent().getStringExtra("id");
    }

    public void phoneActivate( ){
        aiXiaoQiWhereView.showProgress(aiXiaoQiWhereContext.getString(R.string.activate_succeed), false);
        activateType=PHONE_ACTIVATE;
        cardDataModel.getCardDataHttp(((Activity)aiXiaoQiWhereContext).getIntent().getStringExtra("id"),null);

    }

    private void writeCMDSmall(String cardInfo){
        if(SimActivateHelper.getInstance().writeCMDSmall(cardInfo)){
            aiXiaoQiWhereView.showToast(R.string.activate_succeed);
        }else{
            aiXiaoQiWhereView.showToast(R.string.activate_failure);
        }
        aiXiaoQiWhereView.dismissProgress();
    }

    public void equipmentActivate(){
        activateType=EQUIPMENT_ACTIVATE;
        if(equipmentActivateModel.equipmentActivate()){
            IS_TEXT_SIM = false;
            orderStatus = 4;
            aiXiaoQiWhereView.showProgress(aiXiaoQiWhereContext.getString(R.string.activate_begin), false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            },30000);
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (!isActivateSuccess) {
                        aiXiaoQiWhereView.dismissProgress();
                        aiXiaoQiWhereView.showToast(R.string.activate_fail);
                    }
                    break;
            }
        }
    };


    private void registerBroadcast(){
        LocalBroadcastManager.getInstance(aiXiaoQiWhereContext).registerReceiver(isWriteReceiver, setFilter());
    }

    private IntentFilter setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyOrderDetailActivity.FINISH_PROCESS);
        filter.addAction(MyOrderDetailActivity.FINISH_PROCESS_ONLY);
        filter.addAction(MyOrderDetailActivity.CARD_RULE_BREAK);
        return filter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveWriteCardIdEntity(WriteCardEntity entity) {
        String nullcardId = entity.getNullCardId();
        cardDataModel.getCardDataHttp(((Activity)aiXiaoQiWhereContext).getIntent().getStringExtra("id"),nullcardId);
    }

    private BroadcastReceiver isWriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), CARD_RULE_BREAK)) {
                aiXiaoQiWhereView.dismissProgress();
                aiXiaoQiWhereView.showDialog();
            } else if (TextUtils.equals(intent.getAction(), FINISH_PROCESS)) {
//                if (ReceiveBLEMoveReceiver.orderStatus == 4) {
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("statue", 0 + "");
//                    //友盟方法统计
//                    MobclickAgent.onEvent(context, CLICKACTIVECARD, map);
//                    aiXiaoQiWhereView.showToast(R.string.activate_failure);
//                } else {
                isActivateSuccess = true;
                ((Activity)aiXiaoQiWhereContext).finish();
//                }

            } else if (TextUtils.equals(intent.getAction(), FINISH_PROCESS_ONLY)) {
                aiXiaoQiWhereView.dismissProgress();
            }
        }
    };


    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_ORDER_DATA) {
            OrderDataHttp orderDataHttp = (OrderDataHttp) object;
            if (orderDataHttp.getStatus() == 1) {
                if(PHONE_ACTIVATE.equals(activateType)){
                    writeCMDSmall(orderDataHttp.getOrderDataEntity().getData());
                }else if(EQUIPMENT_ACTIVATE.equals(activateType)){
                    String message;
                    if (!SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD)) {
                        message=orderDataHttp.getOrderDataEntity().getData();
                        SendCommandToBluetooth.sendToBlue(orderDataHttp.getOrderDataEntity().getData(),"1300");
                    } else {
                        message= Constant.WRITE_SIM_FIRST;
                        ICSOpenVPNApplication.cardData = orderDataHttp.getOrderDataEntity().getData();
                        Log.i("MyOrderDetailPresenter", "卡数据：" + ICSOpenVPNApplication.cardData);
                        ReceiveBLEMoveReceiver.isGetnullCardid = false;
                        SendCommandToBluetooth.sendToBlue(Constant.WRITE_SIM_FIRST,"1300");
                    }
                    WriteCardFlowModel.lastSendMessageStr = message;
                }
            } else {
                aiXiaoQiWhereView.showToast(orderDataHttp.getMsg());
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        aiXiaoQiWhereView.dismissProgress();
    }

    @Override
    public void noNet() {
        aiXiaoQiWhereView.dismissProgress();
    }

    @Override
    public void onDestroy() {
        aiXiaoQiWhereView=null;
        equipmentActivateModel=null;
        cardDataModel=null;
        if (isWriteReceiver != null)
            LocalBroadcastManager.getInstance(aiXiaoQiWhereContext).unregisterReceiver(isWriteReceiver);
        EventBus.getDefault().unregister(this);
    }
}
