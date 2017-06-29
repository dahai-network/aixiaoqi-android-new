package de.blinkt.openvpn.activities.ShopModules.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.CARD_RULE_BREAK;
import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.FINISH_PROCESS;
import static de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity.FINISH_PROCESS_ONLY;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class AiXiaoQiWhereActivity extends BaseActivity implements DialogInterfaceTypeBase {
    @BindView(R.id.phone_activate)
    TextView phoneActivate;
    @BindView(R.id.equipment_activate)
    TextView equipmentActivate;
    @BindView(R.id.connect_statue_tv)
    TextView connectStatueTv;
    @BindView(R.id.insert_statue_tv)
    TextView insertStatueTv;
    DialogBalance  cardRuleBreakDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aixiaoqi_where);
        ButterKnife.bind(this);
        initUi();
        setTitle();
    }




    private  void setTitle(){
        hasLeftViewTitle(R.string.aixiaoqi_where,-1);
    }


    private void showDialog() {
        //不能按返回键，只能二选其一
        if(cardRuleBreakDialog==null){
            cardRuleBreakDialog = new DialogBalance(this, this, R.layout.dialog_balance, 2);
            cardRuleBreakDialog.setCanClickBack(false);
            cardRuleBreakDialog.changeText(getResources().getString(R.string.no_aixiaoqi_or_rule_break), getResources().getString(R.string.reset));
        }else{
            cardRuleBreakDialog.show();
        }
    }


    private void initUi() {
        if(ICSOpenVPNApplication.uartService!=null&&ICSOpenVPNApplication.uartService.isConnectedBlueTooth()){

            connectStatueTv.setText(getString(R.string.bind_seccess));
        }else{
            connectStatueTv.setText(getString(R.string.index_unbind));
        }

        if( SocketConstant.SIM_TYPE==4){//爱小器卡
            insertStatueTv.setText(getString(R.string.index_aixiaoqicard));
        }else if(SocketConstant.SIM_TYPE==0){//未插卡
            insertStatueTv.setText(getString(R.string.index_un_insert_card));
        }else{//不是爱小器的卡
            insertStatueTv.setText(getString(R.string.no_nullcard_id));
        }
    }

    @OnClick({R.id.phone_activate,
            R.id.equipment_activate
    })
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.phone_activate:

                break;
            case R.id.equipment_activate:

                break;
        }

    }


    @Override
    public void dialogText(int type, String text) {
        if (type == 2) {
            SendCommandToBluetooth.sendMessageToBlueTooth(Constant.RESTORATION);
        }
    }

    @Override
    public void showToast(String showContent) {
        super.showToast(showContent);
    }

    @Override
    public void showToast(int showContentId) {
        super.showToast(showContentId);
    }
}
