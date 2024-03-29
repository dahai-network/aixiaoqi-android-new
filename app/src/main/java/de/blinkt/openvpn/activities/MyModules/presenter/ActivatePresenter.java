package de.blinkt.openvpn.activities.MyModules.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType.WriteCardFlowModel;
import de.blinkt.openvpn.activities.MyModules.model.ActivateMode;
import de.blinkt.openvpn.activities.MyModules.modelImple.ActivateModeImpl;
import de.blinkt.openvpn.activities.MyModules.view.ActivateView;
import de.blinkt.openvpn.activities.MyModules.ui.ActivateActivity;
import de.blinkt.openvpn.activities.SetFlowCard.OutsideFirstStepActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.OrderActivationHttp;
import de.blinkt.openvpn.http.OrderDataHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogYearMonthDayPicker;

import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.isGetnullCardid;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVEPACKAGE;

/**
 * Created by kim
 *
 * @des 激活套餐Presenter
 * on 2017/6/5.
 */

public class ActivatePresenter extends BaseNetActivity implements DialogInterfaceTypeBase {

    private ActivateMode activateMode;
    private ActivateView activateView;
    TextView sureTextView;
    String dataTime;
    String effectTime;
    String orderId;
    private ActivateActivity instance;
    private boolean isActivateSuccess = false;



    public ActivatePresenter(ActivateView activateView) {
        this.activateView = activateView;
        activateMode = new ActivateModeImpl();
        initActivateViewData();
        if(ICSOpenVPNApplication.activateInstance!=null)
        {
            instance=ICSOpenVPNApplication.activateInstance;

        }
        LocalBroadcastManager.getInstance(instance).registerReceiver(isWriteReceiver, setFilter());
        LocalBroadcastManager.getInstance(instance).registerReceiver(finishActivityReceiver, setFinishFilter());
    }

    /**
     * 初始化数据
     */
    private void initActivateViewData() {

        sureTextView = activateView.getSureTextView();
        effectTime=activateView.getEffectTime();

    }

    /**
     * 激活套餐
     */
    public void activatePackage() {
        if (!CommonTools.isFastDoubleClick(3000)) {
            String operator = SharedUtils.getInstance().readString(Constant.OPERATER);
            UartService uartService = ICSOpenVPNApplication.uartService;
            if (TextUtils.isEmpty(operator)
                    && uartService != null
                    && uartService.isConnectedBlueTooth()) {
                //友盟方法统计
                MobclickAgent.onEvent(instance, CLICKACTIVEPACKAGE);
                //激活套餐
                isCanActivatePackage();
            } else {
                showDialog();
            }
        }

    }

    public void isCanActivatePackage() {
        dataTime = activateView.getDataTime();
        orderId = activateView.getOrderId();
        Log.d(TAG, "isCanActivatePackage: "+effectTime);
        if (TextUtils.isEmpty(effectTime)) {
            activateView.showToast(instance.getString(R.string.effective_date_is_null));
            return;
        }
        activateMode.orderActivationHttp(orderId, dataTime, this);
    }

