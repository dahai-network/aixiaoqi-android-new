package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.SmsEntity;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CallRecordAdapter extends RecyclerBaseAdapter<CallRecordAdapter.ViewHolder, SmsEntity> {

    public CallRecordAdapter(Context context, List<SmsEntity> list) {
        super(context, list);

    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
