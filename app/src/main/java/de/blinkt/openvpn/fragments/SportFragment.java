package de.blinkt.openvpn.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.SportPeriodAdapter;
import de.blinkt.openvpn.activities.PersonalCenterActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.SportPeriodHttp;
import de.blinkt.openvpn.http.SportRecordDateHttp;
import de.blinkt.openvpn.model.DataEntitiy;
import de.blinkt.openvpn.model.TimePeriodsEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.ArcProgress;
import de.blinkt.openvpn.views.calendar.CalendarView;
import de.blinkt.openvpn.views.calendar.CalendarViewPagerLisenter;
import de.blinkt.openvpn.views.calendar.CustomViewPagerAdapter;
import de.blinkt.openvpn.views.calendar.doim.CalendarViewBuilder;
import de.blinkt.openvpn.views.calendar.doim.CustomDate;

import static de.blinkt.openvpn.constant.UmengContant.SELECTSPECIFIEDDAYSPORTDATA;
import static de.blinkt.openvpn.constant.UmengContant.SPORTPERSONCENTER;

/**
 * Created by Administrator on 2016/9/1 0001.
 */


/**
 * A simple {@link Fragment} subclass.
 */
public class SportFragment extends Fragment implements View.OnClickListener, CalendarView.CallBack, InterfaceCallback {
	public static String REALTIMESTEP = "realtimestep";
	public static String REFRESHSTEP = "refreshstep";
	public static String CLEARSPORTDATA = "clearsportdata";
	TextView kcalTextView;
	TextView sportTextView;
	TextView kmTextView;
	ArcProgress arcProgress;
	@BindView(R.id.sportDataRecylerView)
	RecyclerView sportDataRecylerView;
	@BindView(R.id.tv_show_month_view)
	TextView tvMonth;
	@BindView(R.id.tv_sport_like)
	TextView tvSportLike;
	@BindView(R.id.viewpager)
	ViewPager viewPager;
	@BindView(R.id.calendar_image)
	TextView calendarImage;
	@BindView(R.id.tv_last_month)
	TextView tvLastMonth;
	@BindView(R.id.tv_show_month_view_dialog)
	TextView tvShowMonthView;
	@BindView(R.id.tv_next_month)
	TextView tvNextMonth;
	@BindView(R.id.ll_dialog_calendar)
	LinearLayout llDialogCalendar;

	@BindView(R.id.rl_sport_data)
	RelativeLayout rlSportData;
	@BindView(R.id.rl_dialog_top)
	RelativeLayout rlDialogTop;
	@BindView(R.id.sportNoDataLinearLayout)
	LinearLayout sportNoDataLinearLayout;
	//历史步数
	private int totalStep;
	//实时步数
	private static int currentStep;


	private View rootView;
	private CalendarView[] views;
	private CalendarViewBuilder builder;
	String TimeDialog;
	private CustomDate defaultCustomDate;
	int i = 0;
	Timer timer;
	private List<TimePeriodsEntity> mList;
	SportPeriodAdapter sportDataAdapter;
	private String kmResult;
	private String kcResult;

