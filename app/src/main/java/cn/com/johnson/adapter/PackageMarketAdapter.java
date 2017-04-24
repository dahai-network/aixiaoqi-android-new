package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.PacketMarketEntity;
import de.blinkt.openvpn.views.DividerGridItemDecoration;

/**
 * Created by Administrator on 2016/9/3.
 */
public class PackageMarketAdapter extends RecyclerView.Adapter {

	private List<List<PacketMarketEntity>> data = null;
	private Context context = null;

	public PackageMarketAdapter(List<List<PacketMarketEntity>> data, Context context) {
		this.data = data;
		this.context = context;


	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(
				context).inflate(R.layout.item_continent, parent,
				false));
		return viewHolder;
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		//强制关闭复用
		holder.setIsRecyclable(false);
		List<PacketMarketEntity> packetMarketEntityList=	data.get(position);
		RecyclerView recyclerView = ((ViewHolder) holder).countryRecyclerView;
		if (packetMarketEntityList.size() == 0) {
			((ViewHolder) holder).continentLinearLayout.setVisibility(View.GONE);
			((ViewHolder) holder).continentTextView.setVisibility(View.GONE);
			recyclerView.setVisibility(View.GONE);
		} else {
			((ViewHolder) holder).continentLinearLayout.setVisibility(View.VISIBLE);
			((ViewHolder) holder).continentTextView.setVisibility(View.VISIBLE);
			 GridLayoutManager  gridLayoutManager=	new GridLayoutManager(context,4);
			gridLayoutManager.setSmoothScrollbarEnabled(false);
			recyclerView.setVisibility(View.VISIBLE);
			recyclerView.addItemDecoration(new DividerGridItemDecoration(context));
			recyclerView.setLayoutManager(gridLayoutManager);

			recyclerView.setAdapter(new CountryAdapter(packetMarketEntityList, context));
			((ViewHolder) holder).continentTextView.setText(packetMarketEntityList.get(0).getContinentsDescr() + "");
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}


	public class ViewHolder extends RecyclerView.ViewHolder {

		private final TextView continentTextView;
		public RecyclerView countryRecyclerView;
		public LinearLayout continentLinearLayout;

		public ViewHolder(View itemView) {
			super(itemView);
			continentTextView = (TextView) itemView.findViewById(R.id.continentTextView);
			countryRecyclerView = (RecyclerView) itemView.findViewById(R.id.countryRecyclerView);
			continentLinearLayout = (LinearLayout) itemView.findViewById(R.id.continentLinearLayout);
		}
	}
}
