package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.MyPackageActivity;
import de.blinkt.openvpn.activities.PersonalCenterActivity;
import de.blinkt.openvpn.util.DialogUtils;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public class DialogSexAndHeaderAndMyPacket extends DialogBase implements View.OnClickListener {
	TextView tvCancel;
	TextView tvTopLayer;
	TextView tvCenterLayer;
	TextView tvBottomLayer;
	View bottom_line_view;


	public DialogSexAndHeaderAndMyPacket(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type) {
		super(dialogInterfaceTypeBase, context, layoutId, type);
		addListener();
	}

	@Override
	public void setDialogContentView(View view) {
		tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
		tvTopLayer = (TextView) view.findViewById(R.id.tv_top_layer);
		tvCenterLayer = (TextView) view.findViewById(R.id.tv_center_layer);
		tvBottomLayer = (TextView) view.findViewById(R.id.tv_bottom_layer);
		bottom_line_view = (View) view.findViewById(R.id.bottom_line_view);
		if (type == PersonalCenterActivity.PHEADER_PIC) {
			tvTopLayer.setText(R.string.album);
			tvCenterLayer.setText(R.string.take_a_picture);
		} else if (type == PersonalCenterActivity.SEX) {
			tvTopLayer.setText(R.string.man);
			tvCenterLayer.setText(R.string.women);
		} else if (type == MyPackageActivity.BOTTOM_LIST) {
			tvBottomLayer.setVisibility(View.VISIBLE);
			bottom_line_view.setVisibility(View.VISIBLE);
			tvTopLayer.setText(R.string.communication_packet);
			tvCenterLayer.setText(R.string.international_flow_packet);
			tvBottomLayer.setText(R.string.bind_packet_giftcard);
		}
	}

	private void addListener() {
		tvCancel.setOnClickListener(this);
		tvCenterLayer.setOnClickListener(this);
		tvTopLayer.setOnClickListener(this);
		tvBottomLayer.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		dialog.dismiss();
		switch (v.getId()) {
			case R.id.tv_top_layer:
				dialogInterfaceTypeBase.dialogText(type, "0");
				break;
			case R.id.tv_center_layer:
				dialogInterfaceTypeBase.dialogText(type, "1");
				break;
			case R.id.tv_cancel:
				break;
			case R.id.tv_bottom_layer:
				dialogInterfaceTypeBase.dialogText(type, "2");
				break;
		}
	}


	@Override
	public void setDialogStyle() {
		DialogUtils.dialogSet(dialog, context, Gravity.BOTTOM, 0.95, 1, true, false, true);
	}
}
