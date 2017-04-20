package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class OutsideFirstStepActivity extends BaseActivity {

    @BindView(R.id.activateTextView)
    Button activateTextView;
    private String statuString;
    private String apnName;
    boolean isSupport4G;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_item01);
        ButterKnife.bind(this);
        initData();
        hasLeftViewTitle(R.string.outside_use_guide,0);
    }
    @OnClick(R.id.activateTextView)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activateTextView:
                toActivity(new Intent(this,OutsideSecondStepActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE, statuString)
                        .putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, isSupport4G)
                        .putExtra(IntentPutKeyConstant.APN_NAME,apnName)
                );
                break;
        }
    }

    private void initData(){

        statuString = getIntent().getStringExtra(IntentPutKeyConstant.OUTSIDE);
        isSupport4G=getIntent().getBooleanExtra(IntentPutKeyConstant.IS_SUPPORT_4G,false);
        apnName=getIntent().getStringExtra(IntentPutKeyConstant.APN_NAME);
    }
}
