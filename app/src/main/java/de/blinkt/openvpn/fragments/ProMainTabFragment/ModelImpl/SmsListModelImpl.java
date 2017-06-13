package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.SmsDeleteByTelsModel;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.SmsListModel;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class SmsListModelImpl extends NetModelBaseImpl implements SmsListModel {
    public SmsListModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestSmsList(String pageNumber) {
        CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_SMS_LIST, pageNumber + "", Constant.PAGESIZE + "");
    }
}
