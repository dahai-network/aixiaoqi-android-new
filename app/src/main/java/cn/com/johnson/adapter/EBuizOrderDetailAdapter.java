package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.EBizOrderDetailEntity;

/**
 * Created by Administrator on 2016/11/29 0029.
 */
public class EBuizOrderDetailAdapter extends RecyclerBaseAdapter<EBuizOrderDetailAdapter.ViewHolder, EBizOrderDetailEntity.NumberInfo> {
    public EBuizOrderDetailAdapter(Context context, List<EBizOrderDetailEntity.NumberInfo> list) {
        super(context, list);


    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EBizOrderDetailEntity.NumberInfo numberInfo=mList.get(position);
        holder.infoAddress.setText(numberInfo.getProvinceName()+"  "+numberInfo.getCityName());
        holder.infoIdCard.setText(numberInfo.getIdentityNumber());
        holder.infoName.setText(numberInfo.getName());
        holder.infoSelectNumber.setText(numberInfo.getMobileNumber());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_order_detail, parent, false));
        return holder;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView infoName;
        TextView infoIdCard;
        TextView infoAddress;
        TextView infoSelectNumber;
        public ViewHolder(View itemView) {
            super(itemView);
            infoName=(TextView) itemView.findViewById(R.id.info_name);
            infoIdCard=(TextView) itemView.findViewById(R.id.info_id_card);
            infoAddress=(TextView) itemView.findViewById(R.id.info_address);
            infoSelectNumber=(TextView) itemView.findViewById(R.id.info_select_number);
        }
    }
}
