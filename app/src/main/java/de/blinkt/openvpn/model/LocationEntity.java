package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class LocationEntity implements Serializable{
    private String Province;
    private List<String> Citys;

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public List<String> getCitys() {
        return Citys;
    }

    public void setCitys(List<String> citys) {
        Citys = citys;
    }
}
