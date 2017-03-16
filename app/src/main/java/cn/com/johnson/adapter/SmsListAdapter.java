package cn.com.johnson.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.User;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class SmsListAdapter extends RecyclerBaseAdapter<SmsListAdapter.ViewHolder, SmsEntity> implements View.OnClickListener,View.OnLongClickListener {


	public SmsListAdapter(Context context, List<SmsEntity> list) {
		super(context, list);

	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		SmsEntity smsEntity = mList.get(position);
		if ("0".equals(smsEntity.getIsRead())) {
			holder.ivUnreadMessage.setVisibility(View.VISIBLE);
		} else {
			holder.ivUnreadMessage.setVisibility(View.GONE);
		}
		if (TextUtils.isEmpty(smsEntity.getRealName())) {
			if (!User.isCurrentUser(smsEntity.getFm()))
				holder.tvContactName.setText(smsEntity.getFm().split(",")[0]);
			else {
				holder.tvContactName.setText(smsEntity.getTo().split(",")[0]);
			}
		} else {
			holder.tvContactName.setText(smsEntity.getRealName());
		}

		holder.tvTime.setText(DateUtils.getTimeStampString(smsEntity.getSMSTime()));
		holder.tvSmsContent.setText(smsEntity.getSMSContent());
		holder.itemView.setTag(smsEntity);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sms_list, parent, false));
		holder.itemView.setOnClickListener(this);
		holder.itemView.setOnLongClickListener(this);
		return holder;
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			//注意这里使用getTag方法获取数据
			onItemClickListener.onItemClick(v, v.getTag());
		}
	}
	public interface OnItemLongClickListener{
		void onItemLongClick(View view, Object data);
	}
public OnItemLongClickListener onItemLongClickListener;

	public void setOnItemLongClickListener(OnItemLongClickListener onItemClickListener) {
		onItemLongClickListener=onItemClickListener;
	}

	@Override
	public boolean onLongClick(View v) {
		onItemLongClickListener.onItemLongClick(v, v.getTag());
		return false;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView ivUnreadMessage;
		TextView tvSmsContent;
		TextView tvTime;
		TextView tvContactName;

		public ViewHolder(View itemView) {
			super(itemView);
			ivUnreadMessage = (ImageView) itemView.findViewById(R.id.iv_unread_message);
			tvContactName = (TextView) itemView.findViewById(R.id.tv_contact_name);
			tvTime = (TextView) itemView.findViewById(R.id.tv_time);
			tvSmsContent = (TextView) itemView.findViewById(R.id.tv_sms_content);

		}
	}

}
