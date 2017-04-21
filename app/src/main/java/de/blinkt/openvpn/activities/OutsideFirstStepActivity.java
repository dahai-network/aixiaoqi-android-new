package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    @BindView(R.id.outside_step_second_content_tv)
    TextView outsideStepSecondContentTv;
    @BindView(R.id.outside_item01_up)
    ImageView outsideItem01Up;
    @BindView(R.id.outside_item01_down)
    ImageView outsideItem01Down;
    private String statuString;
    private String apnName;
    boolean isSupport4G;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_item01);
        ButterKnife.bind(this);
        initData();
        hasLeftViewTitle(R.string.outside_use_guide, 0);
        if(!IntentPutKeyConstant.OUTSIDE.equals(statuString)){
            outsideItem01Up.setImageResource(R.drawable.inland_item01);
            outsideItem01Down.setVisibility(View.GONE);
            outsideStepSecondContentTv.setText(getString(R.string.inland_step_one_content));
        }
    }

    @OnClick(R.id.activateTextView)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activateTextView:
                startActivityForResult(new Intent(this, OutsideSecondStepActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE, statuString)
                        .putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, isSupport4G)
                        .putExtra(IntentPutKeyConstant.APN_NAME, apnName)
               ,100 );
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        e("requestCode="+requestCode+"  ,resultCode="+resultCode);
        switch (resultCode){
            case 200:
                finish();
                break;
        }
    }
    private void initData() {

        statuString = getIntent().getStringExtra(IntentPutKeyConstant.OUTSIDE);
        isSupport4G = getIntent().getBooleanExtra(IntentPutKeyConstant.IS_SUPPORT_4G, false);
        apnName = getIntent().getStringExtra(IntentPutKeyConstant.APN_NAME);
    }
}
