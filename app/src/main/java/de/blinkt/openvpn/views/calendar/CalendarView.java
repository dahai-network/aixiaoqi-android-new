package de.blinkt.openvpn.views.calendar;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.blinkt.openvpn.views.calendar.doim.CustomDate;
import de.blinkt.openvpn.views.calendar.doim.DateUtil;

public class CalendarView extends View {


	private static final int TOTAL_COL = 7;
	private static final int TOTAL_ROW = 6;
	private Paint mCirclePaint;
	private Paint mTextPaint;
	private Paint mLinePaint;
	private int mViewWidth;
	private int mViewHight;
	private int mCellSpace;
	private Row rows[] = new Row[TOTAL_ROW];
	public static CustomDate mShowDate;//
	private CallBack mCallBack;//
	private int touchSlop;
	private boolean callBackCellSpace;

	public interface CallBack {

		void clickDate(CustomDate date);//

		void onMesureCellHeight(int cellSpace);//

		void changeDate(CustomDate date);//
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);

	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

	}

	public CalendarView(Context context) {
		super(context);
		init(context);
	}

	public CalendarView(Context context, CallBack mCallBack) {
		super(context);

		this.mCallBack = mCallBack;
		init(context);
	}

	public CalendarView(Context context, CallBack mCallBack, CustomDate date) {
		super(context);
		this.mCallBack = mCallBack;
		init(context, date);
	}


	List<Integer> clickList = new ArrayList<>();
	int defaultDay=-1;
	int defaultYear=-1;
	int defaultMonth=-1;
	public void setList(List<Integer> clickList) {

		this.clickList.clear();
		this.clickList = clickList;
		fillDate();
	}

	public void setList(List<Integer> clickList,int defaultYear,int defaultMonth,int defaultDay) {
		this.defaultDay=defaultDay;
		this.defaultMonth=defaultMonth;
		this.defaultYear=defaultYear;
		this.clickList.clear();
		this.clickList = clickList;
		fillDate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < TOTAL_ROW; i++) {
			if (rows[i] != null)
				rows[i].drawCells(canvas);
		}
	}

	private void init(Context context) {
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint = new Paint();
		mLinePaint.setColor(Color.RED);
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setStyle(Paint.Style.FILL);
//		mCirclePaint.setColor(Color.parseColor("#F24949"));
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		initDate();

	}

	private void init(Context context, CustomDate date) {
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint = new Paint();
		mLinePaint.setColor(Color.RED);
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setStyle(Paint.Style.FILL);
//		mCirclePaint.setColor(Color.parseColor("#F24949"));
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		initDate(date);

	}

	private void initDate(CustomDate date) {
		mShowDate = date;
		fillDate();
	}

	private void initDate() {
		mShowDate = new CustomDate();
		fillDate();
	}

	public void initDate(int year, int month, int day) {
		mShowDate = new CustomDate(year, month, day);
		fillDate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mViewWidth = w;
		mViewHight = h;
		mCellSpace = Math.min(mViewHight / TOTAL_ROW, mViewWidth / TOTAL_COL);
		if (!callBackCellSpace) {
			mCallBack.onMesureCellHeight(mCellSpace / 2);
			callBackCellSpace = true;
		}
		mTextPaint.setTextSize(mCellSpace / 3);
	}

	private Cell mClickCell;
	private float mDownX;
	private float mDownY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getX();
				mDownY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				float disX = event.getX() - mDownX;
				float disY = event.getY() - mDownY;
				if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
					int col = (int) (mDownX / mCellSpace);
					int row = (int) (mDownY * 3 / (2 * mCellSpace));
					measureClickCell(col, row);
				}
				break;
		}
		return true;
	}

	private void measureClickCell(int col, int row) {
		invalidate();
		int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year, mShowDate.month);
		CalendarView.Row[] localRows = rows;
		List<Integer> localClickList = clickList;
		int length = localClickList.size();
		if (col >= TOTAL_COL || row >= TOTAL_ROW)
			return;
		if (mClickCell != null) {
			localRows[mClickCell.j].cells[mClickCell.i] = mClickCell;
		}
		if (localRows[row] != null) {
			int day = 1 + row * TOTAL_COL + col - firstDayWeek;
			for (int i = 0; i < length; i++) {
				if (day == localClickList.get(i)) {
					mClickCell = new Cell(localRows[row].cells[col].date,
							localRows[row].cells[col].state, localRows[row].cells[col].i,
							localRows[row].cells[col].j);
//					localRows[row].cells[col].state = State.CLICK_DAY;
					CustomDate date = localRows[row].cells[col].date;
					mCallBack.clickDate(date);

					break;
				}
			}
		}
	}


	class Row {
		public int j;

		Row(int j) {
			this.j = j;
		}

		public Cell[] cells = new Cell[TOTAL_COL];

		public void drawCells(Canvas canvas) {
			int length = cells.length;
			for (int i = 0; i < length; i++) {
				if (cells[i] != null)
					cells[i].drawSelf(canvas);
			}

		}
	}


	class Cell {
		public CustomDate date;
		public State state;
		public int i;
		public int j;

		public Cell(CustomDate date, State state, int i, int j) {
			super();
			this.date = date;
			this.state = state;
			this.i = i;
			this.j = j;
		}


		public void drawSelf(Canvas canvas) {
			switch (state) {
				case CURRENT_MONTH_DAY:
					mTextPaint.setColor(Color.parseColor("#ff000000"));
					break;
				case PAST_MONTH_DAY:
					mTextPaint.setColor(Color.parseColor("#ababab"));
					break;
				case CLICK_DAY:
					mCirclePaint.setColor(Color.parseColor("#F24949"));
					mTextPaint.setColor(Color.parseColor("#fffffe"));
					canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
							(float) ((j + 0.5) * mCellSpace * 2 / 3), mCellSpace / 3,
							mCirclePaint);
					break;
				case CLICK_TODAY:
					mTextPaint.setColor(Color.parseColor("#ffff0000"));
					break;
			}
			String content;
			if(date!=null){
				content = date.day + "";
			}else{
				content=" ";
			}
			canvas.drawText(content,
					(float) ((i + 0.5) * mCellSpace - mTextPaint.measureText(content) / 2),
					(float) ((j + 0.8) * mCellSpace * 2 / 3 - mTextPaint.measureText(
							content, 0, 1) / 3), mTextPaint);
		}
	}

	enum State {
		CURRENT_MONTH_DAY, PAST_MONTH_DAY, CLICK_DAY,CLICK_TODAY
	}


	private void fillDate() {
		invalidate();
		fillMonthDate();
//		invalidate();
	}

	private void fillMonthDate() {

		int currentMonthDays = DateUtil.getMonthDays(mShowDate.year, mShowDate.month);
		int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year, mShowDate.month);
		Calendar calendar = Calendar.getInstance();
		int currentMonth = calendar.get(Calendar.MONTH)+1;
		int currentYear = calendar.get(Calendar.YEAR);
		int currentDay =calendar.get(Calendar.DAY_OF_MONTH);
		int day = 0;
