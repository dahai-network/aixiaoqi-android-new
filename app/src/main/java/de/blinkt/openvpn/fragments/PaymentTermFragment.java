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

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kim
 * on 2017/4/11.
 */
public class PaymentTermFragment extends BaseFragment {
    TextView tvContext;
    SharedPreferences pref;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.packagedetail_layout, null);

        tvContext = (TextView) view.findViewById(R.id.tv_context);
        initData();
        String detail = pref.getString("payterms", null);
        if (null != detail)
            tvContext.setText(detail);
        return view;
    }

    private void initData() {
        pref = getActivity().getSharedPreferences("detail_data", MODE_PRIVATE);
    }

    @Override
    protected void lazyLoad() {

    }
}
