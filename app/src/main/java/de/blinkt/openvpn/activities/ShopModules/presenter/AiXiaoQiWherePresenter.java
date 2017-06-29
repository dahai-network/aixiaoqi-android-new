package de.blinkt.openvpn.activities.ShopModules.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.ShopModules.view.AiXiaoQiWhereView;
import de.blinkt.openvpn.util.CommonTools;

import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.CARD_RULE_BREAK;
import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.FINISH_PROCESS;
import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.FINISH_PROCESS_ONLY;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class AiXiaoQiWherePresenter  extends NetPresenterBaseImpl{
    AiXiaoQiWhereView aiXiaoQiWhereView;
    Context context;

    public boolean isActivateSuccess() {
        return isActivateSuccess;
    }

    private boolean isActivateSuccess = false;

    public AiXiaoQiWherePresenter(AiXiaoQiWhereView aiXiaoQiWhereView,Context context ){
        this.aiXiaoQiWhereView=aiXiaoQiWhereView;
        this.context=context;
        registerBroadcast();

    }

    public void phoneActivate(){

    }

    public void equipmentActivate(){

    }

    private void registerBroadcast(){
        LocalBroadcastManager.getInstance(context).registerReceiver(isWriteReceiver, setFilter());
    }

    private IntentFilter setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyOrderDetailActivity.FINISH_PROCESS);
        filter.addAction(MyOrderDetailActivity.FINISH_PROCESS_ONLY);
        filter.addAction(MyOrderDetailActivity.CARD_RULE_BREAK);
        return filter;
    }

    private BroadcastReceiver isWriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), CARD_RULE_BREAK)) {
                aiXiaoQiWhereView.dismissProgress();
                aiXiaoQiWhereView.showDialog();
            } else if (TextUtils.equals(intent.getAction(), FINISH_PROCESS)) {
                if (ReceiveBLEMoveReceiver.orderStatus == 4) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("statue", 0 + "");
                    //友盟方法统计
                    MobclickAgent.onEvent(context, CLICKACTIVECARD, map);
                    aiXiaoQiWhereView.showToast( "激活失败！请检查你的SIM卡是否是爱小器SIM卡");
                } else {
                    isActivateSuccess = true;
                }


//                myOrderDetailPresenter.addData(((Activity)context).getIntent().getStringExtra("id"));
              /*  GetOrderByIdHttp http = new GetOrderByIdHttp(MyOrderDetailActivity.this, HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, getIntent().getStringExtra("id"));
                new Thread(http).start();*/

            } else if (TextUtils.equals(intent.getAction(), FINISH_PROCESS_ONLY)) {
                aiXiaoQiWhereView.dismissProgress();
            }
        }
    };

    @Override
    public void onDestroy() {
        if (isWriteReceiver != null)
            LocalBroadcastManager.getInstance(context).unregisterReceiver(isWriteReceiver);
    }
}