    /**
     * @param nullcardNumber
     */
    public void orderDataHttp(String nullcardNumber) {
        if (nullcardNumber != null) {
            if (SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD))
                nullcardNumber = null;
            if (!CommonTools.isFastDoubleClick(100))
                activateMode.createHttpRequest(orderId, nullcardNumber, this);
        } else {
            activateView.showToast(instance.getString(R.string.no_nullcard_id));
        }
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        Log.d(TAG, "rightComplete: " + cmdType);
        if (cmdType == HttpConfigUrl.COMTYPE_ORDER_ACTIVATION) {
            OrderActivationHttp orderActivationHttp = (OrderActivationHttp) object;
            if (orderActivationHttp.getStatus() == 1) {
                //是否测试卡位置：否，这是写卡！
                IS_TEXT_SIM = false;
                ReceiveBLEMoveReceiver.orderStatus = 4;
                instance.showProgress("正在激活", false);
                isGetnullCardid = true;
                SendCommandToBluetooth.sendMessageToBlueTooth(Constant.UP_TO_POWER_NO_RESPONSE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isActivateSuccess) {
                            instance.dismissProgress();
                            activateView.showToast(instance.getString(R.string.activate_fail));
                        }
                    }
                },20000);
            } else {
                Log.d(TAG, "rightComplete: " + BaseStatusFragment.bleStatus);
                activateView.showToast(orderActivationHttp.getMsg());
                sureTextView.setEnabled(true);
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_ORDER_DATA) {
            OrderDataHttp orderDataHttp = (OrderDataHttp) object;
            if (orderDataHttp.getStatus() == 1) {
                if (!SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD)) {
                    sendMessageSeparate(orderDataHttp.getOrderDataEntity().getData());
                } else {
                    ICSOpenVPNApplication.cardData = orderDataHttp.getOrderDataEntity().getData();
                    Log.i(TAG, "卡数据：" + ICSOpenVPNApplication.cardData);
                    isGetnullCardid = false;
                    sendMessageSeparate(Constant.WRITE_SIM_FIRST);
                }
            } else {
                activateView.showToast(orderDataHttp.getMsg());
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        sureTextView.setEnabled(true);
    }

    @Override
    public void noNet() {
        sureTextView.setEnabled(true);
        instance.dismissProgress();
    }

    private void sendMessageSeparate(final String message) {
        String[] messages = PacketeUtil.Separate(message, "1300");
        WriteCardFlowModel.lastSendMessageStr = message;
        int length = messages.length;
        for (int i = 0; i < length; i++) {
            if (!SendCommandToBluetooth.sendMessageToBlueTooth(messages[i])) {
                instance.dismissProgress();
            }
        }
    }

    @Override
    public void dialogText(int type, String text) {
        if (type == 2) {
            SendCommandToBluetooth.sendMessageToBlueTooth(Constant.RESTORATION);
        } else if (type == 0) {
            // - 24 * 60 * 60 * 1000
            if (System.currentTimeMillis() >= DateUtils.getStringToDate(text + " 00:00:00")) {
                activateView.showToast(instance.getString(R.string.less_current_time));
                return;
            }
            dataTime = text;
            effectTime = DateUtils.getStringToDate(text + " 00:00:00") / 1000 + "";
            Log.d("ActivatePresenter", "dialogText: "+effectTime+":text="+text);
            String[] time = text.split("-");
            TextView payWayTextView = activateView.getPayWayTextView();
            if (payWayTextView != null)
                payWayTextView.setText(time[0] + instance.getString(R.string.year) + time[1] + instance.getString(R.string.month) + time[2] + instance.getString(R.string.daliy));
        }
    }

    /**
     * 弹窗让用户选择时间
     */
    public void choiceTime() {
        DialogYearMonthDayPicker dialogYearMonthDayPicker = new DialogYearMonthDayPicker(this, instance, R.layout.picker_year_month_day_layout, 0);
        dialogYearMonthDayPicker.changeText(instance.getResources().getString(R.string.select_time) + "(" + instance.getIntent().getStringExtra(IntentPutKeyConstant.COUNTRY_NAME) + ")");
    }

    public void showDialog() {
        //不能按返回键，只能二选其一
        DialogBalance cardRuleBreakDialog = new DialogBalance(this, instance, R.layout.dialog_balance, 2);
        cardRuleBreakDialog.setCanClickBack(false);
        cardRuleBreakDialog.changeText(instance.getResources().getString(R.string.no_aixiaoqi_or_rule_break), instance.getResources().getString(R.string.reset));
    }

    //写卡成功关闭process
    private BroadcastReceiver isWriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), MyOrderDetailActivity.CARD_RULE_BREAK)) {
                instance.dismissProgress();
                showDialog();
            } else if (TextUtils.equals(intent.getAction(), MyOrderDetailActivity.FINISH_PROCESS)) {
                if (ReceiveBLEMoveReceiver.orderStatus == 4) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("statue", 0 + "");
                    //友盟方法统计
                    MobclickAgent.onEvent(instance, CLICKACTIVECARD, map);
                    activateView.showToast(instance.getString(R.string.activate_fail));

                } else {
                    Constant.isOutsideSecondStepClick = false;
                    Constant.isOutsideThirdStepClick = false;
                    instance.toActivity(new Intent(instance, OutsideFirstStepActivity.class)
                            .putExtra(IntentPutKeyConstant.OUTSIDE, IntentPutKeyConstant.OUTSIDE)
                            .putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, instance.getIntent().getBooleanExtra(IntentPutKeyConstant.IS_SUPPORT_4G, false))
                    );
                    isActivateSuccess = true;
                }
                instance.dismissProgress();
                //关闭界面
                instance.finish();

            } else if (TextUtils.equals(intent.getAction(), MyOrderDetailActivity.FINISH_PROCESS_ONLY)) {
                instance.dismissProgress();
            }
        }
    };

    private IntentFilter setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyOrderDetailActivity.FINISH_PROCESS);
        filter.addAction(MyOrderDetailActivity.FINISH_PROCESS_ONLY);
        filter.addAction(MyOrderDetailActivity.CARD_RULE_BREAK);
        return filter;
    }

    //上传数据成功关闭Activity
    private BroadcastReceiver finishActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            instance.finish();
        }
    };

    private IntentFilter setFinishFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivateActivity.FINISH_ACTIVITY);
        return filter;
    }

    public void releaseResouce() {
        LocalBroadcastManager.getInstance(instance).unregisterReceiver(isWriteReceiver);
        LocalBroadcastManager.getInstance(instance).unregisterReceiver(finishActivityReceiver);

    }
}
