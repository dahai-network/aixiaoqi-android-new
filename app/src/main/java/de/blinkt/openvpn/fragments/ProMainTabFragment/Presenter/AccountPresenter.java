package de.blinkt.openvpn.fragments.ProMainTabFragment.Presenter;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface AccountPresenter {
    void requestBalance();
    void requestUserPackage();
    void requestUnbindDevice();
    void requestGetBindInfo();
}
