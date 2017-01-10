package com.aixiaoqi.socket;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/24 0024.
 */
public class IccidEntity implements Serializable {
    private String chnString;
    private String lenString;
    private String Iccid;
    private String Immsi;
    private String evtIndex;

    public String getChnString() {
        return chnString;
    }

    public void setChnString(String chnString) {
        this.chnString = chnString;
    }

    public String getLenString() {
        return lenString;
    }

    public void setLenString(String lenString) {
        this.lenString = lenString;
    }

    public String getIccid() {
        return Iccid;
    }

    public void setIccid(String iccid) {
        Iccid = iccid;
    }

    public String getImmsi() {
        return Immsi;
    }

    public void setImmsi(String immsi) {
        Immsi = immsi;
    }

    public String getEvtIndex() {
        return evtIndex;
    }

    public void setEvtIndex(String evtIndex) {
        this.evtIndex = evtIndex;
    }

    @Override
    public String toString() {
        return "IccidEntity{" +
                "chnString='" + chnString + '\'' +
                ", lenString='" + lenString + '\'' +
                ", Iccid='" + Iccid + '\'' +
                ", Immsi='" + Immsi + '\'' +
                ", evtIndex='" + evtIndex + '\'' +
                '}';
    }
}
