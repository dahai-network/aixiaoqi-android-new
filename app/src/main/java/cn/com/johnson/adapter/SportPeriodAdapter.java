package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.TimePeriodsEntity;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2016/10/3 0003.
 */
public class SportPeriodAdapter extends RecyclerBaseAdapter<SportPeriodAdapter.ViewHolder, TimePeriodsEntity> {
	public static final int TYPE_HEADER = 0;
	public static final int TYPE_NORMAL = 1;

	public SportPeriodAdapter(Context context, List<TimePeriodsEntity> list) {
		super(context, list);
	}

	public void setHeaderView(View headerView) {
		mHeaderView = headerView;
		notifyItemInserted(0);
	}

	public View getHeaderView() {
		return mHeaderView;
	}

	private View mHeaderView;

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (getItemViewType(position) == TYPE_HEADER) return;
		int pos = getRealPosition(holder);
		TimePeriodsEntity timePeriods = mList.get(pos);
		holder.activeTimeTextView.setText(DateUtils.getDateToString(Long.parseLong(timePeriods.getStartDateTime())).substring(11, 16) + " - " + DateUtils.getDateToString(Long.parseLong(timePeriods.getEndDateTime())).substring(11, 16));
		holder.stepTextView.setText(timePeriods.getStepNum());

		final int totalStep;
		if (!TextUtils.isEmpty(timePeriods.getStepNum())) {
			totalStep = Integer.parseInt(timePeriods.getStepNum());
		} else {
			totalStep = 0;
		}
		double step = totalStep * 0.683;
		String kmResult;
		if (step >= 1000) {
			DecimalFormat df = new DecimalFormat("0.00");
			kmResult = df.format(step / 1000);
			holder.kmUnite.setText(mContext.getString(R.string.km));
		} else {
			DecimalFormat df = new DecimalFormat("0.0");
			kmResult = df.format(step);
			holder.kmUnite.setText(mContext.getString(R.string.m));
		}
		holder.kmTextView.setText(kmResult);
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

		double weightDouble = weightInt * step / 1000 * 1.036;
		DecimalFormat df = new DecimalFormat("0.0");
		String kcResult = df.format(weightDouble);
		holder.kcalTextView.setText(kcResult);


	}

	public int getRealPosition(RecyclerView.ViewHolder holder) {
		int position = holder.getLayoutPosition();
		return mHeaderView == null ? position : position - 1;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (mHeaderView != null && viewType == TYPE_HEADER) {
			ViewHolder holder = new ViewHolder(mHeaderView);
			return holder;
		}
		ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sport_data, parent, false));

		return holder;
	}

	@Override
	public int getItemViewType(int position) {
		if (mHeaderView == null) return TYPE_NORMAL;
		if (position == 0) return TYPE_HEADER;
		return TYPE_NORMAL;
	}

	@Override
	public int getItemCount() {
		return mHeaderView == null ? mList.size() : mList.size() + 1;
	}

	public int getAllStep() {
		int allStep = 0;
		for (int i = 0; i < mList.size() - 1; i++) {
			allStep += Integer.valueOf(mList.get(i).getStepNum());
		}
		return allStep;
	}


	public class ViewHolder extends RecyclerView.ViewHolder {

		TextView kcalTextView;
		TextView kmTextView;
		TextView kmUnite;

		TextView activeTimeTextView;
		TextView stepTextView;

		public ViewHolder(View itemView) {
			super(itemView);
			activeTimeTextView = ((TextView) itemView.findViewById(R.id.activeTimeTextView));
			stepTextView = ((TextView) itemView.findViewById(R.id.stepTextView));

			kmTextView = ((TextView) itemView.findViewById(R.id.kmTextView));
			kmUnite = ((TextView) itemView.findViewById(R.id.kmUnite));

			kcalTextView = ((TextView) itemView.findViewById(R.id.kcalTextView));
		}
	}

	public TimePeriodsEntity changeStepData(int step) throws ParseException {
		step = step - getAllStep();
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
		String parseString = year + "-" + month + "-" + day + " " + currentHour;
		Date startHourdate = format.parse(parseString);
		if (mList.size() > 0) {
			long listLastTime = Long.valueOf(mList.get(mList.size() - 1).getEndDateTime());
			long hourTime = startHourdate.getTime() / 1000;
			if (listLastTime > hourTime) {
				return changeLastStep(step);
			} else {
				return addLastAllStep(step, startHourdate);
			}
		} else {
			return addLastAllStep(step, startHourdate);
		}

	}

	public TimePeriodsEntity addLastAllStep(int step, Date startHourdate) {

		Date endtHourdate = new Date(startHourdate.getTime() + 3600 * 1000);
		TimePeriodsEntity newEntity = new TimePeriodsEntity();
		newEntity.setStartDateTime((startHourdate.getTime() / 1000) + "");
		newEntity.setEndDateTime((endtHourdate.getTime() / 1000) + "");

		DecimalFormat df = new DecimalFormat("0.0");
		String weigth = SharedUtils.getInstance().readString(Constant.WEIGHT);
		newEntity.setStepNum(step + "");
		String kmResult = df.format(((step) * 0.683 / 1000));
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
		double weightDouble = weightInt * ((step) * 0.683 / 1000) * 1.036;
		String kcResult = df.format(weightDouble);
		newEntity.setKM(kmResult);
		newEntity.setKcal(kcResult);
		mList.add(newEntity);
		return newEntity;
	}

	public TimePeriodsEntity changeLastStep(int step) {
		TimePeriodsEntity entity = mList.get(mList.size() - 1);
		TimePeriodsEntity newEntity = new TimePeriodsEntity();
		newEntity.setStartDateTime(entity.getStartDateTime());
		newEntity.setEndDateTime(entity.getEndDateTime());
		newEntity.setMinute(entity.getMinute());
		DecimalFormat df = new DecimalFormat("0.0");
		String weigth = SharedUtils.getInstance().readString(Constant.WEIGHT);
		newEntity.setStepNum(step + "");
		String kmResult = df.format(((step) * 0.683 / 1000));
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
		double weightDouble = weightInt * (step) * 0.683 / 1000 * 1.036;
		String kcResult = df.format(weightDouble);
		newEntity.setKM(kmResult);
		newEntity.setKcal(kcResult);
		mList.remove(mList.get(mList.size() - 1));
		mList.add(newEntity);
		return newEntity;
	}

	public void clearSportData() {
		mList.clear();
		this.notifyDataSetChanged();
	}
}
