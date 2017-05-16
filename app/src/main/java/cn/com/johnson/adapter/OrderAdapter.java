package cn.com.johnson.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
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
import de.blinkt.openvpn.activities.ActivateActivity;
import de.blinkt.openvpn.activities.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.OutsideFirstStepActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;

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

	public OrderAdapter(Context context, List<BoughtPackageEntity.ListBean> data, int layout) {
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

		if (bean.getOrderStatus() == 0 || bean.getOrderStatus() == 4) {
			normalHolder.stateTextView.setText("未激活");
//			normalHolder.stateTextView.setTextColor(context.getResources().getColorStateList(R.color.activite_color_selector));
		} else if (bean.getOrderStatus() == 2) {
			normalHolder.stateTextView.setText("已过期");
			normalHolder.stateTextView.setVisibility(View.GONE);
		} else if (bean.getOrderStatus() == 3) {
			normalHolder.stateTextView.setText("已取消");
			normalHolder.stateTextView.setVisibility(View.GONE);
		}
//		else if () {
//			normalHolder.stateTextView.setText("激活失败");
//			normalHolder.stateTextView.setVisibility(View.GONE);
//			normalHolder.stateTextView.setTextColor(ContextCompat.getColor(context, R.color.order_item));
//		}
		else {
			normalHolder.stateTextView.setText("境外设置");
			normalHolder.stateTextView.setTextColor(context.getResources().getColorStateList(R.color.gray_background_text_selector));
			normalHolder.stateTextView.setBackgroundResource(R.drawable.circle_light_gray_selector);
			if (1 == bean.getPackageCategory()) {
				normalHolder.stateTextView.setVisibility(View.GONE);

			} else {
				normalHolder.stateTextView.setVisibility(View.VISIBLE);
			}
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
		@OnClick({R.id.rootLinearLayout, R.id.stateTextView})
		public void onClick(View view) {
			//友盟方法统计
			clickPosition = getPosition() - 1;
			BoughtPackageEntity.ListBean bean = data.get(clickPosition);
			switch (view.getId()) {

				case R.id.rootLinearLayout:
					MobclickAgent.onEvent(context, CLICKINDEXORDER);

					MyOrderDetailActivity.launch(context, bean.getOrderID(), 0);

					break;
				case R.id.stateTextView:
					if (bean.getOrderStatus() == 0 || bean.getOrderStatus() == 4) {
						context.startActivity(new Intent(context, ActivateActivity.class).putExtra(IntentPutKeyConstant.ORDER_ID, bean.getOrderID()).putExtra("ExpireDaysInt", bean.getExpireDaysInt())
								.putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, bean.isPackageIsSupport4G())
								.putExtra(IntentPutKeyConstant.COUNTRY_NAME, bean.getCountryName())
								.putExtra(IntentPutKeyConstant.APN_NAME, bean.getPackageApnName()));
					} else {
						Constant.isOutsideSecondStepClick = false;
						Constant.isOutsideThirdStepClick = false;
						context.startActivity(new Intent(context, OutsideFirstStepActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE, IntentPutKeyConstant.AFTER_GOING_ABROAD)
								.putExtra(IntentPutKeyConstant.IS_SUPPORT_4G, bean.isPackageIsSupport4G())
								.putExtra(IntentPutKeyConstant.APN_NAME, bean.getPackageApnName()));
					}
					break;
			}

		}
	}

}