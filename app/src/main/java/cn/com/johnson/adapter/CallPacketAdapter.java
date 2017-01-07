package cn.com.johnson.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.model.PacketEntity;

/**
 * Created by Administrator on 2016/9/23.
 */

public class CallPacketAdapter extends RecyclerView.Adapter<CallPacketAdapter.ViewHolder> {


	private final Context context;
	private List<PacketEntity.ListBean> data = new ArrayList<>();

	public CallPacketAdapter(Context context) {
		this.context = context;
	}

	public List<PacketEntity.ListBean> getData() {
		return data;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder viewholder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_packet, parent, false));
		return viewholder;
	}

	public void addAll(List<PacketEntity.ListBean> data) {
		this.data.addAll(data);
	}

	public void add(List<PacketEntity.ListBean> data) {
		this.data = data;
	}

	public void clearData() {
		data.clear();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.nameTextView.setText(data.get(position).getPackageName());
		holder.timeTextView.setText(context.getResources().getString(R.string.expiry_date) + "：" + data.get(position).getExpireDays() + "天");
		holder.numberTextView.setText(data.get(position).getPrice());
	}

	@Override
	public int getItemCount() {

		return data != null ? data.size() : 0;
	}



	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.nameTextView)
		TextView nameTextView;
		@BindView(R.id.timeTextView)
		TextView timeTextView;
		@BindView(R.id.numberTextView)
		TextView numberTextView;
		@BindView(R.id.rootRelativeLayout)
		RelativeLayout rootRelativeLayout;


		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		@OnClick(R.id.rootRelativeLayout)
		public void onClick() {
			CallTimePacketDetailActivity.launch(context,data.get(getPosition()-1).getPackageId());
		}
	}
}
