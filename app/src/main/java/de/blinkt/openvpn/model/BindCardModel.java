package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/23.
 */

public class BindCardModel implements Serializable{
	/**
	 * CardNum : string,卡号
	 */

	private String CardNum;

	public String getCardNum() {
		return CardNum;
	}

	public void setCardNum(String CardNum) {
		this.CardNum = CardNum;
	}

}
