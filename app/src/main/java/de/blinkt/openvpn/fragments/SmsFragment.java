package de.blinkt.openvpn.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.adapter.SmsListAdapter;
import de.blinkt.openvpn.activities.SMSAcivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.SMSListHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.model.SmsIdsEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.User;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

import static de.blinkt.openvpn.constant.UmengContant.CLICKEDITSMS;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSMSITEM;


/**
 * 会话列表fragment
 */

public class SmsFragment extends Fragment implements XRecyclerView.LoadingListener, InterfaceCallback, RecyclerBaseAdapter.OnItemClickListener, SmsListAdapter.OnItemLongClickListener, View.OnClickListener, View.OnKeyListener {


	List<SmsEntity> list = new ArrayList<>();
	XRecyclerView mRecyclerView;
	SmsListAdapter smsListAdapter;
	RelativeLayout NodataRelativeLayout;
	RelativeLayout NoNetRelativeLayout;
	public static ImageView editSmsImageView;
	private int requestNetCount = 0;
	TextView noDataTextView;
	public static final String NOTIFY_RECEIVED_ACTION = "NOTIFY_RECEIVED_ACTION";
	public static final String DELTE_MESSAGE = "DELTE_MESSAGE";
	//储存需要删除的item
	private HashSet<SmsEntity> ids = new HashSet<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sms, container, false);
		initView(rootView);
		initData();
		addListener();
		return rootView;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerMessageReceiver();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
				smsListAdapter.setDeleteImage(false);
				editSmsImageView.setBackground(getResources().getDrawable(R.drawable.edit_sms_selector));
				smsListAdapter.notifyDataSetChanged();
				ids.clear();
				return true;    //已处理
			}
		}
		return false;
	}

	public class NotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (NOTIFY_RECEIVED_ACTION.equals(intent.getAction())) {

					onRefresh();
			} else if (DELTE_MESSAGE.equals(intent.getAction())) {
				if (mAllTempLists != null && clickPosition != -1 && smsListAdapter != null) {
					mAllTempLists.remove(clickPosition);
					smsListAdapter.addAll(mAllTempLists);
				}
			}
		}
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

	}

	private void initView(View view) {
		mRecyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
		NodataRelativeLayout = (RelativeLayout) view.findViewById(R.id.NodataRelativeLayout);
		noDataTextView = (TextView) view.findViewById(R.id.noDataTextView);
		NoNetRelativeLayout = (RelativeLayout) view.findViewById(R.id.NoNetRelativeLayout);
		editSmsImageView = (ImageView) view.findViewById(R.id.editSmsImageView);
	}

	NotifyReceiver mNotifyReceiver;
	private int position;


	public void registerMessageReceiver() {
		mNotifyReceiver = new NotifyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(NOTIFY_RECEIVED_ACTION);
		filter.addAction(DELTE_MESSAGE);
		getActivity().registerReceiver(mNotifyReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		editSmsImageView = null;
		getActivity().unregisterReceiver(mNotifyReceiver);
	}

	private void initData() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
		mRecyclerView.setLoadingListener(this);
		smsListAdapter = new SmsListAdapter(getActivity(), list);
		smsListAdapter.setOnItemClickListener(this);
		smsListAdapter.setOnItemLongClickListener(this);
		mRecyclerView.setAdapter(smsListAdapter);
		mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
		smsListHttp();
	}

	private void addListener() {
		NoNetRelativeLayout.setOnClickListener(this);
		editSmsImageView.setOnClickListener(this);
		mRecyclerView.setOnKeyListener(this);
	}

	int pageNumber = 1;

	private void smsListHttp() {
		requestNetCount++;
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_SMS_LIST, pageNumber + "", Constant.PAGESIZE + "");
	}

	@Override
	public void onRefresh() {
		mRecyclerView.canMoreLoading();
		pageNumber = 1;
		smsListHttp();
	}

	@Override
	public void onItemLongClick(View view, final Object data) {
//		position=mAllTempLists.indexOf(data);
//		Log.e("position","position="+position);
//		new AlertDialog.Builder(getActivity()).setPositiveButton("删除", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				CreateHttpFactory.instanceHttp(SmsFragment.this,HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TEL,((SmsEntity)data).getFm());
//			}
//		}).show();
		smsListAdapter.setDeleteImage(true);
		editSmsImageView.setBackground(getResources().getDrawable(R.drawable.delete_sms_selector));
		smsListAdapter.notifyDataSetChanged();
		smsListAdapter.clearCheckState();
		ids.clear();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.NoNetRelativeLayout:
				smsListHttp();
				break;
			case R.id.editSmsImageView:
				if (!CommonTools.isFastDoubleClick(3000)) {
					if (!smsListAdapter.isDeleteState()) {
						//友盟方法统计
						MobclickAgent.onEvent(getActivity(), CLICKEDITSMS);
						Intent intent = new Intent(getActivity(), SMSAcivity.class);
						startActivity(intent);
					} else {
						Iterator<SmsEntity> iter = ids.iterator();
						ArrayList<String> fms = new ArrayList<>();
						while (iter.hasNext()) {
							SmsEntity entity = iter.next();
							if (entity.isCheck()) {
								if (!User.isCurrentUser(entity.getFm())) {
									fms.add(entity.getFm());
								} else {
									fms.add(entity.getTo());
								}
							}
						}
						if (fms.size() > 0)
							CreateHttpFactory.instanceHttp(SmsFragment.this, HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TELS, new Gson().toJson(new SmsIdsEntity(fms, null)));
						CommonTools.showShortToast(getActivity(), "删除这些短信：" + new Gson().toJson(new SmsIdsEntity(fms, null)));
					}
				}
				break;
		}
	}

	@Override
	public void onLoadMore() {
		pageNumber = pageNumber + 1;
		smsListHttp();
	}

	private int clickPosition = -1;

	@Override
	public void onItemClick(View view, Object object, boolean isCheck) {
		if (!smsListAdapter.isDeleteState()) {
			//友盟方法统计
			MobclickAgent.onEvent(getActivity(), CLICKSMSITEM);
			Intent intent = new Intent(getActivity(), SMSAcivity.class);
			SmsEntity smsEntity = (SmsEntity) object;
			intent.putExtra(IntentPutKeyConstant.SMS_LIST_KEY, smsEntity);
			clickPosition = mAllTempLists.indexOf(object);
			if ("0".equals(smsEntity.getIsRead())) {
				smsEntity.setIsRead("1");
				int size = mAllTempLists.size();
				for (int i = 0; i < size; i++) {
					if (smsEntity.getFm().equals(mAllTempLists.get(i).getFm()) || smsEntity.getTo().equals(mAllTempLists.get(i).getTo())) {
						mAllTempLists.remove(i);
						mAllTempLists.add(i, smsEntity);
						smsListAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
			getActivity().startActivity(intent);
		} else {
			SmsEntity smsEntity = (SmsEntity) object;
			if (isCheck) {
				ids.add(smsEntity);
				if (TextUtils.isEmpty(smsEntity.getRealName())) {
					if (!User.isCurrentUser(smsEntity.getFm())) {
						CommonTools.showShortToast(getActivity(), "添加短信ID:" + smsEntity.getFm() + "，位置：" + smsEntity.getPosition());
					} else {
						CommonTools.showShortToast(getActivity(), "添加短信ID:" + smsEntity.getTo() + "，位置：" + smsEntity.getPosition());
					}
				} else {
					CommonTools.showShortToast(getActivity(), "添加短信ID:" + smsEntity.getRealName() + "，位置：" + smsEntity.getPosition());
				}
			} else {
				ids.remove(smsEntity);
				if (TextUtils.isEmpty(smsEntity.getRealName())) {
					if (!User.isCurrentUser(smsEntity.getFm())) {
						CommonTools.showShortToast(getActivity(), "删除短信ID:" + smsEntity.getFm() + "，位置：" + smsEntity.getPosition());
					} else {
						CommonTools.showShortToast(getActivity(), "删除短信ID:" + smsEntity.getTo() + "，位置：" + smsEntity.getPosition());
					}
				} else {
					CommonTools.showShortToast(getActivity(), "删除短信ID:" + smsEntity.getRealName() + "，位置：" + smsEntity.getPosition());
				}
			}
		}
	}


	private void setSmsListRealName(List<SmsEntity> smsEntityList) {

		int size = smsEntityList.size();
		for (int i = 0; i < size; i++) {
			String comparePhoneNumber;
			if (User.isCurrentUser(smsEntityList.get(i).getFm())) {
				comparePhoneNumber = smsEntityList.get(i).getTo();
			} else {
				comparePhoneNumber = smsEntityList.get(i).getFm();
			}
			Log.e("smslist", smsEntityList.toString());
			int insideSize = mAllLists.size();
			for (int j = 0; j < insideSize; j++) {
				if (comparePhoneNumber.equals(mAllLists.get(j).getPhoneNum())) {
					smsEntityList.get(i).setRealName(mAllLists.get(j).getDesplayName());
					break;
				}
			}
		}
	}

	List<ContactBean> mAllLists = new ArrayList<>();


	List<SmsEntity> mAllTempLists = new ArrayList<>();

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {

		if (cmdType == HttpConfigUrl.COMTYPE_GET_SMS_LIST) {
			mRecyclerView.loadMoreComplete();
			mRecyclerView.refreshComplete();
			NoNetRelativeLayout.setVisibility(View.GONE);
			SMSListHttp smsListHttp = (SMSListHttp) object;
			if (smsListHttp.getStatus() == 1) {
				List<SmsEntity> smsEntityList = smsListHttp.getSmsEntityList();
				if (smsEntityList.size() != 0) {
					if (smsEntityList.size() < Constant.PAGESIZE)
						mRecyclerView.noMoreLoading();
					mRecyclerView.setVisibility(View.VISIBLE);
					NodataRelativeLayout.setVisibility(View.GONE);
					setSmsListRealName(smsEntityList);
					if (pageNumber == 1) {
						mAllTempLists.clear();
						mAllTempLists.addAll(smsEntityList);
					} else {
						mAllTempLists.addAll(smsEntityList);
					}
					smsListAdapter.addAll(mAllTempLists);

				} else {
					if (pageNumber == 1) {
						mRecyclerView.setVisibility(View.GONE);
						noDataTextView.setText(getString(R.string.no_sms));
						NodataRelativeLayout.setVisibility(View.VISIBLE);
					} else {
						CommonTools.showShortToast(getActivity(), getString(R.string.no_more_content));
					}
					mRecyclerView.noMoreLoading();
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TELS) {

			if (object.getStatus() == 1) {
//				Iterator<SmsEntity> iter
//						= ids.iterator();
//				while (iter.hasNext()) {
//					smsListAdapter.remove(iter.next().getPosition());
//				}
//				smsListAdapter.notifyDataSetChanged();
				smsListAdapter.setDeleteImage(false);
				onRefresh();
				editSmsImageView.setBackground(getResources().getDrawable(R.drawable.edit_sms_selector));

			} else {
				CommonTools.showShortToast(ICSOpenVPNApplication.getContext(), object.getMsg());
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {
		if (requestNetCount == 1) {
			NoNetRelativeLayout.setVisibility(View.VISIBLE);
		} else {
			CommonTools.showShortToast(getActivity(), getString(R.string.no_wifi));
		}
	}
}
