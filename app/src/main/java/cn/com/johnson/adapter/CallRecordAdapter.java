package cn.com.johnson.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.ContactRecodeEntity;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CallRecordAdapter extends RecyclerBaseAdapter<CallRecordAdapter.ViewHolder, ContactRecodeEntity> {



    public CallRecordAdapter(Context context, List<ContactRecodeEntity> list) {
        super(context, list);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactRecodeEntity contactRecodeEntity = mList.get(position);
        holder.callDirIv.setImageResource(R.drawable.icon_bd);
        if (Constant.CALL_INCOMING.equals(contactRecodeEntity.getTypeString())) {

        }
        if (Constant.CALL_MISSED.equals(contactRecodeEntity.getTypeString())) {

        }
        if (Constant.CALL_OUTGOING.equals(contactRecodeEntity.getTypeString())) {

            holder.callDirIv.setVisibility(View.VISIBLE);

        }
        if (!TextUtils.isEmpty(contactRecodeEntity.getData())) {
            holder.callTimeTv.setText(contactRecodeEntity.getData());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewholder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_call_record, parent, false));
        return viewholder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.call_dir_iv)
        ImageView callDirIv;
        @BindView(R.id.call_time_tv)
        TextView callTimeTv;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
