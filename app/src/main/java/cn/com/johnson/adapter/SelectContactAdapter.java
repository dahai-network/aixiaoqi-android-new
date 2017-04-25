package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.ContactBean;





/**
 * Created by jiang on 12/3/15.
 * 根据当前权限进行判断相关的滑动逻辑
 */

public class SelectContactAdapter extends RecyclerBaseAdapter<SelectContactAdapter.ContactViewHolder, ContactBean>
		implements View.OnClickListener {
	/**
	 * 当前处于打开状态的item
	 */

	public SelectContactAdapter(Context ct, List<ContactBean> mLists) {
		super(ct, mLists);
	}

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_address_list, parent, false);
		return new ContactViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ContactViewHolder holder, final int position) {

		ContactBean contactBean = mList.get(position);
		holder.mName.setText(contactBean.getDesplayName());
		if(contactBean.getBitmapHeader()!=null){
			holder.headImage.setImageBitmap(contactBean.getBitmapHeader());
		}else{
			holder.headImage.setImageResource(R.drawable.contact_default_header);
		}
		holder.itemView.setTag(contactBean);
		holder.itemView.setOnClickListener(this);
		// 获取首字母的assii值
		char selection = contactBean.getSortLetters().charAt(0);
		// 通过首字母的assii值来判断是否显示字母
		int positionForSelection = getPositionForSection(selection);
		if (position == positionForSelection) {// 相等说明需要显示字母
			holder.tag.setVisibility(View.VISIBLE);
			holder.tag.setText(String.valueOf(selection));
		} else {
			holder.tag.setVisibility(View.GONE);

		}
	}



	@Override
	public void onClick(View v) {
		if (onItemClickListener != null) {
			onItemClickListener.onItemClick(v, v.getTag(), false);
		}
	}

	public int getPositionForSection(char section) {
		int count = getItemCount();
		List<ContactBean> localList = mList;
		for (int i = 0; i < count; i++) {
			String sortStr = localList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;

	}



	public class ContactViewHolder extends RecyclerView.ViewHolder {
		public TextView mName;
		public ImageView headImage;
		public TextView tag;
		public ContactViewHolder(View itemView) {
			super(itemView);
			mName = (TextView) itemView.findViewById(R.id.nameTextView);
			headImage = (ImageView) itemView.findViewById(R.id.headImageView);
			tag = (TextView) itemView.findViewById(R.id.tag);
		}
	}

}
