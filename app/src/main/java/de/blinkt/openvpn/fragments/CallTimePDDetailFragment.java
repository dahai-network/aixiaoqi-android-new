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
    Unbinder unbinder;
    String features;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.packagedetail_layout, null);
        tvContext = (TextView) view.findViewById(R.id.tv_context);
        unbinder = ButterKnife.bind(this, view);
        String features = SharedUtils.getInstance().readString(Constant.CALLTIME_FEATURES_SIGN);
        tvContext.setText(features);

        return view;
    }


    @Override
    protected void lazyLoad() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

/*    @Subscribe
    public void getData(EvenBusSign s) {

        String features = SharedUtils.getInstance().readString(Constant.CALLTIME_FEATURES_SIGN);
        if (tvContext.getVisibility() == View.VISIBLE)
            tvContext.setText(s.getFeatures());

    }*/

}
