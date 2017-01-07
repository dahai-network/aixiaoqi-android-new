package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/12/1 0001.
 */
public class SelectNumberInfoHttp  extends BaseHttp{
    private String OrderByZCId;
    private String Name;
    private String IdentityNumber;
    private String MobileNumber;
    private String PaymentMethod;
    public SelectNumberInfoHttp(InterfaceCallback call, int cmdType_,String OrderByZCId,String Name,String IdentityNumber,String MobileNumber,String PaymentMethod) {
        super(call,cmdType_);
        this.IdentityNumber=IdentityNumber;
        this.OrderByZCId=OrderByZCId;
        this.Name=Name;
        this.MobileNumber=MobileNumber;
        this.PaymentMethod=PaymentMethod;
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_= HttpConfigUrl.ADD_SELECT_NUMBER_INFO;
        params.put("OrderByZCId",OrderByZCId);
        params.put("Name",Name);
        params.put("IdentityNumber",IdentityNumber);
        params.put("MobileNumber",MobileNumber);
        params.put("PaymentMethod",PaymentMethod);
    }
}
