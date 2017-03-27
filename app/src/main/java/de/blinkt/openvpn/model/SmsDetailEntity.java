package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/12 0012.
 */

public class SmsDetailEntity implements Serializable {
    private String Fm;
    private String To ;
    private String SMSTime;
    private String SMSContent;

    public boolean isSend() {
        return IsSend;
    }

    public void setSend(boolean send) {
        IsSend = send;
    }

    private boolean IsSend;
    private String IsRead;
    private String Status;
    private String SMSID;

    public String getSMSID() {
        return SMSID;
    }

    public void setSMSID(String SMSID) {
        this.SMSID = SMSID;
    }
    public String getFm() {
        return Fm;
    }

    public void setFm(String fm) {
        Fm = fm;
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

    public String getSMSContent() {
        return SMSContent;
    }

    public void setSMSContent(String SMSContent) {
        this.SMSContent = SMSContent;
    }



    public String getIsRead() {
        return IsRead;
    }

    public void setIsRead(String isRead) {
        IsRead = isRead;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

}
