package de.blinkt.openvpn.activities.ShopModules.view;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import de.blinkt.openvpn.http.PacketDtailHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;

/**
 * Created by kim
 * on 2017/6/7.
 */

public interface PackageDetailView {

    /**
     * 加载成功显示界面
     *
     * @param bean
     * @param http
     */
    void loadSuccessShowView(PacketDtailEntity.ListBean bean, PacketDtailHttp http);

    void loadSuccessAndSetImage(Bitmap resource, int height);

    void noNetShowView();
}
