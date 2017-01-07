package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.LocationEntity;
import de.blinkt.openvpn.model.SmsEntity;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class GetLocationListHttp  extends BaseHttp{
    List<LocationEntity> locationEntityList;
    public    List<LocationEntity> getLocationEntityList(){
        if(locationEntityList==null){
            locationEntityList=new ArrayList<>();
        }
        return  locationEntityList;
    }
    public GetLocationListHttp(InterfaceCallback call, int cmdType_ ) {
        super(call,cmdType_);
        isCreateHashMap=false;


    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_=HttpConfigUrl.GET_LOCATION_LIST;
        sendMethod_=GET_MODE;
    }

    @Override
    protected void parseObject(String response) {
        locationEntityList = new Gson().fromJson(response, new TypeToken<List<LocationEntity>>() {
        }.getType());
    }
}
