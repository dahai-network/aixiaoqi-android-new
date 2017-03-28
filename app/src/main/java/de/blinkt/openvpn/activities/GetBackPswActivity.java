package de.blinkt.openvpn.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ForgetPswHttp;
import de.blinkt.openvpn.http.SendMsgHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.ViewUtil;

import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKBUTTON;
import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKSENDCODE;
import static de.blinkt.openvpn.constant.UmengContant.FINDBACKSHOWPASSWORD;

public class GetBackPswActivity extends BaseNetActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	private String TAG = "GetBackPswActivity";
	private EditText phoneNumberEdit;
	private EditText verification_edit;
	private EditText passwordEdit;
	private Button sendBtn;
	private Button sure_btn;
	private CheckBox hindPswCheckBox;
	private LinearLayout getPswLinearLayout;

	private boolean isOpenHind = true;
	private CountDownTimer timer = new CountDownTimer(111000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {
			sendBtn.setText(millisUntilFinished / 1000 + "秒后可重发");
		}

		@Override
		public void onFinish() {
			sendBtn.setEnabled(true);
			sendBtn.setText("发送验证码");
		}
	};
	private InputMethodManager manager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_back_psw);
		init();
		hasLeftViewTitle(R.string.find_password, 0);
	}

	private void init() {
		initView();
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}


	//初始化控件
	private void initView() {
		phoneNumberEdit = (EditText)
				findViewById(R.id.phoneNumberEdit);
		verification_edit = (EditText)
				findViewById(R.id.verification_edit);
		setPhoneNumberEditChangeLisener();
		passwordEdit = (EditText)
				findViewById(R.id.passwordEdit);
		hindPswCheckBox = (CheckBox) findViewById(R.id.hindPswCheckBox);
		hindPswCheckBox.setOnClickListener(this);
		sendBtn = (Button) findViewById(R.id.sendBtn);
		sendBtn.setOnClickListener(this);
		sure_btn = (Button) findViewById(R.id.sure_btn);
		sure_btn.setOnClickListener(this);
		getPswLinearLayout = (LinearLayout) findViewById(R.id.getPswLinearLayout);
		getPswLinearLayout.setOnClickListener(this);

	}

	private void setPhoneNumberEditChangeLisener() {
		phoneNumberEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() != 0) sendBtn.setEnabled(true);
				else {
					sendBtn.setEnabled(false);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.sendBtn:

				if (CheckUtil.isMobileNO(phoneNumberEdit.getText().toString().trim(), GetBackPswActivity.this)) {
					//友盟方法统计
					MobclickAgent.onEvent(this, CLICKFINDBACKSENDCODE);
					sendBtn.setEnabled(false);
					sendBtn.setTextColor(ContextCompat.getColor(this, R.color.regist_send_sms_unenable));
					SendMsgHttp http = new SendMsgHttp(this, HttpConfigUrl.COMTYPE_SEND_SMS, phoneNumberEdit.getText().toString(), 2);
					new Thread(http).start();

				}
				break;
			case R.id.sure_btn:
				String vertificationStr = verification_edit.getText().toString();
				if (!TextUtils.isEmpty(vertificationStr)) {
					String phoneStr = phoneNumberEdit.getText().toString();
					String pswStr = passwordEdit.getText().toString();
					if (CheckUtil.isMobileNO(phoneStr, GetBackPswActivity.this)) {
						if (CheckUtil.isPassWordNo(pswStr, GetBackPswActivity.this)) {
							//友盟方法统计
							MobclickAgent.onEvent(this, CLICKFINDBACKBUTTON);
							createHttpRequest(HttpConfigUrl.COMTYPE_FORGET_PSW,
									phoneStr,
									pswStr,
									vertificationStr);
						}
					}
				} else {
					CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), getResources().getString(R.string.null_verification));
				}
				break;
			case R.id.hindPswCheckBox:
				//友盟方法统计
				MobclickAgent.onEvent(this, FINDBACKSHOWPASSWORD);
				if (isOpenHind) {
					isOpenHind = false;
					//如果选中，显示密码
					passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				} else {
					isOpenHind = true;
					//否则隐藏密码
					passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				break;
			case R.id.getPswLinearLayout:
				ViewUtil.hideSoftKeyboard(this);
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			sure_btn.setEnabled(true);
		} else {
			sure_btn.setEnabled(false);
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_SEND_SMS) {

			SendMsgHttp entity = (SendMsgHttp) object;
			if (entity.getStatus() == 1) {
				sendBtn.setEnabled(false);
				timer.start();
			} else {
				sendBtn.setEnabled(true);
				sendBtn.setTextColor(ContextCompat.getColor(this, R.color.black));
				CommonTools.showShortToast(GetBackPswActivity.this, entity.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_FORGET_PSW) {
			ForgetPswHttp entity = (ForgetPswHttp) object;
			if (entity.getStatus() == 1) {
				CommonTools.showShortToast(GetBackPswActivity.this, entity.getMsg());
				finish();
			} else {
				CommonTools.showShortToast(GetBackPswActivity.this, entity.getMsg());
			}
		}
	}


}
