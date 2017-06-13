package de.blinkt.openvpn.fragments.ProMainTabFragment.View;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface SmsView {
    void editSmsBackground(int colorId);
    void noMoreLoad();
    void loadMoreComplete();
   void  refreshComplete();
    void noNetRelativeLayout(int isVisible);
    void recyclerView(int isVisible);
   void  nodataRelativeLayout(int isVisible);
    void showToast(int toastId);
    void showToast(String toastContent);
    void onDataRefresh();
}
