package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2017/5/8.
 */

public class DialogCanNoRemind extends DialogBalance {

	public CheckBox getRemindCheckBox() {
		return remindCheckBox;
	}

	private CheckBox remindCheckBox;

	public DialogCanNoRemind(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int type) {
		super(dialogInterfaceTypeBase, context, R.layout.dialog_no_remind, type);
	}

	@Override
	protected void setDialogContentView(View view) {
		super.setDialogContentView(view);
		remindCheckBox = (CheckBox) view.findViewById(R.id.remindCheckBox);
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
				dialogInterfaceTypeBase.dialogText(type, "");
				break;
		}
	}
}
