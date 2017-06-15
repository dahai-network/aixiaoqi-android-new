package de.blinkt.openvpn.activities.ShopModules.view;

import java.util.List;
import de.blinkt.openvpn.http.CountryPacketHttp;
import de.blinkt.openvpn.model.CountryPacketEntity;

/**
 * Created by kim
 * on 2017/6/7.
 */

public interface CountryPackageView {
    void loadSuccessShowView(List<CountryPacketEntity> bean, CountryPacketHttp http);
    void noNetShowView();
}
