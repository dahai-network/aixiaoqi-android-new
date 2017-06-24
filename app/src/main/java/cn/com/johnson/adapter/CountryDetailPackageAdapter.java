package cn.com.johnson.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.ShopModules.ui.PackageDetailActivity;
import de.blinkt.openvpn.model.CountryPacketEntity;

/**
 * Created by Administrator on 2016/9/3.
 */
public class CountryDetailPackageAdapter extends RecyclerView.Adapter<CountryDetailPackageAdapter.ViewHolder> {


	private List<CountryPacketEntity> data = new ArrayList<>();
	private Context context;
	private String countryPic;

	public CountryDetailPackageAdapter(Context context, List<CountryPacketEntity> data, String countryPic) {
		this.data = data;
		this.context = context;
		this.countryPic = countryPic;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_country_detail_package, parent, false));
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.packageNameTextView.setText(data.get(position).getPackageName());
		holder.flowTextView.setText(data.get(position).getFlow());
		holder.priceTextView.setText(context.getString(R.string.money_type)+ data.get(position).getPrice());
		setSpan(holder.priceTextView, position);
		Glide.with(context).load(data.get(position).getPic()).into(holder.numberTextView);
		holder.v_line.setVisibility(View.GONE);
		if(TextUtils.isEmpty(data.get(position).getOriginalPrice())){
			holder.originalPriceTextView.setVisibility(View.GONE);
		}else{
			holder.originalPriceTextView.setVisibility(View.VISIBLE);
			holder.originalPriceTextView.setText(context.getString(R.string.original_price)+context.getString(R.string.money_type)+data.get(position).getOriginalPrice());
			holder.originalPriceTextView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线

		}

	}

	//设置大小字体
	public void setSpan(TextView textview, int position) {
		Spannable WordtoSpan = new SpannableString(textview.getText().toString());
		int intLength = String.valueOf((int) (data.get(position).getPrice())).length();
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@Override
	public int getItemCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}


	class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.packageNameTextView)
		TextView packageNameTextView;
		@BindView(R.id.flowTextView)
		TextView flowTextView;
		@BindView(R.id.priceTextView)
		TextView priceTextView;
		@BindView(R.id.originalPriceTextView)
		TextView originalPriceTextView;
		@BindView(R.id.rootLinearLayout)
		RelativeLayout rootLinearLayout;
		@BindView(R.id.numberTextView)
		ImageView numberTextView;
		@BindView(R.id.v_line)
		View v_line;


		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		@OnClick(R.id.rootLinearLayout)
		public void onClick() {
			PackageDetailActivity.launch(context, data.get(getAdapterPosition()).getID(), countryPic);
		}
	}
}
