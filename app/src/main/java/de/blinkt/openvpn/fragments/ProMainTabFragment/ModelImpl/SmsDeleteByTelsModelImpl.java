package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import com.google.gson.Gson;

import java.util.ArrayList;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.SmsDeleteByTelsModel;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ui.SmsFragment;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.model.SmsIdsEntity;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class SmsDeleteByTelsModelImpl extends NetModelBaseImpl implements SmsDeleteByTelsModel {
    public SmsDeleteByTelsModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestSmsDeleteByTels(ArrayList<String> tels) {
      createHttpRequest( HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TELS, new Gson().toJson(new SmsIdsEntity(tels, null)));
    }
}
