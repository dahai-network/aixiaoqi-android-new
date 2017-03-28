package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.ImageEntity;

/**
 * Created by Administrator on 2016/9/20 0020.
 */

public class UploadHeaderHttp extends BaseHttp{
    InterfaceCallback call;
    private ImageEntity imageEntity;
    public ImageEntity getImageEntity(){
        return imageEntity;
    }
    public UploadHeaderHttp(InterfaceCallback interfaceCallback,int cmdType_,String...params){
        super(interfaceCallback,cmdType_,POST_IMAGE,HttpConfigUrl.POST_UPLOAD_HEADER,params);

    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("file",valueParams[0]);

    }

    @Override
    protected void parseObject(String response) {
        imageEntity = new Gson().fromJson(response, ImageEntity.class);
    }
}
