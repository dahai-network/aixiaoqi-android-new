package de.blinkt.openvpn.activities.Set.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aixiaoqi.socket.EventBusUtil;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Set.Presenter.SetPresenter;
import de.blinkt.openvpn.activities.Set.PresenterImpl.SetPresenterImpl;
import de.blinkt.openvpn.activities.Set.View.SetView;
import de.blinkt.openvpn.activities.WebViewActivity;
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

public class SettingActivity extends BaseActivity implements SetView,DialogInterfaceTypeBase {

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
	Unbinder unbinder;
	SetPresenter setPersenter;
	@Override
	public void showToast(String toastContent) {
		if(!TextUtils.isEmpty(toastContent))
			CommonTools.showShortToast(this,toastContent);
	}

	@Override
	public void showToast(int toastContentId) {
		CommonTools.showShortToast(this,getString(toastContentId));
	}

	@Override
	public void finishView() {
		finish();
	}

	@Override
	public void startActivity(Class<?> activity) {
		toActivity(activity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		unbinder=ButterKnife.bind(this);
		init();
		setPersenter=new SetPresenterImpl(this);

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
					WebViewActivity.launch(this, url, "用户许可及服务协议");
				}
				break;
			case R.id.ll_upgrade:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKVERSIONUPGRADE);
				if(!Constant.IS_DEBUG){
				Beta.checkUpgrade();
				}else{
					CommonTools.showShortToast(this,"测试环境不支持升级，请在正式环境下测试");
				}
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
	public void dialogText(int type, String text) {
		if (type == 2) {
			if (!CommonTools.isFastDoubleClick(2000)) {
				//暂不使用退出接口
				setPersenter.requsetExitLogin();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(unbinder!=null){
			unbinder.unbind();
		}
		setPersenter.onDestory();

	}
}
