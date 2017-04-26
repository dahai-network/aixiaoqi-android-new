package de.blinkt.openvpn.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseFragment;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by kim
 * on 2017/4/14.
 * Call time 套餐详细 Fragement
 */

public class CallTimePDDetailFragment extends BaseFragment {
    TextView tvContext;
    String features;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.packagedetail_layout, null);
        tvContext = (TextView) view.findViewById(R.id.tv_context);
        String features = SharedUtils.getInstance().readString(Constant.CALLTIME_FEATURES_SIGN);
        if (features != null)
            tvContext.setText(features);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}
