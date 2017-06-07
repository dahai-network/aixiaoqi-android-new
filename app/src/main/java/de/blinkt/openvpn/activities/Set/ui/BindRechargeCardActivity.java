package de.blinkt.openvpn.activities.Set.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Set.Presenter.BindRechargeCardPresenter;
import de.blinkt.openvpn.activities.Set.View.BindRechargeCardView;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

/**
 * @author kim
 * 使用充值卡界面
 */
public class BindRechargeCardActivity extends BaseActivity implements BindRechargeCardView {
    private String TYPE = "type";
    public static int RECHARGE = 1;
    public static int GIFT = 2;
    @BindView(R.id.cardPswswEditText)
    EditText cardPswswEditText;
    @BindView(R.id.sendBtn)
    Button sendBtn;
    private int bindType;
    private BindRechargeCardPresenter bindRechargeCardPresenter;

    public static void launch(Context context, int type) {
        Intent intent = new Intent(context, BindRechargeCardActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_recharge_card);
        ICSOpenVPNApplication.bindRechargeCardInstance = this;
        ButterKnife.bind(this);
        bindType = getIntent().getIntExtra(TYPE, RECHARGE);
        if (bindType == RECHARGE) {
            hasLeftViewTitle(R.string.bind_recharge_card, 0);
        } else {
            hasLeftViewTitle(R.string.bind_gift_card, 0);
            cardPswswEditText.setHint(R.string.input_gift_card_psw);
        }
        bindRechargeCardPresenter = new BindRechargeCardPresenter(this);
    }

    @OnClick(R.id.sendBtn)
    public void onClick(View v) {
        //充值绑定

        bindRechargeCardPresenter.bindRechargeOrGift();
    }

    @Override
    public String getCardPsw() {
        return cardPswswEditText.getText().toString().trim();
    }

    @Override
    public int getBindType() {
        return bindType;
    }

    @Override
    public void showToast(String msg) {

        CommonTools.showShortToast(BindRechargeCardActivity.this, msg);
    }


}
