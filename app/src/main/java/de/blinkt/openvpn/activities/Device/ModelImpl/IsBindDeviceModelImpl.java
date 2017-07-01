package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.IsBindDeviceModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class IsBindDeviceModelImpl extends NetModelBaseImpl implements IsBindDeviceModel {
    public  IsBindDeviceModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void isBindDevice(String address) {
        if(address==null){
            address = SharedUtils.getInstance().readString(Constant.BRACELETNAME, address);
        }
        Log.d("IsBindDeviceModelImpl", "isBindDevice: "+address);
        createHttpRequest(HttpConfigUrl.COMTYPE_ISBIND_DEVICE, address);
    }

    @Override
    public void getDeviceState(ArrayList<String> addresss) {
        Log.d("IsBindDeviceModelImpl", "getDeviceState: "+addresss.size()+"--addresss="+new Gson().toJson(addresss));

        createHttpRequest( HttpConfigUrl.COMTYPE_GET_BINDS_IMEI, new Gson().toJson(addresss));
    }
}
