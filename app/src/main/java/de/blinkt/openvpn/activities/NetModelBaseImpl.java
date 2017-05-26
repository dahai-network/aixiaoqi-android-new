package de.blinkt.openvpn.activities;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Set.ModelImpl.UserFeedbackModelImpl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;

/**
 * Created by Administrator on 2017/5/26 0026.
 * 有网络请求的Model基类
 *
 */

public class NetModelBaseImpl implements InterfaceCallback{

    OnLoadFinishListener onLoadFinishListener;
    public NetModelBaseImpl(OnLoadFinishListener onLoadFinishListener){
        this.onLoadFinishListener=onLoadFinishListener;
    }



    protected   boolean hasWiFi(){
        if(!NetworkUtils.isNetworkAvailable(ICSOpenVPNApplication.getContext())){
            CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), ICSOpenVPNApplication.getContext().getString(R.string.no_wifi));
            return false;
        }
        return  true;
    }

    protected void createHttpRequest(int cmdType, String... params) {
        CreateHttpFactory.instanceHttp(this, cmdType, params);
    }
    protected void createHttpRequestNoCache(int cmdType, String... params) {
        if(hasWiFi()){
            CreateHttpFactory.instanceHttp(this, cmdType, params);
        }
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        onLoadFinishListener.rightLoad(cmdType,object);
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        if(onLoadFinishListener!=null){
            onLoadFinishListener.errorComplete(cmdType,errorMessage);
        }
    }

    @Override
    public void noNet() {
        onLoadFinishListener.noNet();
    }

    public interface  OnLoadFinishListener{
        void rightLoad(int cmdType, CommonHttp object);
        void  errorComplete(int cmdType, String errorMessage);
        void noNet();
    }
}
