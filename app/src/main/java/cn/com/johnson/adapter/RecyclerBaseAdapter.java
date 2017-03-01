package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import java.util.List;


public abstract class RecyclerBaseAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {
	protected Context mContext;
	protected List<T> mList;
	protected OnItemClickListener onItemClickListener;

	public T getItem(int position) {
		if (position < mList.size())
			return mList.get(position);
		else
			return null;
	}

	public interface OnItemClickListener {
		void onItemClick(View view, Object data);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public RecyclerBaseAdapter(Context mContext, List<T> mList) {
		this.mContext = mContext;
		if (mList == null) {
			this.mList = new ArrayList<>();
		} else {
			this.mList = mList;
		}
	}
	public List<T> getList(){
		return mList;
	}
	@Override
	public int getItemCount() {
		return mList.size();
	}

	public void add(T t) {
		insert(t, mList.size());
	}

	public void add(int position, T t) {
		insert(position, t);
	}
	public void add(T t,int position) {
		insert(t, position);
	}
	public void insert(T t, int position) {
		mList.add(position, t);
		notifyItemInserted(position);
	}

	public void insert(int position, T t) {
		mList.add(position, t);
		notifyDataSetChanged();
	}

	public void remove(int position) {
		mList.remove(position);
		notifyDataSetChanged();

	}

	public void clear() {
		int size = mList.size();
		mList.clear();
		notifyItemRangeRemoved(0, size);
	}

	public void addAll(List<T> ts) {
		mList.clear();
		mList.addAll(ts);
		notifyDataSetChanged();
	}
	public void addTopAll(List<T> ts) {
		mList.addAll(0,ts);
		notifyDataSetChanged();
	}
}
