package de.blinkt.openvpn.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.fragments.base.BaseFragment;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by kim
 * on 2017/4/11.
 * 支付条款
 */
public class PaymentTermFragment extends BaseFragment {
    TextView tvContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.packagedetail_layout, null);
        tvContext = (TextView) view.findViewById(R.id.tv_context);
        String detail = SharedUtils.getInstance().readString(Constant.PAYTERMS_SIGN);
        if (null != detail&&!detail.equals(""))
            tvContext.setText(detail);
        return view;
    }
}
