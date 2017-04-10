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
import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.widget.GlideCircleTransform;
import cn.com.johnson.widget.GlideRoundTransform;
import de.blinkt.openvpn.activities.CountryPackageActivity;

import static de.blinkt.openvpn.constant.UmengContant.CLICKHOTPACKAGE;

/**
 * Created by Administrator on 2016/9/1.
 */
public class HotPackageAdapter extends RecyclerView.Adapter<HotPackageAdapter.ViewHolder> {

	private List<HotPackageEntity> data;
	private Context context = null;
	private boolean isFromIndex = false;

	public HotPackageAdapter(List<HotPackageEntity> data, Context context, boolean isFromIndex) {
		this.data = data;
		this.context = context;
		this.isFromIndex = isFromIndex;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder viewHolder;
		if (!isFromIndex) {
			viewHolder = new ViewHolder(LayoutInflater.from(
					context).inflate(R.layout.item_hot_package, parent,
					false));
		} else {
			viewHolder = new ViewHolder(LayoutInflater.from(
					context).inflate(R.layout.item_hot_package_index, parent,
					false));
		}
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		holder.countryTextView.setText(data.get(position).getCountryName());
		if (!isFromIndex)
			Glide.with(context).load(data.get(position).getLogoPic()).transform(new GlideRoundTransform(context)).into(holder.hotPackageImageView);
		else
			Glide.with(context).load(data.get(position).getLogoPic()).transform(new GlideCircleTransform(context)).into(holder.hotPackageImageView);
		holder.packageLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//友盟方法统计
				HashMap<String, String> map = new HashMap<>();
				map.put("zone", data.get(position).getCountryName());
				MobclickAgent.onEvent(context, CLICKHOTPACKAGE, map);
				CountryPackageActivity.launch(context, data.get(position).getPic(),
						data.get(position).getCountryName(),
						data.get(position).getCountryID());
			}
		});
	}

	@Override
	public int getItemCount() {
		return data.size();
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
