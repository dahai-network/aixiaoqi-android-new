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
import static de.blinkt.openvpn.constant.Constant.isOutsideThirdStepClick;
import static de.blinkt.openvpn.constant.UmengContant.CLICKOPENSYSTEMSET;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class OutsideThirdStepActivity extends BaseActivity {
    @BindView(R.id.outside_step_third_two_content_tv)
    TextView outsideStepThirdTwoContentTv;
    @BindView(R.id.outside_item03_down)
    ImageView outsideItem03Down;
    @BindView(R.id.outside_step_third_bt)
    Button outsideStepThirdBt;
    @BindView(R.id.activateTextView)
    Button activateTextView;
    @BindView(R.id.outside_item03_up)
    ImageView outsideItem03Up;
    @BindView(R.id.outside_step_third_content_tv)
    TextView outsideStepThirdContentTv;
    private String statuString;
    boolean isSupport4G;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_item03);
        ButterKnife.bind(this);
        hasAllViewTitle(R.string.outside_use_guide, R.string.close, R.string.last_step, false);
        initData();
        if (!isOutsideThirdStepClick) {
            activateTextView.setBackgroundResource(R.drawable.circle_gray_ret);
            activateTextView.setEnabled(false);
        }
        if (!isSupport4G) {
            outsideItem03Down.setVisibility(View.GONE);
            outsideStepThirdTwoContentTv.setVisibility(View.GONE);
        }

        if (!IntentPutKeyConstant.OUTSIDE.equals(statuString)) {
            outsideItem03Up.setImageResource(R.drawable.close_net);
            outsideStepThirdContentTv.setText(getString(R.string.inland_step_third_content));
            outsideItem03Down.setVisibility(View.GONE);
            activateTextView.setText(getString(R.string.complete));
            outsideStepThirdTwoContentTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onClickRightView() {
        setResult(200);
        onBackPressed();
    }

    @OnClick({R.id.outside_step_third_bt, R.id.activateTextView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.outside_step_third_bt:
                isOutsideThirdStepClick = true;
                MobclickAgent.onEvent(context, CLICKOPENSYSTEMSET);
                try {
                    startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    CommonTools.showShortToast(this, getString(R.string.not_suppert_open_way));
                }
                break;
            case R.id.activateTextView:
                if (IntentPutKeyConstant.OUTSIDE.equals(statuString)) {
                    startActivityForResult(new Intent(this, OutsideFourStepActivity.class), 100);
                } else {
                    setResult(200);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        e("requestCode=" + requestCode + "  ,resultCode=" + resultCode);
        switch (resultCode) {
            case 200:
                setResult(200);
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isOutsideThirdStepClick) {
            activateTextView.setEnabled(true);
            activateTextView.setBackgroundResource(R.drawable.green_btn_click);
        }
    }

    private void initData() {
        statuString = getIntent().getStringExtra(IntentPutKeyConstant.OUTSIDE);
        isSupport4G = getIntent().getBooleanExtra(IntentPutKeyConstant.IS_SUPPORT_4G, false);
    }
}
