package de.blinkt.openvpn.activities.Set.ModelImpl;

import android.os.Build;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Set.Model.UserFeedbackModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2017/5/25 0025.
 */

public class UserFeedbackModelImpl extends NetModelBaseImpl implements UserFeedbackModel {


    public UserFeedbackModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void loadUserFeedback(String feedbackContent) {
        CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_USER_FEED_BACK, Build.MANUFACTURER + Build.BRAND + Build.MODEL, "" + CommonTools.getVersion(ICSOpenVPNApplication.getContext()), feedbackContent);
    }


}
