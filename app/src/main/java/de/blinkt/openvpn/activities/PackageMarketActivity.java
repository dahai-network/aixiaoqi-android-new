package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.PackageMarketAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetPakcetHttp;
import de.blinkt.openvpn.http.PacketMarketHttp;
import de.blinkt.openvpn.model.PacketEntity;
import de.blinkt.openvpn.model.PacketMarketEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.pinyin.CharacterParser;
import de.blinkt.openvpn.views.addHeaderAndFooterRecyclerView.WrapRecyclerView;


public class PackageMarketActivity extends BaseNetActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_market);
        ButterKnife.bind(this);
        controlCall = getIntent().getStringExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE);
        initSet();
        if (Constant.SHOW.equals(controlCall)) {
            addHeader();
            createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_GET, 1 + "", 2 + "", 1 + "");
        } else {

        }
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
        initData();

    }

    //模拟数据
    private void initData() {
        //全部展示国家套餐，200个
        createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_MARKET, 200 + "");
    }

    CharacterParser characterParser;

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_PACKET_MARKET) {
            PacketMarketHttp http = (PacketMarketHttp) object;
            characterParser = CharacterParser.getInstance();
            List<List<PacketMarketEntity>> data = http.getPacketMarketEntityList();

            if (data != null) {
                if (data.size() != 0) {
                    marketRecyclerView.setVisibility(View.VISIBLE);
                    NoNetRelativeLayout.setVisibility(View.GONE);
                    marketRecyclerView.setAdapter(new PackageMarketAdapter(data, this));
                }

            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_PACKET_GET) {
            GetPakcetHttp http = (GetPakcetHttp) object;
            PacketEntity bean = http.getPacketEntity();
            if (bean != null) {
                List<PacketEntity.ListBean> list = bean.getList();
                if (list.size() == 0) return;
                final PacketEntity.ListBean listBean = list.get(0);
                leftPriceTextView.setText(listBean.getPrice() + getString(R.string.yuan));
                leftContentTextView.setText(listBean.getPackageName());
                leftExpiryDateTextView.setText(getString(R.string.expiry_date) + listBean.getExpireDays() + getString(R.string.day));
                leftPacketRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallTimePacketDetailActivity.launch(PackageMarketActivity.this, listBean.getPackageId());
                    }
                });
                if (list.size() == 1) return;
                final PacketEntity.ListBean list2Bean = list.get(1);
                rightPriceTextView.setText(list2Bean.getPrice() + getString(R.string.yuan));
                rightContentTextView.setText(list2Bean.getPackageName());
                rightExpiryDateTextView.setText(getString(R.string.expiry_date) + list2Bean.getExpireDays() + getString(R.string.day));
                rightPacketRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallTimePacketDetailActivity.launch(PackageMarketActivity.this, list2Bean.getPackageId());
                    }
                });
            } else {
                communicationRelativeLayout.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        CommonTools.showShortToast(this, errorMessage);
    }

    @Override
    public void noNet() {
        marketRecyclerView.setVisibility(View.GONE);
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.retryTextView)
    public void onClick() {
        initData();
    }
}
