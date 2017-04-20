package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.Set;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.SecurityConfig;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.CheckUtil;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.BlackListDBHelp;
import de.blinkt.openvpn.http.BlackListGetHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBasicConfigHttp;
import de.blinkt.openvpn.http.LoginHttp;
import de.blinkt.openvpn.http.SecurityConfigHttp;
import de.blinkt.openvpn.model.BasicConfigEntity;
import de.blinkt.openvpn.model.LoginEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.ViewUtil;

import static de.blinkt.openvpn.constant.UmengContant.CLICKFINDBACKPASSWORD;
import static de.blinkt.openvpn.constant.UmengContant.CLICKLOGINREGISTER;


/**
 * Created by wzj on 2016/6/3.
 */
public class LoginMainActivity extends BaseNetActivity implements View.OnClickListener {
	private Button login_btn;
	private EditText usernameEdit;
	private EditText pwdEdit;
	private TextView registTextView;
	private CheckBox hindPswCheckBox;
	//是否打开密码隐藏
	private boolean isOpenHind = true;
	private TextView forgetPswTextView;
	private LinearLayout loginLinearLayout;
	private TextView loginPasswordTextView;
	private TextView loginUserTextView;

	private static final int MSG_SET_ALIAS = 1001;


	@Override
	public void onPanelClosed(int featureId, Menu menu) {
		super.onPanelClosed(featureId, menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_main);
		init();
		basicConfigHttp();
	}

	private void basicConfigHttp() {
		if (TextUtils.isEmpty(SharedUtils.getInstance().readString(IntentPutKeyConstant.USER_AGREEMENT_URL))) {
			createHttpRequest(HttpConfigUrl.COMTYPE_GET_BASIC_CONFIG);
		}
	}

	SharedUtils sharedUtils;

	private void init() {
		sharedUtils = SharedUtils.getInstance();
		createViewInit();
		setLoginData();
		String otherDeviceLogin = getIntent().getStringExtra(IntentPutKeyConstant.OTHER_DEVICE_LOGIN);
		if (!TextUtils.isEmpty(otherDeviceLogin) && !sharedUtils.readBoolean(Constant.ISFIRSTIN, true)) {
			CommonTools.showShortToast(this, otherDeviceLogin);
		}
	}

	/**
	 * 设置帐号密码记录过的数据
	 */
	private void setLoginData() {

		long currentDate = System.currentTimeMillis();
		long loginDate = sharedUtils.readLong("loginDate");
		long DATEOF15 = 1296000000;
		//登录天数距离小于15天的时候可以复制帐号密码
		if (currentDate - loginDate < DATEOF15) {
			usernameEdit.setText(sharedUtils.readString("userName"));
			if(!TextUtils.isEmpty(usernameEdit.getText().toString()))
				usernameEdit.setSelection(usernameEdit.getText().length());
			pwdEdit.setText(sharedUtils.readString("password"));
		}
	}

	//绑定view
	private void createViewInit() {
		usernameEdit = (EditText) findViewById(R.id.usernameEdit);
		pwdEdit = (EditText) findViewById(R.id.pwdEdit);

		login_btn = (Button) findViewById(R.id.login_btn);
		registTextView = (TextView) findViewById(R.id.registTextView);
		forgetPswTextView = (TextView) findViewById(R.id.forgetPswTextView);
		loginLinearLayout = (LinearLayout) findViewById(R.id.loginLinearLayout);
		loginPasswordTextView = (TextView) findViewById(R.id.tip_login_password);
		loginUserTextView = (TextView) findViewById(R.id.tip_login_user);
		registTextView.setOnClickListener(this);
		forgetPswTextView.setOnClickListener(this);
		loginLinearLayout.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		setTextChangeLisener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		usernameEdit = null;
		pwdEdit = null;
		login_btn = null;
		registTextView = null;
		forgetPswTextView = null;

	}

	private String pswString;

