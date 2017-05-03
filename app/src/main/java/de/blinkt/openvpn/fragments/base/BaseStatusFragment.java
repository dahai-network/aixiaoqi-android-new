package de.blinkt.openvpn.fragments.base;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aixiaoqi.socket.SocketConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.CanClickEntity;
import de.blinkt.openvpn.model.ShowDeviceEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.StateChangeEntity;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.TopProgressView;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class BaseStatusFragment extends Fragment {
	private int id;
	protected TopProgressView topProgressView;
	protected void setLayoutId(int id) {
		this.id = id;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(id,
                container, false);
        initView(rootView);
        EventBus.getDefault().register(this);
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
					//已连接的就断开连接
					ICSOpenVPNApplication.uartService.disconnect();
                }
                break;
            case StateChangeEntity.NET_STATE:
                if (entity.isopen() && getString(R.string.no_wifi).equals(topProgressView.getContent())) {
                    if (checkNetWorkAndBlueIsOpen()) {
                        topProgressView.setVisibility(View.GONE);
                    }
                } else {
                    topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
                    setRegisted(false);
                }
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void canClickEntity(CanClickEntity entity) {
        switch (entity.getJumpTo()) {

            case CanClickEntity.JUMP_MYDEVICE:
                topProgressView.showTopProgressView(getString(R.string.un_connect_tip), -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String braceletName = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
                        if (braceletName != null) {
                            Intent intent = new Intent(getActivity(), MyDeviceActivity.class);
                            intent.putExtra(MyDeviceActivity.BRACELETTYPE, braceletName);
                            intent.putExtra(MyDeviceActivity.BLUESTATUSFROMPROMAIN,
                                    ICSOpenVPNApplication.bleStatusEntity.getStatus());
                            startActivity(intent);
                        }
                    }
                });
                break;
        }

    }

    public   void topProgressGone() {
        topProgressView.setVisibility(View.GONE);
        topProgressView.setProgress(0);
    }

    public void setRegisted(boolean isRegister){

    }

   /* public void showDeviceSummarized(boolean isRegister){

    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)//ui线程
    public void showDevice(ShowDeviceEntity entity) {

       // showDeviceSummarized(entity.isShowDevice());

        if(!entity.isShowDevice()){
            setRegisted(false);
            if (!ICSOpenVPNApplication.isConnect)
                topProgressGone();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)//ui线程
    public void onIsSuccessEntity(SimRegisterStatue entity) {
        switch (entity.getRigsterSimStatue()) {
            case SocketConstant.REGISTER_SUCCESS:
                topProgressGone();
                setRegisted(true);
                break;
            case SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA:

                topProgressGone();
                break;
            case SocketConstant.REGISTER_FAIL:

                topProgressGone();
                break;
            case SocketConstant.REGISTER_FAIL_IMSI_IS_NULL:

                topProgressGone();
                break;
            case SocketConstant.REGISTER_FAIL_IMSI_IS_ERROR:
                topProgressGone();
                break;
            case SocketConstant.REGISTER_CHANGING:
                double percent = entity.getProgressCount();
                if (topProgressView.getVisibility() != View.VISIBLE && SocketConstant.REGISTER_STATUE_CODE != 3) {
                    topProgressView.setVisibility(View.VISIBLE);
                    topProgressView.setContent(getString(R.string.registing));
                    topProgressView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String braceletName = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
                            if (braceletName != null) {
                                Intent intent = new Intent(getActivity(), MyDeviceActivity.class);
                                intent.putExtra(MyDeviceActivity.BRACELETTYPE, braceletName);
                                startActivity(intent);
                            }
                        }
                    });
                }
                int percentInt = (int) (percent / 1.6);
                if (percentInt >= 100) {
                    percentInt = 98;
                }
                topProgressView.setProgress(percentInt);
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
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
        }
    }

    protected void setTopViewBackground(int colorId){
        topProgressView.setBackgroundResource(colorId);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
