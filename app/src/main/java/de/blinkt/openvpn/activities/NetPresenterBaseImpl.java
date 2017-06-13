package de.blinkt.openvpn.activities;

import de.blinkt.openvpn.Logger;
import de.blinkt.openvpn.http.CommonHttp;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class NetPresenterBaseImpl extends Logger implements NetModelBaseImpl.OnLoadFinishListener{
    @Override
    public void rightLoad(int cmdType, CommonHttp object) {

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {

    }

    @Override
    public void noNet() {

    }

    public  void onDestroy(){

    }

}
