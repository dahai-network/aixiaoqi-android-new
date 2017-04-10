package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.EBizOrderListEntity;

/**
 * Created by Administrator on 2016/11/25 0025.
 */
public class EBuzOrderListAdapter extends RecyclerBaseAdapter<EBuzOrderListAdapter.ViewHolder, EBizOrderListEntity.OrderInfo> implements View.OnClickListener{
    public EBuzOrderListAdapter(Context context, List<EBizOrderListEntity.OrderInfo> list) {
        super(context, list);


    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EBizOrderListEntity.OrderInfo orderInfo=mList.get(position);
        holder.orderNumber.setText("订单号： "+orderInfo.getOrderByZCNum());
        holder.parchaseNumber.setText("购买数量："+orderInfo.getQuantity());
        if(orderInfo.getSelectionedNumberList()==null||orderInfo.getSelectionedNumberList().size()==0){
            holder.readlySelectNumber.setText("还没有选择号码");
        }else{
            StringBuilder stringBuilder=new StringBuilder();
            List<EBizOrderListEntity.OrderInfo.SelectNumber> list=    orderInfo.getSelectionedNumberList();
            int length=list.size();
            for(int i=0;i<length;i++){
                stringBuilder.append(list.get(i).getMobileNumber()+" ");
            }
            holder.readlySelectNumber.setText("已选号："+stringBuilder.toString());
        }

        holder.itemView.setTag(orderInfo);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_e_biz_order_list, parent, false));
        holder.itemView.setOnClickListener(this);
        return holder;
    }
    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            onItemClickListener.onItemClick(v, v.getTag(), false);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber;
        TextView parchaseNumber;
        TextView readlySelectNumber;
        public ViewHolder(View itemView) {
            super(itemView);
            orderNumber=(TextView) itemView.findViewById(R.id.order_number);
            parchaseNumber=(TextView) itemView.findViewById(R.id.parchase_number);
            readlySelectNumber=(TextView) itemView.findViewById(R.id.readly_select_number);

        }
    }
}
