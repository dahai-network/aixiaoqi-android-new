package de.blinkt.openvpn.activities.Set.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Set.Model.SetModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;

/**
 * Created by Administrator on 2017/5/26 0026.
 */

public class SetModelImpl extends NetModelBaseImpl implements SetModel{

    public SetModelImpl(OnLoadFinishListener onLoadFinishListener){
       super(onLoadFinishListener);
    }

    @Override
    public void loadExitLogin() {
        createHttpRequest(HttpConfigUrl.COMTYPE_EXIT);
    }



}
