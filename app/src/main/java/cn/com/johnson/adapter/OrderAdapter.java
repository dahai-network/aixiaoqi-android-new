package cn.com.johnson.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.activities.CallTimeOrderDetailActitivy;
import de.blinkt.openvpn.activities.KingCardDetailActivity;
import de.blinkt.openvpn.activities.MyOrderDetailActivity;

import static de.blinkt.openvpn.constant.UmengContant.CLICKINDEXORDER;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	//设置itemLayout
	private int layout;

	private List<BoughtPackageEntity.ListBean> data = new ArrayList<>();
	private Context context;
	private int clickPosition = -1;

	public List<BoughtPackageEntity.ListBean> getData() {
		return data;
	}

	public OrderAdapter(Context context, int layout) {
		this.context = context;
		this.layout = layout;
	}

	public OrderAdapter(Context context, List<BoughtPackageEntity.ListBean> data , int layout) {
		if (data != null) {
			this.data = data;
		}
		this.context = context;
		this.layout = layout;
	}


	public void addAll(List<BoughtPackageEntity.ListBean> listData) {
		data.clear();
		data.addAll(listData);
		notifyDataSetChanged();
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}

	//改变订单写卡状态
	public void changeStatus(int status) {
		data.get(clickPosition).setOrderStatus(status);
		notifyDataSetChanged();
		clickPosition = -1;
	}

	public void add(List<BoughtPackageEntity.ListBean> listData) {
		data.addAll(listData);
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder holder = new NormalViewHolder(LayoutInflater.from(context).inflate(layout, parent, false));
		return holder;
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (data == null) return;
		final NormalViewHolder normalHolder = (NormalViewHolder) holder;
		BoughtPackageEntity.ListBean bean = data.get(position);
		normalHolder.packageNameTextView.setText(bean.getPackageName());
		normalHolder.priceTextView.setText("￥" + bean.getTotalPrice());
		Glide.with(context).load(bean.getLogoPic()).into(normalHolder.countryImageView);
		normalHolder.dateTextView.setText(bean.getExpireDays());
		//如果订单状态是正在使用，那么就计算时间
		normalHolder.stateTextView.setVisibility(View.GONE);
		if (bean.getOrderStatus() == 0) {
			normalHolder.stateTextView.setText("未激活");
			normalHolder.stateTextView.setTextColor(context.getResources().getColorStateList(R.color.activite_color_selector));
		} else if (bean.getOrderStatus() == 2) {
			normalHolder.stateTextView.setText("已过期");
		} else if (bean.getOrderStatus() == 3) {
			normalHolder.stateTextView.setText("已取消");
		} else if (bean.getOrderStatus() == 4) {
			normalHolder.stateTextView.setText("激活失败");
			normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
		} else {
			normalHolder.stateTextView.setText("已激活");
			normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.select_contacct));

		}
		//判断layout，如果是订单列表layout则需要
		setSpan(normalHolder.priceTextView, position);
	}






	//设置大小字体
	public void setSpan(TextView textview, int position) {
		Spannable WordtoSpan = new SpannableString(textview.getText().toString());
		int intLength = String.valueOf((int) (data.get(position).getTotalPrice())).length();
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(22, true), 1, intLength + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(15, true), intLength + 2, textview.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@Override
	public int getItemCount() {
		return data != null ? data.size() : 0;
	}

	class NormalViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.countryImageView)
		ImageView countryImageView;
		@BindView(R.id.packageNameTextView)
		TextView packageNameTextView;
		@BindView(R.id.priceTextView)
		TextView priceTextView;
		@BindView(R.id.dateTextView)
		TextView dateTextView;
		@BindView(R.id.stateTextView)
		TextView stateTextView;
		@BindView(R.id.rootLinearLayout)
		RelativeLayout rootLinearLayout;

		public NormalViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		//item点击事件
		@OnClick(R.id.rootLinearLayout)
		public void onClick(View view) {
			//友盟方法统计
			MobclickAgent.onEvent(context, CLICKINDEXORDER);
			clickPosition = getPosition() - 1;
			MyOrderDetailActivity.launch(context, data.get(clickPosition).getOrderID(),0);

		}
	}

}