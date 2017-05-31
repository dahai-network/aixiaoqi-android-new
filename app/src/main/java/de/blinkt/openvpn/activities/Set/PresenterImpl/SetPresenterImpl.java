package de.blinkt.openvpn.activities.Set.PresenterImpl;

import android.content.Intent;

import com.aixiaoqi.socket.EventBusUtil;
import com.umeng.analytics.MobclickAgent;

import cn.com.johnson.model.AppMode;
import de.blinkt.openvpn.activities.CommomModel.JPush.ModelImpl.JPushSetAliaModelImpl;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.Set.ModelImpl.SetModelImpl;
import de.blinkt.openvpn.activities.Set.Presenter.SetPresenter;
import de.blinkt.openvpn.activities.Set.View.SetView;
import de.blinkt.openvpn.activities.UserInfo.ui.LoginMainActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKEXITLOGIN;

/**
 * Created by Administrator on 2017/5/26 0026.
 */

public class SetPresenterImpl extends NetPresenterBaseImpl implements SetPresenter,NetModelBaseImpl.OnLoadFinishListener {

    SetView setView;
    SetModelImpl setModel;
    JPushSetAliaModelImpl jPushSetAliaModel;
    public SetPresenterImpl(SetView setView){
        this.setView=setView;
        setModel=new SetModelImpl(this);
        jPushSetAliaModel=new JPushSetAliaModelImpl();
    }
    @Override
    public void requsetExitLogin() {
        if(NetworkUtils.hasWiFi())
        setModel.loadExitLogin();
    }

    @Override
    public void onDestory() {
        if(setView!=null)
            setView=null;
        AppMode.getInstance().isClickAddDevice=false;
        AppMode.getInstance().isClickPackage=false;
        jPushSetAliaModel.destoryHandler();
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if(object.getStatus()==1){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ICSOpenVPNApplication.the_sipengineReceive != null) {
                        ICSOpenVPNApplication.the_sipengineReceive.DeRegisterSipAccount();
//                        ICSOpenVPNApplication.the_sipengineReceive.CoreTerminate();
                        ICSOpenVPNApplication.the_sipengineReceive = null;
                    }
                }
            }).start();
            exitOperate();
        }else{
            setView.showToast(object.getMsg());
        }
    }

    private void exitOperate() {
        //友盟方法统计
        jPushSetAliaModel.setJPushAlia("");
        MobclickAgent.onEvent(context, CLICKEXITLOGIN);
        SharedUtils sharedUtils = SharedUtils.getInstance();
        sharedUtils.delete(Constant.TOKEN);
        sharedUtils.delete(Constant.PASSWORD);
        sharedUtils.writeBoolean(Constant.ISFIRSTIN, true);
        sharedUtils.delete(Constant.JPUSH_ALIAS);
        sharedUtils.delete(Constant.LOGIN_DATA);
        sharedUtils.delete(Constant.TEL);
        //友盟账号统计
        MobclickAgent.onProfileSignOff();
        EventBusUtil.cancelCallService();
        Intent intent = new Intent();
        intent.setAction(SportFragment.CLEARSPORTDATA);
        ICSOpenVPNApplication.uartService.disconnect();
        ICSOpenVPNApplication.getInstance().sendBroadcast(intent);
        setView.finishView();
        setView.startActivity(LoginMainActivity.class);
    }


}