	private void setTextChangeLisener() {
		pwdEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					//处理事件
					if (pswString.length() != 0)
						login_btn.performClick();
				}
				return false;
			}
		});
		setEditTextListener(pwdEdit,R.id.pwdEdit);

		setEditTextListener(usernameEdit,R.id.usernameEdit);
	}

	private void setEditTextListener(EditText editText,int id){
		new ExditTextWatcher(editText,id){
			@Override
			public void textChanged(CharSequence s, int id) {
				switch (id){


					case R.id.pwdEdit:
						pswString = s.toString();
						if (s.length() != 0) {
							pwdEdit.setGravity(Gravity.CENTER);
							if (usernameEdit.getText().toString().length() != 0) {
								setLoginBtnAttr(true,R.drawable.green_btn_click);
							} else {
								setLoginBtnAttr(false,R.drawable.circle_gray_ret);
							}
							loginPasswordTextView.setVisibility(View.VISIBLE);
						} else {
							pwdEdit.setGravity(Gravity.LEFT);
							loginPasswordTextView.setVisibility(View.GONE);
							setLoginBtnAttr(false,R.drawable.circle_gray_ret);
						}

						break;
					case R.id.usernameEdit:
						if (s.length() != 0) {
							usernameEdit.setGravity(Gravity.CENTER);
							if (pwdEdit.getText().toString().length() != 0) {
								setLoginBtnAttr(true,R.drawable.green_btn_click);
							} else {
								setLoginBtnAttr(false,R.drawable.circle_gray_ret);
							}
							loginUserTextView.setVisibility(View.VISIBLE);
						} else {
							usernameEdit.setGravity(Gravity.CENTER);
							loginUserTextView.setVisibility(View.GONE);
							setLoginBtnAttr(false,R.drawable.circle_gray_ret);
						}
						break;
				}
			}
		};

	}

	private void setLoginBtnAttr(boolean isClick,int backgroundColor){
		login_btn.setEnabled(isClick);
		login_btn.setBackgroundResource(backgroundColor);

	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
		return true;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.registTextView:
				//友盟方法统计
				MobclickAgent.onEvent(this, CLICKLOGINREGISTER);
				Intent toRegistIntent = new Intent(LoginMainActivity.this, RegistActivity.class);
				startActivity(toRegistIntent);
				break;
			case R.id.login_btn:
				if (pwdEdit != null) {
					if (CheckUtil.isPassWordNo(pwdEdit.getText().toString(), LoginMainActivity.this)) {
						showProgress(R.string.login_loading);
						createHttpRequest(HttpConfigUrl.COMTYPE_LOGIN, usernameEdit.getText().toString(), pwdEdit.getText().toString());
					}
				}
				break;
			case R.id.forgetPswTextView:
				//友盟方法统计
				MobclickAgent.onEvent(this, CLICKFINDBACKPASSWORD);
				//跳转忘记密码
				Intent intent = new Intent(LoginMainActivity.this, GetBackPswActivity.class);
				startActivity(intent);
				break;
			case R.id.loginLinearLayout:
				ViewUtil.hideSoftKeyboard(this);
				break;
		}
	}

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {

			SharedUtils sharedUtils = SharedUtils.getInstance();

			switch (code) {
				case 0:
					sharedUtils.writeString(Constant.JPUSH_ALIAS,
							Constant.JPUSH_ALIAS_SUCCESS);
					break;

				case 6002:

					if (NetworkUtils.isNetworkAvailable(ICSOpenVPNApplication.getContext())) {
						handler.sendMessageDelayed(handler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
					}
					break;

				default:


			}


		}

	};

	private final JpushHandler handler = new JpushHandler(mAliasCallback);

	private static class JpushHandler extends Handler {

		private final WeakReference<TagAliasCallback> callback;

		public JpushHandler(TagAliasCallback mAliasCallback) {
			this.callback = new WeakReference<>(mAliasCallback);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_SET_ALIAS:
					JPushInterface.setAliasAndTags(ICSOpenVPNApplication.getContext(), (String) msg.obj, null, callback.get());
					break;
				default:
			}
		}

	}


	private void setAlias() {
		SharedUtils sharedUtils = SharedUtils.getInstance();
		String alias = "aixiaoqi" + sharedUtils.readString(Constant.TOKEN);
		handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, alias));
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_LOGIN) {
			LoginHttp loginHttp = (LoginHttp) object;
			if (loginHttp.getStatus() == 1) {
				LoginEntity entity = loginHttp.getLoginModel();

				if (entity != null) {
					sharedUtils.writeString(Constant.USER_NAME, entity.getTel());
					if (pwdEdit != null)
						sharedUtils.writeString(Constant.PASSWORD, pwdEdit.getText().toString());
					sharedUtils.writeString(Constant.TOKEN, entity.getToken());
					sharedUtils.writeString(Constant.USER_HEAD, entity.getUserHead());
					sharedUtils.writeString(Constant.NICK_NAME, entity.getNickName());
					sharedUtils.writeString(Constant.HEIGHT, entity.getHeight());
					sharedUtils.writeString(Constant.WEIGHT, entity.getWeight());
					sharedUtils.writeString(Constant.SOPRT_TARGET, entity.getMovingTarget());
					sharedUtils.writeString(Constant.IMEI, entity.getBraceletIMEI());
					sharedUtils.writeString(Constant.BRACELETVERSION, entity.getBraceletVersion());
					sharedUtils.writeInt(Constant.COMING_TEL_REMIND, entity.getNotificaCall());
					sharedUtils.writeInt(Constant.MESSAGE_REMIND, entity.getNotificaSMS());
					sharedUtils.writeInt(Constant.WEIXIN_REMIND, entity.getNotificaWeChat());
					sharedUtils.writeInt(Constant.QQ_REMIND, entity.getNotificaQQ());
					sharedUtils.writeInt(Constant.LIFT_WRIST, entity.getNotificaQQ());
					sharedUtils.writeBoolean(Constant.ISFIRSTIN, false);
					Log.e("token", "token=" + entity.getToken());
					if (!TextUtils.isEmpty(entity.getBirthday())) {
						sharedUtils.writeString(Constant.BRITHDAY, DateUtils.getDateToString(Long.parseLong(entity.getBirthday()) * 1000).substring(0, 7).replace("-", "年"));
					}
					sharedUtils.writeString(Constant.GENDER, entity.getSex());
					//写入登陆天数，如果十五天没有登陆过则重新登录
					sharedUtils.writeLong(Constant.LOGIN_DATA, System.currentTimeMillis());
					sharedUtils.writeLong(Constant.CONFIG_TIME, System.currentTimeMillis());

					if (!usernameEdit.getText().toString().equals(sharedUtils.readString(Constant.TEL)) || !Constant.JPUSH_ALIAS_SUCCESS.equals(sharedUtils.readString(Constant.JPUSH_ALIAS))) {
						setAlias();
					}
					createHttpRequest(HttpConfigUrl.COMTYPE_BLACK_LIST_GET);
					createHttpRequest(HttpConfigUrl.COMTYPE_SECURITY_CONFIG);
				}
			} else {
				CommonTools.showShortToast(this, loginHttp.getMsg());
				dismissProgress();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_SECURITY_CONFIG) {
			SecurityConfigHttp securityConfigHttp = (SecurityConfigHttp) object;
			if (securityConfigHttp.getStatus() == 1) {
				//友盟帐号统计
				MobclickAgent.onProfileSignIn(usernameEdit.getText().toString());
				SecurityConfig.InBean in = securityConfigHttp.getSecurityConfig().getIn();
				SecurityConfig.OutBean out = securityConfigHttp.getSecurityConfig().getOut();
				sharedUtils.writeString(Constant.ASTERISK_IP_IN, in.getAsteriskIp());
				sharedUtils.writeString(Constant.ASTERISK_PORT_IN, in.getAsteriskPort());
				sharedUtils.writeString(Constant.ASTERISK_IP_OUT, out.getAsteriskIp());
				sharedUtils.writeString(Constant.ASTERISK_PORT_OUT, out.getAsteriskPort());
				sharedUtils.writeString(Constant.PUBLIC_PASSWORD, out.getPublicPassword());

				startActivity(new Intent(this, ProMainActivity.class));

				finish();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_BASIC_CONFIG) {
			GetBasicConfigHttp getBasicConfigHttp = (GetBasicConfigHttp) object;
			if (getBasicConfigHttp.getStatus() == 1) {
				BasicConfigEntity basicConfigEntity = getBasicConfigHttp.getBasicConfigEntity();
				sharedUtils.writeString(IntentPutKeyConstant.USER_AGREEMENT_URL, basicConfigEntity.getUserAgreementUrl());
				sharedUtils.writeString(IntentPutKeyConstant.DUALSIM_STANDBYTUTORIAL_URL, basicConfigEntity.getDualSimStandbyTutorialUrl());
				sharedUtils.writeString(IntentPutKeyConstant.BEFORE_GOING_ABROAD_TUTORIAL_URL, basicConfigEntity.getBeforeGoingAbroadTutorialUrl());
				sharedUtils.writeString(IntentPutKeyConstant.PAYMENT_OF_TERMS, basicConfigEntity.getPaymentOfTerms());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_BLACK_LIST_GET) {
			if (object.getStatus() == 1) {
				BlackListGetHttp blackListGetHttp = (BlackListGetHttp) object;
				if (blackListGetHttp.getBlackListEntities().size() != 0) {
					BlackListDBHelp blackListDBHelp = new BlackListDBHelp(this);
					blackListDBHelp.deleteAllDefriend();
					blackListDBHelp.insertDefriendList(blackListGetHttp.getBlackListEntities());
				}
			}

		}
	}


}

