package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/9.
 */

public class SportTotalEntity implements Serializable {

	/**
	 * Date : 11
	 * StepNum : 12602
	 * KM : 1.1
	 * Kcal : 11
	 */

	private String Date;
	private String StepNum;
	private String KM;
	private String Kcal;

	public String getDate() {
		return Date;
	}

	public void setDate(String Date) {
		this.Date = Date;
	}

	public String getStepNum() {
		return StepNum;
	}

	public void setStepNum(String StepNum) {
		this.StepNum = StepNum;
	}

	public String getKM() {
		return KM;
	}

	public void setKM(String KM) {
		this.KM = KM;
	}

	public String getKcal() {
		return Kcal;
	}

	public void setKcal(String Kcal) {
		this.Kcal = Kcal;
	}

}
