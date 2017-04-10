package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.EBuzOrderListAdapter;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetOrderListHttp;

import de.blinkt.openvpn.model.EBizOrderListEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.FullyRecylerView;

/**
 * Created by Administrator on 2016/11/25 0025.
 */
public class EBuzOrderListActivity extends BaseNetActivity implements RecyclerBaseAdapter.OnItemClickListener{
    @BindView(R.id.rv_e_biz_order_list)
    FullyRecylerView rvEBizOrderList;
    @BindView(R.id.add_new_phone_number)
    Button addNewPhoneNumber;
    @BindView(R.id.add_phone_number)
    LinearLayout addPhoneNumber;
    private List<EBizOrderListEntity.OrderInfo> list = new ArrayList<>();
    EBuzOrderListAdapter eBuzOrderListAdapter;
    private Set<String> phoneSet;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_biz_order_list);
        ButterKnife.bind(this);
        hasLeftViewTitle(R.string.e_biz_order_list, 0);
        phoneSet= SharedUtils.getInstance().getStringSet(Constant.PHONE_NUMBER_LIST);
        if(phoneSet==null){
            phoneSet=new LinkedHashSet<>();
            phoneSet.add(SharedUtils.getInstance().readString(Constant.USER_NAME));
        }
        addView();
        initData();
        httpOrder(SharedUtils.getInstance().readString(Constant.USER_NAME));
    }

    private void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvEBizOrderList.setLayoutManager(layoutManager);

        eBuzOrderListAdapter = new EBuzOrderListAdapter(this, list);
        eBuzOrderListAdapter.setOnItemClickListener(this);
        rvEBizOrderList.setAdapter(eBuzOrderListAdapter);
    }

    @Override
    public void onItemClick(View view, Object data, boolean b) {
        toActivity(new Intent(this, EBuizOrderDetailActivity.class).putExtra(IntentPutKeyConstant.E_BUIZ_ORDER,((EBizOrderListEntity.OrderInfo)data)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return ;
        }
        switch (requestCode){
            case Constant.ADD_PHONE_NUMBER:
                String number=   data.getStringExtra(IntentPutKeyConstant.ADD_PHONE_NUMBER);
                phoneSet.add(number);
                SharedUtils.getInstance().writeSetString(Constant.PHONE_NUMBER_LIST,phoneSet);
                addView();
                break;
        }
    }

    private void addView() {
        addPhoneNumber.removeAllViews();
        for(final String strPhone:phoneSet) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_add_phone_number, null);
            TextView textView = (TextView) view.findViewById(R.id.phone_number);
            textView.setText(strPhone);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    httpOrder(strPhone);
                }
            });
            addPhoneNumber.addView(view);
        }
    }

    private void httpOrder(String number){
        createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_LIST,number);
    }

    @OnClick({R.id.add_new_phone_number})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.add_new_phone_number:
                startActivityForResult(new Intent(this,AddNumberActivity.class),Constant.ADD_PHONE_NUMBER);
                break;
        }
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        super.rightComplete(cmdType, object);
        if(cmdType== HttpConfigUrl.COMTYPE_ORDER_LIST){

            GetOrderListHttp getOrderListHttp=      (GetOrderListHttp)object;
            if(getOrderListHttp.getStatus()==1){
                eBuzOrderListAdapter.addAll(getOrderListHttp.geteBizOrderListEntity().getList());
            }else{
                CommonTools.showShortToast(this,getOrderListHttp.getMsg());
            }
        }
    }
}
