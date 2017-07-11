package de.blinkt.openvpn.activities.ShopModules.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.FreeWorryIntroActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ActivateActivity;
import de.blinkt.openvpn.activities.SetFlowCard.OutsideFirstStepActivity;
import de.blinkt.openvpn.activities.ShopModules.presenter.MyOrderDetailPresenter;
import de.blinkt.openvpn.activities.ShopModules.view.MyOrderDetailView;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.GetOrderByIdHttp;
import de.blinkt.openvpn.model.OrderEntity;
import de.blinkt.openvpn.model.WriteCardEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.BuySucceedDialog;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogTip;

import static android.view.View.GONE;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.ReceiveBLEMoveReceiver.orderStatus;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVECARD;
public class MyOrderDetailActivity extends BaseActivity implements DialogInterfaceTypeBase, MyOrderDetailView {

    public static String FINISH_PROCESS = "finish";
    public static String FINISH_PROCESS_ONLY = "finish_process_only";
    public static String CARD_RULE_BREAK = "card_rule_break";
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
    @BindView(R.id.packageStateTextView)
    TextView packageStateTextView;
    @BindView(R.id.expiryDateTextView)
    TextView expiryDateTextView;
    @BindView(R.id.activateTextView)
    TextView activateTextView;
    @BindView(R.id.orderNumberTextView)
    TextView orderNumberTextView;
    @BindView(R.id.orderTimeTextView)
    TextView orderTimeTextView;
    @BindView(R.id.payWayTextView)
    TextView payWayTextView;
    @BindView(R.id.allPriceTextView)
    TextView allPriceTextView;
    @BindView(R.id.cancelOrderButton)
    TextView cancelOrderButton;
    @BindView(R.id.retryTextView)
    TextView retryTextView;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.orderDetailTitleRelativeLayout)
    RelativeLayout orderDetailTitleRelativeLayout;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.line3)
    View line3;
    @BindView(R.id.dateTitleTextView)
    TextView dateTitleTextView;
    @BindView(R.id.expirytitleTextView)
    TextView expirytitleTextView;
    @BindView(R.id.statueTextView)
    TextView statueTextView;
    @BindView(R.id.aboard_how_to_use)
    Button aboardHowToUse;
    @BindView(R.id.inland_reset)
    Button inlandReset;
    private OrderEntity.ListBean bean;
    private boolean isCreateView = false;
    private boolean isActivateSuccess = false;
    MyOrderDetailPresenter myOrderDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ICSOpenVPNApplication.myOrderDetailActivity = this;
        Log.d("MyOrderDetailActivity", "onCreate: ");
        myOrderDetailPresenter = new MyOrderDetailPresenter(this);
        addData();

    }

    public static void launch(Context context, String id, int PackageCategory) {
        Intent intent = new Intent(context, MyOrderDetailActivity.class);

        intent.putExtra("id", id);
        intent.putExtra("PackageCategory", PackageCategory);
        context.startActivity(intent);
    }

    public static void launch(Context context, String id) {
        Intent intent = new Intent(context, MyOrderDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    private void initSet() {
        hasLeftViewTitle(R.string.order_detail, 0);
        if (getIntent().getIntExtra("PackageCategory", -1) != 0) {
//            aboardHowToUse.setVisibility(GONE);
//            inlandReset.setVisibility(GONE);
        } else {
            cancelOrderButton.setVisibility(GONE);
        }
    }


    //获取数据
    private void addData() {
        myOrderDetailPresenter.addData(getIntent().getStringExtra("id"));
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (ReceiveBLEMoveReceiver.orderStatus == 1) {
            packageStateTextView.setText("已激活");
            cancelOrderButton.setVisibility(GONE);
            expiryDateTextView.setText(bean.getExpireDays());
            activateTextView.setText(getString(R.string.activate_now));
            ReceiveBLEMoveReceiver.orderStatus = -1;
        } else if (ReceiveBLEMoveReceiver.orderStatus == 4) {
            packageStateTextView.setText("激活失败");
            cancelOrderButton.setVisibility(GONE);
            expiryDateTextView.setText(bean.getExpireDays());
            activateTextView.setText(getString(R.string.activate_now));
            ReceiveBLEMoveReceiver.orderStatus = -1;
        }
    }

    private void showBuySucceedDialog() {
        //不能按返回键，只能二选其一
       DialogTip dialogTip
        =new DialogTip(this, this, R.layout.dialog_tip, 3);
        dialogTip.setCanClickBack(false);

    }

    private void createViews() {
        setContentView(R.layout.activity_myorder_detail);
        ButterKnife.bind(this);
        initSet();
        isCreateView = true;
    }


    private String getPaymentMethod(String paymentMethod) {
        switch (paymentMethod) {
            case "1":
                return getResources().getString(R.string.ali_pay);
            case "2":
                return getResources().getString(R.string.weixin_pay);
            case "3":
                return getResources().getString(R.string.balance_pay);
            case "4":
                return getResources().getString(R.string.official_gifts);
            default:
                return "";
        }
    }

    //设置大小字体
    public void setSpan(TextView textview) {
        Spannable WordtoSpan = new SpannableString(textview.getText().toString());
        int intLength = String.valueOf((int) (bean.getUnitPrice())).length();
        WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
    }


    @OnClick({R.id.cancelOrderButton, R.id.activateTextView, R.id.retryTextView, R.id.orderDetailTitleRelativeLayout, R.id.aboard_how_to_use, R.id.inland_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelOrderButton:
                if (bean != null) {
                    ICSOpenVPNApplication.getInstance().finishOtherActivity();
                }
                break;
            case R.id.activateTextView:
                toActivity(new Intent(this,AiXiaoQiWhereActivity.class).putExtra("id",bean.getOrderID()));

                break;
            case R.id.retryTextView:
                addData();
                break;
            case R.id.orderDetailTitleRelativeLayout:
                if ("1".equals(bean.getPackageCategory())) {
                    CallTimePacketDetailActivity.launch(this, bean.getPackageId());
                } else if ("4".equals(bean.getPackageCategory())) {
                    CallTimePacketDetailActivity.launch(this, bean.getPackageId(), this.getString(R.string.receive_fw), bean.getOrderStatus() == 2);
                } else if ("5".equals(bean.getPackageCategory())) {
                    FreeWorryIntroActivity.launch(this, bean.getPackageId());
                } else {
                    PackageDetailActivity.launch(this, bean.getPackageId(), bean.getPic());
                }
                break;
            case R.id.inland_reset:
                Constant.isOutsideSecondStepClick = false;
                Constant.isOutsideThirdStepClick = false;
                toActivity(new Intent(this, OutsideFirstStepActivity.class)
                        .putExtra(IntentPutKeyConstant.OUTSIDE, IntentPutKeyConstant.AFTER_GOING_ABROAD)
                        .putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, bean.isPackageIsSupport4G())
                        .putExtra(IntentPutKeyConstant.APN_NAME, bean.getPackageApnName())
                );
                break;
            case R.id.aboard_how_to_use:
                Constant.isOutsideSecondStepClick = false;
                Constant.isOutsideThirdStepClick = false;
                toActivity(new Intent(this, OutsideFirstStepActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE, IntentPutKeyConstant.OUTSIDE)
                        .putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, bean.isPackageIsSupport4G())
                        .putExtra(IntentPutKeyConstant.APN_NAME, bean.getPackageApnName())
                );

                break;
        }
    }



    @Override
    protected void onDestroy() {

        myOrderDetailPresenter.relaseResource();
        super.onDestroy();
    }

    @Override
    public void dialogText(int type, String text) {
        if (type == 2) {
            SendCommandToBluetooth.sendMessageToBlueTooth(Constant.RESTORATION);
        }
    }

    @Override
    public void loadSuccessShowView(GetOrderByIdHttp http) {
        if (!isCreateView) {
            createViews();
        }
        NoNetRelativeLayout.setVisibility(GONE);
        bean = http.getOrderEntity().getList();
        if (bean != null) {
            if (bean.getLogoPic() != null)
                Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(countryImageView);
            packageNameTextView.setText(bean.getPackageName());
            //如果订单状态是正在使用，那么就计算时间
            if (bean.getOrderStatus() == 0) {
                expiryDateTextView.setText(bean.getExpireDays());
                expirytitleTextView.setText(getResources().getString(R.string.expireday));
                packageStateTextView.setText("未激活");
            } else if (bean.getOrderStatus() == 2) {
                packageStateTextView.setText("订单已过期");
                hideView();
                inlandReset.setVisibility(GONE);
                aboardHowToUse.setVisibility(GONE);
                expirytitleTextView.setVisibility(GONE);

            } else if (bean.getOrderStatus() == 3) {
                packageStateTextView.setText("订单已经被取消");
                hideView();
            } else if (bean.getOrderStatus() == 4) {
                packageStateTextView.setText("激活失败");
                cancelOrderButton.setVisibility(GONE);
                expiryDateTextView.setText(bean.getExpireDays());
                activateTextView.setText(getString(R.string.activate_now));
            } else if (bean.getOrderStatus() == 1) {
                packageStateTextView.setText("已激活");
                cancelOrderButton.setVisibility(GONE);
                expiryDateTextView.setText(bean.getExpireDays());
                activateTextView.setText(getString(R.string.activate_now));
            }
            if ("1".equals(bean.getPackageCategory())) {
                hideWidget();
            } else if ("4".equals(bean.getPackageCategory()) || "5".equals(bean.getPackageCategory())) {
                hideWidget();
                statueTextView.setVisibility(GONE);
                packageStateTextView.setVisibility(GONE);
            } else {
                if (getIntent().getIntExtra("PackageCategory", -1) != 0 && !isActivateSuccess) {
                    showBuySucceedDialog();
                }
            }
            priceTextView.setText("￥" + bean.getUnitPrice());
            setSpan(priceTextView);
            packetCountTextView.setText("x" + bean.getQuantity());
            orderNumberTextView.setText(bean.getOrderNum());
            orderTimeTextView.setText(DateUtils.getDateToString(bean.getOrderDate() * 1000));
            allPriceTextView.setText("￥" + bean.getTotalPrice());
            payWayTextView.setText(getPaymentMethod(bean.getPaymentMethod()));
            dateTextView.setText(DateUtils.getDateToString(bean.getLastCanActivationDate() * 1000));
        }
    }

    private void hideView() {
        expiryDateTextView.setVisibility(GONE);
        activateTextView.setVisibility(GONE);
        cancelOrderButton.setVisibility(GONE);
    }

    private void hideWidget() {
        activateTextView.setVisibility(GONE);
        aboardHowToUse.setVisibility(GONE);
        inlandReset.setVisibility(GONE);
        dateTitleTextView.setVisibility(GONE);
        dateTextView.setVisibility(GONE);
    }

    @Override
    public void noNetShowView() {
        createViews();
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
    }
}
