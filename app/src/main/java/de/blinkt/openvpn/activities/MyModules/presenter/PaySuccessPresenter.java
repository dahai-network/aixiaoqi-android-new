package de.blinkt.openvpn.activities.MyModules.presenter;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.PaySuccessMode;
import de.blinkt.openvpn.activities.MyModules.modelImple.PaySuccessImpl;
import de.blinkt.openvpn.activities.MyModules.view.PaySuccessView;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.IsHavePacketHttp;
import de.blinkt.openvpn.model.IsHavePacketEntity;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.constant.Constant.UP_TO_POWER_NO_RESPONSE;

/**
 * Created by kim
 * on 2017/6/8.
 */

public class PaySuccessPresenter extends BaseNetActivity {

    private PaySuccessMode paySuccessMode;
    private PaySuccessView paySuccessView;

    public PaySuccessPresenter(PaySuccessView paySuccessView) {
        this.paySuccessView = paySuccessView;
        paySuccessMode = new PaySuccessImpl();

    }

    /**
     * 检测是否有该套餐
     * @param type 套餐类型
     */
    public void isHavePacket(String type) {
        paySuccessMode.isHavePacket(type, this);

    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (object.getStatus() == 1) {
            IsHavePacketHttp isHavePacketHttp = (IsHavePacketHttp) object;
            IsHavePacketEntity entity = isHavePacketHttp.getOrderDataEntity();
            if (entity.getUsed() == 1) {
                //如果之前无套餐的状态，就上电
                if (!SharedUtils.getInstance().readBoolean(Constant.ISHAVEORDER, false)) {
                    SendCommandToBluetooth.sendMessageToBlueTooth(UP_TO_POWER_NO_RESPONSE);
                }
                //标记新状态
                SharedUtils.getInstance().writeBoolean(Constant.ISHAVEORDER, true);
            } else {
                //TODO 没有通知到设备界面

            }
        }
    }
}
