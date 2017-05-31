package de.blinkt.openvpn.activities.UserInfo.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.UserInfo.Presenter.GetBackPswPresenter;
import de.blinkt.openvpn.activities.UserInfo.PresenterImpl.GetBackPswPresenterImpl;
import de.blinkt.openvpn.activities.UserInfo.View.GetBackPswView;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ForgetPswHttp;
import de.blinkt.openvpn.http.SendMsgHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.ViewUtil;

import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKBUTTON;
import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKSENDCODE;

public class GetBackPswActivity extends BaseActivity implements GetBackPswView {

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
    @BindView(R.id.sure_btn)
    Button sureBtn;
    @BindView(R.id.getPswLinearLayout)
    LinearLayout getPswLinearLayout;
    private Unbinder unbinder;

    GetBackPswPresenter getBackPswPresenter;

    @Override
    public void showToast(String toastContent) {
        CommonTools.showShortToast(this,toastContent);
    }

    @Override
    public void showToast(int toastContentId) {
        CommonTools.showShortToast(this,getString(toastContentId));
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
    public String getPhoneNumberText() {
        return phoneNumberEdit.getText().toString().trim();
    }

    @Override
    public Button getSendCodeBtn() {
        return sendBtn;
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void sendCodeBackground(int backgroundColorId) {
        sendBtn.setTextColor(ContextCompat.getColor(ICSOpenVPNApplication.getContext(), backgroundColorId));
    }

    @Override
    public void sendCodeIsClick(boolean isClick) {
        sendBtn.setEnabled(isClick);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_back_psw);
        unbinder=  ButterKnife.bind(this);
        initEvent();
        hasLeftViewTitle(R.string.find_password, 0);
        getBackPswPresenter=new GetBackPswPresenterImpl(this);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
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

    @OnClick({R.id.sendBtn, R.id.sure_btn, R.id.getPswLinearLayout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                getBackPswPresenter.getVerificationCode();
                break;
            case R.id.sure_btn:
                getBackPswPresenter.findPsw();
                break;
            case R.id.getPswLinearLayout:
                ViewUtil.hideSoftKeyboard(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(getBackPswPresenter!=null){
            getBackPswPresenter.onDestory();
            getBackPswPresenter=null;
        }
        if(unbinder!=null){
            unbinder.unbind();
        }
    }
}
