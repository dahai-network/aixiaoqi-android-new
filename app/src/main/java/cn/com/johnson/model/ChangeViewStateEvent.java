package cn.com.johnson.model;

/**
 * Created by kim
 * on 2017/4/20.
 */

public class ChangeViewStateEvent {

    private  boolean  isNewVersion;//是否有新版本

    public ChangeViewStateEvent(boolean isNewVersion, boolean isNewPackage) {
        this.isNewVersion = isNewVersion;
        this.isNewPackage = isNewPackage;
    }

    public boolean isNewPackage() {
        return isNewPackage;
    }

    public void setNewPackage(boolean newPackage) {
        isNewPackage = newPackage;
    }

    private  boolean  isNewPackage;//是否未激活过

    public boolean isNewVersion() {
        return isNewVersion;
    }

    public void setNewVersion(boolean newVersion) {
        isNewVersion = newVersion;
    }
}
