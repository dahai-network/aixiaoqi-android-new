package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import android.content.Intent;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.UserInfo.Model.BlackListModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.BlackListDBHelp;
import de.blinkt.openvpn.http.BlackListGetHttp;
import de.blinkt.openvpn.http.CommonHttp;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class BlackListModelImpl extends NetModelBaseImpl implements BlackListModel {
    public BlackListModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }

    @Override
    public void requestBlackList() {
        createHttpRequestNoCache(HttpConfigUrl.COMTYPE_BLACK_LIST_GET);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if(cmdType==HttpConfigUrl.COMTYPE_BLACK_LIST_GET){
        if (object.getStatus() == 1) {
            BlackListGetHttp blackListGetHttp = (BlackListGetHttp) object;
            BlackListDBHelp blackListDBHelp = new BlackListDBHelp(ICSOpenVPNApplication.getContext());
            blackListDBHelp.deleteAllDefriend();
            if (blackListGetHttp.getBlackListEntities().size() != 0) {
                blackListDBHelp.insertDefriendList(blackListGetHttp.getBlackListEntities());
            }

        }
        onLoadFinishListener.rightLoad(cmdType,object);
    }
    }
}
