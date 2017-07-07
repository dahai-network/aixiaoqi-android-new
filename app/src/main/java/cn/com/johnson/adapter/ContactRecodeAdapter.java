package cn.com.johnson.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.util.DatabaseDAO;
import de.blinkt.openvpn.util.PhoneFormatUtil;
import de.blinkt.openvpn.util.PhoneNumberZone;


/**
 * Created by Administrator on 2016/9/24 0024.
 */
public class ContactRecodeAdapter extends RecyclerBaseAdapter<ContactRecodeAdapter.ViewHolder, ContactRecodeEntity> implements View.OnClickListener {
	DatabaseDAO dao;
	public String searchContent;
	public ContactRecodeAdapter(DatabaseDAO dao,Context context, List<ContactRecodeEntity> list) {
		super(context, list);
		this.dao=dao;
	}
	public void setSearchChar(String searchContent){
		this.searchContent=searchContent;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {


		ContactRecodeEntity contactRecodeEntity = mList.get(position);

		final 	String phoneNumber = contactRecodeEntity.getPhoneNumber();

		final 	String name = contactRecodeEntity.getName();

		Log.d("ContactRecodeAdapter", "onBindViewHolder:--- "+phoneNumber+"--"+name);

		String address= PhoneNumberZone.getAddress(dao, PhoneFormatUtil.deleteprefix(" ",phoneNumber));
		if (!TextUtils.isEmpty(name)) {
			holder.mNameTv.setVisibility(View.VISIBLE);
			holder.mNameTv.setText(contactRecodeEntity.getName());
			//holder.mPhoneNumber.setText(phoneNumber + "  " + address);
		} else if (!TextUtils.isEmpty(phoneNumber)) {
			holder.mNameTv.setText(phoneNumber);

		}

		holder.mPhoneNumber.setText(address);

		holder.mCallStatusImg.setImageResource(R.drawable.icon_bd);
		//来电
		if (Constant.CALL_INCOMING.equals(contactRecodeEntity.getTypeString())) {
			holder.mNameTv.setTextColor(Color.BLACK);
			//holder.mCallStatusImg.setVisibility(View.INVISIBLE);
			holder.mCallStatusImg.setVisibility(View.VISIBLE);
			holder.mCallStatusImg.setImageResource(R.drawable.image_answer_state);
		}
		//未接
		if (Constant.CALL_MISSED.equals(contactRecodeEntity.getTypeString())) {
			holder.mNameTv.setTextColor(Color.RED);
			holder.mCallStatusImg.setVisibility(View.INVISIBLE);
		}
		//拨出
		if (Constant.CALL_OUTGOING.equals(contactRecodeEntity.getTypeString())) {
			holder.mNameTv.setTextColor(Color.BLACK);
			holder.mCallStatusImg.setVisibility(View.VISIBLE);
			holder.mCallStatusImg.setImageResource(R.drawable.image_dial_state);
		}

		if (!TextUtils.isEmpty(contactRecodeEntity.getData())) {
			holder.mDateTime.setText(contactRecodeEntity.getData());
		}
		if(!TextUtils.isEmpty(searchContent)) {
			if(!TextUtils.isEmpty(name)){
				if (contactRecodeEntity.getFormattedNumber()!=null&&(contactRecodeEntity.getFormattedNumber()[0].contains(searchContent)||contactRecodeEntity.getFormattedNumber()[1].contains(searchContent))) {
					holder.mNameTv.setTextColor(mContext.getResources().getColor(R.color.phone_top_color));
				} else {
					holder.mNameTv.setTextColor(Color.BLACK);
				}
				setSearchContentColor(holder.mPhoneNumber, phoneNumber);
			}else{
				setSearchContentColor(holder.mNameTv, phoneNumber);
			}
		}
		holder.mIvArrow.setOnClickListener(this);
		holder.mIvArrow.setTag(contactRecodeEntity);
		holder.itemView.setTag(contactRecodeEntity);
	}

	private void setSearchContentColor(TextView textView, String phoneNumber) {
		int index=phoneNumber.indexOf(searchContent);
		if(index<0){
			return ;
		}
		SpannableStringBuilder style=new SpannableStringBuilder(phoneNumber);
		style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.phone_top_color)),index,index+searchContent.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		textView.setText(style);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.telephone_records_list_item, parent, false));
		holder.itemView.setOnClickListener(this);
		return holder;
	}


	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			//注意这里使用getTag方法获取数据
			onItemClickListener.onItemClick(v, v.getTag(), false);
		}
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView mNameTv;
		TextView mPhoneNumber, mDateTime;
		ImageView mIvArrow;
		ImageView mCallStatusImg;
		public ViewHolder(View itemView) {
			super(itemView);
			//phone_number_text_view
			mNameTv = (TextView) itemView.findViewById(R.id.name_text_view);
			mPhoneNumber = (TextView) itemView.findViewById(R.id.phone_number_text_view);
			mCallStatusImg = (ImageView) itemView.findViewById(R.id.callstatusimg);
			mIvArrow = (ImageView) itemView.findViewById(R.id.iv_arrow);
			mDateTime = (TextView) itemView.findViewById(R.id.datatime_txt);

		}
	}
}
