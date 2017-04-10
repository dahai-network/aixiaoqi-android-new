package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class SmsEntity implements Serializable {
	public String Fm;
	public String To;
	public String SMSTime;
	public String LookUpKey;
	public String SMSContent;
	public String IsSend;
	public String IsRead;

	public int position;
	public boolean isCheck;
	private int Status;

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean check) {
		isCheck = check;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getRealName() {
		return realName;
	}

	public String getLookUpKey() {
		return LookUpKey;
	}

	public void setLookUpKey(String lookUpKey) {
		LookUpKey = lookUpKey;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String realName;

	public String getSMSContent() {
		return SMSContent;
	}

	public void setSMSContent(String SMSContent) {
		this.SMSContent = SMSContent;
	}


	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getTo() {
		return To;
	}

	public void setTo(String to) {
		To = to;
	}

	public String getSMSTime() {
		return SMSTime;
	}

	public void setSMSTime(String SMSTime) {
		this.SMSTime = SMSTime;
	}


	public String getIsSend() {
		return IsSend;
	}

	public void setIsSend(String isSend) {
		IsSend = isSend;
	}

	public String getIsRead() {
		return IsRead;
	}

	public void setIsRead(String isRead) {
		IsRead = isRead;
	}


	public String getFm() {
		return Fm;
	}

	public void setFm(String fm) {
		Fm = fm;
	}

	@Override
	public String toString() {
		return "Fm" + Fm + "To" + To;
	}
}
