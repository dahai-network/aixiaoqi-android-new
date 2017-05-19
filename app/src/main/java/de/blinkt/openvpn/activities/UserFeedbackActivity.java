package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.UserFeedBackHttp;
import de.blinkt.openvpn.util.CommonTools;

public class UserFeedbackActivity extends BaseNetActivity   {
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
		createHttpRequest( HttpConfigUrl.COMTYPE_USER_FEED_BACK,Build.MANUFACTURER + Build.BRAND + Build.MODEL, "" + CommonTools.getVersion(this), infoEditText.getText().toString());
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {

		if (object.getStatus() == 1) {
			finish();
		}
		CommonTools.showShortToast(UserFeedbackActivity.this, object.getMsg());
	}

}
