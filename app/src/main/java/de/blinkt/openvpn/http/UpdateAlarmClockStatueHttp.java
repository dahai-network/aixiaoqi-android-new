package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/28 0028.
 */
public class UpdateAlarmClockStatueHttp extends BaseHttp {

    private  String  id;
    private String statue;
    public UpdateAlarmClockStatueHttp(InterfaceCallback interfaceCallback,int cmdType_,String id,String statue){
     super(interfaceCallback,cmdType_);
        this.id=id;
        this.statue=statue;
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_= HttpConfigUrl.UPDATE_ALARM_CLOCK_STATUE;

        params.put("ID",id+"");
        params.put("Status",statue);
    }

}
