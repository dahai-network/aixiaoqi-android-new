package de.blinkt.openvpn.activities.MyModules.presenter;

import android.content.Intent;
import android.util.Log;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.ui.BalanceParticularsActivity;
import de.blinkt.openvpn.activities.MyModules.ui.BindRechargeCardActivity;
import de.blinkt.openvpn.activities.MyPackageActivity;
import de.blinkt.openvpn.activities.MyModules.model.BindRechargeCardMode;
import de.blinkt.openvpn.activities.MyModules.modelImple.BindRechargeCardModeImpl;
import de.blinkt.openvpn.activities.MyModules.view.BindRechargeCardView;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by kim
 * on 2017/6/6.
 */

public class BindRechargeCardPresenter extends BaseNetActivity {

    private BindRechargeCardMode bindRechargeCardMode;
    private BindRechargeCardView bindRechargeCardView;
    public static int RECHARGE = 1;
    public static int GIFT = 2;
    private BindRechargeCardActivity instance;

    public BindRechargeCardPresenter(BindRechargeCardView bindRechargeCardView) {
        this.bindRechargeCardView = bindRechargeCardView;
        bindRechargeCardMode = new BindRechargeCardModeImpl();
        instance = ICSOpenVPNApplication.bindRechargeCardInstance;


    }

    /**
     * 绑定充值卡
     */
    public void bindRechargeOrGift() {

        String cardPsw = bindRechargeCardView.getCardPsw();
        int bindType = bindRechargeCardView.getBindType();
        Log.d(TAG, "bindRechargeOrGift: " + cardPsw.length() + "--bindType=" + bindType);

        if (cardPsw.length() != 0) {
            if(bindType == RECHARGE) {
                CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_BIND_RECHARGE_CARD, cardPsw);
            }
            else
            {
                CreateHttpFactory.instanceHttp(this,HttpConfigUrl.COMTYPE_BIND_GIFT,cardPsw);
            }
        } else {
            bindRechargeCardView.showToast(instance.getResources().getString(R.string.input_compelete_password));
        }

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        //super.errorComplete(cmdType, errorMessage);
        Log.d(TAG, "errorComplete: " + errorMessage);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {

        Log.d(TAG, "rightComplete: " + cmdType + ":::" + object.getMsg());
        if (cmdType == HttpConfigUrl.COMTYPE_BIND_RECHARGE_CARD) {

            if (object.getStatus() == 1) {
                bindRechargeCardView.showToast(instance.getResources().getString(R.string.recharge_success));
                Intent intent = new Intent(instance, BalanceParticularsActivity.class);
                instance.startActivity(intent);
                instance.finish();
            } else {

                bindRechargeCardView.showToast(object.getMsg());
            }
        } else {

            if (object.getStatus() == 1) {
                bindRechargeCardView.showToast(instance.getResources().getString(R.string.bind_seccess));
                Intent intent = new Intent(instance, MyPackageActivity.class);
                instance.startActivity(intent);
                instance.finish();
            } else {
                Log.d(TAG, "rightComplete: ---------");
                bindRechargeCardView.showToast(object.getMsg());

            }
        }
    }

}
