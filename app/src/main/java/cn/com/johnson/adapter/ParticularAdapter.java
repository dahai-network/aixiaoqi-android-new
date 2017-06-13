package cn.com.johnson.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.MyModules.ui.MonthlyOrderActivity;
import de.blinkt.openvpn.model.ParticularEntity;
import de.blinkt.openvpn.util.DateUtils;

/**
 * Created by Administrator on 2016/9/23.
 */

public class ParticularAdapter extends RecyclerView.Adapter<ParticularAdapter.ViewHolder> {
	private final Context context;
	private final boolean isDetail;
	private List<ParticularEntity.ListBean> data;
	//支出标记
	private final int EXPENDITURE = 0;
	//收入标记
	private final int INCOME = 1;
	//服务标记
	private final int SERVICE = 2;

	public ParticularAdapter(Context context, List<ParticularEntity.ListBean> data, boolean isDetail) {
		this.context = context;
		this.data = data;
		this.isDetail = isDetail;
	}

	public List<ParticularEntity.ListBean> getData() {
		return data;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder viewholder;
		if (!this.isDetail) {
			viewholder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_particular, parent, false));
		} else {
			viewholder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_particular_detail, parent, false));
		}
		return viewholder;
	}

	public void addAll(List<ParticularEntity.ListBean> data) {
		this.data.addAll(data);
	}

	public void add(List<ParticularEntity.ListBean> data) {
		this.data = data;
	}

	public void clearData() {
		data.clear();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		if (data.get(position).isHadDetail()) {
			holder.rootRelativeLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, MonthlyOrderActivity.class);
					intent.putExtra("ID", data.get(position).getID());
					context.startActivity(intent);
				}
			});
		}
		holder.nameTextView.setText(data.get(position).getDescr());
		holder.timeTextView.setText(DateUtils.getDateToString(data.get(position).getCreateDate() * 1000));
		inputNumberTextView(holder.numberTextView, position);
	}

	private void inputNumberTextView(TextView numberTextView, int position) {
		switch (data.get(position).getBillType()) {
			case EXPENDITURE:
				numberTextView.setTextColor(ContextCompat.getColor(context, R.color.connect_us_red));
//				numberTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
				numberTextView.setText("-￥" + data.get(position).getAmount());
				break;
			case INCOME:
				numberTextView.setTextColor(ContextCompat.getColor(context, R.color.connect_us_red));
//				numberTextView.setTextColor(ContextCompat.getColor(context, R.color.select_contacct));
				numberTextView.setText("+￥" + data.get(position).getAmount());
				break;
			case SERVICE:
				numberTextView.setText("");
//				numberTextView.setBackgroundResource(R.drawable.arrow);
				Drawable dra = context.getResources().getDrawable(R.drawable.arrow);
				dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
				numberTextView.setCompoundDrawables(dra, null, null, null);
				break;
		}
	}


	@Override
	public int getItemCount() {
		return data.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.rootRelativeLayout)
		RelativeLayout rootRelativeLayout;
		@BindView(R.id.nameTextView)
		TextView nameTextView;
		@BindView(R.id.timeTextView)
		TextView timeTextView;
		@BindView(R.id.numberTextView)
		TextView numberTextView;


		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
