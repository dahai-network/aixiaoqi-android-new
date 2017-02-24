package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.DialogUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.PickerScrollView;

/**
 * Created by Administrator on 2016/9/30 0030.
 */
public class DialogTimePicker extends DialogBase implements View.OnClickListener,PickerScrollView.onSelectListener{
	private String yearValue;
	private String monhtValue;
	private TextView tvSure;
	private PickerScrollView pickerscrlllviewyear;
	private PickerScrollView pickerscrlllviewmonth;
	private int year=0;
	private int month=1;
	public DialogTimePicker(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type ){
		super(dialogInterfaceTypeBase,context,layoutId,type);
		addListener();
	}
	@Override
	protected void setDialogContentView(View view) {
		tvSure=(TextView)view.findViewById(R.id.tv_sure);
		pickerscrlllviewyear = (PickerScrollView)view.findViewById(R.id.pickerscrlllviewyear);
		pickerscrlllviewyear.setColor(0x007aff);
		pickerscrlllviewmonth = (PickerScrollView)view.findViewById(R.id.pickerscrlllviewmonth);
		pickerscrlllviewmonth.setColor(0x007aff);
		initData();
	}
	private void addListener(){
		tvSure.setOnClickListener(this);
		pickerscrlllviewmonth.setOnSelectListener(this,month);
		pickerscrlllviewyear.setOnSelectListener(this,year);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_sure:
				dialog.dismiss();
				String value=yearValue+"-"+monhtValue+"-"+"01"+" "+"00"+":"+"00"+":"+"00";
				dialogInterfaceTypeBase.dialogText(type,value);
				break;
		}
	}
	private void initData(){
		List listYear=new ArrayList<>();
		List listMonth=new ArrayList<>();
		pickerscrlllviewyear.setColor(Color.BLUE);
		pickerscrlllviewmonth.setColor(Color.BLUE);
		Calendar calendar=Calendar.getInstance();
		int year=  calendar.get(Calendar.YEAR);

		for(int i=1950;i<=year;i++){
			listYear.add((i)+"");
		}
		pickerscrlllviewyear.setUnit("  "+context.getString(R.string.year));
		pickerscrlllviewyear.setData(listYear);
		SharedUtils sharedUtils=SharedUtils.getInstance();
		String defaultValue=sharedUtils.readString(Constant.BRITHDAY);
		int defaultYear=1990;
		int defaultMonth=1;
		yearValue=String.valueOf(defaultYear);
		monhtValue="0"+String.valueOf(defaultMonth);
		if(defaultValue.length()>6){
			defaultYear=   Integer.parseInt(defaultValue.substring(0,4));
			yearValue=String.valueOf(defaultYear);
			if(defaultValue.endsWith(context.getString(R.string.month)))
				defaultMonth=    Integer.parseInt(defaultValue.substring(5,defaultValue.length()-1));
			else{
				defaultMonth=    Integer.parseInt(defaultValue.substring(5,defaultValue.length()));
			}
			if( defaultMonth<=9)
				monhtValue="0"+String.valueOf(defaultMonth);
			else{
				monhtValue=String.valueOf(defaultMonth);
			}
		}
		for(int i=1;i<=12;i++){
			if(i<=9)
				listMonth.add("0"+i+"");
			else{
				listMonth.add(i+"");
			}
		}
		pickerscrlllviewmonth.setUnit("  "+context.getString(R.string.month));
		pickerscrlllviewmonth.setData(listMonth);
		pickerscrlllviewyear.setSelected(defaultYear-1950);
		pickerscrlllviewmonth.setSelected(defaultMonth-1);



	}
	@Override
	public void onSelect(String pickers,int type) {
		if(type==0){
			yearValue=pickers;
		}else{
			monhtValue=pickers;
		}
	}

	@Override
	protected void setDialogStyle() {
		DialogUtils.dialogSet(dialog, context, Gravity.BOTTOM, 0.95, 1,true,false, true);
	}
}
