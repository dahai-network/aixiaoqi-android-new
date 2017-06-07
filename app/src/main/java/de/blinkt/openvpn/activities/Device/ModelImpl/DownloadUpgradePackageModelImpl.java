package de.blinkt.openvpn.activities.Device.ModelImpl;

import de.blinkt.openvpn.activities.Device.Model.DownloadUpgradePackageModel;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.DownloadSkyUpgradePackageHttp;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public class DownloadUpgradePackageModelImpl extends NetModelBaseImpl implements DownloadUpgradePackageModel {
    public DownloadUpgradePackageModelImpl(OnLoadFinishListener onLoadFinishListener) {
        super(onLoadFinishListener);
    }

    @Override
    public void downloadUpgradePackage(String downloadUrl) {
        createHttpRequest(HttpConfigUrl.COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE, downloadUrl);
    }
}
