package de.blinkt.openvpn.model.enentbus;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/7 0007.
 */

public class BlueReturnData implements Serializable {
    private String dataType;
    private String responeStatue;
    private String valideData;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getResponeStatue() {
        return responeStatue;
    }

    public void setResponeStatue(String responeStatue) {
        this.responeStatue = responeStatue;
    }

    public String getValideData() {
        return valideData;
    }

    public void setValideData(String valideData) {
        this.valideData = valideData;
    }
}
