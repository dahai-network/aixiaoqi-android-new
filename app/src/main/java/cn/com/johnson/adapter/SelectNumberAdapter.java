package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.SelectNumberEntity;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class SelectNumberAdapter  extends RecyclerBaseAdapter<SelectNumberAdapter.ViewHolder, SelectNumberEntity.SelectInfo> implements View.OnClickListener{

    public SelectNumberAdapter(Context context, List< SelectNumberEntity.SelectInfo> list) {
        super(context, list);

    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SelectNumberEntity.SelectInfo selectInfo=     mList.get(position);
        holder.phoneNumber.setText(selectInfo.getMobileNumber());
        if("0".equals(selectInfo.getPrice())||"0.00".equals(selectInfo.getPrice())){
            holder.fee.setText(mContext.getString(R.string.free));
            holder.fee.setTextColor(ContextCompat.getColor(mContext, R.color.select_contacct));
        }else{
            holder.fee.setText("￥"+selectInfo.getPrice());
            holder.fee.setTextColor(ContextCompat.getColor(mContext, R.color.dark_orange));
        }
        holder.itemView.setTag(selectInfo);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_select_number, parent, false));
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            onItemClickListener.onItemClick(v, v.getTag());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView fee;
        TextView phoneNumber;

        public ViewHolder(View itemView) {
            super(itemView);

            phoneNumber = (TextView) itemView.findViewById(R.id.phone_number);
            fee = (TextView) itemView.findViewById(R.id.fee);


        }
    }
}
