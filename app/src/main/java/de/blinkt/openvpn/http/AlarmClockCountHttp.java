package de.blinkt.openvpn.http;


import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.AlarmClockCount;

/**
 * Created by Administrator on 2016/10/28 0028.
 */
public class AlarmClockCountHttp extends BaseHttp{


    public AlarmClockCount getAlarmClockCount() {
        return alarmClockCount;
    }

    private  AlarmClockCount alarmClockCount;

    public AlarmClockCountHttp(InterfaceCallback interfaceCallback,int cmdType_){
        super(interfaceCallback,cmdType_,false,GET_MODE,HttpConfigUrl.ALARM_CLOCK_COUNT);
    }
    @Override
    protected void parseObject(String response) {
        alarmClockCount=new Gson().fromJson(response,AlarmClockCount.class);
    }


}
