package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.DialogUtils;
import de.blinkt.openvpn.views.PickerScrollView;

/**
 * Created by Administrator on 2016/12/1 0001.
 */
public class DialogAddress extends DialogBase implements View.OnClickListener,PickerScrollView.onSelectListener{
    private List<String> list;
    PickerScrollView pickerScrollViewAddress;
    TextView title;
    private String value;
    public DialogAddress(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type,List<String> list){
        super(dialogInterfaceTypeBase,context,layoutId,type);
        this.list=list;
        addListener();
        initData();
        pickerScrollViewAddress.setOnSelectListener(this,type);
    }
    private  void initData(){
        pickerScrollViewAddress.setColor(Color.BLUE);
        pickerScrollViewAddress.setData(list);
        pickerScrollViewAddress.setUnit("");

    }
    public void setDefaultValue(String address){
        pickerScrollViewAddress.setSelected(address);
        value=address;
    }
    private void addListener(){
        title.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title:
                dialog.dismiss();
                dialogInterfaceTypeBase.dialogText(type,value);
                break;
        }
    }

    @Override
    protected void setDialogStyle() {
        DialogUtils.dialogSet(dialog, context, Gravity.BOTTOM, 1, 1,true,false, false);
    }

    @Override
    protected void setDialogContentView(View view) {
        title=  (TextView)view.findViewById(R.id.title);
        pickerScrollViewAddress=  (PickerScrollView)view.findViewById(R.id.pickerscrlllviewaddress);

    }

    @Override
    public void onSelect(String pickers, int type) {
        value=pickers;
    }
}
