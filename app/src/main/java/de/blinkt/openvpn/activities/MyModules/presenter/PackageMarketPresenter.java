package de.blinkt.openvpn.activities.MyModules.presenter;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.PackageMarketAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.MyModules.model.PackageMarketModel;
import de.blinkt.openvpn.activities.MyModules.modelImple.PackageMarketImpl;
import de.blinkt.openvpn.activities.MyModules.ui.PackageMarketActivity;
import de.blinkt.openvpn.activities.MyModules.view.PackageMarketView;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetPakcetHttp;
import de.blinkt.openvpn.http.PacketMarketHttp;
import de.blinkt.openvpn.model.PacketEntity;
import de.blinkt.openvpn.model.PacketMarketEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.pinyin.CharacterParser;
import de.blinkt.openvpn.views.addHeaderAndFooterRecyclerView.WrapRecyclerView;

/**
 * Created by kim
 * on 2017/6/7.
 * 套餐超市
 */
public class PackageMarketPresenter extends BaseNetActivity {


    private PackageMarketModel packageMarketModel;
    private PackageMarketView packageMarketView;
    WrapRecyclerView marketRecyclerView;
    RelativeLayout NoNetRelativeLayout,communicationRelativeLayout,leftPacketRelativeLayout,rightPacketRelativeLayout;
    TextView leftPriceTextView,leftContentTextView,leftExpiryDateTextView,rightPriceTextView,rightContentTextView,rightExpiryDateTextView;
    private PackageMarketActivity instance;

    public PackageMarketPresenter(PackageMarketView packageMarketView) {
        this.packageMarketView = packageMarketView;
        packageMarketModel = new PackageMarketImpl();
        instance=PackageMarketActivity.activity;
        initControlView();
    }

    /**
     * 初始化控件
     */
    private void initControlView() {

        marketRecyclerView = packageMarketView.getMarketRecyclerView();
        NoNetRelativeLayout = packageMarketView.getNoNetRelativeLayout();
        leftPacketRelativeLayout = packageMarketView.getLeftPacketRelativeLayout();
        leftContentTextView = packageMarketView.getLeftContentTextView();
        leftExpiryDateTextView = packageMarketView.getLeftExpiryDateTextView();
        leftPriceTextView = packageMarketView.getLeftPriceTextView();
        /*--------------------------------------------------------------*/
        rightPriceTextView = packageMarketView.getRightPriceTextView();
        rightContentTextView = packageMarketView.getRightContentTextView();
        rightExpiryDateTextView = packageMarketView.getRightExpiryDateTextView();
        rightPacketRelativeLayout = packageMarketView.getRightPacketRelativeLayout();

        communicationRelativeLayout = packageMarketView.getCommunicationRelativeLayout();
    }

    /**
     * 获取套餐数据
     * @param packageNumber
     * @param pageSize
     * @param category
     */
    public void getPackgeData(String packageNumber,String pageSize,String category)
    {
        packageMarketModel.getPackageData(packageNumber,pageSize,category,this);

    }

    /**
     * 获取套餐超市
     * @param pageSize
     */
    public void getPackageMarket(String pageSize)
    {
        packageMarketModel.getPackageMarketData(pageSize,this);

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
                    marketRecyclerView.setAdapter(new PackageMarketAdapter(data, instance));
                }

            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_PACKET_GET) {
            GetPakcetHttp http = (GetPakcetHttp) object;
            PacketEntity bean = http.getPacketEntity();
            if (bean != null) {
                List<PacketEntity.ListBean> list = bean.getList();
                if (list.size() == 0) return;
                final PacketEntity.ListBean listBean = list.get(0);
                leftPriceTextView.setText(listBean.getPrice() + instance.getString(R.string.yuan));
                leftContentTextView.setText(listBean.getPackageName());
                leftExpiryDateTextView.setText(instance.getString(R.string.expiry_date) + listBean.getExpireDays() + instance.getString(R.string.day));
                leftPacketRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("PackageMarketPresenter", "onClick:--- ");
                        CallTimePacketDetailActivity.launch(instance, listBean.getPackageId());
                    }
                });
                if (list.size() == 1) return;
                final PacketEntity.ListBean list2Bean = list.get(1);
                rightPriceTextView.setText(list2Bean.getPrice() + instance.getString(R.string.yuan));
                rightContentTextView.setText(list2Bean.getPackageName());
                rightExpiryDateTextView.setText(instance.getString(R.string.expiry_date) + list2Bean.getExpireDays() + instance.getString(R.string.day));
                rightPacketRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CallTimePacketDetailActivity.launch(instance, list2Bean.getPackageId());
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

}
