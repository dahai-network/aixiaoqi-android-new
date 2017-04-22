package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CallRecordAdapter;
import cn.com.johnson.model.OnlyCallModel;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.BlackListDBHelp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.OnlyCallHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.querylocaldatebase.AsyncQueryContactRecodeHandler;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.QueryCompleteListener;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.Constant.NETWORK_CELL_PHONE;
import static de.blinkt.openvpn.constant.Constant.SIM_CELL_PHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTDETAILCALL;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CallDetailActivity extends BaseNetActivity implements XRecyclerView.LoadingListener, QueryCompleteListener<ContactRecodeEntity>, DialogInterfaceTypeBase {
	@BindView(R.id.user_name_tv)
	TextView userNameTv;
	@BindView(R.id.group_name_tv)
	TextView groupNameTv;
	@BindView(R.id.phone_name_tv)
	TextView phoneNameTv;
	@BindView(R.id.last_call_time_tv)
	TextView lastCallTimeTv;
	@BindView(R.id.sms_tv)
	TextView smsTv;
	@BindView(R.id.net_call_tv)
	TextView netCallTv;
	@BindView(R.id.dual_standby_king_tv)
	TextView dualStandbyKingTv;
	@BindView(R.id.defriend_tv)
	TextView defriendTv;
	@BindView(R.id.call_record_rv)
	XRecyclerView callRecordRv;
	public static String PHONE_INFO = "phone_info";
	@BindView(R.id.tip_record_tv)
	TextView tipRecordTv;
//    private ContactRecodeEntity phoneInfo;

	private int page = 0;
	AsyncQueryContactRecodeHandler asyncQueryContactRecodeHandler;
	List<ContactRecodeEntity> list = new ArrayList<>();
	CallRecordAdapter callRecordAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_detail);
		ButterKnife.bind(this);
		initData();
		initTitle();
		initRecyclerView();
		getCallRecordData();
	}

	private void initTitle() {
		if (isExist) {
			hasAllViewTitle(R.string.call_detail, R.drawable.edit_info_selector, -1, true);
		} else {
			hasAllViewTitle(R.string.call_detail, R.drawable.create_selector, -1, true);
		}
	}

	boolean isExist = false;
	ContactBean contactBean;

	private void isContactExist(String phoneNumber) {

		for (ContactBean contactBean : ICSOpenVPNApplication.getInstance().getContactList()) {
			if (contactBean.getPhoneNum().equals(phoneNumber)) {
				this.contactBean = contactBean;
				isExist = true;
				break;
			}
		}

	}

	@Override
	protected void onClickRightView() {
		if (isExist) {
			Intent intent = new Intent(Intent.ACTION_EDIT);
			//需要获取到数据库contacts表中lookup列中的key值，在上面遍历contacts集合时获取到
			Uri data = ContactsContract.Contacts.getLookupUri(contactBean.getContactId(), contactBean.getLookUpKey());
			intent.setDataAndType(data, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
			intent.putExtra("finishActivityOnSaveCompleted", true);
			startActivity(intent);
		} else {
			Intent addIntent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
			addIntent.setType("vnd.android.cursor.dir/person");
			addIntent.setType("vnd.android.cursor.dir/contact");
			addIntent.setType("vnd.android.cursor.dir/raw_contact");
			addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, contactBean.getPhoneNum());
			startActivity(addIntent);
		}
	}

	private boolean isBlackList = false;

	private void initData() {

		ContactRecodeEntity phoneInfo = (ContactRecodeEntity) getIntent().getSerializableExtra(PHONE_INFO);
		contactBean = (ContactBean) getIntent().getSerializableExtra("contactBean");
		if (contactBean == null) {
			isContactExist(phoneInfo.getPhoneNumber());
		} else {
			isExist = true;
		}
		if (contactBean == null && phoneInfo != null) {
			contactBean = new ContactBean();
			contactBean.setPhoneNum(phoneInfo.getPhoneNumber());
			contactBean.setDesplayName(phoneInfo.getName());
		}
		if (!TextUtils.isEmpty(contactBean.getDesplayName()))
			userNameTv.setText(contactBean.getDesplayName());
		else {
			userNameTv.setVisibility(View.GONE);
		}
		phoneNameTv.setText(contactBean.getPhoneNum());
		if (blackListDBHelp == null)
			blackListDBHelp = new BlackListDBHelp(this);
		isBlackList = blackListDBHelp.isBlackList(contactBean.getPhoneNum());
		setBlackList();
	}

	private void setBlackList() {
		Drawable topDrawable;

		if (isBlackList) {
			topDrawable = getResources().getDrawable(R.drawable.defriended);
			defriendTv.setTextColor(getResources().getColor(R.color.gray_text));
		} else {
			topDrawable = getResources().getDrawable(R.drawable.defriend_selector);
			defriendTv.setTextColor(getResources().getColor(R.color.color_00a0e9));
		}
		if (topDrawable != null) {
			topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
			defriendTv.setCompoundDrawables(null, topDrawable, null, null);
		}
	}

	private void getCallRecordData() {

		asyncQueryContactRecodeHandler = new AsyncQueryContactRecodeHandler(this, getContentResolver(), true);
		queryContactRecoder(page);
	}

	private void initRecyclerView() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		callRecordRv.setLayoutManager(layoutManager);
		callRecordRv.setArrowImageView(R.drawable.iconfont_downgrey);
		callRecordRv.setPullRefreshEnabled(false);
		callRecordRv.setLoadingListener(this);
		callRecordAdapter = new CallRecordAdapter(this, list);
		callRecordRv.setAdapter(callRecordAdapter);
	}

	@Override
	public void onRefresh() {

	}

	private void queryContactRecoder(int page) {
		FindContactUtil.queryContactRecoderData(asyncQueryContactRecodeHandler, contactBean.getPhoneNum(), page + "");

	}

	@Override
	public void onLoadMore() {
		page++;
		queryContactRecoder(page * 10);
	}

	List<ContactRecodeEntity> mAllList = new ArrayList<>();

	@Override
	public void queryComplete(List<ContactRecodeEntity> mAllLists) {

		callRecordRv.loadMoreComplete();
		if (mAllLists.size() < 10) {

			callRecordRv.noMoreLoading();
		}
		mAllList.addAll(mAllLists);
		if (mAllList.size() == 0) {
			callRecordRv.setVisibility(View.GONE);
			tipRecordTv.setVisibility(View.GONE);
		}
		if (mAllLists.size() >= 1)
			lastCallTimeTv.setText(mAllList.get(0).getData());
		callRecordAdapter.addAll(mAllList);
		mAllLists.clear();

	}

	private void requestTimeHttp() {
		createHttpRequest(HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME);
	}

	@OnClick({R.id.sms_tv, R.id.net_call_tv, R.id.dual_standby_king_tv, R.id.defriend_tv})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sms_tv:
				SmsEntity smsEntity = new SmsEntity();
				smsEntity.setFm(SharedUtils.getInstance().readString(Constant.USER_NAME));
				smsEntity.setTo(contactBean.getPhoneNum());
				smsEntity.setRealName(contactBean.getDesplayName());
				toActivity(new Intent(this, SMSAcivity.class).putExtra(IntentPutKeyConstant.SMS_LIST_KEY, smsEntity));
				break;
			case R.id.net_call_tv:
				requestTimeHttp();
				break;
			case R.id.dual_standby_king_tv:
				if (SocketConstant.REGISTER_STATUE_CODE == 3) {
					simCellPhone();
				} else {
					CommonTools.showShortToast(this, getString(R.string.sim_register_phone_tip));
				}
				break;
			case R.id.defriend_tv:
				if (!NetworkUtils.isNetworkAvailable(this) && CommonTools.isFastDoubleClick(1000)) {
					return;
				}
				if (!isBlackList) {
					createHttpRequest(HttpConfigUrl.COMTYPE_BLACK_LIST_ADD, contactBean.getPhoneNum());
				} else {
					createHttpRequest(HttpConfigUrl.COMTYPE_BLACK_LIST_DELETE, contactBean.getPhoneNum());

				}
				break;
		}
	}

	private void simCellPhone() {
		ContactRecodeEntity contactRecodeEntity = new ContactRecodeEntity();
		contactRecodeEntity.setPhoneNumber(deleteprefix("-", contactBean.getPhoneNum()));
		contactRecodeEntity.setName(contactBean.getDesplayName());
		Intent intent = new Intent(this, CallPhoneNewActivity.class);
		intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
		intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, SIM_CELL_PHONE);
		startActivity(intent);
	}

	BlackListDBHelp blackListDBHelp;

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME) {
			//友盟方法统计
			MobclickAgent.onEvent(context, CLICKCONTACTDETAILCALL);
			OnlyCallHttp onlyCallHttp = (OnlyCallHttp) object;
			if (1 == onlyCallHttp.getStatus()) {
				OnlyCallModel onlyCallModel = onlyCallHttp.getOnlyCallModel();
				if (!onlyCallModel.getMaximumPhoneCallTime().equals("0")) {
					ContactRecodeEntity contactRecodeEntity = new ContactRecodeEntity();
					contactRecodeEntity.setPhoneNumber(deleteprefix("-", contactBean.getPhoneNum()));
					contactRecodeEntity.setName(contactBean.getDesplayName());
					Intent intent = new Intent(this, CallPhoneNewActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
					intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, NETWORK_CELL_PHONE);
					intent.putExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME, onlyCallModel.getMaximumPhoneCallTime());
					startActivity(intent);
				} else {
					new DialogBalance(this, this, R.layout.dialog_balance, 0);
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_BLACK_LIST_ADD) {
			CommonTools.showShortToast(this, object.getMsg());
			if (object.getStatus() == 1) {
				isBlackList = !isBlackList;
				blackListDBHelp.insertOneDefriend(contactBean.getPhoneNum());
				setBlackList();
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_BLACK_LIST_DELETE) {
			CommonTools.showShortToast(this, object.getMsg());
			if (object.getStatus() == 1) {

				isBlackList = !isBlackList;
				setBlackList();
				blackListDBHelp.deleteDefriend(contactBean.getPhoneNum());
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (blackListDBHelp != null)
			blackListDBHelp.close();
		super.onDestroy();
	}

	private String deleteprefix(String type, String s) {
		if (TextUtils.isEmpty(s)) {
			return "";
		}
		String phoneNumber;
		if (s.replace(type, "").startsWith("+86")) {
			phoneNumber = s.substring(3, s.length());
		} else if (s.replace(type, "").startsWith("86")) {
			phoneNumber = s.substring(2, s.length());
		} else {
			phoneNumber = s;
		}
		return phoneNumber;
	}

	@Override
	public void dialogText(int type, String text) {

	}
}
