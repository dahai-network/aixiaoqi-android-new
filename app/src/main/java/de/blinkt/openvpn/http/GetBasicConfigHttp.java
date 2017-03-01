package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.BasicConfigEntity;

/**
 * Created by Administrator on 2016/11/10 0010.
 */
public class GetBasicConfigHttp extends BaseHttp {
    InterfaceCallback interfaceCallback;

    public BasicConfigEntity getBasicConfigEntity() {
        return basicConfigEntity;
    }

    private BasicConfigEntity basicConfigEntity;

    public GetBasicConfigHttp(InterfaceCallback interfaceCallback,int cmdType_){
        super(interfaceCallback,cmdType_);
        isCreateHashMap=false;
    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        sendMethod_=GET_MODE;
        slaverDomain_= HttpConfigUrl.GET_BASIC_CONFIG;
    }

    @Override
    protected void parseObject(String response) {
        basicConfigEntity=new Gson().fromJson(response, BasicConfigEntity.class);
    }



}
