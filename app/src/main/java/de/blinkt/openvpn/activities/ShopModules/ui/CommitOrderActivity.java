package de.blinkt.openvpn.activities.ShopModules.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
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
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.OrderAddHttp;
import de.blinkt.openvpn.model.PacketDtailEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogYearMonthDayPicker;

import static de.blinkt.openvpn.constant.UmengContant.CLICKSUREPAGMENT;

/**
 * 提交订单界面
 */
public class CommitOrderActivity extends BaseActivity implements CommitOrderView , DialogInterfaceTypeBase {

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
    @BindView(R.id.orderOriginalPriceTextView)
    TextView orderOriginalPriceTextView;
    @BindView(R.id.payForWhatTextView)
    TextView payForWhatTextView;
    @BindView(R.id.payWayTextView)
    TextView payWayTextView;
    @BindView(R.id.ll_select_date)
    LinearLayout llSelectDate;
    String dataTime;
    private PacketDtailEntity.ListBean bean;
    private int packetCount = 1;
    private int BALANCE_PAY_METHOD = 3;
    private int WEIXIN_PAY_METHOD = 2;
    private int ALI_PAY_METHOD = 1;
    private boolean isAliPayClick = false;
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

    public static void launch(Context context, PacketDtailEntity.ListBean bean) {
        Intent intent = new Intent(context, CommitOrderActivity.class);
        intent.putExtra("order", bean);
        context.startActivity(intent);
    }


