package de.blinkt.openvpn.activities.SetFlowCard;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.util.CommonTools;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.Constant.isOutsideSecondStepClick;
import static de.blinkt.openvpn.constant.UmengContant.CLICKOPENAPNSET;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class OutsideSecondStepActivity extends BaseActivity {

    @BindView(R.id.outside_step_second_content_tv)
    TextView outsideStepSecondContentTv;
    @BindView(R.id.outside_item02_iv)
    ImageView outsideItem02Iv;
    @BindView(R.id.outside_step_second_bt)
    Button outsideStepSecondBt;
    @BindView(R.id.activateTextView)
    Button activateTextView;
    @BindView(R.id.outside_step_second_tv)
    TextView outsideStepSecondTv;
    private String statuString;
    boolean isSupport4G;
    private String apnName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_item02);
        ButterKnife.bind(this);
        initData();
        hasAllViewTitle(R.string.outside_use_guide,R.string.close, R.string.last_step,false);
        if (!isOutsideSecondStepClick) {
            activateTextView.setBackgroundResource(R.drawable.circle_gray_ret);
            activateTextView.setEnabled(false);
        }
        if(IntentPutKeyConstant.OUTSIDE.equals(statuString)){
            outsideStepSecondContentTv.setText(String.format(getString(R.string.outside_step_second_content), apnName));
            if ("263".equals(apnName)) {
                outsideItem02Iv.setImageResource(R.drawable.outside_item02);
            } else if ("263.cs101".equals(apnName)) {
                outsideItem02Iv.setImageResource(R.drawable.outside_seconde_apn_263cs101);
            } else if ("3gnet".equals(apnName)) {
                outsideItem02Iv.setImageResource(R.drawable.outside_second_apn_3gnet);
            }
        }else{
            outsideStepSecondTv.setText(getString(R.string.inland_step_second));
            outsideStepSecondContentTv.setText(String.format(getString(R.string.inland_step_second_content), apnName));
            outsideItem02Iv.setImageResource(R.drawable.inland_item02);

        }

    }

    @Override
    protected void onClickRightView() {
        setResult(200);
        onBackPressed();
    }

    @OnClick({R.id.outside_step_second_bt, R.id.activateTextView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.outside_step_second_bt:
                isOutsideSecondStepClick = true;
                MobclickAgent.onEvent(context, CLICKOPENAPNSET);
                try {
                    startActivity(new Intent(Settings.ACTION_APN_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    CommonTools.delayTime(100);
                    canClickNext();
                    CommonTools.showShortToast(this, getString(R.string.not_suppert_open_way));
                }

                break;
            case R.id.activateTextView:
                startActivityForResult(new Intent(this, OutsideThirdStepActivity.class)
                                .putExtra(IntentPutKeyConstant.OUTSIDE, statuString)
                                .putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, isSupport4G)
                                .putExtra(IntentPutKeyConstant.APN_NAME, apnName)
                        ,100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 200:
                setResult(200);
                onBackPressed();
                break;
        }
    }

    private void initData() {
        statuString = getIntent().getStringExtra(IntentPutKeyConstant.OUTSIDE);
        isSupport4G = getIntent().getBooleanExtra(IntentPutKeyConstant.IS_SUPPORT_4G, false);
        apnName = getIntent().getStringExtra(IntentPutKeyConstant.APN_NAME);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isOutsideSecondStepClick) {
            canClickNext();
        }
    }

    private void canClickNext() {
        activateTextView.setEnabled(true);
        activateTextView.setBackgroundResource(R.drawable.green_btn_click);
    }
}
