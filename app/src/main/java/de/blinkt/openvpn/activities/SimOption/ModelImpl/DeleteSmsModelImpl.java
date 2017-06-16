package de.blinkt.openvpn.activities.SimOption.ModelImpl;

import com.google.gson.Gson;

import java.util.ArrayList;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.SimOption.Model.DeleteSmsModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.model.SmsIdsEntity;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class DeleteSmsModelImpl extends NetModelBaseImpl implements DeleteSmsModel {
    public DeleteSmsModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestDeleteSms(ArrayList<String> ids) {
       createHttpRequest( HttpConfigUrl.COMTYPE_SMS_DELETE_SMSS, new Gson().toJson(new SmsIdsEntity(null, ids)));
    }
}
