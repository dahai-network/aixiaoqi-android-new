package de.blinkt.openvpn.activities.MyModules.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.MyModules.presenter.RechargePresenter;
import de.blinkt.openvpn.activities.MyModules.view.RechargeView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.ViewUtil;
import de.blinkt.openvpn.views.RadioGroup;
import static de.blinkt.openvpn.constant.UmengContant.CHARGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBINDCHARGECARD;

public class RechargeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, RechargeView {

    @BindView(R.id.amountEditText)
    EditText amountEditText;
    @BindView(R.id.ll_edit)
    LinearLayout llEditText;
    @BindView(R.id.weixinPayCheckBox)
    CheckBox weixinPayCheckBox;
    @BindView(R.id.weixinPayLienarLayout)
    RelativeLayout weixinPayLienarLayout;
    @BindView(R.id.aliPayCheckBox)
    CheckBox aliPayCheckBox;
    @BindView(R.id.aliPayLienarLayout)
    RelativeLayout aliPayLienarLayout;
    @BindView(R.id.nextBtn)
    Button nextBtn;
    @BindView(R.id.amountTextView)
    TextView amountTextView;
    @BindView(R.id.moneyRadioGroup)
    RadioGroup moneyRadioGroup;
    private int WEIXIN_PAY = 2;
    private int ALI_PAY = 1;
    private float moneyAmount = 100;
    RechargePresenter rechargePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        ICSOpenVPNApplication.rechargeActivity = this;
        initSet();

        rechargePresenter = new RechargePresenter(this);
    }


    private void initSet() {
        hasLeftViewTitle(R.string.recharge, 0);
        new ExditTextWatcher(amountEditText, R.id.amountEditText) {
            @Override
            public void textChanged(CharSequence s, int id) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        moneyAmount = Float.valueOf(s.toString());
                        amountEditText.setText(s);
                        amountEditText.setSelection(s.length());
                        return;
                    }
                }
                if (s.toString().trim().startsWith(".")) {
                    s = "0" + s;
                    moneyAmount = Float.valueOf(s.toString());
                    amountEditText.setText(s);
                    amountEditText.setSelection(2);
                    return;
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        moneyAmount = Float.valueOf(s.toString());
                        amountEditText.setText(s.subSequence(0, 1));
                        amountEditText.setSelection(1);
                        return;
                    }
                }

                if (!TextUtils.isEmpty(s.toString()) && !s.toString().startsWith("0")) {
                    moneyAmount = Float.valueOf(s.toString());
                    if (moneyAmount > 5000) {
                        moneyAmount = 5000;
                        amountEditText.setText(5000 + "");
                        amountEditText.setSelection(4);
                        CommonTools.showShortToast(RechargeActivity.this, getResources().getString(R.string.max_recharge_amount));
                    }
                }
                if (!TextUtils.isEmpty(s.toString())) {
                    moneyAmount = Float.valueOf(s.toString());
                }
            }
        };
        moneyRadioGroup.setOnCheckedChangeListener(this);

    }
    @OnClick({R.id.nextBtn, R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout, R.id.recharge_card})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBtn:
                //友盟方法统计
                HashMap<String, String> map = new HashMap<>();
                if (CommonTools.isFastDoubleClick(1000)) {
                    return;
                }
                nextBtn.setEnabled(false);
                if (aliPayCheckBox.isChecked()) {
                    map.put("type", ALI_PAY + "");
                    MobclickAgent.onEvent(this, CHARGE, map);
                    showProgress(getString(R.string.ali_paying), true);
                    pay(ALI_PAY);
                } else {
                    if (isWXAppInstalledAndSupported()) {
                        map.put("type", WEIXIN_PAY + "");
                        MobclickAgent.onEvent(this, CHARGE, map);
                        showProgress(getString(R.string.weixin_paying), true);
                        pay(WEIXIN_PAY);
                    } else {
                        nextBtn.setEnabled(true);
                        CommonTools.showShortToast(this, getResources().getString(R.string.no_weixin_yet));
                    }
                }

                break;
            case R.id.weixinPayLienarLayout:
                weixinPayCheckBox.setChecked(true);
                aliPayCheckBox.setChecked(false);
                break;
            case R.id.aliPayLienarLayout:
                weixinPayCheckBox.setChecked(false);
                aliPayCheckBox.setChecked(true);
                break;
            case R.id.recharge_card:
                MobclickAgent.onEvent(this, CLICKBINDCHARGECARD);
                BindRechargeCardActivity.launch(RechargeActivity.this, BindRechargeCardActivity.RECHARGE);
                break;
        }
    }

    private void pay(int payWay) {
        //充值余额
        rechargePresenter.rechargeBalance(moneyAmount, payWay);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton moneyButton = (RadioButton) RechargeActivity.this.findViewById(checkedId);
        if (checkedId != R.id.recharge1000Button) {
            (RechargeActivity.this.findViewById(R.id.recharge1000Button)).setVisibility(View.VISIBLE);
            amountEditText.setText("");
            amountEditText.setVisibility(View.GONE);
            moneyAmount = Float.valueOf(moneyButton.getText().toString());
            ViewUtil.hideSoftKeyboard(this);
        } else {
            moneyAmount = 0;
            (RechargeActivity.this.findViewById(R.id.recharge1000Button)).setVisibility(View.INVISIBLE);
            amountEditText.setVisibility(View.VISIBLE);
            amountEditText.setFocusable(true);
            amountEditText.setFocusableInTouchMode(true);
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(amountEditText, 0);
        }
    }

    private boolean isWXAppInstalledAndSupported() {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp(Constant.WEIXIN_APPID);
        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();
        return sIsWXAppInstalledAndSupported;
    }

    @Override
    protected void onResume() {
        super.onResume();
        dismissProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rechargePresenter.releaseResource();
        ICSOpenVPNApplication.rechargeActivity = null;
    }

    @Override
    public Button getNextBtn() {
        return nextBtn;
    }

    @Override
    public void showToast(String msg) {
        CommonTools.showShortToast(RechargeActivity.this, msg);
    }
}
