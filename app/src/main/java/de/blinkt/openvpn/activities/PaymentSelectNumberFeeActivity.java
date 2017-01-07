package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.OrderAddHttp;

import static de.blinkt.openvpn.constant.UmengContant.CLICKSUREPAGMENT;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class PaymentSelectNumberFeeActivity extends BaseNetActivity {
    @BindView(R.id.balanceTextView)
    TextView balanceTextView;
    @BindView(R.id.balancePayCheckBox)
    CheckBox balancePayCheckBox;
    @BindView(R.id.balancePayLienarLayout)
    LinearLayout balancePayLienarLayout;
    @BindView(R.id.weixin)
    ImageView weixin;
    @BindView(R.id.text_weixin)
    TextView textWeixin;
    @BindView(R.id.weixinPayCheckBox)
    CheckBox weixinPayCheckBox;
    @BindView(R.id.weixinPayLienarLayout)
    RelativeLayout weixinPayLienarLayout;
    @BindView(R.id.zhifubao_icon)
    ImageView zhifubaoIcon;
    @BindView(R.id.zhifubao_text)
    TextView zhifubaoText;
    @BindView(R.id.aliPayCheckBox)
    CheckBox aliPayCheckBox;
    @BindView(R.id.aliPayLienarLayout)
    RelativeLayout aliPayLienarLayout;
    @BindView(R.id.addUpTextView)
    TextView addUpTextView;
    @BindView(R.id.sureTextView)
    TextView sureTextView;
    private String price;
    private int BALANCE_PAY_METHOD = 3;
    private int WEIXIN_PAY_METHOD = 2;
    private int ALI_PAY_METHOD = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_select_fee);
        ButterKnife.bind(this);
        hasLeftViewTitle(R.string.payment,0);
        price= getIntent().getStringExtra(IntentPutKeyConstant.SELECT_NUMBER_FEE);
    }
    @OnClick({ R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout, R.id.sureTextView, R.id.balancePayLienarLayout})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.balancePayLienarLayout:
                weixinPayCheckBox.setChecked(false);
                aliPayCheckBox.setChecked(false);

                balancePayCheckBox.setChecked(true);
                break;
            case R.id.weixinPayLienarLayout:
                balancePayCheckBox.setChecked(false);
                aliPayCheckBox.setChecked(false);
                weixinPayCheckBox.setChecked(true);
                break;
            case R.id.aliPayLienarLayout:
                balancePayCheckBox.setChecked(false);
                weixinPayCheckBox.setChecked(false);
                aliPayCheckBox.setChecked(true);
                break;
            case R.id.sureTextView:
                HashMap<String, String> map = new HashMap<>();
                OrderAddHttp http;
                sureTextView.setEnabled(false);
                Toast.makeText(this, "获取订单中...", Toast.LENGTH_SHORT).show();
//                if (weixinPayCheckBox.isChecked()) {
//                    map.put("type", WEIXIN_PAY_METHOD + "");
//                    //友盟方法统计
//                    MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
//                    http = new OrderAddHttp(this, HttpConfigUrl.COMTYPE_CREATE_ORDER, bean.getPackageId(), 1, WEIXIN_PAY_METHOD, false);
//                } else if (aliPayCheckBox.isChecked()) {
//                    map.put("type", ALI_PAY_METHOD + "");
//                    //友盟方法统计
//                    MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
//                    http = new OrderAddHttp(this, HttpConfigUrl.COMTYPE_CREATE_ORDER, bean.getPackageId(), 1, ALI_PAY_METHOD, false);
//                } else {
//                    map.put("type", BALANCE_PAY_METHOD + "");
//                    //友盟方法统计
//                    MobclickAgent.onEvent(this, CLICKSUREPAGMENT, map);
//                    http = new OrderAddHttp(this, HttpConfigUrl.COMTYPE_CREATE_ORDER, bean.getPackageId(), 1, BALANCE_PAY_METHOD, true);
//                }
//                new Thread(http).start();
                break;
        }

    }
}
