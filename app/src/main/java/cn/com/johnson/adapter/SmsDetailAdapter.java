package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.SMSAcivity;
import de.blinkt.openvpn.model.SmsDetailEntity;
import de.blinkt.openvpn.util.DateUtils;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class SmsDetailAdapter extends RecyclerBaseAdapter<RecyclerView.ViewHolder, SmsDetailEntity> implements View.OnClickListener, View.OnLongClickListener {
	private static final int RIGTH_ME = 0;
	private static final int LEFT_OTHER = 1;
	private boolean isDeleteState;

	public SmsDetailAdapter(Context context, List<SmsDetailEntity> list) {
		super(context, list);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		SmsDetailEntity smsDetailEntity = mList.get(position);
		smsDetailEntity.setPosition(position);
		if (holder instanceof RightViewHolder) {

			((RightViewHolder) holder).rightSmsTv.setText(smsDetailEntity.getSMSContent());
			((RightViewHolder) holder).showTimeTv.setText(DateUtils.getTimeStampDetailString(smsDetailEntity.getSMSTime()));
			((RightViewHolder) holder).deleteSmsDetailCheckBox.setChecked(smsDetailEntity.isCheck());
			if (SMSAcivity.SEND_PROGRESSING.equals(smsDetailEntity.getStatus())) {
				((RightViewHolder) holder).sendErrorIv.setVisibility(View.GONE);
				((RightViewHolder) holder).sendingPb.setVisibility(View.VISIBLE);
			} else if (SMSAcivity.SEND_SUCCEED.equals(smsDetailEntity.getStatus())) {
				((RightViewHolder) holder).sendErrorIv.setVisibility(View.GONE);
				((RightViewHolder) holder).sendingPb.setVisibility(View.GONE);
			} else {
				((RightViewHolder) holder).sendErrorIv.setVisibility(View.VISIBLE);
				((RightViewHolder) holder).sendingPb.setVisibility(View.GONE);
				((RightViewHolder) holder).sendErrorIv.setOnClickListener(this);
//				((RightViewHolder) holder).sendErrorIv.setTag(position);
			}
			if (isDeleteState) {
				((RightViewHolder) holder).deleteSmsDetailCheckBox.setVisibility(View.VISIBLE);
			} else {
				((RightViewHolder) holder).deleteSmsDetailCheckBox.setVisibility(View.GONE);
			}
		} else if (holder instanceof LeftViewHolder) {
			((LeftViewHolder) holder).showTimeTv.setText(DateUtils.getTimeStampDetailString(smsDetailEntity.getSMSTime()));
			((LeftViewHolder) holder).leftSmsTv.setText(smsDetailEntity.getSMSContent());
			((LeftViewHolder) holder).deleteSmsDetailCheckBox.setChecked(smsDetailEntity.isCheck());
			if (isDeleteState) {
				((LeftViewHolder) holder).deleteSmsDetailCheckBox.setVisibility(View.VISIBLE);
			} else {
				((LeftViewHolder) holder).deleteSmsDetailCheckBox.setVisibility(View.GONE);
			}
		}
		holder.itemView.setTag(smsDetailEntity);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder holder;
		if (viewType == RIGTH_ME) {
			holder = new RightViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_right_sms, parent, false));
		} else {
			holder = new LeftViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_left_sms, parent, false));
		}
		holder.itemView.setOnLongClickListener(this);
		holder.itemView.setOnClickListener(this);
		return holder;
	}

	@Override
	public int getItemViewType(int position) {
		return mList.get(position).isSend() ? RIGTH_ME : LEFT_OTHER;
	}

	public void clearCheckState() {
		for (SmsDetailEntity entity : mList) {
			entity.setCheck(false);
		}
	}


	public interface OnItemLongAndResendClickListener {
		void onItemLongClick(View view, Object data);
		void onResendClick(View view, Object data);
	}

	public OnItemLongAndResendClickListener onItemLongAndResendClickListener;

	public void setOnItemLongAndResendClickListener(OnItemLongAndResendClickListener onItemClickListener) {
		onItemLongAndResendClickListener = onItemClickListener;
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			if (v.getId() == R.id.send_error_iv) {
				//注意这里使用getTag方法获取数据
				onItemLongAndResendClickListener.onResendClick(v, v.getTag());
			} else {
				if (isDeleteState) {
					//注意这里使用getTag方法获取数据
					CheckBox check = (CheckBox) v.findViewById(R.id.deleteSmsDetailCheckBox);
					onItemClickListener.onItemClick(v, v.getTag(), !check.isChecked());
					((SmsDetailEntity) v.getTag()).setCheck(!check.isChecked());
					check.setChecked(!check.isChecked());
				}
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		onItemLongAndResendClickListener.onItemLongClick(v, v.getTag());
		return false;
	}

	public class LeftViewHolder extends RecyclerView.ViewHolder {
		TextView leftSmsTv;
		TextView showTimeTv;
		CheckBox deleteSmsDetailCheckBox;

		public LeftViewHolder(View itemView) {
			super(itemView);
			deleteSmsDetailCheckBox = (CheckBox) itemView.findViewById(R.id.deleteSmsDetailCheckBox);
			leftSmsTv = (TextView) itemView.findViewById(R.id.left_content_tv);
			showTimeTv = (TextView) itemView.findViewById(R.id.show_time_tv);
		}
	}

	public void setDeleteState(boolean isDeleteState) {
		this.isDeleteState = isDeleteState;
	}

	public boolean isDeleteState() {
		return isDeleteState;
	}

	public class RightViewHolder extends RecyclerView.ViewHolder {
		CheckBox deleteSmsDetailCheckBox;
		TextView rightSmsTv;
		TextView showTimeTv;
		ImageView sendErrorIv;
		ProgressBar sendingPb;

		public RightViewHolder(View itemView) {
			super(itemView);
			deleteSmsDetailCheckBox = (CheckBox) itemView.findViewById(R.id.deleteSmsDetailCheckBox);
			rightSmsTv = (TextView) itemView.findViewById(R.id.right_content_tv);
			showTimeTv = (TextView) itemView.findViewById(R.id.show_time_tv);
			sendErrorIv = (ImageView) itemView.findViewById(R.id.send_error_iv);
			sendingPb = (ProgressBar) itemView.findViewById(R.id.sending_pb);
		}
	}

}
