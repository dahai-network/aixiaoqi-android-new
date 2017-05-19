package de.blinkt.openvpn.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;

import java.util.ArrayList;
import java.util.List;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.ContactRecodeAdapter;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.model.OnlyCallModel;
import de.blinkt.openvpn.activities.CallDetailActivity;
import de.blinkt.openvpn.activities.CallPhoneNewActivity;
import de.blinkt.openvpn.activities.FreeWorryPacketChoiceActivity;
import de.blinkt.openvpn.activities.ReceiveCallActivity;
import de.blinkt.openvpn.activities.RechargeActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.OnlyCallHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.util.AssetsDatabaseManager;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DatabaseDAO;
import de.blinkt.openvpn.util.SetPermission;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.querylocaldatebase.AsyncQueryContactRecodeHandler;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.QueryCompleteListener;
import de.blinkt.openvpn.util.querylocaldatebase.SearchConnectterHelper;
import de.blinkt.openvpn.views.T9TelephoneDialpadView;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogCanNoRemind;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static de.blinkt.openvpn.constant.Constant.NETWORK_CELL_PHONE;
import static de.blinkt.openvpn.constant.Constant.SIM_CELL_PHONE;


public class Fragment_Phone extends Fragment implements InterfaceCallback, T9TelephoneDialpadView.OnT9TelephoneDialpadView,
		RecyclerBaseAdapter.OnItemClickListener, QueryCompleteListener<ContactRecodeEntity>, DialogInterfaceTypeBase,T9TelephoneDialpadView.OnControlCallOptionListener,View.OnClickListener,View.OnKeyListener {


	RecyclerView rvContactRecode;
	public  T9TelephoneDialpadView t9dialpadview;
	public TextView dial_delete_btn;
	TextView tv_no_permission;
	RelativeLayout rl_no_permission;
	Button jump_permission;
	ContactRecodeAdapter contactRecodeAdapter;
	public SQLiteDatabase sqliteDB;
	public DatabaseDAO dao;
	ConnectedRecoderReceive connectedRecoderReceive;
	ImageView  floatingActionButton;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_phone, container, false);
		initView(rootView);
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "Android:support:fragments";
			savedInstanceState.remove(FRAGMENTS_TAG);
		}

		return rootView;
	}

	private void initView(View view) {
		rvContactRecode = (RecyclerView) view.findViewById(R.id.rv_contact_recode);
		t9dialpadview = (T9TelephoneDialpadView) view.findViewById(R.id.t9dialpadview);
		floatingActionButton = (ImageView) view.findViewById(R.id.floatingActionButton);
		tv_no_permission = (TextView) view.findViewById(R.id.tv_no_permission);
		jump_permission = (Button) view.findViewById(R.id.jump_permission);
		rl_no_permission = (RelativeLayout) view.findViewById(R.id.rl_no_permission);
		tv_no_permission.setText(String.format(getString(R.string.no_permission), getString(R.string.call_recoder)));
		inited();

	}

	/***
	 *手环拨打电话
	 */
	private void braceletDial() {
		int version = Build.VERSION.SDK_INT;
		if (CommonTools.isFastDoubleClick(500)) {
			return;
		}
		if (SocketConstant.REGISTER_STATUE_CODE == 3) {
			//如果没有套餐那么就需要弹出提示框
			if (!SharedUtils.getInstance().readBoolean(Constant.ISHAVEORDER)) {
				//拨打电话
				simCellPhone();
			} else {
				new DialogCanNoRemind(this, getActivity(), 2);
			}
		} else {

			CommonTools.showShortToast(getActivity(), getString(R.string.sim_register_phone_tip));

		}
	}

	public void closedialClicked() {
		t9dialpadview.clearT9Input();
	}

	private void initDB() {
		AssetsDatabaseManager.initManager(getActivity().getApplicationContext());
		AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
		sqliteDB = mg.getDatabase("number_location.zip");
		dao = new DatabaseDAO(sqliteDB);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(connectedRecoderReceive);
		sqliteDB.close();
		dao.closeDB();
		t9dialpadview = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.jump_permission:
				new SetPermission(getActivity());
				break;
			case R.id.floatingActionButton:
				EventBusUtil.optionView(false);
				t9dialpadview.setVisibility(View.VISIBLE);
				break;
		}
	}

	public void inited() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ReceiveCallActivity.UPDATE_CONTACT_REDORE);
		connectedRecoderReceive = new ConnectedRecoderReceive();
		getActivity().registerReceiver(connectedRecoderReceive, filter);
		initDB();
		searchContactRedocer();
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		rvContactRecode.setLayoutManager(layoutManager);
		contactRecodeAdapter = new ContactRecodeAdapter(dao, getActivity(), mAllList);
		contactRecodeAdapter.setOnItemClickListener(this);
		rvContactRecode.setAdapter(contactRecodeAdapter);
		t9dialpadview.setOnT9TelephoneDialpadView(this);
		t9dialpadview.setOnControlCallOptionListener(this);
		floatingActionButton.setOnClickListener(this);
		rvContactRecode.setOnKeyListener(this);
		dial_delete_btn = t9dialpadview.getDeteleBtn();
		jump_permission.setOnClickListener(this);
		if (dial_delete_btn != null) {
			dial_delete_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!TextUtils.isEmpty(curInputStr) && curInputStr.length() > 0) {
						String newCurInputStr = curInputStr.substring(0, curInputStr.length() - 1);
						t9dialpadview.mT9InputEt.setText(newCurInputStr);
						onDialInputTextChanged(newCurInputStr);
					}
				}
			});
		}
	}

	/**
	 * 跳转到权限设置界面
	 */
	private void searchContactRedocer() {
		Log.e(TAG,"time="+System.currentTimeMillis());
		AsyncQueryContactRecodeHandler asyncQueryContactRecodeHandler = new AsyncQueryContactRecodeHandler(this, getActivity().getContentResolver(), false);
		FindContactUtil.queryContactRecoderData(asyncQueryContactRecodeHandler);

	}


	List<ContactRecodeEntity> mAllList = new ArrayList<>();

	@Override
	public void queryComplete(List<ContactRecodeEntity> mAllLists) {
		if (mAllLists == null || mAllLists.size() == 0) {
			rl_no_permission.setVisibility(View.VISIBLE);
		} else {
			rl_no_permission.setVisibility(View.GONE);
			mAllList = mAllLists;
			contactRecodeAdapter.addAll(mAllList);
			Log.e(TAG,"time111="+System.currentTimeMillis());
		}


	}


	ContactRecodeEntity contactRecodeEntity;

	@Override
	public void onItemClick(View view, Object data, boolean b) {


		contactRecodeEntity = (ContactRecodeEntity) data;
		switch (view.getId()) {
			case R.id.iv_arrow:
				Intent intent = new Intent(getActivity(), CallDetailActivity.class);
				intent.putExtra(CallDetailActivity.PHONE_INFO, contactRecodeEntity);
				startActivity(intent);
				break;
			default:
				if (SocketConstant.REGISTER_STATUE_CODE == 3) {
					if (!SharedUtils.getInstance().readBoolean(Constant.ISHAVEORDER)) {
						//拨打电话
						simCellPhone();
					} else {
						new DialogCanNoRemind(this, getActivity(), 2);
					}
				} else {
					CommonTools.showShortToast(getActivity(), getString(R.string.sim_register_phone_tip));
				}
				break;
		}


	}


	private void requestTimeHttp() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME);
	}

	@Override
	public void onAddDialCharacter(String addCharacter) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onDeleteDialCharacter(String deleteCharacter) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onDialInputTextChanging(String curCharacter) {

		if (!TextUtils.isEmpty(curCharacter)) {
			getFilter().filter(curCharacter);
		} else {
			contactRecodeAdapter.setSearchChar("");
			contactRecodeAdapter.addAll(mAllList);
		}
	}

	/**
	 * 监听拨打电话输入文本的变化
	 *
	 * @param curCharacter
	 */
	@Override
	public void onDialInputTextChanged(String curCharacter) {
		// TODO Auto-generated method stub
		//进行逻辑判断
		this.curInputStr = curCharacter;
		EventBusUtil.optionView(curCharacter);
	}

	//查找联系人
	private void searchContect(String str, List<ContactRecodeEntity> searchResultList, boolean isExist) {
		for (ContactBean contactBean : ICSOpenVPNApplication.getInstance().getContactList()) {
			for (int i = 0; i < searchResultList.size(); i++) {
				if (contactBean.getPhoneNum().equals(searchResultList.get(i).getPhoneNumber()) || contactBean.getDesplayName().equals(searchResultList.get(i).getName())) {
					isExist = true;
					break;
				}

			}
			if (!isExist && contactBean.getFormattedNumber()[0].indexOf(str) > -1 || contactBean.getFormattedNumber()[1].indexOf(str) > -1 || contactBean.getPhoneNum().indexOf(str) > -1) {
				ContactRecodeEntity contactRecodeEntity = new ContactRecodeEntity();
				String phoneNumber = contactBean.getPhoneNum().split(",")[0];
				contactRecodeEntity.setPhoneNumber(phoneNumber);
				if (!TextUtils.isEmpty(contactBean.getDesplayName()))
					contactRecodeEntity.setName(contactBean.getDesplayName());
				else {
					contactRecodeEntity.setName(contactBean.getPhoneNum());
				}
				contactRecodeEntity.setFormattedNumber(contactBean.getFormattedNumber());
				searchResultList.add(contactRecodeEntity);
			} else {
				isExist = false;
			}
		}
	}

	private String TAG = "Fragment_Phone";
	//根据条件进行过滤
	public Filter getFilter() {
		Filter filter = new Filter() {
			String str;

			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results.values != null)
					Log.e(TAG, "str=" + str);
				contactRecodeAdapter.setSearchChar(str);
				contactRecodeAdapter.addAll((ArrayList<ContactRecodeEntity>) results.values);
			}

			protected FilterResults performFiltering(CharSequence s) {
				str = s.toString();
				FilterResults results = new FilterResults();
				ArrayList<ContactRecodeEntity> contactList = new ArrayList<>();
				searchContactRecoder(str, contactList);
				searchContect(str, contactList, false);
				removeDuplicate(contactList);
				results.values = contactList;
				return results;
			}
		};
		return filter;
	}
	//搜索通话记录
	private void searchContactRecoder(String str, List<ContactRecodeEntity> searchResultList) {
		try {
			for (ContactRecodeEntity contactRecodeEntityntact : mAllList) {
				if (contactRecodeEntityntact.getFormattedNumber()[0].indexOf(str) > -1 || contactRecodeEntityntact.getFormattedNumber()[1].indexOf(str) > -1 || contactRecodeEntityntact.getPhoneNumber().indexOf(str) > -1) {
					if (!searchResultList.contains(contactRecodeEntityntact)) {
						searchResultList.add(contactRecodeEntityntact);
					}
				}
			}
		} catch (Exception e) {

		}
	}
	//去重
	public List<ContactRecodeEntity> removeDuplicate(List<ContactRecodeEntity> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).getPhoneNumber().equals(list.get(i).getPhoneNumber())) {
					list.remove(j);
				}
			}
		}
		return list;
	}


	//对话框
	@Override
	public void dialogText(int type, String text) {
		if (type == 0) {
			Intent intent = new Intent(getActivity(), RechargeActivity.class);
			getActivity().startActivity(intent);
		} else if (type == 1) {
			simCellPhone();
		} else if (type == 2) {
			Intent intent = new Intent(getActivity(), FreeWorryPacketChoiceActivity.class);
			startActivity(intent);
		}
	}

	private void simCellPhone() {
		CommonTools.delayTime(500);
		Intent intent = new Intent(getActivity(), CallPhoneNewActivity.class);
		intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
		intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, SIM_CELL_PHONE);
		getActivity().startActivity(intent);
	}

	public String curInputStr;

	//网络请求结果
	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME) {
			OnlyCallHttp onlyCallHttp = (OnlyCallHttp) object;
			if (1 == onlyCallHttp.getStatus()) {
				OnlyCallModel onlyCallModel = onlyCallHttp.getOnlyCallModel();
				if (!onlyCallModel.getMaximumPhoneCallTime().equals("0")) {
					Intent intent = new Intent(getActivity(), CallPhoneNewActivity.class);
					intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
					intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, NETWORK_CELL_PHONE);
					intent.putExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME, onlyCallModel.getMaximumPhoneCallTime());
					getActivity().startActivity(intent);
				} else {
					new DialogBalance(this, getActivity(), R.layout.dialog_balance, 0);
				}
			} else {
				CommonTools.showShortToast(getActivity(), onlyCallHttp.getMsg());
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(getActivity(), getResources().getString(R.string.no_wifi));
	}


	//更新列表
	class ConnectedRecoderReceive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ReceiveCallActivity.UPDATE_CONTACT_REDORE.equals(action)) {
				ContactRecodeEntity contactRecodeEntity = (ContactRecodeEntity) intent.getSerializableExtra(IntentPutKeyConstant.CONTACT_RECODE_ENTITY);
				for (int i = 0; i < mAllList.size(); i++) {
					if (mAllList.get(i).getPhoneNumber().equals(contactRecodeEntity.getPhoneNumber())) {
						mAllList.remove(i);
						break;
					}
				}
				mAllList.add(0, contactRecodeEntity);
				contactRecodeAdapter.addAll(mAllList);
			}
		}
	}

	int REQUEST_CODE_ASK_PERMISSIONS = 123;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.e(TAG,"isVisibleToUser="+isVisibleToUser);
		if(!isVisibleToUser){
			hindT9DiaView();
		}
	}

	private void hindT9DiaView() {
		if(t9dialpadview!=null)
			t9dialpadview.setVisibility(View.GONE);
		EventBusUtil.optionView(true);
	}

	//隐藏自定义键盘
	@Override
	public void hideT9() {
		hindT9DiaView();
	}


	//点击键盘的拨打电话
	@Override
	public void callPhone() {
		int hasWriteContactsPermission = 0;
		int version = Build.VERSION.SDK_INT;
		if (t9dialpadview.getT9Input() != null && t9dialpadview.getT9Input().length() > 0) {
			//检测是否开启读取联系人电话
			if (getActivity() != null) {
				hasWriteContactsPermission = checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS);
			}
			if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED && version > 22) {
				requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
						REQUEST_CODE_ASK_PERMISSIONS);
				return;
			}
			contactRecodeEntity = new ContactRecodeEntity();
			contactRecodeEntity.setPhoneNumber(t9dialpadview.getT9Input());
			contactRecodeEntity.setName(SearchConnectterHelper.getContactNameByPhoneNumber(getActivity(), contactRecodeEntity.getPhoneNumber()));
			braceletDial();
			closedialClicked();
		} else {
			CommonTools.showShortToast(getActivity(), "请输入要拨打的电话号码");
		}
	}


	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
				if (t9dialpadview!=null&&t9dialpadview.getVisibility()==View.VISIBLE) {
					hindT9DiaView();
				} else {
					return false;
				}
				return true;    //已处理
			}
		}
		return false;
	}
}
