package de.blinkt.openvpn.http;


import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/27 0027.
 */
public class DeleteAlarmClockHttp extends BaseHttp{


    public DeleteAlarmClockHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params){
        super(interfaceCallback,cmdType_,HttpConfigUrl.ALARM_CLOCK_DELETE,params);

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("ID",valueParams[0]);
    }

}
