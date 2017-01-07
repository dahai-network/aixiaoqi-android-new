package de.blinkt.openvpn.views.calendar.doim;

import android.content.Context;

import de.blinkt.openvpn.views.calendar.CalendarView;


/**
 * CalendarView的辅助类
 * @author huang
 *
 */
public class CalendarViewBuilder {
	private CalendarView[] calendarViews;
	/**
	 * 生产多个CalendarView
	 * @param context
	 * @param count
	 * @param callBack
	 * @return
	 */
	public  CalendarView[] createMassCalendarViews(Context context,int count,CalendarView.CallBack callBack){
		calendarViews = new CalendarView[count];
		for(int i = 0; i < count;i++){
			calendarViews[i] = new CalendarView(context,callBack);
		}
		return calendarViews;
	}
	/**
	 * 生产多个CalendarView
	 * @param context
	 * @param count
	 * @param callBack
	 * @return
	 */
	public  CalendarView[] createMassCalendarViews(Context context,int count,CalendarView.CallBack callBack,CustomDate date){
		calendarViews = new CalendarView[count];
		for(int i = 0; i < count;i++){
			calendarViews[i] = new CalendarView(context,callBack,date);
		}
		return calendarViews;
	}
}
