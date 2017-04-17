package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class BuySucceedDialog extends DialogBalance {
    public BuySucceedDialog(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type) {
        super(dialogInterfaceTypeBase, context, layoutId, type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                dialog.dismiss();
                //如果我的设备传入的type则取消需要返回
                dialogInterfaceTypeBase.dialogText(type, "取消");
                break;
            case R.id.tv_rechange:
                dialog.dismiss();
                dialogInterfaceTypeBase.dialogText(type, "");
                break;
        }
    }

    public void changeText(String title, String sureText,String cancelText) {
        titleTextView.setText(title);
        tvRechange.setText(sureText);
        tvCancel.setText(cancelText);
    }
}
