package de.blinkt.openvpn.http;


import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/27 0027.
 */
public class DeleteAlarmClockHttp extends BaseHttp{
    private String id;

    public DeleteAlarmClockHttp(InterfaceCallback interfaceCallback, int cmdType_, String id){
        super(interfaceCallback,cmdType_);
        this.id=id;

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_= HttpConfigUrl.ALARM_CLOCK_DELETE;
        params.put("ID",id);
    }

}
