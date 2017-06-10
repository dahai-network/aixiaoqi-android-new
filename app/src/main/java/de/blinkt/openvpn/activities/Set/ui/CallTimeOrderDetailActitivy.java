package de.blinkt.openvpn.activities.Set.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.model.CallTime_CreatViewEvent;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.Set.Presenter.CallTimeOrderDetailPresenter;
import de.blinkt.openvpn.activities.Set.View.CallTimeOrderDetailView;
import de.blinkt.openvpn.model.OrderEntity;
import de.blinkt.openvpn.util.CommonTools;


public class CallTimeOrderDetailActitivy extends BaseActivity implements CallTimeOrderDetailView {

    public static CallTimeOrderDetailActitivy actitivy;
    @BindView(R.id.retryTextView)
    TextView retryTextView;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.packetImageView)
    ImageView packetImageView;
    @BindView(R.id.packageNameTextView)
    TextView packageNameTextView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.orderNumberTextView)
    TextView orderNumberTextView;
    @BindView(R.id.orderTimeTextView)
    TextView orderTimeTextView;
    @BindView(R.id.payWayTextView)
    TextView payWayTextView;
    @BindView(R.id.expiryDateTextView)
    TextView expiryDateTextView;
    @BindView(R.id.packetStatusTextView)
    TextView packetStatusTextView;
    @BindView(R.id.headBarRelativeLayout)
    RelativeLayout headBarRelativeLayout;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.line3)
    View line3;
    private boolean isCreateView;
    private OrderEntity.ListBean bean;
    private CallTimeOrderDetailPresenter callPackageListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actitivy = this;
        EventBus.getDefault().register(this);
        callPackageListPresenter = new CallTimeOrderDetailPresenter(this);
        addData();
    }

    public static void launch(Context context, String id) {
        Intent intent = new Intent(context, CallTimeOrderDetailActitivy.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }


    //获取数据
    private void addData() {
        showDefaultProgress();
        callPackageListPresenter.getOrderDetailData();

    }

    @OnClick({R.id.retryTextView, R.id.headBarRelativeLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retryTextView:
                addData();
                break;
            case R.id.headBarRelativeLayout:
                CallTimePacketDetailActivity.launch(CallTimeOrderDetailActitivy.this, bean.getPackageId());
                break;
        }
    }

    @Subscribe
    private void createViews(CallTime_CreatViewEvent eEvent) {
        setContentView(R.layout.activity_call_time_order_detail_actitivy);
        ButterKnife.bind(this);
        initSet();
        isCreateView = eEvent.isShow();
    }

    private void initSet() {
        hasLeftViewTitle(R.string.order_detail, 0);
    }


    @Override
    public String getOrderId() {
        return getIntent().getStringExtra("id");
    }

    @Override
    public RelativeLayout getNoNetRelativeLayout() {
        return NoNetRelativeLayout;
    }

    @Override
    public TextView getPackageNameTextView() {
        return packageNameTextView;
    }

    @Override
    public ImageView getPacketImageView() {
        return packetImageView;
    }

    @Override
    public TextView getExpiryDateTextView() {
        return expiryDateTextView;
    }

    @Override
    public TextView getPacketStatusTextView() {
        return packetStatusTextView;
    }

    @Override
    public TextView getPriceTextView() {
        return priceTextView;
    }

    @Override
    public TextView getOrderNumberTextView() {
        return orderNumberTextView;
    }

    @Override
    public TextView getOrderTimeTextView() {
        return orderTimeTextView;
    }

    @Override
    public TextView getPayWayTextView() {
        return payWayTextView;
    }

    @Override
    public boolean getIsCreateView() {
        return isCreateView;
    }

    @Override
    public void showToast(String msg) {
        CommonTools.showShortToast(this, msg);
    }
}
