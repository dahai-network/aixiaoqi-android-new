package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.BasicConfigEntity;

/**
 * Created by Administrator on 2016/11/10 0010.
 */
public class GetBasicConfigHttp extends BaseHttp {


    public BasicConfigEntity getBasicConfigEntity() {
        return basicConfigEntity;
    }
    private BasicConfigEntity basicConfigEntity;

    public GetBasicConfigHttp(InterfaceCallback interfaceCallback,int cmdType_){
        super(interfaceCallback,cmdType_,false,GET_MODE,HttpConfigUrl.GET_BASIC_CONFIG);

    }


    @Override
    protected void parseObject(String response) {
        basicConfigEntity=new Gson().fromJson(response, BasicConfigEntity.class);
    }



}
