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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.fragments.base.BaseFragment;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by kim
 * on 2017/4/11.
 * 套餐详情
 */
public class PackageDetailsFragment extends BaseFragment {

    @BindView(R.id.tv_context)
    TextView tvContext;
    Unbinder unbinder;
    String detail;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (tvContext != null)
                tvContext.setText(detail);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.packagedetail_layout, null);
        tvContext = (TextView) view.findViewById(R.id.tv_context);
        unbinder = ButterKnife.bind(this, view);
        setView();
        return view;
    }

    /**
     * 设置界面
     */
    private void setView() {
        detail = SharedUtils.getInstance().readString(Constant.DETAIL_SIGN);
        if (null != detail && !detail.equals("")) {
            tvContext.setText(detail);
        } else {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    detail = intent.getStringExtra(Constant.DETAIL_SIGN);
                    if (detail != null) {
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }, new IntentFilter(Constant.LOCALBROADCAST_INTENT_DATA));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
