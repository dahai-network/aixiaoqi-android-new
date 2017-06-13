package de.blinkt.openvpn.activities.UserInfo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import de.blinkt.openvpn.activities.Device.ui.ProMainActivity;
import de.blinkt.openvpn.activities.UserInfo.PresenterImpl.LoginPresenterImpl;
import de.blinkt.openvpn.activities.UserInfo.View.LoginView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.ViewUtil;

import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKPASSWORD;
import static de.blinkt.openvpn.constant.UmengContant.CLICKLOGINREGISTER;


/**
 * Created by wzj on 2016/6/3.
 */
public class LoginMainActivity extends BaseActivity implements LoginView, View.OnClickListener {
    @BindView(R.id.usernameEdit)
    EditText usernameEdit;
    @BindView(R.id.pwdEdit)
    EditText pwdEdit;
    @BindView(R.id.login_btn)
    Button loginBtn;
    @BindView(R.id.registTextView)
    TextView registTextView;
    @BindView(R.id.forgetPswTextView)
    TextView forgetPswTextView;
    @BindView(R.id.loginLinearLayout)
    LinearLayout loginLinearLayout;
    @BindView(R.id.tip_login_user)
    TextView tipLoginUser;
    @BindView(R.id.tip_login_password)
    TextView tipLoginPassword;
    LoginPresenterImpl loginPresenter;
    Unbinder unbinder;
    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }


    @Override
    public void showProgress(int id) {
        super.showProgress(id);
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    @Override
    public void showToast(String toastContent) {
        CommonTools.showShortToast(this, toastContent);
    }

    @Override
    public void showToast(int toastContentId) {
        CommonTools.showShortToast(this, getString(toastContentId));
    }

    @Override
    public String getUserPassword() {
        return pwdEdit.getText().toString().trim();
    }

    @Override
    public String getUserPhone() {
        return usernameEdit.getText().toString().trim();
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void toProMainActivity() {
        toActivity(ProMainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        unbinder=ButterKnife.bind(this);
        init();
        loginPresenter=new LoginPresenterImpl(this);
        basicConfigHttp();
    }

    private void basicConfigHttp() {
        loginPresenter.requestBasicConfig();
    }

    SharedUtils sharedUtils;

    private void init() {
        sharedUtils = SharedUtils.getInstance();
        createViewInit();
        setLoginData();
        String otherDeviceLogin = getIntent().getStringExtra(IntentPutKeyConstant.OTHER_DEVICE_LOGIN);
        if (!TextUtils.isEmpty(otherDeviceLogin) && !sharedUtils.readBoolean(Constant.ISFIRSTIN, true)) {
            CommonTools.showShortToast(this, otherDeviceLogin);
        }

    }

    /**
     * 设置帐号密码记录过的数据
     */
    private void setLoginData() {
        long currentDate = System.currentTimeMillis();
        long loginDate = sharedUtils.readLong("loginDate");
        long DATEOF15 = 1296000000;
        //登录天数距离小于15天的时候可以复制帐号密码
        if (currentDate - loginDate < DATEOF15) {
            usernameEdit.setText(sharedUtils.readString("userName"));
            if (!TextUtils.isEmpty(usernameEdit.getText().toString()))
                usernameEdit.setSelection(usernameEdit.getText().length());
            pwdEdit.setText(sharedUtils.readString("password"));
        }
    }

    //绑定view
    private void createViewInit() {
        setTextChangeLisener();
        if (TextUtils.isEmpty(usernameEdit.getText().toString()) || TextUtils.isEmpty(pwdEdit.getText().toString())) {
            setLoginBtnAttr(false, R.drawable.circle_gray_ret);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loginPresenter!=null){
            loginPresenter.onDestory();
            loginPresenter=null;
        }
        if(unbinder!=null){
            unbinder.unbind();
        }


    }

    private void setTextChangeLisener() {

        setEditTextListener(pwdEdit, R.id.pwdEdit);
        setEditTextListener(usernameEdit, R.id.usernameEdit);
    }

    private void setEditTextListener(EditText editText, int id) {
        new ExditTextWatcher(editText, id) {
            @Override
            public void textChanged(CharSequence s, int id) {
                switch (id) {
                    case R.id.pwdEdit:
                        tipLoginPassword.setVisibility(s.length() != 0?View.VISIBLE:View.GONE);
                        pwdEdit.setGravity(s.length() != 0?Gravity.CENTER:Gravity.LEFT);
                        setLoginBtnAttr(s.length() != 0&&usernameEdit.getText().toString().length() != 0, s.length() != 0&&usernameEdit.getText().toString().length() != 0?R.drawable.green_btn_click:R.drawable.circle_gray_ret);
                        break;
                    case R.id.usernameEdit:
                        tipLoginUser.setVisibility(s.length() != 0?View.VISIBLE:View.GONE);
                        usernameEdit.setGravity(s.length() != 0?Gravity.CENTER:Gravity.LEFT);
                        setLoginBtnAttr(s.length() != 0&&pwdEdit.getText().toString().length() != 0, s.length() != 0&&pwdEdit.getText().toString().length() != 0?R.drawable.green_btn_click:R.drawable.circle_gray_ret);
                        break;
                }
            }
        };

    }


    private void setLoginBtnAttr(boolean isClick, int backgroundColor) {
        loginBtn.setEnabled(isClick);
        loginBtn.setBackgroundResource(backgroundColor);

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        return true;
    }


    @OnClick({R.id.registTextView, R.id.login_btn, R.id.forgetPswTextView,R.id.loginLinearLayout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registTextView:
                //友盟方法统计
                MobclickAgent.onEvent(this, CLICKLOGINREGISTER);
                toActivity(RegistActivity.class);
                break;
            case R.id.login_btn:
                loginPresenter.requestLogin();
                break;
            case R.id.forgetPswTextView:
                //友盟方法统计
                MobclickAgent.onEvent(this, CLICKFINDBACKPASSWORD);
                //跳转忘记密码
                toActivity(GetBackPswActivity.class);
                break;
            case R.id.loginLinearLayout:
                ViewUtil.hideSoftKeyboard(this);
                break;
        }
    }


}

