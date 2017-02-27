package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/10 0010.
 */
public class BasicConfigEntity  implements Serializable{
    private String userAgreementUrl;
    private String dualSimStandbyTutorialUrl;
    private String beforeGoingAbroadTutorialUrl;
    private String paymentOfTerms;


    public String getUserAgreementUrl() {
        return userAgreementUrl;
    }

	public String getDualSimStandbyTutorialUrl() {
		return dualSimStandbyTutorialUrl;
	}

	public void setDualSimStandbyTutorialUrl(String dualSimStandbyTutorialUrl) {
		this.dualSimStandbyTutorialUrl = dualSimStandbyTutorialUrl;
	}

	public String getBeforeGoingAbroadTutorialUrl() {
		return beforeGoingAbroadTutorialUrl;
	}

	public void setBeforeGoingAbroadTutorialUrl(String beforeGoingAbroadTutorialUrl) {
		this.beforeGoingAbroadTutorialUrl = beforeGoingAbroadTutorialUrl;
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
