package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetPakcetHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.PacketMarketHttp;
import de.blinkt.openvpn.model.PacketEntity;
import de.blinkt.openvpn.model.PacketMarketEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.pinyin.CharacterParser;


public class PackageMarketActivity extends BaseNetActivity   {

    public static PackageMarketActivity activity;
    @BindView(R.id.marketRecyclerView)
    RecyclerView marketRecyclerView;
    @BindView(R.id.retryTextView)
    TextView retryTextView;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.noDataTextView)
    TextView noDataTextView;
    @BindView(R.id.NodataRelativeLayout)
    RelativeLayout NodataRelativeLayout;
    @BindView(R.id.communicationTextView)
    TextView communicationTextView;
    @BindView(R.id.leftPriceTextView)
    TextView leftPriceTextView;
    @BindView(R.id.leftContentTextView)
    TextView leftContentTextView;
    @BindView(R.id.leftExpiryDateTextView)
    TextView leftExpiryDateTextView;
    @BindView(R.id.leftPacketRelativeLayout)
    RelativeLayout leftPacketRelativeLayout;
    @BindView(R.id.rightPriceTextView)
    TextView rightPriceTextView;
    @BindView(R.id.rightContentTextView)
    TextView rightContentTextView;
    @BindView(R.id.rightExpiryDateTextView)
    TextView rightExpiryDateTextView;
    @BindView(R.id.rightPacketRelativeLayout)
    RelativeLayout rightPacketRelativeLayout;
    @BindView(R.id.communicationRelativeLayout)
    RelativeLayout communicationRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_market);
        ButterKnife.bind(this);
        String controlCall= getIntent().getStringExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE);
        if(Constant.SHOW.equals(controlCall)){
            communicationRelativeLayout.setVisibility(View.VISIBLE);
        }else{
            communicationRelativeLayout.setVisibility(View.GONE);
        }
        initSet();

    }

    private void initSet() {
        activity = this;
        hasLeftViewTitle(R.string.package_market, 0);
        marketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();

    }

    //模拟数据
    private void initData() {
        //全部展示国家套餐，200个
        createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_MARKET, 200 + "");
        createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_GET, 1 + "", 2 + "", 1 + "");

    }

    CharacterParser characterParser;

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if(cmdType==HttpConfigUrl.COMTYPE_PACKET_MARKET) {
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
        }
        else   if (cmdType == HttpConfigUrl.COMTYPE_PACKET_GET) {
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
