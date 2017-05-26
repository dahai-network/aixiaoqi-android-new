package de.blinkt.openvpn.activities.Set.PresenterImpl;

import android.content.Intent;

import com.aixiaoqi.socket.EventBusUtil;
import com.umeng.analytics.MobclickAgent;

import cn.com.johnson.model.AppMode;
import de.blinkt.openvpn.activities.LoginMainActivity;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Set.ModelImpl.SetModelImpl;
import de.blinkt.openvpn.activities.Set.Presenter.SetPersenter;
import de.blinkt.openvpn.activities.Set.Presenter.UserFeedbackPersenter;
import de.blinkt.openvpn.activities.Set.View.SetView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.SharedUtils;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKEXITLOGIN;

/**
 * Created by Administrator on 2017/5/26 0026.
 */

public class SetPersenterImpl implements SetPersenter,NetModelBaseImpl.OnLoadFinishListener {

    SetView setView;
    SetModelImpl setModel;

    public SetPersenterImpl(SetView setView){
        this.setView=setView;
        setModel=new SetModelImpl(this);
    }
    @Override
    public void requsetExitLogin() {
        setModel.loadExitLogin();
    }

    @Override
    public void onDestory() {
        if(setView!=null)
        setView=null;
        AppMode.getInstance().isClickAddDevice=false;
        AppMode.getInstance().isClickPackage=false;
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
if(object.getStatus()==1){
    new Thread(new Runnable() {
        @Override
        public void run() {
            if (ICSOpenVPNApplication.the_sipengineReceive != null) {
                ICSOpenVPNApplication.the_sipengineReceive.DeRegisterSipAccount();
                ICSOpenVPNApplication.the_sipengineReceive.CoreTerminate();
                ICSOpenVPNApplication.the_sipengineReceive = null;
            }
        }
    }).start();
    exitOperate();
}
    }

    private void exitOperate() {
        //友盟方法统计
        MobclickAgent.onEvent(context, CLICKEXITLOGIN);
        SharedUtils sharedUtils = SharedUtils.getInstance();
        sharedUtils.delete(Constant.TOKEN);
        sharedUtils.delete(Constant.PHONE_NUMBER_LIST);
        sharedUtils.delete(Constant.PASSWORD);
        sharedUtils.writeBoolean(Constant.ISFIRSTIN, true);
        sharedUtils.delete(Constant.JPUSH_ALIAS);
        sharedUtils.delete(Constant.TEL);
        sharedUtils.delete(Constant.IMEI);
        sharedUtils.delete(Constant.BRACELETNAME);
        //友盟账号统计
        MobclickAgent.onProfileSignOff();
        EventBusUtil.cancelCallService();
        Intent intent = new Intent();
        intent.setAction(SportFragment.CLEARSPORTDATA);
        ICSOpenVPNApplication.uartService.disconnect();
        ICSOpenVPNApplication.getInstance().sendBroadcast(intent);

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {

    }

    @Override
    public void noNet() {

    }
}
