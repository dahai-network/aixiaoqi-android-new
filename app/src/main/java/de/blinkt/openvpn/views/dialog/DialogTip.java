package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.DialogUtils;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class DialogTip extends DialogBase implements View.OnClickListener{


    protected TextView tvRechange;
    protected TextView tvContent;
    protected  ImageView ivTip;
    public DialogTip(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type) {
        super(dialogInterfaceTypeBase, context, layoutId, type);
        addListener();
    }

    private void addListener() {
        tvRechange.setOnClickListener(this);
    }

    @Override
    protected void setDialogStyle() {
        DialogUtils.dialogSet(dialog, context, Gravity.CENTER, 0.9, 1, true, false, false);
    }


    public void setCanClickBack(boolean isCanClickBack) {
        dialog.setCancelable(isCanClickBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_tv:
                dialog.dismiss();
                dialogInterfaceTypeBase.dialogText(type,null);
                break;
        }
    }
    public  void setTvRechangeText(int tvRechangeId){
        if(tvRechange!=null){
            tvRechange.setText(context.getString(tvRechangeId));
        }
    }

    public void setTvContentText(int contentid){
        if(tvContent!=null){
            tvContent.setText(context.getString(contentid));
        }
    }

    public void setIvTipDrawable(int drawableId){
        if(ivTip!=null){
            ivTip.setImageResource(drawableId);
        }
    }

    public void setIvTipVisibility(int viaiable){
        if(ivTip!=null){
            ivTip.setVisibility(viaiable);
        }
    }

    @Override
    protected void setDialogContentView(View view) {
        tvContent=(TextView)view.findViewById(R.id.tv_content);
        tvRechange = (TextView) view.findViewById(R.id.sure_tv);
        ivTip = (ImageView) view.findViewById(R.id.iv_tip);
    }
}
