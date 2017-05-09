package cn.com.johnson.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2017/5/9.
 */

public class FreeWorryPacketChoiceAdapter extends RecyclerView.Adapter {

	public FreeWorryPacketChoiceAdapter() {

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_free_worry_packet_choice, parent, false);
		return new FreeWorryPacketChoiceViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

	}

	@Override
	public int getItemCount() {
		return 2;
	}

	public class FreeWorryPacketChoiceViewHolder extends RecyclerView.ViewHolder {
		public ImageView showImageView;
		public FreeWorryPacketChoiceViewHolder(View itemView) {
			super(itemView);
			showImageView = (ImageView) itemView.findViewById(R.id.showImageView);
		}
	}
}
