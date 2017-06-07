package de.blinkt.openvpn.activities.Set.Presenter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.CallTime_CreatViewEvent;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.Set.Model.CallTimeOrderDetailMode;
import de.blinkt.openvpn.activities.Set.ModelImpl.CallTimeOrderDetailImpl;
import de.blinkt.openvpn.activities.Set.View.CallTimeOrderDetailView;
import de.blinkt.openvpn.activities.Set.ui.CallTimeOrderDetailActitivy;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetOrderByIdHttp;
import de.blinkt.openvpn.model.OrderEntity;
import de.blinkt.openvpn.util.DateUtils;
/**
 * Created by kim
 * on 2017/6/6.
 */

public class CallTimeOrderDetailPresenter extends BaseNetActivity {

    private CallTimeOrderDetailMode callTimeOrderDetailMode;

    private CallTimeOrderDetailView callTimeOrderDetailView;
    private OrderEntity.ListBean bean;
    RelativeLayout NoNetRelativeLayout;
    ImageView packetImageView;
    TextView expiryDateTextView, packetStatusTextView, packageNameTextView, priceTextView, orderNumberTextView, orderTimeTextView,payWayTextView;
    boolean isCreateView;
    private CallTimeOrderDetailActitivy instance;
    public CallTimeOrderDetailPresenter(CallTimeOrderDetailView callTimeOrderDetailView) {
        this.callTimeOrderDetailView = callTimeOrderDetailView;
        callTimeOrderDetailMode = new CallTimeOrderDetailImpl();
        instance=CallTimeOrderDetailActitivy.actitivy;
    }

    public void getOrderDetailData() {
        NoNetRelativeLayout = callTimeOrderDetailView.getNoNetRelativeLayout();
        packageNameTextView = callTimeOrderDetailView.getPackageNameTextView();
        packetImageView = callTimeOrderDetailView.getPacketImageView();
        expiryDateTextView = callTimeOrderDetailView.getExpiryDateTextView();
        packetStatusTextView = callTimeOrderDetailView.getPacketStatusTextView();
        priceTextView = callTimeOrderDetailView.getPriceTextView();
        orderNumberTextView = callTimeOrderDetailView.getOrderNumberTextView();
        orderTimeTextView = callTimeOrderDetailView.getOrderTimeTextView();
        payWayTextView = callTimeOrderDetailView.getPayWayTextView();
        isCreateView = callTimeOrderDetailView.getIsCreateView();
        String orderId = callTimeOrderDetailView.getOrderId();

       // EventBus.getDefault().register(instance);
        //获取订单数据
        callTimeOrderDetailMode.getOrderDetailData(orderId, this);

    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID) {
            dismissProgress();
            if (object.getStatus() == 1) {
                dismissProgress();
                if (!isCreateView) {
                   // createViews();
                    EventBus.getDefault().post(new CallTime_CreatViewEvent(true));
                }
                NoNetRelativeLayout.setVisibility(View.GONE);
                GetOrderByIdHttp http = (GetOrderByIdHttp) object;
                bean = http.getOrderEntity().getList();
                Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getLogoPic()).into(packetImageView);
                packageNameTextView.setText(bean.getPackageName());
                //如果订单状态是正在使用，那么就计算时间
                String expireStr = bean.getExpireDays().replace(instance.getResources().getString(R.string.expireday), "");
                if (bean.getOrderStatus() == 0) {
                    expiryDateTextView.setText(expireStr);
                    packetStatusTextView.setText("未激活");

                } else if (bean.getOrderStatus() == 2) {
                    packetStatusTextView.setText("订单已过期");

                } else if (bean.getOrderStatus() == 3) {
                    packetStatusTextView.setText("订单已经被取消");
                } else if (bean.getOrderStatus() == 4) {
                    packetStatusTextView.setText("激活失败");
                    expiryDateTextView.setText(expireStr);
                } else if (bean.getOrderStatus() == 1) {
                    packetStatusTextView.setTextColor(Color.BLACK);
                    packetStatusTextView.setText(instance.getResources().getString(R.string.residue) + bean.getRemainingCallMinutes() + getResources().getString(R.string.minute));
                    setResidueMinueSpan(packetStatusTextView, bean.getRemainingCallMinutes());
                    expiryDateTextView.setText(expireStr);
                }
                String payWayStr = getPaymentMethod(bean.getPaymentMethod());
                if (payWayStr != null) {
                    payWayTextView.setText(payWayStr);
                }
                priceTextView.setText("￥" + bean.getUnitPrice());
                setSpan(priceTextView);
                orderNumberTextView.setText(bean.getOrderNum());
                orderTimeTextView.setText(DateUtils.getDateToString(bean.getOrderDate() * 1000));
            } else {
                callTimeOrderDetailView.showToast(object.getMsg());
            }
        }
    }

    private void setResidueMinueSpan(TextView stateTextView, int remainingCallMinutes) {
        Spannable WordtoSpan = new SpannableString(stateTextView.getText().toString());
        WordtoSpan.setSpan(new ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.select_contacct)), 2, 2 + String.valueOf(remainingCallMinutes).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        stateTextView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
    }

    /**
     *
     * @param paymentMethod 支付方法
     * @return
     */
    private String getPaymentMethod(String paymentMethod) {
        switch (paymentMethod) {
            case "1":
                return instance.getResources().getString(R.string.ali_pay);
            case "2":
                return instance.getResources().getString(R.string.weixin_pay);
            case "3":
                return instance.getResources().getString(R.string.balance_pay);
            case "4":
                return instance.getResources().getString(R.string.official_gifts);
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
}