    private void initSet() {
        bean = (PacketDtailEntity.ListBean) getIntent().getSerializableExtra("order");
        if (!bean.isCanBuyMultiple()) {
            pricell.setVisibility(View.GONE);
            dateTextView.setVisibility(View.INVISIBLE);
        } else {
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
        if (TextUtils.isEmpty(bean.getOriginalPrice())) {
            orderOriginalPriceTextView.setVisibility(View.GONE);
        } else {
            orderOriginalPriceTextView.setVisibility(View.VISIBLE);
            orderOriginalPriceTextView.setText(getString(R.string.original_price) + getString(R.string.money_type) + bean.getOriginalPrice());
            orderOriginalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        }
        Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(countryImageView);
        setSpan(addUpTextView);
        setSpan(priceTextView);
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

    private void setCheck(boolean weixinCheckBox, boolean aliCheckBox, boolean balanceCheckBox, boolean isAliPay) {
        weixinPayCheckBox.setChecked(weixinCheckBox);
        aliPayCheckBox.setChecked(aliCheckBox);
        isAliPayClick = isAliPay;
        balancePayCheckBox.setChecked(balanceCheckBox);
    }

    @OnClick({R.id.addImageView, R.id.reduceImageView, R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout, R.id.sureTextView, R.id.balancePayLienarLayout,R.id.ll_select_date})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addImageView:
                if (packetCount < Constant.LIMIT_COUNT) {
                    buyDaysTextView.setText("" + (++packetCount));
                    packCount();
                }
                break;
            case R.id.reduceImageView:
                //数量限制在30个
                if (packetCount > 1) {
                    buyDaysTextView.setText("" + (--packetCount));
                    packCount();
                }
                break;
            case R.id.balancePayLienarLayout:
                setCheck(false, false, true, false);
                break;
            case R.id.weixinPayLienarLayout:
                setCheck(true, false, false, false);
                break;
            case R.id.aliPayLienarLayout:
                setCheck(false, true, false, true);
                break;
            case R.id.ll_select_date:
                DialogYearMonthDayPicker dialogYearMonthDayPicker = new DialogYearMonthDayPicker(this, this, R.layout.picker_year_month_day_layout, 0);
                dialogYearMonthDayPicker.changeText(getString(R.string.select_time) + "(" + bean.getCountryName() + ")");
                break;
            case R.id.sureTextView:
                if (noSelectTime()) return;
                HashMap<String, String> map = new HashMap<>();
                sureTextView.setEnabled(false);
                if (weixinPayCheckBox.isChecked()) {
                    if (isWXAppInstalledAndSupported()) {
                        map.put("type", WEIXIN_PAY_METHOD + "");
                        //友盟方法统计
                        MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
                        commitOrderPresenter.commitOrder(bean.getPackageId(), packetCount + "", WEIXIN_PAY_METHOD + "",dataTime);
                    } else {
                        CommonTools.showShortToast(this, getResources().getString(R.string.no_weixin_yet));
                        sureTextView.setEnabled(true);
                    }
                } else if (aliPayCheckBox.isChecked()) {
                    map.put("type", ALI_PAY_METHOD + "");
                    //友盟方法统计
                    MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
                    commitOrderPresenter.commitOrder(bean.getPackageId(), packetCount + "", ALI_PAY_METHOD + "",dataTime);
                } else {
                    Log.d("CommitOrderActivity", "sureTextView: " + bean.getPackageId() + "" + packetCount);
                    map.put("type", BALANCE_PAY_METHOD + "");
                    //友盟方法统计
                    MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);

                    commitOrderPresenter.commitOrder(bean.getPackageId(), packetCount + "", BALANCE_PAY_METHOD + "",dataTime);
                }
                break;
        }
    }

    private boolean noSelectTime() {
        if(TextUtils.isEmpty(dataTime)){
            CommonTools.showShortToast(this,getString(R.string.effective_date_is_null));
            return true;
        }
        return false;
    }

    private void packCount() {
        totalPriceTextView.setText("￥" + bean.getPrice() * packetCount);
        addUpTextView.setText("￥" + bean.getPrice() * packetCount);
        setSpan(addUpTextView);
        checkBalance();
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


    private void checkBalance() {
        if (weixinPayCheckBox.isChecked()) {
            showBalanceCheckBox();
            return;
        }
        if (aliPayCheckBox.isChecked() && isAliPayClick) {
            showBalanceCheckBox();
            return;
        }
        if (bean.getPrice() * packetCount<= balanceFloat) {
            //setBalanceSpan(balanceTextView, balanceFloat);
            //setSpan(balanceTextView);
            balancePayCheckBox.setChecked(true);
            aliPayCheckBox.setChecked(false);
            setBalanceCheckBox(getResources().getString(R.string.balance_pay) + "(剩余￥" + balanceFloat + ")", true, View.VISIBLE, true);
        } else {
            balancePayCheckBox.setChecked(false);
            setBalanceCheckBox(getResources().getString(R.string.not_enough_balance), false, View.GONE, false);
            aliPayCheckBox.setChecked(true);
        }
    }

    private void setBalanceCheckBox(String balanceText, boolean balanceTextViewEnable, int isVisible, boolean balancePayLienarLayoutEnable) {
        balanceTextView.setText(balanceText);
        balanceTextView.setEnabled(balanceTextViewEnable);
        balancePayCheckBox.setVisibility(isVisible);
        balancePayLienarLayout.setEnabled(balancePayLienarLayoutEnable);
    }

    private void showBalanceCheckBox() {
        if (bean.getPrice() * packetCount < balanceFloat) {
            setBalanceCheckBox(getResources().getString(R.string.balance_pay) + "(剩余￥" + balanceFloat + ")", true, View.VISIBLE, true);
        } else {
            setBalanceCheckBox(getResources().getString(R.string.not_enough_balance), false, View.GONE, false);
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

    @Override
    public void dialogText(int type, String text) {
        if (type == 0) {
            // - 24 * 60 * 60 * 1000
            e("System.currentTimeMillis()="+(System.currentTimeMillis()- 24 * 60 * 60 * 1000l));
            e("selectTime="+(DateUtils.getStringToDate(text + " 00:00:00")));
            if (System.currentTimeMillis()- 24 * 60 * 60 * 1000l >= DateUtils.getStringToDate(text + " 00:00:00")) {
                CommonTools.showShortToast(this,getString(R.string.less_current_time));
                return;
            }
            else if(System.currentTimeMillis()+180*24*60*60*1000l < DateUtils.getStringToDate(text + " 00:00:00")){
                CommonTools.showShortToast(this,getString(R.string.last_current_time));
                return;
            }
             dataTime = text;
            payWayTextView.setText(text);
        }
    }
}
