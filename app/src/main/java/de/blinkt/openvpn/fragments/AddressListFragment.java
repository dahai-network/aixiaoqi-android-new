package de.blinkt.openvpn.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.com.aixiaoqi.R;

import cn.com.johnson.adapter.ContactAdapter;
import de.blinkt.openvpn.activities.CallDetailActivity;
import de.blinkt.openvpn.activities.ContactDetailActivity;

import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.pinyin.CharacterParser;


import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.contact.SideBar;
import de.blinkt.openvpn.views.contact.TouchableRecyclerView;
import de.blinkt.openvpn.views.contact.expand.StickyRecyclerHeadersDecoration;

public class AddressListFragment extends Fragment implements ContactAdapter.CallLisener, ContactDetailActivity.ContactChangeDataListener {

	private SideBar mSideBar;
	private TextView mUserDialog;
	private TextView tvNoPermission;
	private TouchableRecyclerView mRecyclerView;
	private ContactAdapter mAdapter;


	List<ContactBean> mAllLists=new ArrayList<>();
	private EditText searchEditText;
//	private StickyRecyclerHeadersDecoration headersDecor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_selection_common,
				container, false);
		initView(rootView);
		return rootView;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initView(View rootView) {

		TitleBar title = (TitleBar) rootView.findViewById(R.id.title);
		title.setTextTitle(getString(R.string.address_list));
		mSideBar = (SideBar) rootView.findViewById(R.id.contact_sidebar);
		mUserDialog = (TextView) rootView.findViewById(R.id.contact_dialog);
		tvNoPermission = (TextView) rootView.findViewById(R.id.tv_no_permission);
		searchEditText = (EditText) rootView.findViewById(R.id.searchEditText);
		mRecyclerView = (TouchableRecyclerView) rootView.findViewById(R.id.contact_member);
		mSideBar.setTextView(mUserDialog);
		// 实例化
		mAdapter = new ContactAdapter(getActivity(), mAllLists, this);
		mAllLists=ICSOpenVPNApplication.getInstance().getContactList();
		if(mAllLists!=null&&mAllLists.size()!=0){
			tvNoPermission.setVisibility(View.GONE);
		}else{
			tvNoPermission.setVisibility(View.VISIBLE);
		}
		mAdapter.addAll(mAllLists);
		int orientation = LinearLayoutManager.VERTICAL;
		final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), orientation, false);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(mAdapter);
//		headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
//		mRecyclerView.addItemDecoration(headersDecor);
////		mRecyclerView.addItemDecoration(new SpaceItemDecoration(28));
//		mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//			@Override
//			public void onChanged() {
//				headersDecor.invalidateHeaders();
//			}
//		});
		setSearchLinstener();
	}

	private void setSearchLinstener() {
		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s.toString().trim())) {
					mAdapter.addAll(search(s.toString().trim()));
//					mRecyclerView.removeItemDecoration(headersDecor);
				} else {

					mAdapter.addAll(mAllLists);
//					mRecyclerView.addItemDecoration(headersDecor);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
//				if (mAdapter != null) {
//					mAdapter.closeOpenedSwipeItemLayoutWithAnim();
//				}
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mRecyclerView.scrollToPosition(position);
				}

			}
		});
	}


	/**
	 * 模糊查询
	 *
	 * @param str
	 * @return
	 */
	private List<ContactBean> search(String str) {
		CharacterParser characterParser = CharacterParser.getInstance();
		List<ContactBean> searchResultList = new ArrayList<>();//过滤后的list
		for (ContactBean contact : mAllLists) {
			if ((contact.getPhoneNum() != null && contact.getDesplayName() != null)||(contact.getPhoneNum() != null && contact.getDesplayName() != null)){
				if ((contact.getPhoneNum().contains(str) || contact.getDesplayName().contains(str))||(contact.getDesplayName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE)) ||
						characterParser.getSelling(contact.getDesplayName().toLowerCase(Locale.CHINESE)).contains(characterParser.getSelling(str.toLowerCase(Locale.CHINESE)))
				)){
					if (!searchResultList.contains(contact)) {
						searchResultList.add(contact);
					}
				}
			}

		}


		return searchResultList;
	}



	@Override
	public void gotoActivity(ContactBean contactBean,int position) {
		String[] arrayNum = contactBean.getPhoneNum().split(",");
		Intent intent;
		if(arrayNum.length>1){
			 intent=new Intent(getActivity(),ContactDetailActivity.class);
			ContactDetailActivity.setNotifyFragmentDataListener(this);
			if(contactBean.getBitmapHeader()!=null) {
				tempObject(contactBean, intent);
				Bundle b = new Bundle();
				b.putParcelable("bitmap", contactBean.getBitmapHeader());
				intent.putExtras(b);
			}else{
				intent.putExtra("contactBean",contactBean);
			}

			intent.putExtra("position",position);

		}else{
			 intent=new Intent(getActivity(),CallDetailActivity.class);
			if(contactBean.getBitmapHeader()!=null) {
				tempObject(contactBean, intent);
			}else{
				intent.putExtra("contactBean",contactBean);
			}

		}
		startActivity(intent);
	}

	private void tempObject(ContactBean contactBean, Intent intent) {
		ContactBean contactBean1;
		contactBean1 = new ContactBean();
		contactBean1.setBitmapHeader(null);
		contactBean1.setContactId(contactBean.getContactId());
		contactBean1.setDesplayName(contactBean.getDesplayName());
		contactBean1.setPhoneNum(contactBean.getPhoneNum());
		contactBean1.setSortKey(contactBean.getSortKey());
		contactBean1.setPhotoId(contactBean.getPhotoId());
		contactBean1.setLookUpKey(contactBean.getLookUpKey());
		contactBean1.setFormattedNumber(contactBean.getFormattedNumber());
		contactBean1.setSelected(contactBean.getSelected());
		contactBean1.setPinyin(contactBean.getPinyin());
		contactBean1.setSortLetters(contactBean.getSortLetters());
		intent.putExtra("contactBean",contactBean1);
	}


	/**
	 * @author Administrator
	 */


	@Override
	public void contactChangeData(int position) {
		mAdapter.notifyItemRemoved(position);
	}

	public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

		private int space;

		public SpaceItemDecoration(int space) {
			this.space = space;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			outRect.left = space;
		}
	}

}
