package de.blinkt.openvpn.activities.UserInfo.PresenterImpl;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CommomModel.JPush.ModelImpl.JPushSetAliaModelImpl;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.BasicConfigModelImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.BlackListModelImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.LoginModelImpl;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.SecurityConfigImpl;
import de.blinkt.openvpn.activities.UserInfo.Presenter.LoginPresenter;
import de.blinkt.openvpn.activities.UserInfo.View.LoginView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class LoginPresenterImpl extends NetPresenterBaseImpl implements LoginPresenter{
    LoginView loginView;
    BasicConfigModelImpl basicConfigModel;
    LoginModelImpl loginModel;
    BlackListModelImpl blackListModel;
    SecurityConfigImpl securityConfig;
    JPushSetAliaModelImpl jPushSetAliaModel;
    public  LoginPresenterImpl(LoginView loginView){
        this.loginView=loginView;
        basicConfigModel=new BasicConfigModelImpl() ;
        loginModel=new LoginModelImpl(this);
        blackListModel=new BlackListModelImpl(this);
        securityConfig=new SecurityConfigImpl(this);
        jPushSetAliaModel=new JPushSetAliaModelImpl();

    }

    @Override
    public void requestBlackList() {
        blackListModel.requestBlackList();
    }

    @Override
    public void requestLogin() {
        if(NetworkUtils.hasWiFi()){
            if (CheckUtil.isPassWordNo(loginView.getUserPassword(), ICSOpenVPNApplication.getContext())) {
                loginView.showProgress(R.string.login_loading);
                loginModel.requestLogin(loginView.getUserPhone(), loginView.getUserPassword());

            }
        }
    }

    @Override
    public void requestSecurityConfig() {
        securityConfig.requestSecurityConfig();
    }

    @Override
    public void requestBasicConfig() {
        basicConfigModel.requestBasicConfig();
    }

    @Override
    public void onDestory() {
        if(basicConfigModel!=null){
            basicConfigModel=null;
        }
        if(loginModel!=null){
            loginModel=null;
        }
        if(blackListModel!=null){
            blackListModel=null;
        }
        if(securityConfig!=null){
            securityConfig=null;
        }
        if(jPushSetAliaModel!=null){
            jPushSetAliaModel=null;
        }
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_LOGIN) {
            if (object.getStatus() == 1) {
                requestSecurityConfig();
                if (!loginView.getUserPhone().equals(SharedUtils.getInstance().readString(Constant.TEL)) || !Constant.JPUSH_ALIAS_SUCCESS.equals(SharedUtils.getInstance().readString(Constant.JPUSH_ALIAS))) {
                    jPushSetAliaModel.setJPushAlia("aixiaoqi" + SharedUtils.getInstance().readString(Constant.TOKEN));
                }
            }else{
                loginView.showToast(object.getMsg());
                loginView.dismissProgress();
            }

        }else if (cmdType == HttpConfigUrl.COMTYPE_SECURITY_CONFIG){
            if(object.getStatus()==1){
                requestBlackList();
            }else{
                loginView.dismissProgress();
            }

        }else if(cmdType==HttpConfigUrl.COMTYPE_BLACK_LIST_GET){
            loginView.dismissProgress();
            if(object.getStatus()==1){
                loginView.toProMainActivity();
                loginView.finishView();
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        loginView.dismissProgress();
    }

    @Override
    public void noNet() {
        loginView.dismissProgress();
    }
}
