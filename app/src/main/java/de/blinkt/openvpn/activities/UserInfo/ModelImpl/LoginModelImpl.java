package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import android.text.TextUtils;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.Model.LoginModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.LoginHttp;
import de.blinkt.openvpn.model.LoginEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class LoginModelImpl extends NetModelBaseImpl implements LoginModel {
    private String password;



    public LoginModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestLogin(String phone, String password) {
        this.password=password;
        createHttpRequestNoCache(HttpConfigUrl.COMTYPE_LOGIN, phone, password);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        LoginHttp loginHttp = (LoginHttp) object;
        if (loginHttp.getStatus() == 1) {
            LoginEntity entity = loginHttp.getLoginModel();
            SharedUtils sharedUtils=SharedUtils.getInstance();
            if (entity != null) {
                sharedUtils.writeString(Constant.USER_NAME, entity.getTel());
                if (!TextUtils.isEmpty(password))
                    sharedUtils.writeString(Constant.PASSWORD, password);
                sharedUtils.writeString(Constant.TOKEN, entity.getToken());
                sharedUtils.writeString(Constant.USER_HEAD, entity.getUserHead());
                sharedUtils.writeString(Constant.NICK_NAME, entity.getNickName());
                sharedUtils.writeString(Constant.HEIGHT, entity.getHeight());
                sharedUtils.writeString(Constant.WEIGHT, entity.getWeight());
                sharedUtils.writeString(Constant.SOPRT_TARGET, entity.getMovingTarget());
                //登录回来缺少连接设备类型，只有MAC
                //按MAC地址保存版本号
                if (!TextUtils.isEmpty(entity.getBraceletIMEI())) {
                    sharedUtils.writeString(Constant.IMEI, entity.getBraceletIMEI().toUpperCase());
                    sharedUtils.writeString(entity.getBraceletIMEI().toUpperCase(), entity.getBraceletVersion());
                }
                sharedUtils.writeInt(Constant.COMING_TEL_REMIND, entity.getNotificaCall());
                sharedUtils.writeInt(Constant.MESSAGE_REMIND, entity.getNotificaSMS());
                sharedUtils.writeInt(Constant.WEIXIN_REMIND, entity.getNotificaWeChat());
                sharedUtils.writeInt(Constant.QQ_REMIND, entity.getNotificaQQ());
                sharedUtils.writeInt(Constant.LIFT_WRIST, entity.getNotificaQQ());
                sharedUtils.writeBoolean(Constant.ISFIRSTIN, false);
                if (!TextUtils.isEmpty(entity.getBirthday())) {
                    sharedUtils.writeString(Constant.BRITHDAY, DateUtils.getDateToString(Long.parseLong(entity.getBirthday()) * 1000).substring(0, 7).replace("-", "年"));
                }
                sharedUtils.writeString(Constant.GENDER, entity.getSex());
                //写入登陆天数，如果十五天没有登陆过则重新登录
                sharedUtils.writeLong(Constant.LOGIN_DATA, System.currentTimeMillis());
                sharedUtils.writeLong(Constant.CONFIG_TIME, System.currentTimeMillis());
            }
        }
        onLoadFinishListener.rightLoad(cmdType,object);//回调到相应界面设置签名
    }
}