	public SportFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		rootView = inflater.inflate(R.layout.fragment_sport, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		addListener();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(!isVisibleToUser){
			if(llDialogCalendar!=null)
			llDialogCalendar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_show_month_view:
			case R.id.calendar_image:
				showDialogCalendar();
				break;
			case R.id.tv_sport_like:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), SPORTPERSONCENTER);
				Intent intent = new Intent(getActivity(), PersonalCenterActivity.class);
				getActivity().startActivity(intent);
				break;
			case R.id.tv_last_month:
				views[viewPager.getCurrentItem() % views.length].lastMonth();
				break;
			case R.id.tv_next_month:
				views[viewPager.getCurrentItem() % views.length].nextMonth();
				break;
			case R.id.rl_sport_data:
				llDialogCalendar.setVisibility(View.GONE);
				break;

		}
	}

	public void showDialogCalendar() {
		llDialogCalendar.setVisibility(View.VISIBLE);
		if(!TimeDialog.endsWith(getString(R.string.month))){
			tvShowMonthView.setText(TimeDialog+getString(R.string.month));
		}else{
			tvShowMonthView.setText(TimeDialog);
		}
		String stringRecordTime = Integer.parseInt(TimeDialog.substring(0, 4)) + "-" +TimeDialog.substring(5, 7) + "-" + "01" + " 12:00:00";
		List<Integer> listDate = new ArrayList<>();
		listDate.add(defaultCustomDate.day);
		views[viewPager.getCurrentItem() % views.length].setList(listDate);
		views[viewPager.getCurrentItem() % views.length].initDate(Integer.parseInt(TimeDialog.substring(0, 4)), Integer.parseInt(TimeDialog.substring(5, 7)), 1);

		long timeRecordStamp = DateUtils.getStringToDate(stringRecordTime);
		String recordTime = String.valueOf(timeRecordStamp / 1000);
		SportRecordHttp(recordTime);
	}


	Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					int step = msg.arg1;
					int stepTemp = 0;
					int targetStep = getTargetStep();
					int progress = step * 100 / targetStep;
					if (progress > 100) {
						progress = 100;
					}
					if (i <= progress) {
						if (0 < i && i < 100) {
							stepTemp = step * i / progress;
							arcProgress.setStep(i, stepTemp);
						} else {
							arcProgress.setStep(i, step);
						}
						i++;
					} else {
						arcProgress.invalidate();
						timer.cancel();
					}
					break;
			}

		}
	};

	private int getTargetStep() {
		SharedUtils sharedUtils = SharedUtils.getInstance();
		int targetStep = 0;
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.SOPRT_TARGET))) {
			targetStep = Integer.parseInt(sharedUtils.readString(Constant.SOPRT_TARGET));
		}
		if (targetStep < 1000) {
			targetStep = 8000;
		}
		return targetStep;
	}

	@Override
	public void onResume() {
		super.onResume();
		sportTextView.setText(getTargetStep() + "");
	}


	private void init() {

		builder = new CalendarViewBuilder();
		defaultCustomDate = new CustomDate();
		views = builder.createMassCalendarViews(getActivity(), 5, this);
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		defaultCustomDate.day = day;
		defaultCustomDate.month = month + 1;
		defaultCustomDate.year = calendar.get(Calendar.YEAR);
		String dayString;
		if (day < 10) {
			dayString = "0" + day;
		} else {
			dayString = day + "";
		}
		String monthString;
		if (month < 10) {
			monthString = "0" + (month+1);
		} else {
			monthString =  (month+1)+"";
		}

		tvMonth.setText(monthString + getString(R.string.month) + dayString+getString(R.string.daliy));
		TimeDialog = calendar.get(Calendar.YEAR) + getString(R.string.year) + monthString + getString(R.string.month);
		initSportData(calendar, month, day);
		CustomViewPagerAdapter<CalendarView> viewPagerAdapter = new CustomViewPagerAdapter<>(views);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(498);
		viewPager.setOnPageChangeListener(new CalendarViewPagerLisenter(viewPagerAdapter));
		sportDataRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		sportDataRecylerView.setNestedScrollingEnabled(false);
		mList = new ArrayList<>();
		sportDataAdapter = new SportPeriodAdapter(getActivity(), mList);
		sportDataRecylerView.setAdapter(sportDataAdapter);
		setHeader(sportDataRecylerView);
		ICSOpenVPNApplication.getInstance().registerReceiver(realStepReceiver, getFilter());
	}

	private void setHeader(ViewGroup view) {
		View header = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sport_recycler_header, view, false);
		arcProgress = (ArcProgress) header.findViewById(R.id.arc_progress);
		kcalTextView = (TextView) header.findViewById(R.id.kcalTextView);
		sportTextView = (TextView) header.findViewById(R.id.sportTextView);
		kmTextView = (TextView) header.findViewById(R.id.kmTextView);
		if (view instanceof RecyclerView)
			sportDataAdapter.setHeaderView(header);
		else if (view instanceof LinearLayout) {

		}
	}


	private void initSportData(Calendar calendar, int month, int day) {
		String stringTime = calendar.get(Calendar.YEAR) + "-" + (month + 1) + "-" + day + " 00:00:00";
		long timeStamp = DateUtils.getStringToDate(stringTime);
		String time = String.valueOf(timeStamp / 1000);
		SportPeriodHttp(time);
		String stringRecordTime = calendar.get(Calendar.YEAR) + "-" + (month + 1) + "-" + "01" + " 12:00:00";
		long timeRecordStamp = DateUtils.getStringToDate(stringRecordTime);
		String recordTime = String.valueOf(timeRecordStamp / 1000);
		SportRecordHttp(recordTime);
	}

	private void addListener() {
		tvMonth.setOnClickListener(this);
		tvSportLike.setOnClickListener(this);
		tvNextMonth.setOnClickListener(this);
		tvLastMonth.setOnClickListener(this);
		rlSportData.setOnClickListener(this);
		rlDialogTop.setOnClickListener(this);
		calendarImage.setOnClickListener(this);
		llDialogCalendar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				llDialogCalendar.setVisibility(View.GONE);
			}
		});
	}

	public void setShowDateViewText(int year, int month, int day) {
		String monthString;
		if (month < 10) {
			monthString = "0" + month;
		} else {
			monthString = month + "";
		}
		tvShowMonthView.setText(year + getString(R.string.year) + monthString + getString(R.string.month));
	}

	public void setShowDateText(int year, int month, int day) {
		String monthString;
		String dayString;
		if (month < 10) {
			monthString = "0" + month;
		} else {
			monthString = month + "";
		}
		if (day < 10) {
			dayString = "0" + day;
		} else {
			dayString = day + "";
		}
		tvMonth.setText(monthString + getString(R.string.month) + dayString+getString(R.string.daliy));
	}

	@Override
	public void onMesureCellHeight(int cellSpace) {

	}

	@Override
	public void clickDate(CustomDate date) {
		//友盟方法统计
		if(!NetworkUtils.isNetworkAvailable(getActivity())){
			CommonTools.showShortToast(getActivity(), getString(R.string.no_wifi));
			return;
		}
		MobclickAgent.onEvent(getActivity(), SELECTSPECIFIEDDAYSPORTDATA);
		defaultCustomDate = date;
		llDialogCalendar.setVisibility(View.GONE);
		setShowDateText(date.year, date.month, date.day);
		String stringTime = date.year + "-" + date.month + "-" + date.day + " 00:00:00";
		String month;
		if (date.month < 10) {
			month = "0" + date.month;
		} else {
			month = date.month + "";
		}
		TimeDialog = date.year + getString(R.string.year) + month;

		long timeStamp = DateUtils.getStringToDate(stringTime);
		String time = String.valueOf(timeStamp / 1000);
		SportPeriodHttp(time);
	}

	private void SportPeriodHttp(String time) {
		SportPeriodHttp sportPeriodHttp = new SportPeriodHttp(this, HttpConfigUrl.COMTYPE_SPORT_GET_TIME_PERIOD_DATE, time);
		new Thread(sportPeriodHttp).start();
	}

	@Override
	public void changeDate(CustomDate date) {
		setShowDateViewText(date.year, date.month, date.day);
		String stringTime = date.year + "-" + date.month + "-" + "01" + " 00:00:00";
		long timeStamp = DateUtils.getStringToDate(stringTime);
		String time = String.valueOf(timeStamp / 1000);
		SportRecordHttp(time);
	}

	private void SportRecordHttp(String time) {

		SportRecordDateHttp sportRecordDateHttp = new SportRecordDateHttp(this, HttpConfigUrl.COMTYPE_SPORT_GET_RECORD_DATE, time);
		new Thread(sportRecordDateHttp).start();
	}


	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_SPORT_GET_TIME_PERIOD_DATE) {
			final SportPeriodHttp sportPeriodHttp = (SportPeriodHttp) object;
			if (sportPeriodHttp.getStatus() == 1) {
				if (!TextUtils.isEmpty(sportPeriodHttp.getSportPeriodEntity().getTotalStepNum())) {
					sportNoDataLinearLayout.setVisibility(View.GONE);
					sportDataRecylerView.setVisibility(View.VISIBLE);
					i = 0;
					arcProgress.invalidate();
					mhandler.removeCallbacksAndMessages(null);
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					timer = new Timer();
					DecimalFormat df = new DecimalFormat("0.0");

					totalStep = Integer.parseInt(sportPeriodHttp.getSportPeriodEntity().getTotalStepNum());

					Calendar calender = Calendar.getInstance();
					calender.setTime(new Date());
					final int allStep;
					if (defaultCustomDate.year == calender.get(Calendar.YEAR) &&
							defaultCustomDate.month == calender.get(Calendar.MONTH) + 1 &&
							defaultCustomDate.day == calender.get(Calendar.DAY_OF_MONTH)) {
						allStep = totalStep + currentStep;
					} else {
						allStep = totalStep;
					}

					kmResult = df.format(((allStep) * 0.683 / 1000));
					kmTextView.setText(kmResult);
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							Message msg = new Message();
							msg.what = 1;
							msg.arg1 = allStep;
							mhandler.sendMessage(msg);
						}
					}, 30, 30);
					String weigth = SharedUtils.getInstance().readString(Constant.WEIGHT);
					int weightInt;
					if (TextUtils.isEmpty(weigth)) {
						weightInt = 30;
					} else {
						if (Integer.parseInt(weigth) < 30) {
							weightInt = 30;
						} else {
							weightInt = Integer.parseInt(weigth);
						}
					}
					double weightDouble = weightInt *((allStep) * 0.683 / 1000) * 1.036;
					kcResult = df.format(weightDouble);
					kcalTextView.setText(kcResult);
					sportDataAdapter.addAll(sportPeriodHttp.getSportPeriodEntity().getTimePeriods());
				}
			} else {
				kmTextView.setText("0.0");
				kcalTextView.setText("0.0");
				sportDataRecylerView.setVisibility(View.GONE);
				if (totalStep == 0) {
					setHeader(sportNoDataLinearLayout);
				} else {
					sportNoDataLinearLayout.setVisibility(View.GONE);
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_SPORT_GET_RECORD_DATE) {
			SportRecordDateHttp sportRecordDateHttp = (SportRecordDateHttp) object;
			if (sportRecordDateHttp.getStatus() == 1) {
				List<DataEntitiy> list = sportRecordDateHttp.getDataEntitiyList();
				List<Integer> listDate = new ArrayList<>();
				for (int i = 0; list != null && i < list.size(); i++) {
					listDate.add(Integer.parseInt(DateUtils.getDateToString(Long.parseLong(list.get(i).getDate())).substring(8, 10)));
				}
				String strMonth=tvShowMonthView.getText().toString();
				if(!TextUtils.isEmpty(strMonth)&&defaultCustomDate.month==Integer.parseInt(tvShowMonthView.getText().toString().substring(5, 7))&&defaultCustomDate.year==Integer.parseInt(tvShowMonthView.getText().toString().substring(0, 4)))
				{
					if(!listDate.contains(defaultCustomDate.day)){
						listDate.add(defaultCustomDate.day);
					}
				}
				views[viewPager.getCurrentItem() % views.length].setList(listDate);
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ICSOpenVPNApplication.getInstance().unregisterReceiver(realStepReceiver);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(getActivity(), getString(R.string.no_wifi));
		if (sportDataAdapter.getItemCount() <= 0) {
			List<Integer> listDate = new ArrayList<>();
			views[viewPager.getCurrentItem() % views.length].setList(listDate);
		}

	}


	BroadcastReceiver realStepReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Calendar calender = Calendar.getInstance();
			calender.setTime(new Date());
			currentStep = intent.getIntExtra(Constant.REAL_TIME_STEPS, 0);
			if (REALTIMESTEP.equals(intent.getAction())) {
				if (defaultCustomDate.year == calender.get(Calendar.YEAR) &&
						defaultCustomDate.month == calender.get(Calendar.MONTH) + 1 &&
						defaultCustomDate.day == calender.get(Calendar.DAY_OF_MONTH)) {
					int persent = (totalStep + currentStep) * 100 / getTargetStep();
					if (persent <= 100) {
						arcProgress.setStep(persent, totalStep + currentStep);
					} else {
						arcProgress.setStep(100, totalStep + currentStep);
					}
					//更新adapter
					try {
						TimePeriodsEntity newEntity = sportDataAdapter.changeStepData(currentStep);
						float entityKm = Float.valueOf(newEntity.getKM());
						float entityKcal = Float.valueOf(newEntity.getKcal());
						if (kmResult != null && kcResult != null) {
							float kmInt = Float.valueOf(kmResult);
							float kcInt = Float.valueOf(kcResult);
							if (newEntity != null) {
								kmTextView.setText(kmInt + entityKm + "");
								kcalTextView.setText(entityKcal + kcInt + "");
							} else {
								kmTextView.setText(kmResult);
								kcalTextView.setText(kcResult);
							}
						} else {
							kmTextView.setText(entityKm + "");
							kcalTextView.setText(entityKcal + "");
						}
						sportDataAdapter.notifyDataSetChanged();
						sportNoDataLinearLayout.setVisibility(View.GONE);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			} else if (REFRESHSTEP.equals(intent.getAction())) {
				initSportData(Calendar.getInstance(), defaultCustomDate.month, defaultCustomDate.day);
			} else if (CLEARSPORTDATA.equals(intent.getAction())) {
				clearAdapterData();
			}
		}
	};

	private IntentFilter getFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(REALTIMESTEP);
		filter.addAction(REFRESHSTEP);
		filter.addAction(CLEARSPORTDATA);
		return filter;
	}

	//清除运动数据
	public void clearAdapterData() {
		sportDataAdapter.clearSportData();
		currentStep = 0;
		kcalTextView.setText("0");
		kmTextView.setText("0");
		arcProgress.setStep(0, 0);
		sportNoDataLinearLayout.setVisibility(View.VISIBLE);
	}
}
