package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.EBuizOrderDetailAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetOrderDetailHttp;
import de.blinkt.openvpn.model.EBizOrderDetailEntity;
import de.blinkt.openvpn.model.EBizOrderListEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.FullyRecylerView;

/**
 * Created by Administrator on 2016/11/29 0029.
 */
public class EBuizOrderDetailActivity extends BaseNetActivity {
    @BindView(R.id.order_number)
    TextView orderNumber;
    @BindView(R.id.parchase_number)
    TextView parchaseNumber;
    @BindView(R.id.rv_order_detail)
    FullyRecylerView rvOrderDetail;
    @BindView(R.id.select_btn)
    Button selecctBtn;
    private List<EBizOrderDetailEntity.NumberInfo> list = new ArrayList<>();
    EBuizOrderDetailAdapter eBuizOrderDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasLeftViewTitle(R.string.e_biz_order_detail, 0);
        setContentView(R.layout.activity_e_buiz_order_detail);
        ButterKnife.bind(this);
        initData();

    }
    private String quantity;
    private void initData() {
        EBizOrderListEntity.OrderInfo orderInfo = (EBizOrderListEntity.OrderInfo) getIntent().getSerializableExtra(IntentPutKeyConstant.E_BUIZ_ORDER);
        orderNumber.setText(orderInfo.getOrderByZCNum());
        quantity=orderInfo.getQuantity();
        parchaseNumber.setText(quantity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOrderDetail.setLayoutManager(layoutManager);
        eBuizOrderDetailAdapter = new EBuizOrderDetailAdapter(this, list);
        rvOrderDetail.setAdapter(eBuizOrderDetailAdapter);
        httpOrderDatail(orderInfo.getOrderByZCID());
    }
    @OnClick({R.id.select_btn})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.select_btn:
                startActivityForResult(new Intent(this,SelectNumberInfoActivity.class).putExtra(IntentPutKeyConstant.E_BUIZ_ORDER_ID,((EBizOrderListEntity.OrderInfo) getIntent().getSerializableExtra(IntentPutKeyConstant.E_BUIZ_ORDER)).getOrderByZCID())
                        ,SelectNumberInfoActivity.COMMIT_USER_INFO_SUCCEED);
                break;
        }
    }
    private void httpOrderDatail(String orderID) {
        GetOrderDetailHttp getOrderDetailHttp = new GetOrderDetailHttp(this, HttpConfigUrl.COMTYPE_ORDER_DETAIL, orderID);
        new Thread(getOrderDetailHttp).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        switch (requestCode){
            case SelectNumberInfoActivity.COMMIT_USER_INFO_SUCCEED:
                EBizOrderDetailEntity.NumberInfo numberInfo=new  EBizOrderDetailEntity().new NumberInfo();
                numberInfo.setProvinceName(data.getStringExtra(IntentPutKeyConstant.PROVINCE));
                numberInfo.setCityName(data.getStringExtra(IntentPutKeyConstant.CITY));
                numberInfo.setIdentityNumber(data.getStringExtra(IntentPutKeyConstant.ID_CARD_NUMBER));
                numberInfo.setMobileNumber(data.getStringExtra(IntentPutKeyConstant.SELECT_NUMBER));
                numberInfo.setName(data.getStringExtra(IntentPutKeyConstant.REALNAME));
                eBuizOrderDetailAdapter.add(numberInfo);
                break;
        }
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        super.rightComplete(cmdType, object);
        if (cmdType == HttpConfigUrl.COMTYPE_ORDER_DETAIL) {
            GetOrderDetailHttp getOrderDetailHttp = (GetOrderDetailHttp) object;
            if (getOrderDetailHttp.getStatus() == 1) {
                List<EBizOrderDetailEntity.NumberInfo> list=     getOrderDetailHttp.geteBizOrderDetailEntity().getSelectionedNumberList();
                if(list.size()!=0)
                    eBuizOrderDetailAdapter.addAll(list);
                if(list.size()>=Integer.parseInt(quantity)){
                    selecctBtn.setVisibility(View.GONE);
                }else{
                    selecctBtn.setVisibility(View.VISIBLE);
                }
            } else {
                CommonTools.showShortToast(this, getOrderDetailHttp.getMsg());
            }
        }
    }
}
