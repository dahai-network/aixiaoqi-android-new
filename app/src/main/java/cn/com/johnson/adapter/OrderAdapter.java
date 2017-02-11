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
	private boolean isHaveHeader = true;
	private List<BoughtPackageEntity.ListBean> data = new ArrayList<>();
	private Context context;
	private int clickPosition = -1;
	//订单类型
	private final int NORMALPACKET = 0;
	private final int CALLPACKET = 1;
	private final int KINGCARDPACKET = 2;
	private final int DUALCARDSSTANDBY = 3;//双卡双待


	public List<BoughtPackageEntity.ListBean> getData() {
		return data;
	}

	public OrderAdapter(Context context, int layout) {
		this.context = context;
		this.layout = layout;
	}

	public OrderAdapter(Context context, List<BoughtPackageEntity.ListBean> data, boolean isHaveHeader, int layout) {
		if (data != null) {
			this.data = data;
		}
		this.context = context;
		this.isHaveHeader = isHaveHeader;
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
		RecyclerView.ViewHolder holder = null;
		switch (viewType) {
			case NORMALPACKET:
				holder = new NormalViewHolder(LayoutInflater.from(context).inflate(layout, parent, false));
				break;
			case CALLPACKET:
				holder = new CallViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_order, parent, false));
				break;
			case KINGCARDPACKET:
				holder = new KingCardViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kingcard_order, parent, false));
				break;
			case DUALCARDSSTANDBY:
				holder = new KingCardViewHolder(LayoutInflater.from(context).inflate(R.layout.item_call_order, parent, false));
				break;
		}
		return holder;
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (data == null) return;
		int type = getItemViewType(position);
		if (type == NORMALPACKET) {
			final NormalViewHolder normalHolder = (NormalViewHolder) holder;
			BoughtPackageEntity.ListBean bean = data.get(position);
			normalHolder.packageNameTextView.setText(bean.getPackageName());
			normalHolder.priceTextView.setText("￥" + bean.getTotalPrice());
			Glide.with(context).load(bean.getLogoPic()).into(normalHolder.countryImageView);
			normalHolder.dateTextView.setText(bean.getExpireDays());
			//如果订单状态是正在使用，那么就计算时间
			if (bean.getOrderStatus() == 0) {
				normalHolder.stateTextView.setText("未激活");
				normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				normalHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);

			} else if (bean.getOrderStatus() == 2) {
				normalHolder.stateTextView.setText("已过期");
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				normalHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			} else if (bean.getOrderStatus() == 3) {
				normalHolder.stateTextView.setText("已取消");
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				normalHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			} else if (bean.getOrderStatus() == 4) {
				normalHolder.stateTextView.setText("激活失败");
				normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				normalHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			} else {
				normalHolder.stateTextView.setText("已激活");
				normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.select_contacct));
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.effective);
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				normalHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			}
			//判断layout，如果是订单列表layout则需要
			if (layout == R.layout.item_order) {
				setSpan(normalHolder.priceTextView, position);
			}
		} else if (type == CALLPACKET) {
			final CallViewHolder callViewHolder = (CallViewHolder) holder;
			BoughtPackageEntity.ListBean callBean = data.get(position);
			callViewHolder.packageNameTextView.setText(callBean.getPackageName());
			callViewHolder.priceTextView.setText("￥" + callBean.getTotalPrice());
			Glide.with(context).load(callBean.getLogoPic()).into(callViewHolder.packetImageView);
			callViewHolder.dateTextView.setText(callBean.getExpireDays());
			callViewHolder.stateTextView.setText(context.getResources().getString(R.string.residue) + callBean.getRemainingCallMinutes() + context.getResources().getString(R.string.minute));
			setResidueMinueSpan(callViewHolder.stateTextView, callBean.getRemainingCallMinutes());
			//判断layout，如果是订单列表layout则需要
			if (layout == R.layout.item_order) {
				setSpan(callViewHolder.priceTextView, position);
			}

		} else if (type == KINGCARDPACKET || type == DUALCARDSSTANDBY) {
			final KingCardViewHolder kingCardViewHolder = (KingCardViewHolder) holder;
			BoughtPackageEntity.ListBean kingBean = data.get(position);
			kingCardViewHolder.packageNameTextView.setText(kingBean.getPackageName());
			kingCardViewHolder.priceTextView.setText("￥" + kingBean.getTotalPrice());
			Glide.with(context).load(kingBean.getLogoPic()).into(kingCardViewHolder.packetImageView);
			kingCardViewHolder.dateTextView.setText(kingBean.getExpireDays());
			//如果订单状态是正在使用，那么就计算时间
			if (kingBean.getOrderStatus() == 0) {
				kingCardViewHolder.stateTextView.setText("未激活");
				kingCardViewHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				kingCardViewHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);

			} else if (kingBean.getOrderStatus() == 2) {
				kingCardViewHolder.stateTextView.setText("已过期");
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				kingCardViewHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				kingCardViewHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			} else if (kingBean.getOrderStatus() == 3) {
				kingCardViewHolder.stateTextView.setText("已取消");
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				kingCardViewHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				kingCardViewHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			} else if (kingBean.getOrderStatus() == 4) {
				kingCardViewHolder.stateTextView.setText("激活失败");
				kingCardViewHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.un_effective);
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				kingCardViewHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			} else {
				kingCardViewHolder.stateTextView.setText("已激活");
				kingCardViewHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.select_contacct));
				Drawable drawable = ContextCompat.getDrawable(context, R.drawable.effective);
				/// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				kingCardViewHolder.stateTextView.setCompoundDrawables(drawable, null, null, null);
			}
			//判断layout，如果是订单列表layout则需要
			if (layout == R.layout.item_order) {
				setSpan(kingCardViewHolder.priceTextView, position);
			}
		}

	}

	private void setResidueMinueSpan(TextView stateTextView, int remainingCallMinutes) {
		Spannable WordtoSpan = new SpannableString(stateTextView.getText().toString());
		WordtoSpan.setSpan(new ForegroundColorSpan(
				ContextCompat.getColor(context, R.color.select_contacct)), 2, 2 + String.valueOf(remainingCallMinutes).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		stateTextView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@Override
	public int getItemViewType(int position) {
		return data.get(position).getPackageCategory();
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
			if (isHaveHeader) {
				clickPosition = getPosition() - 1;
				MyOrderDetailActivity.launch(context, data.get(getPosition() - 1).getOrderID());
			} else {
				clickPosition = getPosition();
				MyOrderDetailActivity.launch(context, data.get(getPosition()).getOrderID());
			}
		}
	}

	class CallViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.packetImageView)
		ImageView packetImageView;
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

		public CallViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		//item点击事件
		@OnClick(R.id.rootLinearLayout)
		public void onClick(View view) {
			if (isHaveHeader) {
				clickPosition = getPosition() - 1;
				CallTimeOrderDetailActitivy.launch(context, data.get(getPosition() - 1).getOrderID());
			} else {
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKINDEXORDER);
				clickPosition = getPosition();
				Log.e("onClick","clickPosition="+clickPosition);
				CallTimeOrderDetailActitivy.launch(context, data.get(getPosition()).getOrderID());
			}
		}
	}

	class KingCardViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.packetImageView)
		ImageView packetImageView;
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

		public KingCardViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		//item点击事件
		@OnClick(R.id.rootLinearLayout)
		public void onClick(View view) {
			if (isHaveHeader) {
				clickPosition = getPosition() - 1;
				KingCardDetailActivity.launch(context, data.get(getPosition() - 1).getOrderID(), data.get(getPosition()-1).getOrderStatus());
			} else {
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKINDEXORDER);
				clickPosition = getPosition();
				KingCardDetailActivity.launch(context, data.get(getPosition()).getOrderID(), data.get(getPosition()).getOrderStatus());
			}
		}

	}
}