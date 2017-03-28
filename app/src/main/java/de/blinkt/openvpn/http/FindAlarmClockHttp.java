package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.AlarmClockEntity;


/**
 * Created by Administrator on 2016/10/27 0027.
 */
public class FindAlarmClockHttp extends BaseHttp{
    public List<AlarmClockEntity> getAlarmClockEntityList() {
        return alarmClockEntityList;
    }

    List<AlarmClockEntity> alarmClockEntityList;
    public FindAlarmClockHttp(InterfaceCallback interfaceCallback,int cmdType_){
        super(interfaceCallback,cmdType_,false,GET_MODE,HttpConfigUrl.ALARM_CLOCK_GET);

    }

    @Override
    protected void parseObject(String response) {
        alarmClockEntityList = new Gson().fromJson(response, new TypeToken<List<AlarmClockEntity>>() {
        }.getType());
    }




}
