package de.blinkt.openvpn.fragments.base;

import android.support.v4.app.Fragment;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class BaseNetFragment extends Fragment implements InterfaceCallback {
    @Override
    public void noNet() {

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {

    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {

    }



    protected void createHttpRequest(int cmdType) {

        CreateHttpFactory.instanceHttp(this, cmdType);
    }

    protected void createHttpRequestNoCache(int cmdType) {
        if (hasWiFi()) {
            CreateHttpFactory.instanceHttp(this, cmdType);
        }
    }

    private  boolean hasWiFi(){
        if(!NetworkUtils.isNetworkAvailable(getActivity())){
            CommonTools.showShortToast(getActivity(), getString(R.string.no_wifi));
            return false;
        }
        return  true;
    }

    protected void createHttpRequest(int cmdType, String... params) {
        CreateHttpFactory.instanceHttp(this, cmdType, params);
    }
}
