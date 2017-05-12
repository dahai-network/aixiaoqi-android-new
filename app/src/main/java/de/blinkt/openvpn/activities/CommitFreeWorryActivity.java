package de.blinkt.openvpn.activities;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.model.PayResult;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.RadioGroup;

public class CommitFreeWorryActivity extends BaseNetActivity implements RadioGroup.OnCheckedChangeListener, TextWatcher {

	@BindView(R.id.monthlyFeeEditText)
	EditText monthlyFeeEditText;
	@BindView(R.id.callTimeTextView)
	TextView callTimeTextView;
	@BindView(R.id.serviceFeeTextView)
	TextView serviceFeeTextView;
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
	@BindView(R.id.originalPriceTextView)
	TextView originalPriceTextView;
	@BindView(R.id.buyMonthRadioGroup)
	RadioGroup buyMonthRadioGroup;
	private int WEIXIN_PAY = 2;
	private int ALI_PAY = 1;
	private int pay_way = 0;
	private final int SDK_PAY_FLAG = 1;
	private IWXAPI api;
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
//					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为9000则代表支付成功
					if (TextUtils.equals(resultStatus, "9000")) {
						// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//						PaySuccessActivity.launch(CommitFreeWorryActivity.this, PaySuccessActivity.RECHARGE, PaySuccessActivity.ALI, orderEntity.getPayment().getAmount() + "", null);
						finish();
					} else if (TextUtils.equals(resultStatus, "6002")) {
//						nextBtn.setEnabled(true);
						CommonTools.showShortToast(CommitFreeWorryActivity.this, payResult.getMemo());
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						Toast.makeText(CommitFreeWorryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//						nextBtn.setEnabled(true);
					}
					break;
				}
				default:
					break;
			}
		}

	};
	//每月费用
	private int monthlyFeeInt;
	//购买月数
	private int monthCount = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commit_free_worry);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.dredge_free_for_worry, 0);
		originalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		buyMonthRadioGroup.setOnCheckedChangeListener(this);
		monthlyFeeEditText.addTextChangedListener(this);
		buyMonthRadioGroup.check(R.id.month1RadioButton);
		addUpTextView.setText("￥0");
		setSpan(addUpTextView);
	}

	@OnClick({R.id.balancePayLienarLayout, R.id.weixinPayLienarLayout, R.id.aliPayLienarLayout, R.id.sureTextView})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.balancePayLienarLayout:
				weixinPayCheckBox.setChecked(false);
				aliPayCheckBox.setChecked(false);
				balancePayCheckBox.setChecked(true);
				break;
			case R.id.weixinPayLienarLayout:
				balancePayCheckBox.setChecked(false);
				weixinPayCheckBox.setChecked(true);
				aliPayCheckBox.setChecked(false);
				break;
			case R.id.aliPayLienarLayout:
				balancePayCheckBox.setChecked(false);
				weixinPayCheckBox.setChecked(false);
				aliPayCheckBox.setChecked(true);
				break;
			case R.id.sureTextView:
				CommonTools.showShortToast(this, "支付！");
				break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		monthCount = Integer.parseInt((String) findViewById(checkedId).getTag());
		if (monthlyFeeInt != 0) {
			addUpTextView.setText("￥" + monthlyFeeInt * monthCount);
			setSpan(addUpTextView);
		}
	}


	//设置大小字体
	public void setSpan(TextView textview) {
		Spannable WordtoSpan = new SpannableString(textview.getText().toString());
		int intLength = String.valueOf(monthlyFeeInt * monthCount).length();
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() != 0) {
			monthlyFeeInt = Integer.parseInt(s.toString());
			if (monthCount != 0) {
				addUpTextView.setText("￥" + monthlyFeeInt * monthCount);
				setSpan(addUpTextView);
			}
		} else {
			addUpTextView.setText("￥0");
			setSpan(addUpTextView);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}
