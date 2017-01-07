package com.aixiaoqi.socket;

/**
 * Created by Administrator on 2016/12/24 0024.
 */
public class PreDataEntity {
    private String chnString;
    private String lenString;
    private String preDataString;
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

    public String getPreDataString() {
        return preDataString;
    }

    public void setPreDataString(String preDataString) {
        this.preDataString = preDataString;
    }

    public String getEvtIndex() {
        return evtIndex;
    }

    public void setEvtIndex(String evtIndex) {
        this.evtIndex = evtIndex;
    }

    @Override
    public String toString() {
        return "PreDataEntity{" +
                "chnString='" + chnString + '\'' +
                ", lenString='" + lenString + '\'' +
                ", evtIndex='" + evtIndex + '\'' +
                '}';
    }
}
