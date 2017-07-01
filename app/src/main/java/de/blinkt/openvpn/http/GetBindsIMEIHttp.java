package de.blinkt.openvpn.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import cn.com.johnson.model.GetBindsIMEIHttpEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by KIM
 * on 2017/6/29.
 */

public class GetBindsIMEIHttp extends BaseHttp {

    public GetBindsIMEIHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
        //DeviceBracelet/GetBindsIMEI
        super(interfaceCallback, cmdType_,GET_MODE, HttpConfigUrl.GET_BINDS_IMEI,params);
    }

    private GetBindsIMEIHttpEntity getBindsIMEIHttpEntity;

    public GetBindsIMEIHttpEntity getBindsIMEIHttpEntity() {

        return getBindsIMEIHttpEntity;
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        String valueParam = valueParams[0];
        Type type = new TypeToken<ArrayList<String>>()
        {}.getType();
        ArrayList<String> jsonObjects = new Gson().fromJson(valueParam, type);

        for (int i=0;i<jsonObjects.size();i++){
            params.put("IMEIs["+i+"]",jsonObjects.get(i));
        }
    }

    @Override
    protected void parseObject(String response) {

        Log.d("GetBindsIMEIHttp", "parseObject: "+response);
        getBindsIMEIHttpEntity = new Gson().fromJson(response, GetBindsIMEIHttpEntity.class);
        Log.d("GetBindsIMEIHttp", "parseObject: "+getBindsIMEIHttpEntity.getList().size());


    }
}
