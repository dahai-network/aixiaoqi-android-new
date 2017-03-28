package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.http.AddNumberHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.SendMsgHttp;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class AddNumberActivity extends BaseNetActivity {
    @BindView(R.id.phoneNumberEdit)
    EditText phoneNumberEdit;
    @BindView(R.id.verification_edit)
    EditText verificationEdit;
    @BindView(R.id.sendBtn)
    Button sendBtn;
    @BindView(R.id.add_btn)
    Button addBtn;
    private CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_number);
        ButterKnife.bind(this);
        hasLeftViewTitle(R.string.add_number, 0);
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendBtn.setText(millisUntilFinished / 1000 + "秒后可重发");
            }

            @Override
            public void onFinish() {
                sendBtn.setEnabled(true);
                sendBtn.setText("发送验证码");
            }
        };
    }
    @OnClick ({R.id.sendBtn,R.id.add_btn})
    public void  OnClick(View v){
        switch (v.getId()) {
            case R.id.sendBtn:
                phoneNum = phoneNumberEdit.getText().toString();
                if (CheckUtil.isMobileNO(phoneNum, AddNumberActivity.this)) {
                    sendBtn.setEnabled(false);
                    sendBtn.setTextColor(ContextCompat.getColor(this, R.color.regist_send_sms_unenable));
                    SendMsgHttp http = new SendMsgHttp(this, HttpConfigUrl.COMTYPE_SEND_SMS, phoneNum, 3);
                    new Thread(http).start();
                }
                break;
            case R.id.add_btn:

                httpAddNumber();

                break;
        }
    }
    String phoneNum;
    private void httpAddNumber(){
        if(!CheckUtil.isMobileNO(phoneNum,this)){
            return ;
        }
        if(TextUtils.isEmpty(verificationEdit.getText().toString())){
            CommonTools.showShortToast(this,getString(R.string.input_verification));
            return ;
        }
        CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_ADD_NUMBER,phoneNum,verificationEdit.getText().toString());
    }
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_SEND_SMS) {
            SendMsgHttp entity = (SendMsgHttp) object;
            if (entity.getStatus() == 1) {
                sendBtn.setEnabled(false);
                timer.start();
            } else {
                sendBtn.setEnabled(true);
                sendBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                CommonTools.showShortToast(this, entity.getMsg());
            }
        }else if(cmdType==HttpConfigUrl.COMTYPE_ADD_NUMBER){
            AddNumberHttp addNumberHttp = (AddNumberHttp) object;
            if(addNumberHttp.getStatus()==1){
                Intent intent =new Intent();
                intent.putExtra(IntentPutKeyConstant.ADD_PHONE_NUMBER,phoneNum);
                setResult(Constant.ADD_PHONE_NUMBER,intent);
                finish();
            }else{
                CommonTools.showShortToast(this,addNumberHttp.getMsg());
            }
        }
    }


}
