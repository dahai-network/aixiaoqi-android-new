package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.EBizOrderListEntity;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class GetOrderListHttp  extends BaseHttp{
    private String phoneNumber;
    EBizOrderListEntity eBizOrderListEntity;
    public  EBizOrderListEntity geteBizOrderListEntity(){
        return  eBizOrderListEntity;
    }
    public GetOrderListHttp(InterfaceCallback call, int cmdType_, String phoneNumber) {
        super(call,cmdType_);
        this.phoneNumber = phoneNumber;

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        sendMethod_=GET_MODE;
        slaverDomain_= HttpConfigUrl.ORDER_LIST;
        params.put("CallPhone",phoneNumber);
        params.put("PageNumber","1");
        params.put("PageSize","1000");
    }

    @Override
    protected void parseObject(String response) {
        eBizOrderListEntity = new Gson().fromJson(response, EBizOrderListEntity.class);
    }
}
