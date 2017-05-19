package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2017/5/16.
 */

public class CommitFreeWorryAdapter extends RecyclerView.Adapter<CommitFreeWorryAdapter.CommitViewHolder> {


	private ArrayList<String> data;
	private Context context;
	private OnClickItemLisener lisener;
	private int choicePosition;

	public CommitFreeWorryAdapter(Context context, ArrayList<String> data, OnClickItemLisener lisener) {
		this.context = context;
		this.data = data;
		this.lisener = lisener;
	}

	public void setCheck(int position) {
		this.choicePosition = position;
	}


	@Override
	public CommitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CommitViewHolder viewHolder = new CommitViewHolder(LayoutInflater.from(context).inflate(R.layout.item_commit_fw, parent, false));
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(CommitViewHolder holder, final int position) {
		holder.monthButton.setText(data.get(position));
		holder.monthButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				lisener.onItemClick(data.get(position), position);
			}
		});
		if (position == choicePosition) {
			holder.monthButton.setChecked(true);
		} else {
			holder.monthButton.setChecked(false);
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	class CommitViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.monthButton)
		CheckBox monthButton;

		public CommitViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	public interface OnClickItemLisener {
		void onItemClick(String textContent, int position);
	}
}
