package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.EBizOrderDetailEntity;
import de.blinkt.openvpn.model.EBizOrderListEntity;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class GetOrderDetailHttp extends BaseHttp {
    private String orderId;
    EBizOrderDetailEntity eBizOrderDetailEntity;
    public  EBizOrderDetailEntity geteBizOrderDetailEntity(){
        return eBizOrderDetailEntity;
    }
    public GetOrderDetailHttp(InterfaceCallback call, int cmdType_, String orderId) {
        super(call,cmdType_);
        this.orderId = orderId;

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        sendMethod_=GET_MODE;
        slaverDomain_= HttpConfigUrl.ORDER_DETAIL;
        params.put("id",orderId);
    }

    @Override
    protected void parseObject(String response) {
        eBizOrderDetailEntity = new Gson().fromJson(response, EBizOrderDetailEntity.class);

    }
}
