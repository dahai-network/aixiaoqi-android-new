package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class OutsideFourStepActivity extends BaseActivity {
    @BindView(R.id.outside_step_four_bt)
    Button outsideStepFourBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_item04);
        ButterKnife.bind(this);
        hasLeftViewTitle(R.string.outside_use_guide,0);
    }

    @OnClick(R.id.outside_step_four_bt)
    public void onClick() {

    }
}
