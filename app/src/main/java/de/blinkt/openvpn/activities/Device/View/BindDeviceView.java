package de.blinkt.openvpn.activities.Device.View;

import java.util.List;
import de.blinkt.openvpn.activities.CommomView.Toast;
import de.blinkt.openvpn.http.GetBindsIMEIHttp;
import de.blinkt.openvpn.model.BluetoothEntity;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public interface BindDeviceView extends Toast{
    void finishView();
    void searchBluetoothText(int searchId);
    void setFindedImageView(int IsVisible);
    void SetUniImageViewBackground(int sourceId);
    void tipSearchText(int tipText);
    String getDeviceName();
    void toActivity();
    void showNotSearchDeviceDialog();
    void connect(String macAddress);
    void scanLeDevice(boolean enable);
    void afterConnDevice();
    void showDeviceView( List<BluetoothEntity> list,GetBindsIMEIHttp http);
}
