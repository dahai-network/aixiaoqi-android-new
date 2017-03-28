package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SportPeriodEntity;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class SportPeriodHttp extends BaseHttp {




    public SportPeriodEntity getSportPeriodEntity() {
        return sportPeriodEntity;
    }

    private  SportPeriodEntity     sportPeriodEntity;


    public SportPeriodHttp(InterfaceCallback interfaceCallback, int cmdType_,String...params ){
      super(interfaceCallback,cmdType_,GET_MODE,HttpConfigUrl.SPORT_GET_TIME_PERIOD_DATE,params);
    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("Date",valueParams[0]);
    }

    @Override
    protected void parseObject(String response) {
        sportPeriodEntity= new Gson().fromJson(response,SportPeriodEntity.class);
    }




}
