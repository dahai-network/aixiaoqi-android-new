package de.blinkt.openvpn.activities;

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
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.BindGiftHttp;
import de.blinkt.openvpn.http.BindRechargeHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.CommonTools;


public class BindRechargeCardActivity extends BaseNetActivity {
	private String TYPE = "type";
	public static int RECHARGE = 1;
	public static int GIFT = 2;

	@BindView(R.id.cardPswswEditText)
	EditText cardPswswEditText;
	@BindView(R.id.sendBtn)
	Button sendBtn;
	private int bindType;

	public static void launch(Context context, int type) {
		Intent intent = new Intent(context, BindRechargeCardActivity.class);
		intent.putExtra("type", type);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_recharge_card);
		ButterKnife.bind(this);
		bindType = getIntent().getIntExtra(TYPE, RECHARGE);
		if (bindType == RECHARGE) {
			hasLeftViewTitle(R.string.bind_recharge_card, 0);
		} else {
			hasLeftViewTitle(R.string.bind_gift_card, 0);
			cardPswswEditText.setHint(R.string.input_gift_card_psw);
		}
	}


	@OnClick(R.id.sendBtn)
	public void onClick(View v) {
		if (cardPswswEditText.getText().toString().length() != 0) {
			if(bindType == RECHARGE) {
				BindRechargeHttp http = new BindRechargeHttp(this, HttpConfigUrl.COMTYPE_BIND_RECHARGE_CARD, cardPswswEditText.getText().toString());
				new Thread(http).start();
			}
			else
			{
				BindGiftHttp http = new BindGiftHttp(this,HttpConfigUrl.COMTYPE_BIND_GIFT,cardPswswEditText.getText().toString());
				new Thread(http).start();
			}
		} else {
			CommonTools.showShortToast(this, getResources().getString(R.string.input_compelete_password));
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_BIND_RECHARGE_CARD) {
			if (object.getStatus() == 1) {
				CommonTools.showShortToast(this, getResources().getString(R.string.recharge_success));
				Intent intent = new Intent(this, BalanceParticularsActivity.class);
				startActivity(intent);
				finish();
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		} else {
			if (object.getStatus() == 1) {
				CommonTools.showShortToast(this, getResources().getString(R.string.bind_seccess));
				Intent intent = new Intent(this, MyPackageActivity.class);
				startActivity(intent);
				finish();
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		}
	}

}
