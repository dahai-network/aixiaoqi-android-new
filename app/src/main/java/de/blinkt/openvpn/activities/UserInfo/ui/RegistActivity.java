package de.blinkt.openvpn.activities.UserInfo.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.UserInfo.PresenterImpl.RegisterPresenterImpl;
import de.blinkt.openvpn.activities.UserInfo.View.RegisterView;
import de.blinkt.openvpn.activities.WebViewActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.ViewUtil;

public class RegistActivity extends BaseActivity implements RegisterView {

    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.phoneNumberEdit)
    EditText phoneNumberEdit;
    @BindView(R.id.textview_1)
    TextView textview1;
    @BindView(R.id.verification_edit)
    EditText verificationEdit;
    @BindView(R.id.textview_2)
    TextView textview2;
    @BindView(R.id.sendBtn)
    Button sendBtn;
    @BindView(R.id.passwordEdit)
    EditText passwordEdit;
    @BindView(R.id.textview_3)
    TextView textview3;
    @BindView(R.id.regist_btn)
    Button registBtn;
    @BindView(R.id.allowCheckBox)
    CheckBox allowCheckBox;
    @BindView(R.id.agreementTextView)
    TextView agreementTextView;
    @BindView(R.id.text_boottom_login)
    TextView textBoottomLogin;
    @BindView(R.id.registRelativeLayout)
    RelativeLayout registRelativeLayout;
    Unbinder unbinder;
    RegisterPresenterImpl registerPresenter;
    @Override
    public void showToast(String toastContent) {
        CommonTools.showShortToast(this,toastContent);
    }


    @Override
    public void showToast(int toastContentId) {
        CommonTools.showShortToast(this,getString(toastContentId));
    }

    @Override
    public Button getSendCodeBtn() {
        return sendBtn;
    }

    @Override
    public void sendCodeIsClick(boolean isClick) {
        sendBtn.setEnabled(isClick);
    }

    @Override
    public void sendCodeBackground(int backgroundColorId) {
        sendBtn.setTextColor(ContextCompat.getColor(ICSOpenVPNApplication.getContext(), backgroundColorId));
    }

    @Override
    public void showProgress(int progressContent) {
          super.showProgress(progressContent);
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public String getPhoneNumberText() {
        return phoneNumberEdit.getText().toString().trim();
    }

    @Override
    public String getPswText() {
        return passwordEdit.getText().toString().trim();
    }


    @Override
    public String getVerificationCode() {
        return verificationEdit.getText().toString().trim();
    }

    @Override
    public void registerIsClick(boolean isClick) {
        registBtn.setEnabled(isClick);
    }

    @Override
    public boolean isAgree() {
        return allowCheckBox.isChecked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        unbinder=ButterKnife.bind(this);
        registerPresenter=new RegisterPresenterImpl(this);
        initEvent();
    }




    /**
     * 初始化事件
     */
    private void initEvent() {
        allowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                allowCheckBox.setChecked(isChecked);
            }
        });

        setEditChangeLisener(phoneNumberEdit, R.id.phoneNumberEdit);
        setEditChangeLisener(verificationEdit, R.id.verification_edit);
        setEditChangeLisener(passwordEdit, R.id.passwordEdit);
    }

    private void setEditChangeLisener(EditText editText, final int id) {

        new ExditTextWatcher(editText, id) {
            @Override
            public void textChanged(CharSequence s, int id) {
                switch (id) {
                    case R.id.phoneNumberEdit:
                        setViewVisibleOrGone(textview1, s);
                        break;
                    case R.id.verification_edit:
                        setViewVisibleOrGone(textview2, s);
                        break;
                    case R.id.passwordEdit:
                        setViewVisibleOrGone(textview3, s);
                        break;
                }
            }
        };

    }

    public void setViewVisibleOrGone(TextView textview, CharSequence s) {
        textview.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
    }

    @OnClick({R.id.sendBtn, R.id.regist_btn, R.id.agreementTextView,R.id.registRelativeLayout,R.id.text_boottom_login})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sendBtn:
                registerPresenter.getVerificationCode();
                break;
            case R.id.regist_btn:
                registerPresenter.requestRegister();
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
                toActivity(LoginMainActivity.class);
                finish();
                break;

        }
    }

    @Override
    public void toProMainActivity() {
        ICSOpenVPNApplication.getInstance().finishActivity(LoginMainActivity.class);
        toActivity(ProMainActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(registerPresenter!=null){
            registerPresenter.onDestory();
            registerPresenter=null;
        }
        if(unbinder!=null){
            unbinder.unbind();
        }
    }


}
