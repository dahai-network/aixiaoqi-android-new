package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.UserInfo.Model.BasicConfigModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBasicConfigHttp;
import de.blinkt.openvpn.model.BasicConfigEntity;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class BasicConfigModelImpl extends NetModelBaseImpl implements BasicConfigModel {

    @Override
    public void requestBasicConfig() {
        createHttpRequestNoCache(HttpConfigUrl.COMTYPE_GET_BASIC_CONFIG);

    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        GetBasicConfigHttp getBasicConfigHttp = (GetBasicConfigHttp) object;
        if (getBasicConfigHttp.getStatus() == 1) {
            BasicConfigEntity basicConfigEntity = getBasicConfigHttp.getBasicConfigEntity();
            SharedUtils sharedUtils=SharedUtils.getInstance();
            sharedUtils.writeString(IntentPutKeyConstant.USER_AGREEMENT_URL, basicConfigEntity.getUserAgreementUrl());
            sharedUtils.writeString(IntentPutKeyConstant.DUALSIM_STANDBYTUTORIAL_URL, basicConfigEntity.getDualSimStandbyTutorialUrl());
            sharedUtils.writeString(IntentPutKeyConstant.BEFORE_GOING_ABROAD_TUTORIAL_URL, basicConfigEntity.getBeforeGoingAbroadTutorialUrl());
            sharedUtils.writeString(IntentPutKeyConstant.PAYMENT_OF_TERMS, basicConfigEntity.getPaymentOfTerms());
        }
    }
}
