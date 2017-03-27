package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.EBizOrderDetailEntity;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class GetOrderDetailHttp extends BaseHttp {

    EBizOrderDetailEntity eBizOrderDetailEntity;
    public  EBizOrderDetailEntity geteBizOrderDetailEntity(){
        return eBizOrderDetailEntity;
    }
    public GetOrderDetailHttp(InterfaceCallback call, int cmdType_, String ...params) {
        super(call,cmdType_,GET_MODE,HttpConfigUrl.ORDER_DETAIL,params);


    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("id",valueParams[0]);
    }

    @Override
    protected void parseObject(String response) {
        eBizOrderDetailEntity = new Gson().fromJson(response, EBizOrderDetailEntity.class);

    }
}
