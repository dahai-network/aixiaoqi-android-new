package de.blinkt.openvpn.fragments.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.StateChangeEntity;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.views.TopProgressView;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class BaseStatusFragment extends Fragment {
    private int id;
    protected   TopProgressView topProgressView;
    protected  void setLayoutId(int id){
        this.id=id;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(id,
                container, false);
        initView(rootView);
        return rootView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveStateChangeEntity(StateChangeEntity entity) {
        switch (entity.getStateType()) {
            case StateChangeEntity.BLUETOOTH_STATE:
                if (entity.isopen() && getString(R.string.bluetooth_unopen).equals(topProgressView.getContent())) {
                    if (checkNetWorkAndBlueIsOpen()) {
                        topProgressView.setVisibility(View.GONE);
                    }
                } else {
                    topProgressView.showTopProgressView(getString(R.string.bluetooth_unopen), -1, null);
                }
                break;
            case StateChangeEntity.NET_STATE:
                if (entity.isopen() && getString(R.string.no_wifi).equals(topProgressView.getContent())) {
                    if (checkNetWorkAndBlueIsOpen()) {
                        topProgressView.setVisibility(View.GONE);
                    }
                } else {
                    topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);

                }
                break;
        }

    }


    private boolean checkNetWorkAndBlueIsOpen() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
            return false;
        } else if (!ICSOpenVPNApplication.uartService.isOpenBlueTooth()) {
            topProgressView.showTopProgressView(getString(R.string.bluetooth_unopen), -1, null);
            return false;
        }
        return true;
    }
    private void initView(View view){
        topProgressView=(TopProgressView)view.findViewById(R.id.top_view);
    }

    protected void setTopViewBackground(int colorId){
        topProgressView.setBackgroundResource(colorId);
    }
}
