package de.blinkt.openvpn.http;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/26 0026.
 */
public class BaseHttp extends CommonHttp {
    protected  InterfaceCallback interfaceCallback;
    protected  int cmdType_;
    protected  boolean  isCreateHashMap=true;
    protected  String[] valueParams;
    public  BaseHttp(InterfaceCallback interfaceCallback,int cmdType_){
        this.interfaceCallback=interfaceCallback;
        this.cmdType_=cmdType_;
    }

    public  BaseHttp(InterfaceCallback interfaceCallback,int cmdType_,String...params){
        this.interfaceCallback=interfaceCallback;
        this.cmdType_=cmdType_;
        valueParams=params;
    }
    public  BaseHttp(){

    }
    @Override
    protected void BuildParams() throws Exception {
        if (params == null&&isCreateHashMap) {
            params = new HashMap<>();
        }
    }


    protected  void parseObject(String response){

    }
    @Override
    protected void parseResult(String response) {
        if(!TextUtils.isEmpty(response)){
            parseObject(response);
        }
        interfaceCallback.rightComplete(cmdType_, this);
    }

    @Override
    protected void errorResult(String s) {
        interfaceCallback.errorComplete(cmdType_,s);
    }

    @Override
    protected void noNet() {
        interfaceCallback.noNet();
    }
}
