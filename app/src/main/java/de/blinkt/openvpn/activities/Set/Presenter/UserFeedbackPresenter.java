package de.blinkt.openvpn.activities.Set.Presenter;

import de.blinkt.openvpn.activities.Set.ModelImpl.UserFeedbackModelImpl;
import de.blinkt.openvpn.activities.Set.View.UserFeedbackView;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by Administrator on 2017/5/25 0025.
 */

public interface UserFeedbackPresenter {
void requsetUserFeedback( );
    void onDestory();
}
