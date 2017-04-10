package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CallDetailActivity extends BaseNetActivity implements XRecyclerView.LoadingListener {
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.group_name_tv)
    TextView groupNameTv;
    @BindView(R.id.phone_name_tv)
    TextView phoneNameTv;
    @BindView(R.id.last_call_time_tv)
    TextView lastCallTimeTv;
    @BindView(R.id.sms_tv)
    TextView smsTv;
    @BindView(R.id.net_call_tv)
    TextView netCallTv;
    @BindView(R.id.dual_standby_king_tv)
    TextView dualStandbyKingTv;
    @BindView(R.id.defriend_tv)
    TextView defriendTv;
    @BindView(R.id.call_record_rv)
    XRecyclerView callRecordRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_detail);
        ButterKnife.bind(this);

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
