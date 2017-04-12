package de.blinkt.openvpn.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kim
 * on 2017/4/11.
 */
public class PackageDetailsFragment extends BaseFragment {

    @BindView(R.id.tv_context)
    TextView tvContext;
    Unbinder unbinder;
    String detail;
    SharedPreferences pref;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvContext.setText(detail);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.packagedetail_layout, null);
        initData();
        tvContext = (TextView) view.findViewById(R.id.tv_context);
        unbinder = ButterKnife.bind(this, view);
        setView();
        return view;
    }
    /**
     * 设置界面
     */
    private void setView() {
        detail = pref.getString("detail", null);
        if (null != detail) {
            tvContext.setText(detail);
        } else {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    PackageDetailsFragment.this.detail = intent.getStringExtra("detail");
                    if (PackageDetailsFragment.this.detail != null) {
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }, new IntentFilter("net_data"));
        }
    }
    private void initData() {
        pref = getActivity().getSharedPreferences("detail_data", MODE_PRIVATE);


    }
    @Override
    protected void lazyLoad() {
        // TODO Auto-generated method stub

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
