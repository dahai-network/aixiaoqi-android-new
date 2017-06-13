package de.blinkt.openvpn.fragments.ProMainTabFragment.Presenter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface SmsPresenter {
   void  requestSmsList(int pageNumber,int requestNetCount);
    void requestSmsDeleteByTels(ArrayList<String> tels);
    void onDestory();
}
