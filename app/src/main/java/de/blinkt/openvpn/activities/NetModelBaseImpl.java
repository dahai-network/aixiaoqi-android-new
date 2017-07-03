package de.blinkt.openvpn.activities;

import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;

import static de.blinkt.openvpn.util.NetworkUtils.hasWiFi;

/**
 * Created by Administrator on 2017/5/26 0026.
 * 有网络请求的Model基类
 *
 */

public class NetModelBaseImpl implements InterfaceCallback{

   protected OnLoadFinishListener onLoadFinishListener;
    public NetModelBaseImpl(OnLoadFinishListener onLoadFinishListener){
        this.onLoadFinishListener=onLoadFinishListener;
    }
    public NetModelBaseImpl(){
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
        if(onLoadFinishListener!=null)
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
        if(onLoadFinishListener!=null)
        onLoadFinishListener.noNet();
    }

    public interface  OnLoadFinishListener{
        void rightLoad(int cmdType, CommonHttp object);
        void  errorComplete(int cmdType, String errorMessage);
        void noNet();
    }
}
