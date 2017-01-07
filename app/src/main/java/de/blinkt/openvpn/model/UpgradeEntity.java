package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/20 0020.
 */
public class UpgradeEntity implements Serializable{
    private int Version;
    private String VersionName;
    private String Descr;
    private String Url;

    public int getVersion() {
        return Version;
    }

    public String getVersionName() {
        return VersionName;
    }

    public String getDescr() {
        return Descr;
    }

    public String getUrl() {
        return Url;
    }
}
