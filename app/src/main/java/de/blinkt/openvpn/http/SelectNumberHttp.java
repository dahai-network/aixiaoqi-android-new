package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SelectNumberEntity;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class SelectNumberHttp extends BaseHttp{

   SelectNumberEntity selectNumberEntity;

    public  SelectNumberEntity getSelectNumberEntity(){
        if(selectNumberEntity==null){
            selectNumberEntity=new SelectNumberEntity();
        }

        return selectNumberEntity;
    }
    public SelectNumberHttp(InterfaceCallback call, int cmdType_, String...params ) {
        super(call,cmdType_,GET_MODE,HttpConfigUrl.GET_SELECT_NUMBER,params);

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("PageNumber",valueParams[0]+"");
        params.put("PageSize",valueParams[1]+"");
        params.put("Province",valueParams[2]);
        params.put("City",valueParams[3]);
        if(!TextUtils.isEmpty(valueParams[4]))
            params.put("MobileNumber",valueParams[4]);
    }

    @Override
    protected void parseObject(String response) {
        selectNumberEntity = new Gson().fromJson(response,SelectNumberEntity.class);
    }
}
