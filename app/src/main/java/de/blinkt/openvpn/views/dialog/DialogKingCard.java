package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2016/12/22.
 */

public class DialogKingCard extends DialogBalance {
	//价格
	private final float price;
	private EditText inputKingcardPhoneNumberEditText;

	public DialogKingCard(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, float price) {
		super(dialogInterfaceTypeBase, context, R.layout.dialog_activate_kingcard, 2);
		this.price = price;
	}

	@Override
	protected void setDialogContentView(View view) {
		super.setDialogContentView(view);
		inputKingcardPhoneNumberEditText = (EditText) view.findViewById(R.id.inputKingcardPhoneNumberEditText);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_cancel:
				dialog.dismiss();
				//如果我的设备传入的type则取消需要返回
				if (type == 2) {
					dialogInterfaceTypeBase.dialogText(1, "");
				}
				break;
			case R.id.tv_rechange:
				dialog.dismiss();
				dialogInterfaceTypeBase.dialogText(type, inputKingcardPhoneNumberEditText.getText().toString());
				break;
		}
	}

	@Override
	public void changeText(String title, String sureText) {
		super.changeText("￥" + title, sureText);
		setSpan(getTitleTextView());
	}

	//设置大小字体
	public void setSpan(TextView textview) {
		Spannable WordtoSpan = new SpannableString(textview.getText().toString());
		int intLength = String.valueOf((int) price).length();
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

}
