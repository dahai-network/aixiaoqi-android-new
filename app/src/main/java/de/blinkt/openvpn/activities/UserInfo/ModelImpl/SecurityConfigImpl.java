package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import com.umeng.analytics.MobclickAgent;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.Model.SecurityConfig;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.SecurityConfigHttp;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class SecurityConfigImpl extends NetModelBaseImpl implements SecurityConfig {
    public SecurityConfigImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }


    @Override
    public void requestSecurityConfig() {
        createHttpRequestNoCache(HttpConfigUrl.COMTYPE_SECURITY_CONFIG);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if(cmdType==HttpConfigUrl.COMTYPE_SECURITY_CONFIG){
            SecurityConfigHttp securityConfigHttp = (SecurityConfigHttp) object;
            if (securityConfigHttp.getStatus() == 1) {
                //友盟帐号统计
                SharedUtils sharedUtils=SharedUtils.getInstance();
                MobclickAgent.onProfileSignIn(sharedUtils.readString(Constant.TEL));
                cn.com.johnson.model.SecurityConfig.InBean in = securityConfigHttp.getSecurityConfig().getIn();
                cn.com.johnson.model.SecurityConfig.OutBean out = securityConfigHttp.getSecurityConfig().getOut();
                sharedUtils.writeString(Constant.ASTERISK_IP_IN, in.getAsteriskIp());
                sharedUtils.writeString(Constant.ASTERISK_PORT_IN, in.getAsteriskPort());
                sharedUtils.writeString(Constant.ASTERISK_IP_OUT, out.getAsteriskIp());
                sharedUtils.writeString(Constant.ASTERISK_PORT_OUT, out.getAsteriskPort());
                sharedUtils.writeString(Constant.PUBLIC_PASSWORD, out.getPublicPassword());
                createHttpRequestNoCache(HttpConfigUrl.COMTYPE_BLACK_LIST_GET);
            }

            if(onLoadFinishListener!=null){
                onLoadFinishListener.rightLoad(cmdType,object);
            }
        }
    }
}
