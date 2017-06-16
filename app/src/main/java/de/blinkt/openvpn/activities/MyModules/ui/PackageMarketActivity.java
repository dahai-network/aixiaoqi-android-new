package de.blinkt.openvpn.activities.MyModules.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.MyModules.presenter.PackageMarketPresenter;
import de.blinkt.openvpn.activities.MyModules.view.PackageMarketView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.addHeaderAndFooterRecyclerView.WrapRecyclerView;

/**
 * 套餐超市
 */
public class PackageMarketActivity extends BaseActivity implements PackageMarketView{

    public static PackageMarketActivity activity;
    @BindView(R.id.marketRecyclerView)
    WrapRecyclerView marketRecyclerView;
    @BindView(R.id.retryTextView)
    TextView retryTextView;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.noDataTextView)
    TextView noDataTextView;
    @BindView(R.id.NodataRelativeLayout)
    RelativeLayout NodataRelativeLayout;
    String controlCall;
    TextView communicationTextView;
    TextView leftPriceTextView;
    TextView leftContentTextView;
    TextView leftExpiryDateTextView;
    RelativeLayout leftPacketRelativeLayout;
    TextView rightPriceTextView;
    TextView rightContentTextView;
    TextView rightExpiryDateTextView;
    RelativeLayout rightPacketRelativeLayout;
    RelativeLayout communicationRelativeLayout;

    public int count=200;

    private PackageMarketPresenter packageMarketPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("PackageMarketActivity", "onCreate: ");
        setContentView(R.layout.activity_package_market);
        ButterKnife.bind(this);
        controlCall = getIntent().getStringExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE);
        initSet();
        packageMarketPresenter=new PackageMarketPresenter(this);
        if (Constant.SHOW.equals(controlCall)) {
            addHeader();
            packageMarketPresenter.getPackgeData(1 + "",2 + "",1 + "");
        } else {

        }

        initData();
    }

    private void addHeader() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.inland_package, null);
        communicationTextView= (TextView)view.findViewById(R.id.communicationTextView);
        leftPriceTextView= (TextView)view.findViewById(R.id.leftPriceTextView);
        leftContentTextView= (TextView)view.findViewById(R.id.leftContentTextView);
        leftExpiryDateTextView= (TextView)view.findViewById(R.id.leftExpiryDateTextView);
        leftPacketRelativeLayout= (RelativeLayout)view.findViewById(R.id.leftPacketRelativeLayout);
        rightPriceTextView= (TextView)view.findViewById(R.id.rightPriceTextView);
        rightContentTextView= (TextView)view.findViewById(R.id.rightContentTextView);
        rightExpiryDateTextView= (TextView)view.findViewById(R.id.rightExpiryDateTextView);
        rightPacketRelativeLayout= (RelativeLayout)view.findViewById(R.id.rightPacketRelativeLayout);
        communicationRelativeLayout= (RelativeLayout)view.findViewById(R.id.communicationRelativeLayout);
        marketRecyclerView.addHeaderView(view);
    }

    private void initSet() {
        activity = this;
        hasLeftViewTitle(R.string.package_market, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        marketRecyclerView.setLayoutManager(linearLayoutManager);


    }

    //模拟数据
    private void initData() {
        //全部展示国家套餐，200个
        packageMarketPresenter.getPackageMarket(count+"");
    }

    @OnClick(R.id.retryTextView)
    public void onClick() {
        initData();
    }

    @Override
    public WrapRecyclerView getMarketRecyclerView() {
        return marketRecyclerView;
    }

    @Override
    public RelativeLayout getNoNetRelativeLayout() {
        return NodataRelativeLayout;
    }

    @Override
    public TextView getLeftPriceTextView() {
        return leftPriceTextView;
    }

    @Override
    public TextView getLeftContentTextView() {
        return leftContentTextView;
    }

    @Override
    public TextView getLeftExpiryDateTextView() {
        return leftExpiryDateTextView;
    }

    @Override
    public RelativeLayout getLeftPacketRelativeLayout() {
        return leftPacketRelativeLayout;
    }

    @Override
    public TextView getRightPriceTextView() {
        return rightPriceTextView;
    }

    @Override
    public TextView getRightContentTextView() {
        return rightContentTextView;
    }

    @Override
    public TextView getRightExpiryDateTextView() {
        return rightExpiryDateTextView;
    }

    @Override
    public RelativeLayout getRightPacketRelativeLayout() {
        return rightPacketRelativeLayout;
    }

    @Override
    public RelativeLayout getCommunicationRelativeLayout() {
        return communicationRelativeLayout;
    }

    @Override
    public void showToast(String msg) {

        CommonTools.showShortToast(PackageMarketActivity.this,msg);

    }
}
