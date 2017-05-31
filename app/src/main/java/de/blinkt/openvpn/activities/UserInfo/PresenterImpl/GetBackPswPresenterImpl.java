package de.blinkt.openvpn.activities.UserInfo.PresenterImpl;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CommomModel.GetVerificationCode.Model.GetCodeModel;
import de.blinkt.openvpn.activities.CommomModel.GetVerificationCode.ModelImpl.GetCodeModelImpl;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.Model.GetBackPswModel;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.GetBackPswModelImpl;
import de.blinkt.openvpn.activities.UserInfo.Presenter.GetBackPswPresenter;
import de.blinkt.openvpn.activities.UserInfo.View.GetBackPswView;
import de.blinkt.openvpn.activities.UserInfo.ui.GetBackPswActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;

import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKBUTTON;
import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKSENDCODE;
import static de.blinkt.openvpn.util.NetworkUtils.hasWiFi;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class GetBackPswPresenterImpl extends NetPresenterBaseImpl implements GetBackPswPresenter {
    GetBackPswModel getBackPswModel;
    GetCodeModelImpl getCode;
    GetBackPswView getBackPswView;

    public  GetBackPswPresenterImpl(GetBackPswView getBackPswView){
        this.getBackPswView=getBackPswView;
        getBackPswModel=new GetBackPswModelImpl(this);
        getCode=new GetCodeModelImpl(getBackPswView.getSendCodeBtn(),this);
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if(cmdType==HttpConfigUrl.COMTYPE_FORGET_PSW){

            if (object.getStatus() == 1) {
                getBackPswView.finishView();
            }
            if(!TextUtils.isEmpty(object.getMsg()))
                getBackPswView.showToast(object.getMsg());

        }else if(cmdType==HttpConfigUrl.COMTYPE_SEND_SMS){
            if (object.getStatus() != 1) {
                sendCodeIsClick(true, R.color.black);
                getBackPswView.showToast(object.getMsg());

            }
        }
    }

    private void sendCodeIsClick(boolean isClick, int black) {
        getBackPswView.sendCodeIsClick(isClick);
        getBackPswView.sendCodeBackground(black);
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        sendCodeIsClick(true, R.color.black);
        super.errorComplete(cmdType, errorMessage);
    }

    @Override
    public void findPsw() {
        String vertificationStr = getBackPswView.getVerificationCode();
        if (!TextUtils.isEmpty(vertificationStr)) {
            String phoneStr = getBackPswView.getPhoneNumberText();
            String pswStr = getBackPswView.getPswText();
            if (CheckUtil.isMobileNO(phoneStr, ICSOpenVPNApplication.getContext())) {
                if (CheckUtil.isPassWordNo(pswStr, ICSOpenVPNApplication.getContext())) {
                    //友盟方法统计
                    MobclickAgent.onEvent(ICSOpenVPNApplication.getContext(), CLICKFINDBACKBUTTON);
                    getBackPswModel.requestGetBackPsw(getBackPswView.getPhoneNumberText(),getBackPswView.getPswText(),getBackPswView.getVerificationCode());
                }
            }
        } else {
            getBackPswView.showToast(R.string.null_verification);
        }


    }


    @Override
    public void getVerificationCode() {

        if (CheckUtil.isMobileNO(getBackPswView.getPhoneNumberText(), ICSOpenVPNApplication.getContext())) {
            //友盟方法统计
            MobclickAgent.onEvent(ICSOpenVPNApplication.getContext(), CLICKFINDBACKSENDCODE);

            if(hasWiFi()){
                sendCodeIsClick(false, R.color.regist_send_sms_unenable);
                getCode.startTimer();
                getCode.getCode(getBackPswView.getPhoneNumberText(),"2");
            }
        }

    }

    @Override
    public void onDestory() {

        if(getBackPswView!=null){
            getBackPswView=null;
        }
        if(getCode!=null){
            getCode.endTime();
            getCode=null;
        }
        if(getBackPswModel!=null){
            getBackPswModel=null;
        }


    }
}
