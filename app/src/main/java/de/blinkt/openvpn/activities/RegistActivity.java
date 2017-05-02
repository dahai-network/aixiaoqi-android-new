package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import java.util.Set;
import cn.com.aixiaoqi.R;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.LoginHttp;
import de.blinkt.openvpn.http.RegistHttp;
import de.blinkt.openvpn.http.SecurityConfigHttp;
import de.blinkt.openvpn.http.SendMsgHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.ViewUtil;
import static de.blinkt.openvpn.constant.UmengContant.CLICKREGISTERBUTTON;
import static de.blinkt.openvpn.constant.UmengContant.CLICKREGISTERSENDCODE;
import static de.blinkt.openvpn.constant.UmengContant.REGISTERSHOWPASSWORD;

public class RegistActivity extends BaseNetActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, InterfaceCallback {

    private String TAG = "RegistActivity";
    private EditText phoneNumberEdit;
    private EditText verification_edit;
    private EditText passwordEdit;
    private Button sendBtn;
    private Button regist_btn;
    private TextView agreementTextView;
    private CheckBox hindPswCheckBox;
    private CheckBox allowCheckBox;
    private RelativeLayout registRelativeLayout;
    private boolean isOpenHind = true;
    private CountDownTimer timer;
    private InputMethodManager manager = null;
    private static final int MSG_SET_ALIAS = 1001;
    private TextView text_boottom_login;

