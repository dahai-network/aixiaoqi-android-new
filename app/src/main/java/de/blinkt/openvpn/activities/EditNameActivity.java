package de.blinkt.openvpn.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.ModifyPersonInfoHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class EditNameActivity extends BaseNetActivity {
    EditText etNickName;
    String name;
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        name=getIntent().getStringExtra(IntentPutKeyConstant.REAL_NAME_EDIT);
        type=getIntent().getIntExtra(IntentPutKeyConstant.EDIT_TYPE,-1);
        initTitle();
        initView();
        initData();

    }

    private void initTitle(){
        int id=-1;
        if(type==IntentPutKeyConstant.EDIT_NICKNAME){
            id=R.string.name_title;
        }else if(type==IntentPutKeyConstant.EDIT_USER_NAME){
            id=R.string.real_name_title;
        }else if(type==IntentPutKeyConstant.EDIT_ID_CARD){
            id=R.string.id_card_title;
        }
        hasAllViewTitle(id,R.string.save,R.string.cancel,false);


    }
    private void initView(){
        etNickName=(EditText)findViewById(R.id.et_nick_name);
        int id=-1;
        if(type==IntentPutKeyConstant.EDIT_NICKNAME){
            id=R.string.nick_name;
        }else if(type==IntentPutKeyConstant.EDIT_USER_NAME){
            id=R.string.info_name;
        }else if(type==IntentPutKeyConstant.EDIT_ID_CARD){
            id=R.string.info_id_crad;
        }
        etNickName.setHint(getResources().getString(id));
    }
    private void initData(){
        if(!TextUtils.isEmpty(name)){
            etNickName.setText(name);
            etNickName.setSelection(etNickName.getText().length());
        }
    }



    @Override
    protected void onClickRightView() {

        String realName;
        if(!TextUtils.isEmpty(etNickName.getText().toString())){
            realName=etNickName.getText().toString();
        }else{
            if(type==IntentPutKeyConstant.EDIT_NICKNAME)
                CommonTools.showShortToast(this,getString(R.string.name_is_null));
            else if(type==IntentPutKeyConstant.EDIT_USER_NAME){
                CommonTools.showShortToast(this,getString(R.string.real_name_is_null));
            }else if(type==IntentPutKeyConstant.EDIT_ID_CARD){
                CommonTools.showShortToast(this,getString(R.string.id_card_is_null));
            }
            return;
        }
        if(type==IntentPutKeyConstant.EDIT_NICKNAME){
            ModifyPersonInfoHttp modifyPersonInfoHttp=new ModifyPersonInfoHttp(this);
            modifyPersonInfoHttp.setNickName(realName, HttpConfigUrl.COMTYPE_POST_MODIFY_NICK);
            new Thread(modifyPersonInfoHttp).start();
        }   else if(type==IntentPutKeyConstant.EDIT_USER_NAME){
            Intent realNameIntent=   new Intent();
            realNameIntent.putExtra(IntentPutKeyConstant.REALNAME,realName);
            setResult(Constant.SAVE_REALNAME,realNameIntent);
            finish();
        }else if(type==IntentPutKeyConstant.EDIT_ID_CARD){
            Intent idCardIntent=   new Intent();
            idCardIntent.putExtra(IntentPutKeyConstant.ID_CARD_NUMBER,realName);
            setResult(Constant.SAVE_ID_CARD_NUMBER,idCardIntent);
            finish();
        }
    }


    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if(cmdType==HttpConfigUrl.COMTYPE_POST_MODIFY_NICK){
            ModifyPersonInfoHttp modifyPersonInfoHttp=(ModifyPersonInfoHttp)object;
            if(modifyPersonInfoHttp.getStatus()==1){
                SharedUtils sharedUtils=SharedUtils.getInstance();
                sharedUtils.writeString(Constant.NICK_NAME,etNickName.getText().toString());
                finish();
            }else{
                CommonTools.showShortToast(this,modifyPersonInfoHttp.getMsg());
            }
        }

    }

}
