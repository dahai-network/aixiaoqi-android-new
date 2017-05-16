package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2017/5/16.
 */

public class CommitFreeWorryAdapter extends RecyclerView.Adapter<CommitFreeWorryAdapter.CommitViewHolder> {


	private ArrayList<Integer> data;
	private Context context;
//	public static final int

	public CommitFreeWorryAdapter(Context context, ArrayList<Integer> data , int type) {
		this.context = context;
		this.data = data;
	}

	@Override
	public CommitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		CommitViewHolder viewHolder = new CommitViewHolder(LayoutInflater.from(context).inflate(R.layout.item_commit_fw, parent, false));
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(CommitViewHolder holder, int position) {
        holder.monthButton.setText(data.get(position)+"个月");
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	class CommitViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.monthButton)
		Button monthButton;

		public CommitViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

}
