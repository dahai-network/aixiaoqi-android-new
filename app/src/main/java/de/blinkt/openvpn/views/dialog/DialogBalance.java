package de.blinkt.openvpn.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.DialogUtils;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class DialogBalance extends DialogBase implements View.OnClickListener {
	protected TextView tvRechange;
	protected TextView tvCancel;
	protected TextView titleTextView;

	public DialogBalance(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type) {
		super(dialogInterfaceTypeBase, context, layoutId, type);
        addListener();
	}

	private void addListener() {
		tvCancel.setOnClickListener(this);
		tvRechange.setOnClickListener(this);
	}

	@Override
	protected void setDialogStyle() {
		DialogUtils.dialogSet(dialog, context, Gravity.CENTER, 0.7, 1, true, false, false);
	}

	public void changeText(String title, String sureText) {
		titleTextView.setText(title);
		tvRechange.setText(sureText);
	}
	public void setCanClickBack(boolean isCanClickBack) {
		dialog.setCancelable(isCanClickBack);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.tv_cancel:
				dialog.dismiss();
				//如果我的设备传入的type则取消需要返回
				if (type == 2) {
					dialogInterfaceTypeBase.dialogText(1, "");
				}else if(type == 3){
					dialogInterfaceTypeBase.dialogText(10, "");
				}
				break;
			case R.id.tv_rechange:
				dialog.dismiss();
				dialogInterfaceTypeBase.dialogText(type, "");
				break;
		}
	}

	public TextView getTitleTextView()
	{
		return titleTextView;
	}

	@Override
	protected void setDialogContentView(View view) {
		tvRechange = (TextView) view.findViewById(R.id.tv_rechange);
		tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
		titleTextView = (TextView) view.findViewById(R.id.titleTextView);

	}
}
