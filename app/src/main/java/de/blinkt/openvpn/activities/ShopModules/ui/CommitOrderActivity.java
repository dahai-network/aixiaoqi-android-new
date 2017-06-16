package de.blinkt.openvpn.activities.ShopModules.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.ShopModules.presenter.CommitOrderPresenter;
import de.blinkt.openvpn.activities.ShopModules.view.CommitOrderView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.OrderAddHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;

import static de.blinkt.openvpn.constant.UmengContant.CLICKSUREPAGMENT;

/**
 * 提交订单界面
 */
public class CommitOrderActivity extends BaseActivity implements CommitOrderView {

    @BindView(R.id.countryImageView)
    ImageView countryImageView;
    @BindView(R.id.packageNameTextView)
    TextView packageNameTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.packetCountTextView)
    TextView packetCountTextView;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView(R.id.buyDaysTextView)
    TextView buyDaysTextView;
    @BindView(R.id.reduceImageView)
    ImageView reduceImageView;
    @BindView(R.id.myPackageLinearLayout)
    LinearLayout myPackageLinearLayout;
    @BindView(R.id.unitePriceTextView)
    TextView unitePriceTextView;
    @BindView(R.id.myDeviceLinearLayout)
    LinearLayout myDeviceLinearLayout;
    @BindView(R.id.totalPriceTextView)
    TextView totalPriceTextView;
    @BindView(R.id.myMessageLinearLayout)
    LinearLayout myMessageLinearLayout;
    @BindView(R.id.weixinPayCheckBox)
    CheckBox weixinPayCheckBox;
    @BindView(R.id.weixinPayLienarLayout)
    RelativeLayout weixinPayLienarLayout;
    @BindView(R.id.aliPayCheckBox)
    CheckBox aliPayCheckBox;
    @BindView(R.id.aliPayLienarLayout)
    RelativeLayout aliPayLienarLayout;
    @BindView(R.id.addUpTextView)
    TextView addUpTextView;
    @BindView(R.id.sureTextView)
    TextView sureTextView;
    @BindView(R.id.balanceTextView)
    TextView balanceTextView;
    @BindView(R.id.balancePayCheckBox)
    CheckBox balancePayCheckBox;
    @BindView(R.id.balancePayLienarLayout)
    LinearLayout balancePayLienarLayout;
    @BindView(R.id.pricell)
    LinearLayout pricell;
    private PacketDtailEntity.ListBean bean;
    private int packetCount = 1;
    private int BALANCE_PAY_METHOD = 3;
    private int WEIXIN_PAY_METHOD = 2;
    private int ALI_PAY_METHOD = 1;
    private boolean isAliPayClick = false;
    //微信支付类
    private IWXAPI api;

    //余额
    private float balanceFloat;
    public CommitOrderPresenter commitOrderPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_order);
        ICSOpenVPNApplication.commitOrderActivity = this;
        ButterKnife.bind(this);
        initSet();
        commitOrderPresenter = new CommitOrderPresenter(this) {

            @Override
            public void getBalance(BalanceHttp http) {
                balanceFloat = http.getBalanceEntity().getAmount();
                checkBalance();
            }
        };
    }

    public static void launch(Context context, PacketDtailEntity.ListBean bean, int type) {
        Intent intent = new Intent(context, CommitOrderActivity.class);
        intent.putExtra("order", bean);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }


    private void initSet() {
        bean = (PacketDtailEntity.ListBean) getIntent().getSerializableExtra("order");
        int type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            pricell.setVisibility(View.GONE);
            dateTextView.setVisibility(View.INVISIBLE);
        } else if (type == 1) {
            pricell.setVisibility(View.VISIBLE);
            dateTextView.setVisibility(View.VISIBLE);
        }
        hasLeftViewTitle(R.string.commit_order, 0);
        packageNameTextView.setText(bean.getPackageName());
        priceTextView.setText("￥" + bean.getPrice());
        unitePriceTextView.setText("￥" + bean.getPrice());
        totalPriceTextView.setText("￥" + bean.getPrice());
        addUpTextView.setText("￥" + bean.getPrice());
        dateTextView.setText("最晚激活日期：" + DateUtils.getAdd180DayDate());
        Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(countryImageView);
        setSpan(addUpTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询余额
        commitOrderPresenter.checkNetBalance();
        dismissProgress();
    }

    public void resetCount() {
        buyDaysTextView.setText("1");
        packetCount = 1;
        String unitPriceStr = unitePriceTextView.getText().toString();
        totalPriceTextView.setText(unitPriceStr);
        addUpTextView.setText(unitPriceStr);
        setSpan(addUpTextView);
    }

    @OnClick({R.id.addImageView, R.id.reduceImageView, R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout, R.id.sureTextView, R.id.balancePayLienarLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addImageView:
                if (packetCount < Constant.LIMIT_COUNT) {
                    buyDaysTextView.setText("" + (++packetCount));
                    totalPriceTextView.setText("￥" + bean.getPrice() * packetCount);
                    addUpTextView.setText("￥" + bean.getPrice() * packetCount);
                    setSpan(addUpTextView);
                    checkBalance();
                }
                break;
            case R.id.reduceImageView:
                //数量限制在30个
                if (packetCount > 1) {
                    buyDaysTextView.setText("" + (--packetCount));
                    totalPriceTextView.setText("￥" + bean.getPrice() * packetCount);
                    addUpTextView.setText("￥" + bean.getPrice() * packetCount);
                    setSpan(addUpTextView);
                    checkBalance();
                }
                break;
            case R.id.balancePayLienarLayout:
                weixinPayCheckBox.setChecked(false);
                aliPayCheckBox.setChecked(false);
                isAliPayClick = false;
                balancePayCheckBox.setChecked(true);
                break;
            case R.id.weixinPayLienarLayout:
                balancePayCheckBox.setChecked(false);
                aliPayCheckBox.setChecked(false);
                isAliPayClick = false;
                weixinPayCheckBox.setChecked(true);
                break;
            case R.id.aliPayLienarLayout:
                balancePayCheckBox.setChecked(false);
                weixinPayCheckBox.setChecked(false);
                isAliPayClick = true;
                aliPayCheckBox.setChecked(true);
                break;
            case R.id.sureTextView:
                HashMap<String, String> map = new HashMap<>();
                OrderAddHttp http;
                sureTextView.setEnabled(false);
                if (weixinPayCheckBox.isChecked()) {
                    if (isWXAppInstalledAndSupported()) {
                        map.put("type", WEIXIN_PAY_METHOD + "");
                        //友盟方法统计
                        MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
                        commitOrderPresenter.commitOrder(bean.getPackageId(), packetCount + "", WEIXIN_PAY_METHOD + "");
                    } else {
                        CommonTools.showShortToast(this, getResources().getString(R.string.no_weixin_yet));
                        sureTextView.setEnabled(true);
                    }
                } else if (aliPayCheckBox.isChecked()) {
                    map.put("type", ALI_PAY_METHOD + "");
                    //友盟方法统计
                    MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
                    commitOrderPresenter.commitOrder(bean.getPackageId(), packetCount + "", ALI_PAY_METHOD + "");
                } else {
                    Log.d("CommitOrderActivity", "sureTextView: "+bean.getPackageId()+""+packetCount );
                    map.put("type", BALANCE_PAY_METHOD + "");
                    //友盟方法统计
                    MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
                    commitOrderPresenter.commitOrder(bean.getPackageId(), packetCount + "", BALANCE_PAY_METHOD + "");
                }
                break;
        }
    }

    private boolean isWXAppInstalledAndSupported() {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp(Constant.WEIXIN_APPID);
        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();
        return sIsWXAppInstalledAndSupported;
    }

    //设置大小字体
    public void setSpan(TextView textview) {
        Spannable WordtoSpan = new SpannableString(textview.getText().toString());
        int intLength = String.valueOf((int) (bean.getPrice() * packetCount)).length();
        WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
    }

    //设置余额span
    private void setBalanceSpan(TextView balanceTextView, float balanceFloat) {
        Spannable WordtoSpan = new SpannableString(balanceTextView.getText().toString());
        WordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.order_detail_orange)), 7, 8 + (balanceFloat + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        balanceTextView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
    }

    private void checkBalance() {
        if (weixinPayCheckBox.isChecked()) {
            showBalanceCheckBox();
            return;
        }
        if (aliPayCheckBox.isChecked() && isAliPayClick) {
            showBalanceCheckBox();
            return;
        }
        if (bean.getPrice() * packetCount < balanceFloat) {
            balanceTextView.setText(getResources().getString(R.string.balance_pay) + "(剩余￥" + balanceFloat + ")");
            balanceTextView.setEnabled(true);
            setBalanceSpan(balanceTextView, balanceFloat);
            balancePayCheckBox.setVisibility(View.VISIBLE);
            balancePayCheckBox.setChecked(true);
            balancePayLienarLayout.setEnabled(true);
            aliPayCheckBox.setChecked(false);
        } else {
            balancePayCheckBox.setChecked(false);
            balanceTextView.setEnabled(false);
            balancePayCheckBox.setVisibility(View.GONE);
            balanceTextView.setText(getResources().getString(R.string.not_enough_balance));
            balancePayLienarLayout.setEnabled(false);
            aliPayCheckBox.setChecked(true);
        }
    }

    private void showBalanceCheckBox() {
        if (bean.getPrice() * packetCount < balanceFloat) {
            balanceTextView.setText(getResources().getString(R.string.balance_pay) + "(剩余￥" + balanceFloat + ")");
            balanceTextView.setEnabled(true);
            balancePayCheckBox.setVisibility(View.VISIBLE);
            balancePayLienarLayout.setEnabled(true);
        } else {
            balanceTextView.setEnabled(false);
            balancePayCheckBox.setVisibility(View.GONE);
            balanceTextView.setText(getResources().getString(R.string.not_enough_balance));
            balancePayLienarLayout.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        commitOrderPresenter.releaseResouce();
    }

    @Override
    public CheckBox getAliPayCheckBox() {
        return aliPayCheckBox;
    }

    @Override
    public CheckBox getWeixinPayCheckBox() {
        return weixinPayCheckBox;
    }

    @Override
    public void playShowView() {
        sureTextView.setEnabled(true);
    }

    @Override
    public void resetCountPresenter() {
        resetCount();
    }

    @Override
    public void showToast(String msg) {
        CommonTools.showShortToast(CommitOrderActivity.this, msg);
    }
}
