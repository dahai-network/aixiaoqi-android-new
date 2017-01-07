package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import java.util.HashMap;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.ImageEntity;

/**
 * Created by Administrator on 2016/9/20 0020.
 */

public class UploadHeaderHttp extends BaseHttp{
    InterfaceCallback call;
    private String url;
    private ImageEntity imageEntity;
    public ImageEntity getImageEntity(){
        return imageEntity;
    }
    public UploadHeaderHttp(InterfaceCallback interfaceCallback,int cmdType_,String url){
        super(interfaceCallback,cmdType_);
        this.url=url;
    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        sendMethod_=POST_IMAGE;
        slaverDomain_= HttpConfigUrl.POST_UPLOAD_HEADER;
        params.put("file",url);

    }

    @Override
    protected void parseObject(String response) {
        imageEntity = new Gson().fromJson(response, ImageEntity.class);
    }
}
