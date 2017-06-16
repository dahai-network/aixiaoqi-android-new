package de.blinkt.openvpn.fragments.ProMainTabFragment.View;

import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface SmsView extends Toast{
    void editSmsBackground(int colorId);
    void noMoreLoad();
    void loadMoreComplete();
   void  refreshComplete();
    void noNetRelativeLayout(int isVisible);
    void recyclerView(int isVisible);
   void  nodataRelativeLayout(int isVisible);
    void onDataRefresh();
}
