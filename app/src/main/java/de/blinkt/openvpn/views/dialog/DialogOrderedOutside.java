package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.DialogUtils;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class DialogOrderedOutside extends DialogBase implements View.OnClickListener {
    private TextView cancelDialog;
    private TextView knowNotTip;
    @Override
    protected void setDialogContentView(View view) {
        cancelDialog = (TextView) view.findViewById(R.id.cancel_dialog);
        knowNotTip = (TextView) view.findViewById(R.id.know_not_tip);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void setDialogStyle() {
        DialogUtils.dialogSet(dialog, context, Gravity.CENTER, 0.7, 1, true, false, false);
    }
    public DialogOrderedOutside(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type) {
        super(dialogInterfaceTypeBase, context, layoutId, type);
        addListener();
    }
    private void addListener(){
        knowNotTip.setOnClickListener(this);
        cancelDialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
        switch (v.getId()){
            case R.id.cancel_dialog:
                break;
            case R.id.know_not_tip:
                dialogInterfaceTypeBase.dialogText(type,"");
                break;
        }
    }
}
