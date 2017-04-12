package de.blinkt.openvpn.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseFragment;
import de.blinkt.openvpn.constant.Constant;

/**
 * Created by Administrator on 2017/4/11.
 */
public class ProductFeatureFragment extends BaseFragment {
    TextView tvContext;
    String features;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvContext.setText(features);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.packagedetail_layout, null);

        tvContext = (TextView) view.findViewById(R.id.tv_context);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                features = intent.getStringExtra(Constant.FEATURES_SIGN);
                if (features != null) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        }, new IntentFilter(Constant.LOCALBROADCAST_INTENT_DATA));

        return view;
    }

    @Override
    protected void lazyLoad() {

    }
}
