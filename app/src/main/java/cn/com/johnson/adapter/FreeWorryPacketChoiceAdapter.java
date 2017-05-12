package cn.com.johnson.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CallTimePacketDetailActivity;
import de.blinkt.openvpn.activities.FreeWorryIntroActivity;
import de.blinkt.openvpn.model.FreeWorryEntity;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2017/5/9.
 */

public class FreeWorryPacketChoiceAdapter extends
		RecyclerView.Adapter<FreeWorryPacketChoiceAdapter.FreeWorryPacketChoiceViewHolder> {

	private final Context context;
	private List<FreeWorryEntity.ListBean> data = new ArrayList<>();
	private String FREEPACKET = "4";
	private String FREEWORRY = "5";

	public FreeWorryPacketChoiceAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<FreeWorryEntity.ListBean> listBean) {
		this.data = listBean;
	}

	public void addData(List<FreeWorryEntity.ListBean> listBean) {
		this.data.addAll(listBean);
	}

	@Override
	public FreeWorryPacketChoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_free_worry_packet_choice, parent, false);
		return new FreeWorryPacketChoiceViewHolder(view);
	}

	@Override
	public void onBindViewHolder(FreeWorryPacketChoiceViewHolder holder, final int position) {
		Glide.with(context).load(data.get(position).getPic()).into(holder.showImageView);
		holder.showImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (FREEPACKET.equals(data.get(position).getCategory())) {
					if (data.get(position).isHaveed()) {
						CommonTools.showShortToast(context, context.getString(R.string.already_get));
					} else {
						CallTimePacketDetailActivity.launch(context, data.get(position).getPackageId(), context.getString(R.string.receive_fw));
					}
				} else if (FREEWORRY.equals(data.get(position).getCategory())) {
					Intent intent = new Intent(context, FreeWorryIntroActivity.class);
					context.startActivity(intent);
				}
			}
		});

	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class FreeWorryPacketChoiceViewHolder extends RecyclerView.ViewHolder {
		public ImageView showImageView;

		public FreeWorryPacketChoiceViewHolder(View itemView) {
			super(itemView);
			showImageView = (ImageView) itemView.findViewById(R.id.showImageView);
		}
	}
}
