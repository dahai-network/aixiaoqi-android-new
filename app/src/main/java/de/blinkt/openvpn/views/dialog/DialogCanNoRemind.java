package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.widget.CheckBox;

/**
 * Created by Administrator on 2017/5/8.
 */

public class DialogCanNoRemind extends DialogBalance{

	private CheckBox remindCheckBox;

	public DialogCanNoRemind(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type) {
		super(dialogInterfaceTypeBase, context, layoutId, type);
	}
}
