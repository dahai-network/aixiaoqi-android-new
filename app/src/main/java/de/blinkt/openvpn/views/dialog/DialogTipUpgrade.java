package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DialogUtils;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class DialogTipUpgrade extends DialogBase implements View.OnClickListener {
    private TextView tvRechange;
    private TextView tvCancel;
    private TextView titleTextView;
    private TextView upgradeTextView;

    public DialogTipUpgrade(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type) {
        super(dialogInterfaceTypeBase, context, layoutId, type);

    }

    private void addListener() {
        tvCancel.setOnClickListener(this);
        tvRechange.setOnClickListener(this);
    }

    public void changeText(String title, String dsc) {
        if(titleTextView==null){
            CommonTools.delayTime(10);
            changeText(title,dsc);
            return;
        }
        titleTextView.setText(title);
        upgradeTextView.setText(dsc);
    }



    @Override
    protected void setDialogStyle() {
        DialogUtils.dialogSet(dialog, context, Gravity.CENTER, 0.7, 1, true, false, false);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.tv_upgrade:
                dialogInterfaceTypeBase.dialogText(type, "");
                break;
        }
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    @Override
    protected void setDialogContentView(View view) {
        tvRechange = (TextView) view.findViewById(R.id.tv_upgrade);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        upgradeTextView = (TextView) view.findViewById(R.id.upgradeTextView);
        addListener();
    }
}
