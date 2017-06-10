package de.blinkt.openvpn.activities.ShopModules.presenter;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

import cn.com.aixiaoqi.R;
import cn.com.aixiaoqi.wxapi.WXPayEntryActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.CommitOrderMode;
import de.blinkt.openvpn.activities.ShopModules.modelImpl.CommitOrderImpl;
import de.blinkt.openvpn.activities.ShopModules.ui.CommitOrderActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.ShopModules.view.CommitOrderView;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BalanceGetPayIdHttp;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.OrderAddHttp;
import de.blinkt.openvpn.http.WeixinGetPayIdHttp;
import de.blinkt.openvpn.model.OrderAddEntity;
import de.blinkt.openvpn.model.PayResult;
import de.blinkt.openvpn.model.WeiXinResultEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.OrderInfoUtil2_0;

import static de.blinkt.openvpn.constant.Constant.ALI_APPID;
import static de.blinkt.openvpn.constant.Constant.RSA_PRIVATE;
import static de.blinkt.openvpn.constant.Constant.WEIXIN_APPID;

/**
 * Created by kim
 * on 2017/6/7.
 */

public abstract class CommitOrderPresenter extends BaseNetActivity {

    private CommitOrderMode commitOrderMode;
    private CommitOrderView commitOrderView;
    private CommitOrderActivity instance;
    public CheckBox aliPayCheckBox;
    TextView sureTextView;
    CheckBox weixinPayCheckBox;

    private static final int SDK_PAY_FLAG = 1;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        commitOrderView.showToast("支付成功");
                        MyOrderDetailActivity.launch(instance, orderEntity.getOrder().getOrderID());
                        sureTextView.setEnabled(true);
                        resetCountPresenter();
                        instance.finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        commitOrderView.showToast("支付失败");
                        sureTextView.setEnabled(true);
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };

    public CommitOrderPresenter(CommitOrderView commitOrderView) {
        this.commitOrderView = commitOrderView;
        commitOrderMode = new CommitOrderImpl();
        instance = ICSOpenVPNApplication.commitOrderActivity;
        initControlView();
    }

    /**
     * 初始化控件
     */
    private void initControlView() {
        aliPayCheckBox = commitOrderView.getAliPayCheckBox();
        sureTextView = commitOrderView.getSureTextView();
        weixinPayCheckBox = commitOrderView.getWeixinPayCheckBox();
    }

    /**
     * 提交订单
     *
     * @param packageId
     * @param packetCount
     * @param playMethod
     */
    public void commitOrder(String packageId, String packetCount, String playMethod) {
        commitOrderMode.commitOrder(packageId, packetCount, playMethod, this);
    }

    OrderAddEntity orderEntity;
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {

        if (cmdType == HttpConfigUrl.COMTYPE_CREATE_ORDER) {
            if (object.getStatus() == 1) {
                OrderAddHttp http = (OrderAddHttp) object;
                orderEntity = http.getOrderEntity();
                if (aliPayCheckBox.isChecked()) {
                    //向支付宝支付
                    PayForAli();
                } else if (weixinPayCheckBox.isChecked()) {
                    //向微信支付
                    payForWeixin();
                } else {
                    payForBalance();
                }

            } else {
                CommonTools.showShortToast(instance, object.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID) {
            try {
                WXPayEntryActivity.PAY_PURPOSE = WXPayEntryActivity.PAY_ORDER;
                IWXAPI api = WXAPIFactory.createWXAPI(instance, WEIXIN_APPID);
                WeixinGetPayIdHttp http = (WeixinGetPayIdHttp) object;
                WeiXinResultEntity entity = http.getWeixinResultEntity();
                PayReq req = new PayReq();
                //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                req.appId = entity.getAppid();
                req.partnerId = entity.getPartnerid();
                req.prepayId = entity.getPrepayid();
                req.nonceStr = entity.getNoncestr();
                req.timeStamp = entity.getTimestamp();
                req.packageValue = entity.getPackageX();
                req.sign = entity.getSign();
                req.extData = "app data"; // optional
                api.sendReq(req);
            } catch (Exception e) {
                Log.e("PAY_GET", "异常：" + e.getMessage());
                commitOrderView.showToast(e.getMessage());
            }

        } else if (cmdType == HttpConfigUrl.COMTYPE_BALANCE_GETPAYID) {
            sureTextView.setEnabled(true);
            if (object.getStatus() == 1) {
                resetCountPresenter();
                MyOrderDetailActivity.launch(instance, orderEntity.getOrder().getOrderID());
                ICSOpenVPNApplication.getInstance().finishOtherActivity();
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_GET_BALANCE) {
            BalanceHttp http = (BalanceHttp) object;
            getBalance(http);
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        CommonTools.showShortToast(instance, errorMessage);
        sureTextView.setEnabled(true);
    }

    @Override
    public void noNet() {
        CommonTools.showShortToast(instance, "请检查您的网络设置！");
        sureTextView.setEnabled(true);
    }

    public void checkNetBalance() {
        BalanceHttp http = new BalanceHttp(this, HttpConfigUrl.COMTYPE_GET_BALANCE);
        new Thread(http).start();
    }

    /**
     * 微信支付
     */
    private void payForWeixin() {
        showProgress(instance.getResources().getString(R.string.weixin_paying), true);
        SharedPreferences preferences = instance.getSharedPreferences("order", instance.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("orderId", orderEntity.getOrder().getOrderID());
        editor.putString("orderAmount", orderEntity.getOrder().getTotalPrice() + "");
        editor.commit();
        createHttpRequest(HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID, orderEntity.getOrder().getOrderNum());
        sureTextView.setEnabled(true);
    }

    /**
     * 支付宝支付
     */
    private void PayForAli() {
        //将APPID和订单参数发送到订单信息工具
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(ALI_APPID, orderEntity);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(instance);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 余额支付
     */
    private void payForBalance() {
        BalanceGetPayIdHttp http = new BalanceGetPayIdHttp(this, HttpConfigUrl.COMTYPE_BALANCE_GETPAYID, orderEntity.getOrder().getOrderID());
        new Thread(http).start();
    }

    public void releaseResouce() {
        if (mHandler != null && mHandler.getLooper() == Looper.getMainLooper()) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;

    }

    public abstract void resetCountPresenter();

    public abstract void getBalance(BalanceHttp http);


}
