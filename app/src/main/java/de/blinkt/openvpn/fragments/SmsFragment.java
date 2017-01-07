package de.blinkt.openvpn.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
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
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.SMSListHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.User;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

import static de.blinkt.openvpn.constant.UmengContant.CLICKSMSITEM;


/**
 * 会话列表fragment
 */

public class SmsFragment extends Fragment implements XRecyclerView.LoadingListener, InterfaceCallback, RecyclerBaseAdapter.OnItemClickListener, View.OnClickListener {


	List<SmsEntity> list = new ArrayList<>();
	XRecyclerView mRecyclerView;
	SmsListAdapter smsListAdapter;
	RelativeLayout NodataRelativeLayout;
	RelativeLayout NoNetRelativeLayout;
	private int requestNetCount = 0;
	TextView noDataTextView;
	public static boolean isForeground = false;
	public static final String NOTIFY_RECEIVED_ACTION = "NOTIFY_RECEIVED_ACTION";

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

	public class NotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (NOTIFY_RECEIVED_ACTION.equals(intent.getAction())) {
				if (!isForeground)
					onRefresh();
			}
		}
	}


	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		isForeground = isVisibleToUser ? true : false;
	}

	private void initView(View view) {
		mRecyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
		NodataRelativeLayout = (RelativeLayout) view.findViewById(R.id.NodataRelativeLayout);
		noDataTextView = (TextView) view.findViewById(R.id.noDataTextView);
		NoNetRelativeLayout = (RelativeLayout) view.findViewById(R.id.NoNetRelativeLayout);

	}

	NotifyReceiver mNotifyReceiver;

	public void registerMessageReceiver() {
		mNotifyReceiver = new NotifyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(NOTIFY_RECEIVED_ACTION);
		getActivity().registerReceiver(mNotifyReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		mRecyclerView.setAdapter(smsListAdapter);
		mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
		smsListHttp();
	}

	private void addListener() {
		NoNetRelativeLayout.setOnClickListener(this);
	}

	int pageNumber = 1;

	private void smsListHttp() {
		requestNetCount++;
		SMSListHttp smsListHttp = new SMSListHttp(this, HttpConfigUrl.COMTYPE_GET_SMS_LIST, pageNumber, Constant.PAGESIZE);
		new Thread(smsListHttp).start();
	}

	@Override
	public void onRefresh() {
		mRecyclerView.canMoreLoading();
		pageNumber = 1;
		smsListHttp();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.NoNetRelativeLayout:
				smsListHttp();
				break;
		}
	}

	@Override
	public void onLoadMore() {
		pageNumber = pageNumber + 1;
		smsListHttp();
	}


	@Override
	public void onItemClick(View view, Object object) {
		//友盟方法统计
		MobclickAgent.onEvent(getActivity(), CLICKSMSITEM);
		Intent intent = new Intent(getActivity(), SMSAcivity.class);
		SmsEntity smsEntity = (SmsEntity) object;
		intent.putExtra(IntentPutKeyConstant.SMS_LIST_KEY, smsEntity);
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
		mRecyclerView.loadMoreComplete();
		mRecyclerView.refreshComplete();
		NoNetRelativeLayout.setVisibility(View.GONE);
		if (cmdType == HttpConfigUrl.COMTYPE_GET_SMS_LIST) {
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
