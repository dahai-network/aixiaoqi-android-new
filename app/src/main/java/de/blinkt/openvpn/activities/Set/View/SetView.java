package de.blinkt.openvpn.activities.Set.View;

/**
 * Created by Administrator on 2017/5/26 0026.
 */

public interface SetView {
    void finishView();
    void startActivity(Class<?> activity);
    void showToast(int toastContentId);
    void showToast(String  toastContent);
}
