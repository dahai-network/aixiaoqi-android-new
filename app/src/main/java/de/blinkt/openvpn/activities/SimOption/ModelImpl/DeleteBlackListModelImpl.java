package de.blinkt.openvpn.activities.SimOption.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.SimOption.Model.DeleteBlackListModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class DeleteBlackListModelImpl extends NetModelBaseImpl implements DeleteBlackListModel {
    public DeleteBlackListModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestDeleteBlackList(String phoneNumber) {
        createHttpRequest(HttpConfigUrl.COMTYPE_BLACK_LIST_DELETE,phoneNumber);
    }
}
