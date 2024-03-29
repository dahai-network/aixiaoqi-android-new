package de.blinkt.openvpn.activities.MyModules.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.MyModules.presenter.ActivatePresenter;
import de.blinkt.openvpn.activities.MyModules.view.ActivateView;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;

/**
 * @author kim
 * 激活套餐界面
 */
public class ActivateActivity extends BaseActivity implements View.OnClickListener, ActivateView {

    public static String FINISH_ACTIVITY = "finish_activity";
    TextView connectStatusTextView;
    TextView payForWhatTextView;
    TextView payWayTextView;
    TextView expireDaysTextView;
    TextView sureTextView;
    public static String orderId;
    private UartService mService = ICSOpenVPNApplication.uartService;
    private ActivatePresenter activatePresenter;
    private String effectTime;
    private String dataTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        ICSOpenVPNApplication.activateInstance=this;
        initView();
        initData();
        EventBus.getDefault().register(this);
        hasLeftViewTitle(R.string.activate_packet, -1);
        addListener();
        activatePresenter = new ActivatePresenter(this);
    }

    private void initData() {
        orderId = getIntent().getStringExtra(IntentPutKeyConstant.ORDER_ID);
        payWayTextView.setText(DateUtils.getCurrentDate());
        effectTime = (System.currentTimeMillis() / 1000) + "";
        dataTime = DateUtils.getDateToString(System.currentTimeMillis()).substring(0, 10);
        expireDaysTextView.setText(getIntent().getIntExtra("ExpireDaysInt", 0) + "天");
    }

    private void initView() {
        expireDaysTextView = (TextView) findViewById(R.id.expireDaysTextView);
        sureTextView = (TextView) findViewById(R.id.sureTextView);
        payWayTextView = (TextView) findViewById(R.id.payWayTextView);
        payForWhatTextView = (TextView) findViewById(R.id.payForWhatTextView);
        connectStatusTextView = (TextView) findViewById(R.id.connectStatusTextView);

        //判断设备是否连接成功
        connectStatusTextView.setText(getResources().getString(mService != null && mService.mConnectionState == UartService.STATE_CONNECTED?R.string.connect_success:R.string.activate_unconnected));
    }
    private void addListener() {
        expireDaysTextView.setOnClickListener(this);
        sureTextView.setOnClickListener(this);
        payWayTextView.setOnClickListener(this);
        payForWhatTextView.setOnClickListener(this);
        connectStatusTextView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.expireDaysTextView:
                break;
            case R.id.sureTextView:
                //激活套餐
                activatePresenter.activatePackage();
                break;
            case R.id.payWayTextView:
                activatePresenter.choiceTime();
                break;
            case R.id.payForWhatTextView:
                break;
            case R.id.connectStatusTextView:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveWriteCardIdEntity(WriteCardEntity entity) {
        String nullcardId = entity.getNullCardId();
        //获取写卡数据，然后发给蓝牙写卡
        activatePresenter.orderDataHttp(nullcardId);
    }
    @Override
    public void showToast(String msg) {
        CommonTools.showShortToast(ActivateActivity.this, msg);
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getDataTime() {
        return dataTime;
    }

    @Override
    public String getEffectTime() {
        return effectTime;
    }

    @Override
    public TextView getSureTextView() {
        return sureTextView;
    }

    @Override
    public TextView getPayWayTextView() {
        return payWayTextView;
    }

    @Override
    protected void onStop() {
        dismissProgress();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //释放资源
        activatePresenter.releaseResouce();
        EventBus.getDefault().unregister(this);
        ICSOpenVPNApplication.activateInstance=null;
        super.onDestroy();
    }
}