//		Log.e("CalenderView","year="+mShowDate.year+","+currentYear+"month="+mShowDate.month+","+currentMonth+"day="+day+","+currentDay);
		for (int j = 0; j < TOTAL_ROW; j++) {
			rows[j] = new Row(j);
			Col:
			for (int i = 0; i < TOTAL_COL; i++) {
				int postion = i + j * TOTAL_COL;
				if (postion >= firstDayWeek
						&& postion < firstDayWeek + currentMonthDays) {
					day++;
					for (int k = 0; k < clickList.size(); k++) {
						if (day == clickList.get(k)) {
							if (mShowDate.year == currentYear && mShowDate.month == currentMonth && day == currentDay) {
								CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
								mClickCell = new Cell(date, State.CURRENT_MONTH_DAY, i, j);
								rows[j].cells[i] = new Cell(date, State.CLICK_TODAY, i, j);
							} else if(mShowDate.year == defaultYear && mShowDate.month == defaultMonth&&day==defaultDay){
								CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
								mClickCell = new Cell(date, State.CURRENT_MONTH_DAY, i, j);
								rows[j].cells[i] = new Cell(date, State.CLICK_DAY, i, j);
							}else {
								CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
								rows[j].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, j);
							}
							continue Col;
						}

					}
					rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(mShowDate, day),
							State.PAST_MONTH_DAY, i, j);
				}


	}

}
}
	public void update() {
		fillDate();
		mCallBack.changeDate(mShowDate);

	}


	public void nextMonth() {
//		Calendar calendar = Calendar.getInstance();
//		int month = calendar.get(Calendar.MONTH)+1;
//		int year = calendar.get(Calendar.YEAR);
//
//		if(year==mShowDate.year&&month==mShowDate.month){
//			return;
//		}
		if (mShowDate.month == 12) {
			mShowDate.month = 1;
			mShowDate.year += 1;
		} else {
			mShowDate.month += 1;
		}
		update();
	}

	public void lastMonth() {
		if (mShowDate.month == 1) {
			mShowDate.month = 12;
			mShowDate.year -= 1;
		} else {
			mShowDate.month -= 1;
		}
		update();
	}
}
