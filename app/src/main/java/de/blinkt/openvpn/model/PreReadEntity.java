package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class PreReadEntity implements Serializable{
    private String iccid;
    private String imsi;
    private String preReadData;
    private String dataLength;

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getPreReadData() {
        return preReadData;
    }

    public void setPreReadData(String preReadData) {
        this.preReadData = preReadData;
    }


    @Override
    public String toString() {
        return "PreReadEntity{" +
                "iccid='" + iccid + '\'' +
                ", imsi='" + imsi + '\'' +
                ", preReadData='" + preReadData + '\'' +
                ", dataLength='" + dataLength + '\'' +
                '}';
    }
}
