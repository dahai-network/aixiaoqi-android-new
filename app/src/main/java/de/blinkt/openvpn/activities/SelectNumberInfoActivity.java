package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.SelectNumberInfoHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class SelectNumberInfoActivity extends BaseNetActivity implements DialogInterfaceTypeBase {
    @BindView(R.id.info_name)
    TextView infoName;
    @BindView(R.id.info_id_card)
    TextView infoIdCard;
    @BindView(R.id.info_address)
    TextView infoAddress;
    @BindView(R.id.info_select_number)
    TextView infoSelectNumber;
    @BindView(R.id.addUpTextView)
    TextView addUpTextView;
    @BindView(R.id.sureTextView)
    TextView sureTextView;
    private String province;
    private String city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_number_info);
        ButterKnife.bind(this);
        hasLeftViewTitle(R.string.select_number_info,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(data==null){
            return;
        }
        switch (requestCode){
            case Constant.SAVE_REALNAME:
                infoName.setText(data.getStringExtra(IntentPutKeyConstant.REALNAME));
                break;
            case Constant.SAVE_ID_CARD_NUMBER:
                infoIdCard.setText(data.getStringExtra(IntentPutKeyConstant.ID_CARD_NUMBER));
                break;
            case Constant.SAVE_ADDRESS:
                province=data.getStringExtra(IntentPutKeyConstant.PROVINCE);
                city=data.getStringExtra(IntentPutKeyConstant.CITY);
                infoAddress.setText(province+city);
                break;
            case Constant.SAVE_SELECT_NUMBER:
                  fee=data.getStringExtra(IntentPutKeyConstant.SELECT_NUMBER_FEE);
                String number=data.getStringExtra(IntentPutKeyConstant.SELECT_NUMBER);
                addUpTextView.setText("￥"+fee);
                infoSelectNumber.setText(number);
                break;
        }
    }
    String  fee;
    @OnClick({R.id.info_name,R.id.info_id_card,R.id.info_address,R.id.info_select_number,R.id.sureTextView})
    public  void onClick(View v){
        switch (v.getId()){
            case R.id.info_name:
                Intent realNameIntent=    new Intent(this,EditNameActivity.class);
                realNameIntent.putExtra(IntentPutKeyConstant.REAL_NAME_EDIT,infoName.getText().toString());
                realNameIntent.putExtra(IntentPutKeyConstant.EDIT_TYPE,IntentPutKeyConstant.EDIT_USER_NAME);
                startActivityForResult(realNameIntent,Constant.SAVE_REALNAME);
                break;
            case R.id.info_id_card:
                Intent idCardIntent=    new Intent(this,EditNameActivity.class);
                idCardIntent.putExtra(IntentPutKeyConstant.REAL_NAME_EDIT,infoIdCard.getText().toString());
                idCardIntent.putExtra(IntentPutKeyConstant.EDIT_TYPE,IntentPutKeyConstant.EDIT_ID_CARD);
                startActivityForResult(idCardIntent,Constant.SAVE_ID_CARD_NUMBER);
                break;
            case R.id.info_address:
                startActivityForResult(new Intent(this,ChinaAddressActivity.class).putExtra(IntentPutKeyConstant.PROVINCE,province).putExtra(IntentPutKeyConstant.CITY,city),Constant.SAVE_ADDRESS);
                break;
            case R.id.info_select_number:
                if(TextUtils.isEmpty(province)&&TextUtils.isEmpty(city)){
                    CommonTools.showShortToast(this,getString(R.string.address_is_null));
                    return;
                }
                startActivityForResult(new Intent(this,SelectNumberActivity.class).putExtra(IntentPutKeyConstant.PROVINCE,province).putExtra(IntentPutKeyConstant.CITY,city),Constant.SAVE_SELECT_NUMBER);
                break;
            case R.id.sureTextView:
                 name=  infoName.getText().toString();
                 idCard=  infoIdCard.getText().toString();
                 number=  infoSelectNumber.getText().toString();
                if(TextUtils.isEmpty(name)){
                    CommonTools.showShortToast(this,getString(R.string.real_name_is_null));
                    return;
                }
                if(TextUtils.isEmpty(idCard)){
                    CommonTools.showShortToast(this,getString(R.string.id_card_is_null));
                    return;
                }
                if(TextUtils.isEmpty(number)){
                    CommonTools.showShortToast(this,getString(R.string.phone_number_is_null));
                    return;
                }
                showDialog();break;
        }
    }
    String name;
    String idCard;
    String number;
    private void showDialog() {
       new DialogBalance(this, this, R.layout.dialog_commit_info, 100);

    }
    private void httpSelectInfo(String paymentType){
        createHttpRequest(  HttpConfigUrl.COMTYPE_ADD_SELECT_NUMBER_INFO,
                getIntent().getStringExtra(IntentPutKeyConstant.E_BUIZ_ORDER_ID),
                name ,
                idCard ,
                number ,
                paymentType );


    }
public static final int COMMIT_USER_INFO_SUCCEED=100;
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if(object.getStatus()==1){
            Intent intent=new Intent();
            intent.putExtra(IntentPutKeyConstant.REALNAME,infoName.getText().toString());
            intent.putExtra(IntentPutKeyConstant.ID_CARD_NUMBER,infoIdCard.getText().toString());
            intent.putExtra(IntentPutKeyConstant.SELECT_NUMBER,infoSelectNumber.getText().toString());
            intent.putExtra(IntentPutKeyConstant.PROVINCE,province);
            intent.putExtra(IntentPutKeyConstant.CITY,city);
            setResult(COMMIT_USER_INFO_SUCCEED,intent);
            finish();
        }else {
            CommonTools.showShortToast(this,object.getMsg());
        }
    }

    @Override
    public void dialogText(int type, String text) {
            httpSelectInfo(1+"");
    }
}
