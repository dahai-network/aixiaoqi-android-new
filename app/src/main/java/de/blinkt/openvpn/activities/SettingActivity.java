package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.qfishphone.sipengine.SipEngineCore;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.SportFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ExitHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOUR;
import static de.blinkt.openvpn.constant.UmengContant.CLICKEXITLOGIN;
import static de.blinkt.openvpn.constant.UmengContant.CLICKUSERFEEDBACKSEND;
import static de.blinkt.openvpn.constant.UmengContant.CLICKVERSIONUPGRADE;

public class SettingActivity extends BaseActivity implements InterfaceCallback, DialogInterfaceTypeBase {

	@BindView(R.id.contactUsTextView)
	TextView contactUsTextView;
	@BindView(R.id.userFeedbackTextView)
	TextView userFeedbackTextView;
	@BindView(R.id.agreementUsTextView)
	TextView agreementUsTextView;
	@BindView(R.id.ll_upgrade)
	LinearLayout upgradeLl;

	@BindView(R.id.appVersionTextView)
	TextView appVersionTextView;
	@BindView(R.id.exitBtn)
	Button exitBtn;
	private SipEngineCore sipEngineCore;
	private static final int MSG_SET_ALIAS = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ButterKnife.bind(this);
		init();
	}

	private void init() {
		hasLeftViewTitle(R.string.setting, 0);
		appVersionTextView.setText(CommonTools.getVersion(this));

	}

	@OnClick({R.id.contactUsTextView, R.id.userFeedbackTextView, R.id.agreementUsTextView, R.id.exitBtn, R.id.ll_upgrade})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.contactUsTextView:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKCONTACTOUR);
				toActivity(ContactUsActivity.class);
				break;
			case R.id.userFeedbackTextView:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKUSERFEEDBACKSEND);
				toActivity(UserFeedbackActivity.class);
				break;
			case R.id.agreementUsTextView:
				String url = SharedUtils.getInstance().readString(IntentPutKeyConstant.USER_AGREEMENT_URL);
				if (!TextUtils.isEmpty(url)) {
					WebViewActivity.launch(SettingActivity.this, url, "用户许可及服务协议");
				}
				break;
			case R.id.ll_upgrade:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKVERSIONUPGRADE);

				Beta.checkUpgrade();
				break;
			case R.id.exitBtn:
				showDialog();
				break;
		}
	}

	private void showDialog() {
		//不能按返回键，只能二选其一
		DialogBalance cardRuleBreakDialog = new DialogBalance(this, this, R.layout.dialog_balance, 2);
		cardRuleBreakDialog.setCanClickBack(false);
		cardRuleBreakDialog.changeText(getResources().getString(R.string.are_you_sure_exit_login), getResources().getString(R.string.sure));
	}


	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_EXIT) {
			ExitHttp exitHttp = (ExitHttp) object;
			if (exitHttp.getStatus() == 1) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (sipEngineCore != null) {
							sipEngineCore = ICSOpenVPNApplication.the_sipengineReceive;
							sipEngineCore.DeRegisterSipAccount();
							sipEngineCore.CoreTerminate();
							ICSOpenVPNApplication.the_sipengineReceive = null;
						}
					}
				}).start();
				exitOperate();
			} else {
				CommonTools.showShortToast(this, exitHttp.getMsg());
			}
		}

	}


	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		exitOperate();
	}

	@Override
	public void noNet() {
		exitOperate();
	}

	/**
	 * 退出操作
	 */

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
		String alias = "";
		handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, alias));
	}

	private void exitOperate() {
		//友盟方法统计
		setAlias();
		MobclickAgent.onEvent(context, CLICKEXITLOGIN);
		SharedUtils sharedUtils = SharedUtils.getInstance();
		sharedUtils.delete(Constant.TOKEN);
		sharedUtils.delete(Constant.PHONE_NUMBER_LIST);
		sharedUtils.delete(Constant.PASSWORD);
		sharedUtils.writeBoolean(Constant.ISFIRSTIN, true);
		sharedUtils.delete(Constant.JPUSH_ALIAS);
		sharedUtils.delete(Constant.TEL);
		sharedUtils.delete(Constant.IMEI);
		sharedUtils.delete(Constant.BRACELETNAME);
		sharedUtils.delete(Constant.NULLCARD_SERIALNUMBER);
		//友盟账号统计
		MobclickAgent.onProfileSignOff();
		Intent intent = new Intent();
		intent.setAction(SportFragment.CLEARSPORTDATA);
		intent.setAction(ProMainActivity.STOP_CELL_PHONE_SERVICE);
		ICSOpenVPNApplication.uartService.disconnect();
		ICSOpenVPNApplication.getInstance().sendBroadcast(intent);
		finish();
		ProMainActivity activity = ProMainActivity.instance;
		if (activity != null)
			activity.finish();
		toActivity(LoginMainActivity.class);
	}


	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			if (!CommonTools.isFastDoubleClick(2000)) {
				//暂不使用退出接口
				ExitHttp http = new ExitHttp(this, HttpConfigUrl.COMTYPE_EXIT);
				new Thread(http).start();
			}
		}
	}
}
