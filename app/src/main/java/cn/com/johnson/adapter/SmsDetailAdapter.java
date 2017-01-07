package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.SMSAcivity;
import de.blinkt.openvpn.model.SmsDetailEntity;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.User;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class SmsDetailAdapter extends RecyclerBaseAdapter<RecyclerView.ViewHolder, SmsDetailEntity> implements View.OnClickListener {
	private static final int RIGTH_ME = 0;
	private static final int LEFT_OTHER = 1;

	public SmsDetailAdapter(Context context, List<SmsDetailEntity> list) {
		super(context, list);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		SmsDetailEntity smsDetailEntity = mList.get(position);
		if (holder instanceof RightViewHolder) {

			((RightViewHolder) holder).rightSmsTv.setText(smsDetailEntity.getSMSContent());
			((RightViewHolder) holder).showTimeTv.setText(DateUtils.getTimeStampDetailString(smsDetailEntity.getSMSTime()));
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
				((RightViewHolder) holder).sendErrorIv.setTag(position);
			}
		} else if (holder instanceof LeftViewHolder) {
			((LeftViewHolder) holder).showTimeTv.setText(DateUtils.getTimeStampDetailString(smsDetailEntity.getSMSTime()));
			((LeftViewHolder) holder).leftSmsTv.setText(smsDetailEntity.getSMSContent());
		}

	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder holder;
		if (viewType == RIGTH_ME) {
			holder = new RightViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_right_sms, parent, false));
		} else {
			holder = new LeftViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_left_sms, parent, false));
		}

		return holder;
	}

	@Override
	public int getItemViewType(int position) {
		return User.isCurrentUser(mList.get(position).getFm()) ? RIGTH_ME : LEFT_OTHER;
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			onItemClickListener.onItemClick(v, v.getTag());
		}
	}

	public class LeftViewHolder extends RecyclerView.ViewHolder {
		TextView leftSmsTv;
		TextView showTimeTv;

		public LeftViewHolder(View itemView) {
			super(itemView);
			leftSmsTv = (TextView) itemView.findViewById(R.id.left_content_tv);
			showTimeTv = (TextView) itemView.findViewById(R.id.show_time_tv);
		}
	}

	public class RightViewHolder extends RecyclerView.ViewHolder {
		TextView rightSmsTv;
		TextView showTimeTv;
		ImageView sendErrorIv;
		ProgressBar sendingPb;

		public RightViewHolder(View itemView) {
			super(itemView);
			rightSmsTv = (TextView) itemView.findViewById(R.id.right_content_tv);
			showTimeTv = (TextView) itemView.findViewById(R.id.show_time_tv);
			sendErrorIv = (ImageView) itemView.findViewById(R.id.send_error_iv);
			sendingPb = (ProgressBar) itemView.findViewById(R.id.sending_pb);
		}
	}

}
