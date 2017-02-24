package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.PersonalCenterActivity;
import de.blinkt.openvpn.util.DialogUtils;
import de.blinkt.openvpn.views.PickerScrollView;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public class DialogPicker extends DialogBase implements View.OnClickListener, PickerScrollView.onSelectListener {
	private Button pickerYes;
	private PickerScrollView pickerscrlllview;
	private TextView dialogType;


	public DialogPicker(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type, String title) {
		super(dialogInterfaceTypeBase, context, layoutId, type);
		addListener();

	}

	public DialogPicker(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type, int defaultValue) {
		super(dialogInterfaceTypeBase, context, layoutId, type, defaultValue);
		addListener();

	}

	private void addListener() {
		pickerYes.setOnClickListener(this);
		pickerscrlllview.setOnSelectListener(this,0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.picker_yes: {
				dialog.dismiss();
				dialogInterfaceTypeBase.dialogText(type, value);
			}
		}
	}

	@Override
	public void onSelect(String pickers,int type) {
		value = pickers;
	}

	private String value;

	@Override
	protected void setDialogStyle() {
		DialogUtils.dialogSet(dialog, context, Gravity.BOTTOM, 0.95, 1, true, false, true);
	}

	@Override
	protected void setDialogContentView(View view) {
		pickerscrlllview = (PickerScrollView) view.findViewById(R.id.pickerscrlllview);
		pickerscrlllview.setColor(0x007aff);
		pickerYes = (Button) view.findViewById(R.id.picker_yes);
		dialogType = (TextView) view.findViewById(R.id.dialog_type);
		initData();

	}

	private void initData() {
		List<String> list = new ArrayList<>();
		pickerscrlllview.setColor(Color.BLUE);
		if (type == PersonalCenterActivity.HEIGHT) {
			dialogType.setText(context.getString(R.string.stature));
			for (int i = 0; i <= 110; i++) {
				list.add((110 + i) + "");
			}
			pickerscrlllview.setUnit("  " + context.getString(R.string.cm));
			pickerscrlllview.setData(list);
			if (defaultValue < 110) {
				value = "175";
				pickerscrlllview.setSelected(65);
			} else {
				pickerscrlllview.setSelected(defaultValue - 110);
				value=defaultValue+"";
			}


		} else if (type == PersonalCenterActivity.SPORT_TARGET) {
			dialogType.setText(context.getString(R.string.sport_target));
			for (int i = 0; i <= 30; i++) {
				list.add((1000 + 1000 * i) + "");
			}
			pickerscrlllview.setUnit("  " + context.getString(R.string.step));
			pickerscrlllview.setData(list);
			if (defaultValue < 1000) {
				value = "8000";
				pickerscrlllview.setSelected(7);
			} else {
				pickerscrlllview.setSelected((defaultValue - 1000) / 1000);
				value=defaultValue+"";
			}


		} else if (type == PersonalCenterActivity.WEIGHT) {
			dialogType.setText(context.getString(R.string.weight));
			for (int i = 0; i <= 70; i++) {
				list.add((40 + i) + "");
			}

			pickerscrlllview.setUnit("  " + context.getString(R.string.kg));
			pickerscrlllview.setData(list);


			if (defaultValue < 40) {
				value = "55";
				pickerscrlllview.setSelected(15);
			} else {
				value=defaultValue+"";
				pickerscrlllview.setSelected(defaultValue - 40);
			}

		}
	}

}
