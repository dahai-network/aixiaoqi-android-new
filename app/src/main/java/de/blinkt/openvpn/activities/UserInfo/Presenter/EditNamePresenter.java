package de.blinkt.openvpn.activities.UserInfo.Presenter;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.UserInfo.Model.EditNameModel;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.EditNameImpl;
import de.blinkt.openvpn.activities.UserInfo.ui.EditNameActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ModifyPersonInfoHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by kim
 * on 2017/6/9.
 */

public class EditNamePresenter extends BaseNetActivity {

    private EditNameModel editNameModel;
    private String realName;
    private EditNameActivity instance;

    public EditNamePresenter() {

        editNameModel = new EditNameImpl();
        if (ICSOpenVPNApplication.editNameActivity != null)
            instance = ICSOpenVPNApplication.editNameActivity;
        else
            return;


    }
    /**
     * 设置昵称
     *
     * @param realName
     */
    public void setNickName(String realName) {

        this.realName = realName;
        ModifyPersonInfoHttp modifyPersonInfoHttp = new ModifyPersonInfoHttp(this);
        editNameModel.setNickName(realName, modifyPersonInfoHttp);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {

        if (cmdType == HttpConfigUrl.COMTYPE_POST_MODIFY_NICK) {
            ModifyPersonInfoHttp modifyPersonInfoHttp = (ModifyPersonInfoHttp) object;
            if (modifyPersonInfoHttp.getStatus() == 1) {
                SharedUtils sharedUtils = SharedUtils.getInstance();
                sharedUtils.writeString(Constant.NICK_NAME, realName);
                instance.finish();
            } else {
                CommonTools.showShortToast(instance, modifyPersonInfoHttp.getMsg());
            }
        }
    }


}
