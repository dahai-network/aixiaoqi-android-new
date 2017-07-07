package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.listener.MyItemClickListener;
import cn.com.johnson.model.GetBindsIMEIHttpEntity;
import de.blinkt.openvpn.model.BluetoothEntity;
/**
 * Created by kim
 * on 2017/6/27.
 */

public class BindDevcieAdapter extends RecyclerView.Adapter<BindDevcieAdapter.ViewHolder> {

    private MyItemClickListener mItemClickListener;
    private Context mContext;
    private  List<BluetoothEntity> mList=new ArrayList<>();
    private GetBindsIMEIHttpEntity bindsIMEIHttpEntity;
    public BindDevcieAdapter(Context mContext, GetBindsIMEIHttpEntity bindsIMEIHttpEntity) {
        this.mContext=mContext;
        this.bindsIMEIHttpEntity=bindsIMEIHttpEntity;

    }
    public List<BluetoothEntity> getData() {
        return mList;
    }

    public void addAll(List<BluetoothEntity> data) {
        this.mList.addAll(data);
    }

    public void add(List<BluetoothEntity> data) {
        this.mList = data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       ViewHolder holder = new ViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_address, parent,
                false),mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String currentAddress=getData().get(position).getAddress();
        holder.deviceAddress.setText(currentAddress);

    if(bindsIMEIHttpEntity!=null){
        for (int i=0;i<bindsIMEIHttpEntity.getList().size();i++){
            String bindState = bindsIMEIHttpEntity.getList().get(i);
            Log.d("BindDevcieAdapter", "onBindViewHolder: bindState="+bindState +"--"+getData().get(position).getAddress()+"----i"+i+"--getList="+bindsIMEIHttpEntity.getList().size());
            if(currentAddress.equalsIgnoreCase(bindState)){
                Log.d("BindDevcieAdapter", "onBindViewHolder--: "+getData().get(position).getAddress().equalsIgnoreCase(bindState));
                holder.deviceState.setVisibility(View.GONE);
                holder.device_state_tv.setVisibility(View.VISIBLE);
                holder.device_state_tv.setTextColor(mContext.getResources().getColor(R.color.color_cccccc));
                holder.deviceAddress.setTextColor(mContext.getResources().getColor(R.color.color_cccccc));
                break;
            }else {
                holder.deviceState.setVisibility(View.VISIBLE);
                holder.device_state_tv.setVisibility(View.GONE);
                holder.deviceState.setText("绑定");
                holder.deviceAddress.setTextColor(mContext.getResources().getColor(R.color.color_333333));

            }

        }
    }
    }

    @Override
    public int getItemCount() {
        if(mList!=null)
            return getData().size()>20?20:getData().size();
        else
            return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.device_icon)
        ImageView deviceIcon;
        @BindView(R.id.device_address)
        TextView deviceAddress;
        @BindView(R.id.device_state)
        Button deviceState;
        @BindView(R.id.device_state_tv)
        TextView device_state_tv;
        private MyItemClickListener mListener;

        public ViewHolder(View itemView,MyItemClickListener mListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mListener=mListener;
            deviceState.setOnClickListener(this);
        }

        /**
         * 点击监听
         */
        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }

    }
    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }



}
