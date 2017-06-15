package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.WebViewActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.ProductEntity;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

	private final List<ProductEntity> data;
	private final Context context;

	public ProductsAdapter(List<ProductEntity> data, Context context) {
		this.data = data;
		this.context = context;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final ProductEntity bean = data.get(position);
		Glide.with(ICSOpenVPNApplication.getContext()).load(bean.getImage()).into(holder.productImageView);
		holder.productNameTextView.setText(bean.getTitle());
		if (bean.getPrice() != 0) {
			holder.productPriceTextView.setText("￥ " + bean.getPrice());
		}

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(bean.getUrl()))
					WebViewActivity.launch(context, bean.getUrl(), bean.getTitle());
			}
		});
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView productImageView;
		private TextView productNameTextView;
		private TextView productPriceTextView;

		public ViewHolder(View itemView) {
			super(itemView);
			productImageView = (ImageView) itemView.findViewById(R.id.productImageView);
			productNameTextView = (TextView) itemView.findViewById(R.id.productNameTextView);
			productPriceTextView = (TextView) itemView.findViewById(R.id.productPriceTextView);
		}
	}
}
