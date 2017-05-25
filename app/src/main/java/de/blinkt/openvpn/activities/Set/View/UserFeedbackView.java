package de.blinkt.openvpn.activities.Set.View;

/**
 * Created by Administrator on 2017/5/25 0025.
 */

public interface UserFeedbackView {
    String getUserFeedbackContent();
    void showToast(int toastContentId);
    void showToast(String toastContent);
    void finishView();
}
