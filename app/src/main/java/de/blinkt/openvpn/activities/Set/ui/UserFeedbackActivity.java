package de.blinkt.openvpn.activities.Set.ui;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Set.PresenterImpl.UserFeedbackPersenterImpl;
import de.blinkt.openvpn.activities.Set.View.UserFeedbackView;
import de.blinkt.openvpn.util.CommonTools;

public class UserFeedbackActivity extends BaseActivity implements UserFeedbackView {
	@BindView(R.id.infoEditText)
	EditText infoEditText;
	@BindView(R.id.sendBtn)
	Button sendBtn;
	UserFeedbackPersenterImpl userFeedbackPersenter;
	@Override
	public void showToast(int toastContentId) {
		CommonTools.showShortToast(this,getString(toastContentId));
	}

	@Override
	public void showToast(String toastContent) {
		CommonTools.showShortToast(this,toastContent);
	}

	@Override
	public String getUserFeedbackContent() {
		return infoEditText.getText().toString().trim();
	}

	@Override
	public void finishView() {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_feedback);
		ButterKnife.bind(this);
		init();
		userFeedbackPersenter=new UserFeedbackPersenterImpl(this);
	}

	private void init() {
		hasLeftViewTitle(R.string.user_feedback,0);
	}
	@OnClick(R.id.sendBtn)
	public void onClick() {
		userFeedbackPersenter.requsetUserFeedback();
	}

	@Override
	protected void onDestroy() {
		userFeedbackPersenter.onDestory();
		if(userFeedbackPersenter!=null){
			userFeedbackPersenter=null;
		}
		super.onDestroy();
	}
}
