package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.UserFeedBackHttp;
import de.blinkt.openvpn.util.CommonTools;

public class UserFeedbackActivity extends BaseActivity implements InterfaceCallback {

	@BindView(R.id.infoEditText)
	EditText infoEditText;
	@BindView(R.id.sendBtn)
	Button sendBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_feedback);
		ButterKnife.bind(this);
		init();
	}

	private void init() {
		hasLeftViewTitle(R.string.user_feedback,0);
	}

	//获取当前版本号
	private String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo("cn.com.aixiaoqi", 0);
			versionName = packageInfo.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}


	@OnClick(R.id.sendBtn)
	public void onClick() {
		if(TextUtils.isEmpty(infoEditText.getText().toString().trim())){
			CommonTools.showShortToast(this,getString(R.string.feedback_content_is_null));
			return;
		}
		if(infoEditText.getText().toString().trim().length()<10){
			CommonTools.showShortToast(this,getString(R.string.feedback_content_is_too_short));
			return;
		}
		if(infoEditText.getText().toString().trim().length()>500){
			CommonTools.showShortToast(this,getString(R.string.feedback_content_is_too_long));
			return;
		}
		UserFeedBackHttp http = new UserFeedBackHttp(this, HttpConfigUrl.COMTYPE_USER_FEED_BACK, Build.BRAND + Build.MODEL, "" + getAppVersionName(this), infoEditText.getText().toString());
		new Thread(http).start();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		UserFeedBackHttp http = (UserFeedBackHttp) object;
		if (http.getStatus() == 1) {
			CommonTools.showShortToast(UserFeedbackActivity.this, http.getMsg());
			finish();
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(this, "网络异常，请检查您的网络");
	}
}
