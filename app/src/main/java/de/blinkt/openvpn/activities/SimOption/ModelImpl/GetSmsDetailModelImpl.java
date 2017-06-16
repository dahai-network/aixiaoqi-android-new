package de.blinkt.openvpn.activities.SimOption.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.SimOption.Model.GetSmsDetailModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.util.User;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class GetSmsDetailModelImpl extends NetModelBaseImpl implements GetSmsDetailModel {
    public GetSmsDetailModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestGetSmsDetail(String phoneNumber,String pageNumber) {
        createHttpRequest(HttpConfigUrl.COMTYPE_GET_SMS_DETAIL, phoneNumber, pageNumber + "", Constant.PAGESIZE + "");
    }
}
