/**
 * created by jiang, 12/3/15
 * Copyright (c) 2015, jyuesong@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */

package cn.com.johnson.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.views.RoundImageView;
import de.blinkt.openvpn.views.SwipeItemLayout;


/**
 * Created by jiang on 12/3/15.
 * 根据当前权限进行判断相关的滑动逻辑
 */
public class ContactAdapter extends RecyclerBaseAdapter<ContactAdapter.ContactViewHolder, ContactBean> {
	/**
	 * 当前处于打开状态的item
	 */
	private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();
	private CallLisener callLisener;
	private View headView;

	public ContactAdapter(Context ct, List<ContactBean> mLists, CallLisener callLisener) {
		super(ct, mLists);
		this.callLisener = callLisener;
	}

	@Override
	public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_address_list, parent, false);
		return new ContactViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, final int position) {
		ContactBean contactBean=	getItem(position);
		holder.mName.setText(contactBean.getDesplayName());
//		holder.headImage.setImageResource(getItem(position).getHeader());
		if(contactBean.getBitmapHeader()!=null){
			holder.headImage.setImageBitmap(contactBean.getBitmapHeader());
		}else{
			holder.headImage.setImageResource(R.drawable.contact_default_header);
		}
		holder.headImage.setBorderRadius(3);
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

//	@Override
//	public long getHeaderId(int position) {
//
//		return getItem(position).getSortLetters().charAt(0);
//	}
//
//	@Override
	public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
		headView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.contact_header, parent, false);
		return new RecyclerView.ViewHolder(headView) {
		};
	}
//
//	@Override
//	public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
//		TextView textView = (TextView) headView.findViewById(R.id.headNameTextView);
//		String showValue = String.valueOf(getAlpha(getItem(position).getSortLetters()));
//		textView.setText(showValue);
//	}


	public int getPositionForSection(char section) {
		List<ContactBean> localList = mList;
		int count = getItemCount();
		for (int i = 0; i < count; i++) {
			String sortStr = localList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;

	}

	public void closeOpenedSwipeItemLayoutWithAnim() {
		List<SwipeItemLayout> localMOpenedSil = mOpenedSil;
		for (SwipeItemLayout sil : localMOpenedSil) {
			sil.closeWithAnim();
		}
		localMOpenedSil.clear();
	}

	public class ContactViewHolder extends RecyclerView.ViewHolder {

		public TextView mName;
		//        public SwipeItemLayout mRoot;
		public RoundImageView headImage;
		public TextView tag;
		RelativeLayout clickRl;
		public ContactViewHolder(View itemView) {
			super(itemView);

			mName = (TextView) itemView.findViewById(R.id.nameTextView);
			headImage = (RoundImageView) itemView.findViewById(R.id.headImageView);
			tag = (TextView) itemView.findViewById(R.id.tag);
			clickRl = (RelativeLayout) itemView.findViewById(R.id.click_rl);
			clickRl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					callLisener.gotoActivity(getItem(getAdapterPosition()), getAdapterPosition());
				}
			});
		}


	}


	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式匹配
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 将小写字母转换为大写
		} else {
			return "#";
		}
	}

	//拨打电话接口
	public interface CallLisener {
		void gotoActivity(ContactBean contactBean, int position);
	}

}
