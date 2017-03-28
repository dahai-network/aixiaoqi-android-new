package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/28 0028.
 */
public class UpdateAlarmClockStatueHttp extends BaseHttp {


    public UpdateAlarmClockStatueHttp(InterfaceCallback interfaceCallback,int cmdType_,String... params){
     super(interfaceCallback,cmdType_,HttpConfigUrl.UPDATE_ALARM_CLOCK_STATUE,params);

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();

        params.put("ID",valueParams[0]+"");
        params.put("Status",valueParams[1]);
    }

}
