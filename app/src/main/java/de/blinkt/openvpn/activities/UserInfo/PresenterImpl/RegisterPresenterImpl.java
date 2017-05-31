package de.blinkt.openvpn.activities.UserInfo.PresenterImpl;

import android.text.TextUtils;
import com.umeng.analytics.MobclickAgent;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CommomModel.GetVerificationCode.ModelImpl.GetCodeModelImpl;
import de.blinkt.openvpn.activities.CommomModel.JPush.ModelImpl.JPushSetAliaModelImpl;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.BlackListModelImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.LoginModelImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.RegisterModelImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.SecurityConfigImpl;
import de.blinkt.openvpn.activities.UserInfo.Presenter.RegisterPresenter;
import de.blinkt.openvpn.activities.UserInfo.View.RegisterView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKSENDCODE;
import static de.blinkt.openvpn.util.NetworkUtils.hasWiFi;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class RegisterPresenterImpl  extends NetPresenterBaseImpl implements RegisterPresenter {
    RegisterView registerView;
    GetCodeModelImpl getCodeModel;
    SecurityConfigImpl securityConfig;
    LoginModelImpl loginModel;
    RegisterModelImpl registerModel;
    JPushSetAliaModelImpl jPushSetAliaModel;
    BlackListModelImpl blackListModel;
    public  RegisterPresenterImpl(RegisterView registerView){
        this.registerView=registerView;
        getCodeModel=new GetCodeModelImpl(registerView.getSendCodeBtn(),this);
        securityConfig=new SecurityConfigImpl(this);
        loginModel=new LoginModelImpl(this);
        registerModel=new RegisterModelImpl(this);
        jPushSetAliaModel=new JPushSetAliaModelImpl();
        blackListModel=new BlackListModelImpl(this);
    }

    private void sendCodeIsClick(boolean isClick, int black) {
        registerView.sendCodeIsClick(isClick);
        registerView.sendCodeBackground(black);
    }
    @Override
    public void getVerificationCode() {
        if (CheckUtil.isMobileNO(registerView.getPhoneNumberText(), ICSOpenVPNApplication.getContext())) {
            //友盟方法统计

            if(hasWiFi()){
                MobclickAgent.onEvent(ICSOpenVPNApplication.getContext(), CLICKFINDBACKSENDCODE);
                sendCodeIsClick(false, R.color.regist_send_sms_unenable);
                getCodeModel.startTimer();
                getCodeModel.getCode(registerView.getPhoneNumberText(),"1");
            }
        }
    }

    @Override
    public void requestSecurityConfig() {
        securityConfig.requestSecurityConfig();
    }

    @Override
    public void requestLogin() {
        loginModel.requestLogin(registerView.getPhoneNumberText(),registerView.getPswText());
    }

    @Override
    public void requestBlackList() {
        blackListModel.requestBlackList();
    }

    @Override
    public void requestRegister() {
        if(!registerView.isAgree()){
            registerView.showToast(R.string.un_readed);
            return;
        }

        if(hasWiFi()){
            if (!TextUtils.isEmpty(registerView.getVerificationCode())) {
                if (CheckUtil.isMobileNO(registerView.getPhoneNumberText(), ICSOpenVPNApplication.getContext())) {
                    if (CheckUtil.isPassWordNo(registerView.getPswText(), ICSOpenVPNApplication.getContext())) {
                        //友盟方法统计
                        registerView.registerIsClick(false);
                        registerView.showProgress(R.string.user_registing);
                        registerModel.requestRegister(registerView.getPhoneNumberText(),registerView.getPswText(),registerView.getVerificationCode());
                    }
                }
            } else {
                registerView.showToast(R.string.null_verification);
            }
        }
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if(cmdType== HttpConfigUrl.COMTYPE_SEND_SMS){
            if (object.getStatus() == 1010) {
                getCodeModel.endTime();
                getCodeModel.finishTimer();
                registerView.showToast(object.getMsg());
            }else if(object.getStatus()!=1) {
                sendCodeIsClick(true, R.color.black);
                registerView.showToast(object.getMsg());
            }
        }else if(cmdType== HttpConfigUrl.COMTYPE_REGIST){
            if (object.getStatus() == 1) {
                registerView.showToast(R.string.regist_success);
                registerView.showProgress(R.string.login_loading);
                requestLogin();
            } else {
                registerView.dismissProgress();
                registerView.registerIsClick(true);
                registerView.showToast(object.getMsg());
            }
        }else    if (cmdType == HttpConfigUrl.COMTYPE_LOGIN) {
            if (object.getStatus() == 1) {
                requestSecurityConfig();
                if (!registerView.getPhoneNumberText().equals(SharedUtils.getInstance().readString(Constant.TEL)) || !Constant.JPUSH_ALIAS_SUCCESS.equals(SharedUtils.getInstance().readString(Constant.JPUSH_ALIAS))) {
                    jPushSetAliaModel.setJPushAlia("aixiaoqi" + SharedUtils.getInstance().readString(Constant.USER_NAME));
                }
            }else{
                registerView.showToast(object.getMsg());
            }

        } else if (cmdType == HttpConfigUrl.COMTYPE_SECURITY_CONFIG){
            if(object.getStatus()==1){
                requestBlackList();
            }

        }else if(cmdType==HttpConfigUrl.COMTYPE_BLACK_LIST_GET){
            registerView.dismissProgress();
            if(object.getStatus()==1){
                registerView.toProMainActivity();
                registerView.finishView();
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        registerView.dismissProgress();
        if(cmdType== HttpConfigUrl.COMTYPE_REGIST){
            registerView.registerIsClick(true);
        }else if(cmdType== HttpConfigUrl.COMTYPE_SEND_SMS){
            sendCodeIsClick(true, R.color.black);
        }
    }

    @Override
    public void noNet() {
        registerView.dismissProgress();
    }

    @Override
    public void onDestory() {
        if(registerView!=null){
            registerView=null;
        }
        if(getCodeModel!=null){
            getCodeModel.endTime();
            getCodeModel=null;
        }
        if(securityConfig!=null){
            securityConfig=null;
        }
        if(loginModel!=null){
            loginModel=null;
        }
        if(registerModel!=null){
            registerModel=null;
        }

    }
}
