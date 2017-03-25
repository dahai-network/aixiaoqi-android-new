package de.blinkt.openvpn.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
abstract public class DialogBase{
    protected int defaultValue;
    protected DialogInterfaceTypeBase dialogInterfaceTypeBase;
    protected Dialog dialog;
    protected Context context;
    private     int layoutId;
    protected int type;
    public DialogBase(DialogInterfaceTypeBase dialogInterfaceTypeBase,Context context,int layoutId,int type){
        this.dialogInterfaceTypeBase = dialogInterfaceTypeBase;
        this.context=context;
        this.layoutId=layoutId;
        this.type=type;
        initDialog();
    }
    public DialogBase(DialogInterfaceTypeBase dialogInterfaceTypeBase,Context context,int layoutId,int type,int defaultValue){
        this.dialogInterfaceTypeBase = dialogInterfaceTypeBase;
        this.context=context;
        this.layoutId=layoutId;
        this.defaultValue=defaultValue;
        this.type=type;
        initDialog();
    }

    abstract protected void setDialogContentView(View view);
    abstract protected void setDialogStyle();
    private void initDialog(){
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(layoutId, null);
            dialog = new Dialog(context, R.style.dialog);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(view);
            setDialogContentView(view);
            setDialogStyle();
            dialog.show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
