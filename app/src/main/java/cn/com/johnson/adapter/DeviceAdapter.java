package cn.com.johnson.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import cn.com.aixiaoqi.R;


/**
 * Created by Administrator on 2016/10/3 0003.
 */
public class DeviceAdapter extends RecyclerBaseAdapter<DeviceAdapter.ViewHolder, BluetoothDevice> implements View.OnClickListener {

	public DeviceAdapter(Context context, List<BluetoothDevice> list) {
		super(context, list);


	}

	@Override
	public void onBindViewHolder(DeviceAdapter.ViewHolder holder, int position) {
		BluetoothDevice device = mList.get(position);
		holder.tvname.setText(device.getName());
		holder.tvadd.setText(device.getAddress());
		holder.itemView.setTag(device);
	}

	@Override
	public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.device_element, parent, false));
		holder.itemView.setOnClickListener(this);
		return holder;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		TextView tvadd;
		TextView tvname;

		public ViewHolder(View itemView) {
			super(itemView);
			tvadd = ((TextView) itemView.findViewById(R.id.address));
			tvname = ((TextView) itemView.findViewById(R.id.name));
		}
	}

	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			//注意这里使用getTag方法获取数据
			onItemClickListener.onItemClick(v, v.getTag(), false);
		}
	}


}