    private TextView textview_1;
    private TextView textview_2;
    private TextView textview_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        init();
    }

    private void init() {
        initView();
        initEvent();
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    //初始化控件
    private void initView() {
        phoneNumberEdit = (EditText)
                findViewById(R.id.phoneNumberEdit);
        verification_edit = (EditText)
                findViewById(R.id.verification_edit);
        passwordEdit = (EditText)
                findViewById(R.id.passwordEdit);

        hindPswCheckBox = (CheckBox) findViewById(R.id.hindPswCheckBox);
        hindPswCheckBox.setOnClickListener(this);
        allowCheckBox = (CheckBox) findViewById(R.id.allowCheckBox);
        agreementTextView = (TextView) findViewById(R.id.agreementTextView);
        registRelativeLayout = (RelativeLayout) findViewById(R.id.registRelativeLayout);
        text_boottom_login = (TextView) findViewById(R.id.text_boottom_login);

        textview_1 = (TextView) findViewById(R.id.textview_1);
        textview_2 = (TextView) findViewById(R.id.textview_2);
        textview_3 = (TextView) findViewById(R.id.textview_3);

        agreementTextView.setOnClickListener(this);
        allowCheckBox.setOnCheckedChangeListener(this);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        text_boottom_login.setOnClickListener(this);
        regist_btn = (Button) findViewById(R.id.regist_btn);
        regist_btn.setOnClickListener(this);
        registRelativeLayout.setOnClickListener(this);


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

    /**
     * 初始化事件
     */
    private void initEvent() {
        setEditChangeLisener(phoneNumberEdit, R.id.phoneNumberEdit);
        setEditChangeLisener(verification_edit, R.id.verification_edit);
        setEditChangeLisener(passwordEdit, R.id.passwordEdit);
    }

    private void setEditChangeLisener(EditText editText, final int id) {

        new ExditTextWatcher(editText, id) {
            @Override
            public void textChanged(CharSequence s, int id) {
                switch (id) {
                    case R.id.phoneNumberEdit:
                        setViewVisibleOrGone(textview_1, s);
                        break;
                    case R.id.verification_edit:
                        setViewVisibleOrGone(textview_2, s);
                        break;
                    case R.id.passwordEdit:
                        setViewVisibleOrGone(textview_3, s);
                        break;
                }
            }
        };


    }

    public void setViewVisibleOrGone(TextView textview, CharSequence s) {
        int view_state = s.length() == 0 ? View.GONE : View.VISIBLE;
        textview.setVisibility(view_state);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sendBtn:
                final String phoneNum = phoneNumberEdit.getText().toString();
                if (CheckUtil.isMobileNO(phoneNum, RegistActivity.this)) {
                    //友盟方法统计
                    MobclickAgent.onEvent(this, CLICKREGISTERSENDCODE);


                    sendBtn.setEnabled(false);
                    sendBtn.setTextColor(ContextCompat.getColor(this, R.color.regist_send_sms_unenable));

                    createHttpRequest(HttpConfigUrl.COMTYPE_SEND_SMS, phoneNum, 1 + "");
                }
                break;
            case R.id.regist_btn:
                if (!TextUtils.isEmpty(verification_edit.getText())) {
                    if (CheckUtil.isMobileNO(phoneNumberEdit.getText().toString(), RegistActivity.this)) {
                        if (CheckUtil.isPassWordNo(passwordEdit.getText().toString(), RegistActivity.this)) {
                            //友盟方法统计
                            MobclickAgent.onEvent(this, CLICKREGISTERBUTTON);
                            regist_btn.setEnabled(false);
                            createHttpRequest(HttpConfigUrl.COMTYPE_REGIST, phoneNumberEdit.getText().toString(),
                                    passwordEdit.getText().toString(), verification_edit.getText().toString());
                        }
                    }
                } else {
                    CommonTools.showShortToast(this, getResources().getString(R.string.null_verification));
                }
                break;
            case R.id.hindPswCheckBox:
                //友盟方法统计
                MobclickAgent.onEvent(this, REGISTERSHOWPASSWORD);
                if (isOpenHind) {
                    isOpenHind = false;
                    //如果选中，显示密码
                    passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    isOpenHind = true;
                    //否则隐藏密码
                    passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            case R.id.agreementTextView:
                //进入H5协议页面。
                String url = SharedUtils.getInstance().readString(IntentPutKeyConstant.USER_AGREEMENT_URL);
                if (!TextUtils.isEmpty(url))
                    WebViewActivity.launch(RegistActivity.this, url, "用户许可及服务协议");
                break;
            case R.id.registRelativeLayout:
                ViewUtil.hideSoftKeyboard(this);
                break;
            case R.id.text_boottom_login:
                Intent intent = new Intent();
                intent.setClass(RegistActivity.this, LoginMainActivity.class);
                startActivity(intent);
                finish();

                break;

        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                default:

            }
        }
    };

    private TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {

            switch (code) {
                case 0:
                    SharedUtils.getInstance().writeString(Constant.JPUSH_ALIAS,
                            Constant.JPUSH_ALIAS_SUCCESS);
                    break;

                case 6002:

                    if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    }
                    break;
                default:

            }
        }

    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            regist_btn.setEnabled(true);
        } else {
            regist_btn.setEnabled(false);
        }
    }

    private void setAlias() {
        SharedUtils sharedUtils = SharedUtils.getInstance();
        String alias = "aixiaoqi" + sharedUtils.readString(Constant.USER_NAME);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {

        Log.d("aixiaoqi___", "rightComplete: " +cmdType);

        if (cmdType == HttpConfigUrl.COMTYPE_SEND_SMS) {

            SendMsgHttp entity = (SendMsgHttp) object;

            Log.d("aixiaoqi___", "rightComplete: " +entity.getData());

            if (entity.getStatus() == 1) {
                sendBtn.setEnabled(false);
                timer.start();
            } else {
                sendBtn.setEnabled(true);
                sendBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
                CommonTools.showShortToast(RegistActivity.this, entity.getMsg());
            }
        }
        if (cmdType == HttpConfigUrl.COMTYPE_REGIST) {

            RegistHttp entity = (RegistHttp) object;
            if (entity.getStatus() == 1) {
                CommonTools.showShortToast(RegistActivity.this, RegistActivity.this.getResources().getString(R.string.regist_success));
                sendBtn.setEnabled(true);
                showProgress(R.string.login_loading);
                createHttpRequest(HttpConfigUrl.COMTYPE_LOGIN, phoneNumberEdit.getText().toString(), passwordEdit.getText().toString());
            } else {
                regist_btn.setEnabled(true);
                CommonTools.showShortToast(RegistActivity.this, entity.getMsg());
            }
        }
        if (cmdType == HttpConfigUrl.COMTYPE_LOGIN) {
            LoginHttp loginHttp = (LoginHttp) object;
            if (loginHttp.getStatus() == 1) {
                SharedUtils sharedUtils = SharedUtils.getInstance();
                sharedUtils.writeString(Constant.USER_NAME, loginHttp.getLoginModel().getTel());
                sharedUtils.writeString(Constant.PASSWORD, passwordEdit.getText().toString());
                sharedUtils.writeString(Constant.TOKEN, loginHttp.getLoginModel().getToken());
                sharedUtils.writeString(Constant.NICK_NAME, loginHttp.getLoginModel().getNickName());
                sharedUtils.writeString(Constant.HEIGHT, loginHttp.getLoginModel().getHeight());
                sharedUtils.writeString(Constant.WEIGHT, loginHttp.getLoginModel().getWeight());
                sharedUtils.writeString(Constant.SOPRT_TARGET, loginHttp.getLoginModel().getMovingTarget());
                sharedUtils.writeString(Constant.BRITHDAY, DateUtils.getDateToString(Long.parseLong(loginHttp.getLoginModel().getBirthday()) * 1000).substring(0, 7).replace("-", "年"));
                sharedUtils.writeString(Constant.GENDER, loginHttp.getLoginModel().getSex());
                sharedUtils.writeString(Constant.NICK_NAME, loginHttp.getLoginModel().getNickName());
                //写入登陆天数，如果十五天没有登陆过则重新登录
                sharedUtils.writeLong(Constant.LOGIN_DATA, System.currentTimeMillis());
                SecurityConfigHttp securityConfigHttp = new SecurityConfigHttp(this, HttpConfigUrl.COMTYPE_SECURITY_CONFIG);
                new Thread(securityConfigHttp).start();
                if (!phoneNumberEdit.getText().toString().equals(sharedUtils.readString(Constant.TEL)) || !Constant.JPUSH_ALIAS_SUCCESS.equals(sharedUtils.readString(Constant.JPUSH_ALIAS))) {
                    setAlias();
                }
            } else {
                dismissProgress();
                CommonTools.showShortToast(this, loginHttp.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_SECURITY_CONFIG) {
            dismissProgress();
            SecurityConfigHttp securityConfigHttp = (SecurityConfigHttp) object;
            if (securityConfigHttp.getStatus() == 1) {
                SharedUtils sharedUtils = SharedUtils.getInstance();
                sharedUtils.writeString(Constant.ASTERISK_IP_IN, securityConfigHttp.getSecurityConfig().getIn().getAsteriskIp());
                sharedUtils.writeString(Constant.ASTERISK_PORT_IN, securityConfigHttp.getSecurityConfig().getIn().getAsteriskPort());
                sharedUtils.writeString(Constant.ASTERISK_IP_OUT, securityConfigHttp.getSecurityConfig().getOut().getAsteriskIp());
                sharedUtils.writeString(Constant.ASTERISK_PORT_OUT, securityConfigHttp.getSecurityConfig().getOut().getAsteriskPort());
                sharedUtils.writeString(Constant.PUBLIC_PASSWORD, securityConfigHttp.getSecurityConfig().getOut().getPublicPassword());
                startActivity(new Intent(RegistActivity.this, ProMainActivity.class));
                finish();
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        CommonTools.showShortToast(this, errorMessage);
        d("errorComplete: "+errorMessage);
        regist_btn.setEnabled(true);
    }

    @Override
    public void noNet() {
        CommonTools.showShortToast(this, "请检查您的网络设置！");
        regist_btn.setEnabled(true);
    }

}
