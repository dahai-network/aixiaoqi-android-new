package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;

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
		String noremindDateStr = SharedUtils.getInstance().readString(Constant.DIALOG_NOREMIND_DATE);
		if (noremindDateStr != null && noremindDateStr.equals(DateUtils.getCurrentDate())) {
			remindCheckBox.setChecked(false);
			dialogInterfaceTypeBase.dialogText(1, "");
			dialog.dismiss();
		}
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
					if (remindCheckBox.isChecked()) {
						SharedUtils.getInstance().writeString(Constant.DIALOG_NOREMIND_DATE, DateUtils.getCurrentDate());
					}
					dialogInterfaceTypeBase.dialogText(1, "");
				}
				break;
			case R.id.tv_rechange:
				dialog.dismiss();
				//如果我的设备传入的type则取消需要返回
				if (remindCheckBox.isChecked()) {
					SharedUtils.getInstance().writeString(Constant.DIALOG_NOREMIND_DATE, DateUtils.getCurrentDate());
				}
				dialogInterfaceTypeBase.dialogText(type, "");
				break;
		}
	}
}
