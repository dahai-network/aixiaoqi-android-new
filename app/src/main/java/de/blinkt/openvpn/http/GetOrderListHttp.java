package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.EBizOrderListEntity;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class GetOrderListHttp  extends BaseHttp{
    EBizOrderListEntity eBizOrderListEntity;
    public  EBizOrderListEntity geteBizOrderListEntity(){
        return  eBizOrderListEntity;
    }
    public GetOrderListHttp(InterfaceCallback call, int cmdType_, String ...params) {
        super(call,cmdType_,GET_MODE,HttpConfigUrl.ORDER_LIST,params);
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("CallPhone",valueParams[0]);
        params.put("PageNumber","1");
        params.put("PageSize","1000");
    }

    @Override
    protected void parseObject(String response) {
        eBizOrderListEntity = new Gson().fromJson(response, EBizOrderListEntity.class);
    }
}
