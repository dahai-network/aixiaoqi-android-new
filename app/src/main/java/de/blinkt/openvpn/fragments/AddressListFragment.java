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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.com.aixiaoqi.R;

import cn.com.johnson.adapter.ContactAdapter;
import de.blinkt.openvpn.activities.CallDetailActivity;
import de.blinkt.openvpn.activities.ContactDetailActivity;

import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.SetPermission;
import de.blinkt.openvpn.util.pinyin.CharacterParser;


import de.blinkt.openvpn.util.querylocaldatebase.AsyncQueryContactHandler;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.QueryCompleteListener;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.contact.SideBar;
import de.blinkt.openvpn.views.contact.TouchableRecyclerView;

public class AddressListFragment extends BaseStatusFragment implements QueryCompleteListener<ContactBean>, ContactAdapter.CallLisener, ContactDetailActivity.ContactChangeDataListener {

	private SideBar mSideBar;
	private TextView mUserDialog;
	private TextView tvNoPermission;
	RelativeLayout rl_no_permission;
	Button jump_permission;
	private TouchableRecyclerView mRecyclerView;
	private ContactAdapter mAdapter;


	List<ContactBean> mAllLists=new ArrayList<>();
	private EditText searchEditText;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		setLayoutId(R.layout.fragment_selection_common);
		View rootView =super.onCreateView( inflater,  container,
				 savedInstanceState);
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
		jump_permission = (Button) rootView.findViewById(R.id.jump_permission);
		rl_no_permission = (RelativeLayout) rootView.findViewById(R.id.rl_no_permission);
		tvNoPermission.setText(String.format(getString(R.string.no_permission), getString(R.string.address_list)));
		mSideBar.setTextView(mUserDialog);
		jump_permission.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SetPermission(getActivity());
			}
		});
		// 实例化
		mAdapter = new ContactAdapter(getActivity(), mAllLists, this);

		int orientation = LinearLayoutManager.VERTICAL;
		final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), orientation, false);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(mAdapter);
		setSearchLinstener();
	}
	private void searchContact() {
		AsyncQueryContactHandler asyncQueryHandler = new AsyncQueryContactHandler(this, getActivity().getContentResolver());
		FindContactUtil.queryContactData(asyncQueryHandler);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			mAllLists=ICSOpenVPNApplication.getInstance().getContactList();
			if(mAllLists!=null&&mAllLists.size()!=0){
				rl_no_permission.setVisibility(View.GONE);
				mAdapter.addAll(mAllLists);
			}else{
				searchContact();
			}

		}
	}

	@Override
	public void queryComplete(List<ContactBean> mAllLists) {
		ICSOpenVPNApplication.getInstance().setmAllList(mAllLists);
		if(mAllLists==null||mAllLists.size()==0){
			rl_no_permission.setVisibility(View.VISIBLE);
		}else{
			rl_no_permission.setVisibility(View.GONE);
			mAdapter.addAll(mAllLists);
		}
	}
	private void setSearchLinstener() {

		new ExditTextWatcher(searchEditText,R.id.searchEditText){
			@Override
			public void textChanged(CharSequence s, int id) {
				if (!TextUtils.isEmpty(s.toString().trim())) {
					mAdapter.addAll(search(s.toString().trim()));

				} else {

					mAdapter.addAll(mAllLists);

				}
			}
		};

		mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
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


}
