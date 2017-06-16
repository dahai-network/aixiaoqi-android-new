package de.blinkt.openvpn.activities.SimOption.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.SimOption.Model.AddBlackListModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class AddBlackListModelImpl extends NetModelBaseImpl implements AddBlackListModel {
    public AddBlackListModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestAddBlackList(String phoneNumber) {
        createHttpRequest(HttpConfigUrl.COMTYPE_BLACK_LIST_ADD, phoneNumber);
    }
}
