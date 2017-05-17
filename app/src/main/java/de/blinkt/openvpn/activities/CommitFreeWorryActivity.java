package de.blinkt.openvpn.activities;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CommitFreeWorryAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetAttrsByIdHttp;
import de.blinkt.openvpn.model.GetAttrsByIdEntity;
import de.blinkt.openvpn.model.PayResult;
import de.blinkt.openvpn.util.CommonTools;

public class CommitFreeWorryActivity extends BaseNetActivity implements TextWatcher {

	@BindView(R.id.monthlyFeeEditText)
	EditText monthlyFeeEditText;
	/*@BindView(R.id.callTimeTextView)
	TextView callTimeTextView;*/
	@BindView(R.id.callPacketRecyclerView)
	RecyclerView callPacketRecyclerView;
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
	@BindView(R.id.monthCountRecyclerView)
	RecyclerView monthCountRecyclerView;
	//	@BindView(R.id.buyMonthRadioGroup)
//	RadioGroup buyMonthRadioGroup;
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
	//	//购买月数
//	private int monthCount = 1;
	private ArrayList<String> monthData = new ArrayList<>();
	private ArrayList<String> callPacketData = new ArrayList<>();
	private List<GetAttrsByIdEntity.ListBean> allData;
	private CommitFreeWorryAdapter callPacketAdapter;
	private CommitFreeWorryAdapter monthCountAdapter;
	private String callPacket;
	private String monthCountStr;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commit_free_worry);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.dredge_free_for_worry, 0);
		float originalPrice = Float.parseFloat(getIntent().getStringExtra("originalPrice"));
		double price = getIntent().getDoubleExtra("price", 0);
		String priceStr = String.format(getString(R.string.price_everymonth) + "", price);
		String originalPriceStr = String.format(getString(R.string.origin_price) + "", originalPrice);
		originalPriceTextView.setText(originalPriceStr);
		serviceFeeTextView.setText(priceStr);
		originalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		monthCountAdapter = new CommitFreeWorryAdapter(this, monthData, new CommitFreeWorryAdapter.OnClickItemLisener() {
			@Override
			public void onItemClick(String textContent, int position) {
				monthCountStr = textContent;
				monthCountAdapter.setCheck(position);
				checkMoney();
			}
		});
		monthCountRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		monthCountRecyclerView.setAdapter(monthCountAdapter);
		callPacketAdapter = new CommitFreeWorryAdapter(this, callPacketData, new CommitFreeWorryAdapter.OnClickItemLisener() {
			@Override
			public void onItemClick(String textContent, int position) {
				callPacket = textContent;
				callPacketAdapter.setCheck(position);
				checkMoney();
			}
		});
		callPacketRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
		callPacketRecyclerView.setAdapter(callPacketAdapter);
//		buyMonthRadioGroup.setOnCheckedChangeListener(this);
		monthlyFeeEditText.addTextChangedListener(this);
//		buyMonthRadioGroup.check(R.id.month1RadioButton);
		addUpTextView.setText("￥0");
		setSpan(addUpTextView);
		addData();
	}


	//比对结算金额
	private void checkMoney() {
		if (callPacket != null && monthCountStr != null) {
			for (GetAttrsByIdEntity.ListBean bean : allData) {
				Log.i("compare", "比对：bean.getCallMinutesDescr():" + bean.getCallMinutesDescr()
						+ ",callPacket:" + callPacket + "   bean.getExpireDaysDescr():"
						+ bean.getExpireDaysDescr() + " , monthCountStr:" + monthCountStr);
				if (bean.getCallMinutesDescr().equals(callPacket) && bean.getExpireDaysDescr().equals(monthCountStr)) {
					addUpTextView.setText("￥" + (Float.valueOf(bean.getPrice()) + (monthlyFeeInt * Float.valueOf(bean.getExpireDays()))));
				}
			}
		} else {
			CommonTools.showShortToast(this, "callPacket/monthCountStr null!");
		}
	}

	//获取数据
	private void addData() {
		createHttpRequest(HttpConfigUrl.COMTYPE_GET_ATTRS_BY_ID, getIntent().getStringExtra("id"));
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

//	@Override
//	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		monthCount = Integer.parseInt((String) findViewById(checkedId).getTag());
//		if (monthlyFeeInt != 0) {
//			addUpTextView.setText("￥" + monthlyFeeInt * monthCount);
//			setSpan(addUpTextView);
//		}
//	}


	//设置大小字体
	public void setSpan(TextView textview) {
//		String moneyStr = textview.getText().toString();
//		String moneyIntergerStr = moneyStr.split(":")[0];
//		Spannable WordtoSpan = new SpannableString(moneyStr);
//		int intLength = moneyIntergerStr.length();
//		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.length() != 0) {
			monthlyFeeInt = Integer.parseInt(s.toString());
			checkMoney();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}


	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_ATTRS_BY_ID) {
			GetAttrsByIdHttp http = (GetAttrsByIdHttp) object;
			if (http.getStatus() == 1) {
				for (GetAttrsByIdEntity.ListBean entity : http.getAttrsList()) {
					monthData.add(entity.getExpireDaysDescr());
					callPacketData.add(entity.getCallMinutesDescr());
				}
				allData = http.getAttrsList();
				removeDuplicate();
				callPacket = callPacketData.get(0);
				monthCountStr = monthData.get(0);
				monthCountAdapter.notifyDataSetChanged();
				callPacketAdapter.notifyDataSetChanged();
			}
		}
	}

	//去重
	private void removeDuplicate() {
		for (int i = 0; i < monthData.size() - 1; i++) {
			for (int j = monthData.size() - 1; j > i; j--) {
				if (monthData.get(j).equals(monthData.get(i))) {
					monthData.remove(j);
				}
			}
		}
		for (int i = 0; i < callPacketData.size() - 1; i++) {
			for (int j = callPacketData.size() - 1; j > i; j--) {
				if (callPacketData.get(j).equals(callPacketData.get(i))) {
					callPacketData.remove(j);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		monthData.clear();
		monthData = null;
		callPacketData.clear();
		callPacketData = null;
	}

}
