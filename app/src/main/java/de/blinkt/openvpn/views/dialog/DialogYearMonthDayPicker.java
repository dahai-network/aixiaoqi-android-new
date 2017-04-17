package de.blinkt.openvpn.views.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.DialogUtils;
import de.blinkt.openvpn.views.PickerScrollView;


/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class DialogYearMonthDayPicker extends DialogBase implements View.OnClickListener,PickerScrollView.onSelectListener {
    TextView tvSure;
    PickerScrollView pickerscrlllviewyear;
    PickerScrollView pickerscrlllviewmonth;
    PickerScrollView  pickerscrlllviewday;
    List<String> list_big;
    List<String> list_little;
    private List listYear;
    private List listMonth;
    private List  listDay;
    int yearType=0;
    int monthType=1;
    int dayType=2;
    int year;
    int month;
    int day;
    public String value;
    private TextView title;
    public DialogYearMonthDayPicker(DialogInterfaceTypeBase dialogInterfaceTypeBase, Context context, int layoutId, int type ){
        super(dialogInterfaceTypeBase,context,layoutId,type);
        addListener();
    }
    @Override
    protected void setDialogContentView(View view) {
        tvSure=(TextView)view.findViewById(R.id.tv_sure);
        title=(TextView)view.findViewById(R.id.title);
        pickerscrlllviewyear = (PickerScrollView)view.findViewById(R.id.pickerscrlllviewyear);
        pickerscrlllviewyear.setColor(0x007aff);
        pickerscrlllviewmonth = (PickerScrollView)view.findViewById(R.id.pickerscrlllviewmonth);
        pickerscrlllviewmonth.setColor(0x007aff);
        pickerscrlllviewday = (PickerScrollView)view.findViewById(R.id.pickerscrlllviewday);
        pickerscrlllviewday.setColor(0x007aff);
        initData();
    }
    private void addListener(){
        tvSure.setOnClickListener(this);
        pickerscrlllviewmonth.setOnSelectListener(this,monthType);
        pickerscrlllviewyear.setOnSelectListener(this,yearType);
        pickerscrlllviewday.setOnSelectListener(this,dayType);

    }
    public void changeText(String titleText){
        title.setText(titleText);
    }
    private  void initData(){
        String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
        String[] months_little = { "4", "6", "9", "11" };
        list_big = Arrays.asList(months_big);
        list_little = Arrays.asList(months_little);
        listYear=new ArrayList<>();
        listMonth=new ArrayList<>();
        listDay=new ArrayList<>();
        pickerscrlllviewyear.setColor(Color.BLUE);
        pickerscrlllviewmonth.setColor(Color.BLUE);
        Calendar calendar=Calendar.getInstance();
        year=   calendar.get(Calendar.YEAR);
        month=   calendar.get(Calendar.MONTH)+1;
        day=   calendar.get(Calendar.DAY_OF_MONTH);
		int totalYear = year;
		List totalListYear = listYear;
        for(int i=totalYear;i<=totalYear+10;i++){
			totalListYear.add((i)+"");
        }
        pickerscrlllviewyear.setUnit("  "+context.getString(R.string.year));
        pickerscrlllviewyear.setData(listYear);
		List totalListMonth = listMonth;
        for(int i=1;i<=12;i++){
            if(i<=9)
				totalListMonth.add("0"+i+"");
            else{
				totalListMonth.add(i+"");
            }
        }
        pickerscrlllviewmonth.setUnit("  "+context.getString(R.string.month));
        pickerscrlllviewmonth.setData(listMonth);
        pickerscrlllviewyear.setSelected(0);
        pickerscrlllviewmonth.setSelected(month-1);

        setDay(year, month);
        pickerscrlllviewday.setUnit("  "+context.getString(R.string.daliy));

    }

    private void setDay(int year, int month) {
        if (list_big.contains(String.valueOf(month))) {
            setDayCount(31);
        } else if (list_little.contains(String.valueOf(month))) {
            setDayCount(30);
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                setDayCount(29);
            else
                setDayCount(28);
        }
    }

    private void setDayCount(int count) {
        listDay.clear();
		List totalListDay = listDay;
        for(int i=1;i<=count;i++){
            if(i<=9)
				totalListDay.add("0"+i+"");
            else{
				totalListDay.add(i+"");
            }
        }
        pickerscrlllviewday.setData(listDay);
        if(day>count)
            pickerscrlllviewday.setSelected(count-1);
        else
        pickerscrlllviewday.setSelected(day-1);
    }

    @Override
    protected void setDialogStyle() {
        DialogUtils.dialogSet(dialog, context, Gravity.BOTTOM, 0.95, 1,true,false, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sure:
                dialog.dismiss();
                value=year+"-"+month+"-"+day;
                dialogInterfaceTypeBase.dialogText(type,value);
                break;
        }
    }

    @Override
    public void onSelect(String pickers,int type) {
        if(type==yearType){
            year=Integer.parseInt(pickers);
            setDay(year,month);
        }else if(type==monthType){
            month=Integer.parseInt(pickers);
            setDay(year,month);
        }else{
            day=Integer.parseInt(pickers);
        }

    }
}
