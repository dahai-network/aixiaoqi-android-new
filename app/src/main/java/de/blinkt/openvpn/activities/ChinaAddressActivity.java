package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetLocationListHttp;
import de.blinkt.openvpn.model.LocationEntity;
import de.blinkt.openvpn.views.dialog.DialogAddress;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class ChinaAddressActivity extends BaseNetActivity implements DialogInterfaceTypeBase {
    List<String> provinceList = new ArrayList<>();
    List<String> cityList = new ArrayList<>();
    @BindView(R.id.provice_tv)
    TextView proviceTv;
    @BindView(R.id.city_tv)
    TextView cityTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasAllViewTitle(R.string.info_address, R.string.save, R.string.cancel, false);
        setContentView(R.layout.activity_china_address);
        ButterKnife.bind(this);
        initData();
        httpAddress();
    }
    private void initData(){
        String pronvic= getIntent().getStringExtra(IntentPutKeyConstant.PROVINCE);
        String city= getIntent().getStringExtra(IntentPutKeyConstant.CITY);
        if(!TextUtils.isEmpty(pronvic)&&!TextUtils.isEmpty(city)){
            proviceTv.setText(pronvic);
            cityTv.setText(city);
        }
    }
    private void httpAddress() {
        createHttpRequest(HttpConfigUrl.COMTYPE_GET_LOCATION_LIST);
    }
    @OnClick({R.id.provice_tv,R.id.city_tv})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.provice_tv:
                if(provinceList.size()==0){
                    return;
                }
                showAddressDialog(PROVICE,provinceList);
                break;
            case R.id.city_tv:
                if(cityList.size()==0){
                    return;
                }
                showAddressDialog(CITY,cityList);
                break;

        }
    }

    @Override
    protected void onClickRightView() {
        Intent intent =new Intent();
        intent.putExtra(IntentPutKeyConstant.PROVINCE,proviceTv.getText().toString());
        intent.putExtra(IntentPutKeyConstant.CITY,cityTv.getText().toString());
        setResult(Constant.SAVE_ADDRESS,intent);
        finish();
    }

    List<LocationEntity> list;
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        super.rightComplete(cmdType, object);
        if (cmdType == HttpConfigUrl.COMTYPE_GET_LOCATION_LIST) {
            GetLocationListHttp getLocationListHttp = (GetLocationListHttp) object;
            list = getLocationListHttp.getLocationEntityList();
            if (list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    provinceList.add(list.get(i).getProvince());
                    if(TextUtils.isEmpty(proviceTv.getText().toString())){
                        proviceTv.setText(list.get(0).getProvince());
                        cityTv.setText(list.get(0).getCitys().get(0));

                    }
                }
                getCityList(proviceTv.getText().toString());
                showAddressDialog(PROVICE,provinceList);
            }else{
                httpAddress();
            }
        }
    }
    private static final int PROVICE=0;
    private static final int CITY=1;
    private  void showAddressDialog(int type, List<String> list){
        DialogAddress dialogAddress=  new DialogAddress(this,this,R.layout.picker_address_layout,type,list);
        if(type==PROVICE){
            dialogAddress.setDefaultValue(proviceTv.getText().toString());
        }else if(type==CITY){
            dialogAddress.setDefaultValue(cityTv.getText().toString());
        }
    }

    @Override
    public void dialogText(int type, String text) {
        if(type==PROVICE){
            proviceTv.setText(text);
            getCityList(text);
            showAddressDialog(CITY,cityList);
        }else if(type==CITY){
            cityTv.setText(text);
        }
    }

    private void getCityList(String text) {
        cityList.clear();
        for(int i=0;i<list.size();i++){
            if(text.equals(list.get(i).getProvince())){
                cityTv.setText(list.get(i).getCitys().get(0));
                cityList.addAll(list.get(i).getCitys());
                break;
            }

        }
    }

    @Override
    public void noNet() {
        super.noNet();
        httpAddress();
    }
}
