package de.blinkt.openvpn.activities.MyModules.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

import cn.com.aixiaoqi.R;
import cn.com.aixiaoqi.wxapi.WXPayEntryActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.RechargeMode;
import de.blinkt.openvpn.activities.MyModules.modelImple.RechargeImpl;
import de.blinkt.openvpn.activities.MyModules.ui.PaySuccessActivity;
import de.blinkt.openvpn.activities.MyModules.ui.RechargeActivity;
import de.blinkt.openvpn.activities.MyModules.view.RechargeView;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.RechargeHttp;
import de.blinkt.openvpn.http.WeixinGetPayIdHttp;
import de.blinkt.openvpn.model.PayResult;
import de.blinkt.openvpn.model.RechargeEntity;
import de.blinkt.openvpn.model.WeiXinResultEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.OrderInfoUtil2_0;

import static de.blinkt.openvpn.constant.Constant.ALI_APPID;
import static de.blinkt.openvpn.constant.Constant.RSA_PRIVATE;
import static de.blinkt.openvpn.constant.Constant.WEIXIN_APPID;

/**
 * Created by kim
 * on 2017/6/8.
 */
public class RechargePresenter extends BaseNetActivity {

    private RechargeMode rechargeMode;
    private RechargeView rechargeView;
    private int WEIXIN_PAY = 2;
    private int ALI_PAY = 1;
    private final int SDK_PAY_FLAG = 1;
    private RechargeActivity instance;
    Button nextBtn;
    private int playWay = 0;

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
                        PaySuccessActivity.launch(instance, PaySuccessActivity.RECHARGE, PaySuccessActivity.ALI, orderEntity.getPayment().getAmount() + "", null);
                        instance.finish();
                    } else if (TextUtils.equals(resultStatus, "6002")) {
                        nextBtn.setEnabled(true);
                        rechargeView.showToast(payResult.getMemo());
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        rechargeView.showToast("支付失败");
                        nextBtn.setEnabled(true);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    public RechargePresenter(RechargeView rechargeView) {
        this.rechargeView = rechargeView;
        rechargeMode = new RechargeImpl();
        instance = ICSOpenVPNApplication.rechargeActivity;
        initControlView();
    }

    private void initControlView() {

        nextBtn = rechargeView.getNextBtn();

    }

    /**
     * 充值余额
     *
     * @param moneyAmount 金额
     * @param playWay     支付方式
     */
    public void rechargeBalance(float moneyAmount, int playWay) {
        this.playWay = playWay;
        if (!"".equals(moneyAmount + "")) {
            if (moneyAmount != 0) {
                if (moneyAmount > 5000) {
                    moneyAmount = 5000;
                }
                rechargeMode.rechargeModeBalance(moneyAmount + "", playWay + "", this);
            } else {
                nextBtn.setEnabled(true);
                instance.dismissProgress();
                rechargeView.showToast(instance.getResources().getString(R.string.input_money_0));
            }
        } else {
            rechargeView.showToast(instance.getResources().getString(R.string.input_money_null));
            nextBtn.setEnabled(true);
        }
    }

    RechargeEntity orderEntity;
    IWXAPI api;

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_RECHARGE_ORDER) {
            RechargeHttp http = (RechargeHttp) object;
            if (http.getRechargeEntity() != null) {
                nextBtn.setEnabled(true);
                //获取订单用于支付成功后传到充值成功界面
                orderEntity = http.getRechargeEntity();
                if (playWay == ALI_PAY) {
                    payForAli(http.getRechargeEntity());
                } else {
                    payForWeixin(http.getRechargeEntity());
                }

            } else {
                rechargeView.showToast(http.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID) {
            try {
                WXPayEntryActivity.PAY_PURPOSE = WXPayEntryActivity.PAY_RECHARGE;
                api = WXAPIFactory.createWXAPI(instance, WEIXIN_APPID);
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
                Toast.makeText(instance, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 支付宝支付
     *
     * @param rechargeEntity 充值订单类
     */
    private void payForAli(RechargeEntity rechargeEntity) {
        //将APPID和订单参数发送到订单信息工具
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(ALI_APPID, rechargeEntity);
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

    RechargeReceiver receiver;

    private void payForWeixin(RechargeEntity rechargeEntity) {
        showProgress(instance.getResources().getString(R.string.weixin_paying), true);
        createHttpRequest(HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID, rechargeEntity.getPayment().getPaymentNum());
        nextBtn.setEnabled(true);
        if (receiver == null) {
            receiver = new RechargeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.recharge");
            instance.registerReceiver(receiver, filter);
        }
    }

    public class RechargeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            PaySuccessActivity.launch(instance, PaySuccessActivity.RECHARGE, PaySuccessActivity.WEIXIN, orderEntity.getPayment().getAmount() + "", null);
            instance.finish();
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        rechargeView.showToast(errorMessage);
    }

    @Override
    public void noNet() {
        rechargeView.showToast(instance.getResources().getString(R.string.no_wifi));
    }

    public void releaseResource() {
        if (receiver != null) {
            instance.unregisterReceiver(receiver);
        }

    }
}
