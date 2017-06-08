package cn.com.johnson.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

import cn.com.aixiaoqi.R;
import cn.com.johnson.widget.GlideRoundTransform;
import de.blinkt.openvpn.activities.ShopModules.ui.CountryPackageActivity;
import de.blinkt.openvpn.model.PacketMarketEntity;

import static de.blinkt.openvpn.constant.UmengContant.CLICKZONEPACKAGEITEM;

/**
 * Created by Administrator on 2016/9/14.
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {

	private List<PacketMarketEntity> data;
	private Context context = null;


	public CountryAdapter(List<PacketMarketEntity> data, Context context) {
		this.data = data;
		this.context = context;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(
				context).inflate(R.layout.item_hot_package, parent,
				false));
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		holder.countryTextView.setText(data.get(position).getCountryName());
		Glide.with(context).load(data.get(position).getLogoPic()).transform(new GlideRoundTransform(context)).into(holder.hotPackageImageView);
		holder.packageLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String,String> map = new HashMap<>();
				map.put("zone",data.get(position).getCountryName());
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKZONEPACKAGEITEM , map);
				PacketMarketEntity bean = data.get(position);
				CountryPackageActivity.launch(context, bean.getPic(), bean.getCountryName(), bean.getCountryID());
			}
		});
	}

	@Override
	public int getItemCount() {
		if (data != null) {
			return data.size();
		} else {
			return 0;
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		ImageView hotPackageImageView;
		TextView countryTextView;
		LinearLayout packageLinearLayout;

		public ViewHolder(View itemView) {
			super(itemView);
			hotPackageImageView = (ImageView) itemView.findViewById(R.id.hotPackageImageView);
			countryTextView = (TextView) itemView.findViewById(R.id.countryTextView);
			packageLinearLayout = (LinearLayout) itemView.findViewById(R.id.packageLinearLayout);
		}
	}
}
