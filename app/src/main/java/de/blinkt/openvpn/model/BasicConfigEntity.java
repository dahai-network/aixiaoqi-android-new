package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/10 0010.
 */
public class BasicConfigEntity  implements Serializable{
    private String userAgreementUrl;
    private String paymentOfTerms;


    public String getUserAgreementUrl() {
        return userAgreementUrl;
    }

    public void setUserAgreementUrl(String userAgreementUrl) {
        this.userAgreementUrl = userAgreementUrl;
    }

    public String getPaymentOfTerms() {
        return paymentOfTerms;
    }

    public void setPaymentOfTerms(String paymentOfTerms) {
        this.paymentOfTerms = paymentOfTerms;
    }

}
