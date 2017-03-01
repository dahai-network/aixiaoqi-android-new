package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;

public class LableActivity extends BaseActivity {

	@BindView(R.id.lableEditText)
	EditText lableEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lable);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasAllViewTitle(R.string.lable,R.string.save_alarm,R.string.cancel,false);
		String lableText = getIntent().getStringExtra("lableText");
		if(!TextUtils.isEmpty(lableText)) {
			lableEditText.setText(lableText);
			lableEditText.setSelection(lableEditText.getText().length());
		}
	}

	@Override
	protected void onClickRightView() {
		Intent intent = new Intent();
		intent.putExtra("lableText",lableEditText.getText().toString());
		setResult(RESULT_OK,intent);
		finish();
	}
}
