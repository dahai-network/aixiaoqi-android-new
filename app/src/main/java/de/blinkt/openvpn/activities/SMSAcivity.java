package de.blinkt.openvpn.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.adapter.SmsDetailAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.SendRetryForErrorHttp;
import de.blinkt.openvpn.http.SendSmsHttp;
import de.blinkt.openvpn.http.SmsDetailHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.SmsDetailEntity;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.User;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKADDCONTACT;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSENDSMS;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSMSDETAILINFO;
import static de.blinkt.openvpn.constant.UmengContant.INPUTPHONENUMBER;

/**
 * Created by Administrator on 2016/9/1 0001.
 */
public class SMSAcivity extends BaseActivity implements View.OnClickListener, InterfaceCallback, SwipeRefreshLayout.OnRefreshListener, RecyclerBaseAdapter.OnItemClickListener, DialogInterfaceTypeBase {
	SmsEntity smsEntity;
	SwipeRefreshLayout swipeRefreshLayout;
	RecyclerView recyclerView;
	EditText smsContentEt;
	TextView sendSmsTv;
	List<SmsDetailEntity> list = new ArrayList<>();
	SmsDetailAdapter smsDetailAdapter;
	RelativeLayout consigneeLl;
	//    EditText contactNameEt;
	ImageView addContactIv;
	RelativeLayout NoNetRelativeLayout;
	LinearLayout selectContactLl;
	Map<String, String> map = new LinkedHashMap<>();
	public static boolean isForeground = false;
	public static final String MESSAGE_RECEIVED_ACTION = "com.aixiaoqi.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	public static final String SEND_SUCCEED = "1";
	public static final String SEND_PROGRESSING = "0";
	public static final String SEND_FAIL = "2";
	List<ContactBean> mAllLists = new ArrayList<>();
	ScrollView scrollView;

	private boolean isNoFocus;
	private boolean isDelete;
	int pageNumber = 1;

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}

	MessageReceiver mMessageReceiver;

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String extras = intent.getStringExtra(KEY_EXTRAS);


				if (!TextUtils.isEmpty(extras)) {
					JsonObject jsonObject = new JsonParser().parse(extras).getAsJsonObject();
					String status = jsonObject.get("Status").getAsString();
					String tel = jsonObject.get("Tel").getAsString();
					String smsID = jsonObject.get("SMSID").getAsString();

					if (smsEntity.getTo().equals(tel)) {
						List<SmsDetailEntity> list = smsDetailAdapter.getList();
						int length = list.size();
						for (int i = 0; i < length; i++) {
							if (smsID.equals(list.get(i).getSMSID())) {
								if ("1".equals(status)) {
									list.get(i).setStatus(SEND_SUCCEED);
								} else {
									list.get(i).setStatus(SEND_FAIL);
								}
								smsDetailAdapter.notifyDataSetChanged();
								break;
							}
						}
					}
				}
			}

		}
	}

	@Override
	protected void onDestroy() {
		isForeground = false;
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	String receivePhoneNumer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
		setContentView(R.layout.activity_sms);
		Object object = getIntent().getSerializableExtra(IntentPutKeyConstant.SMS_LIST_KEY);
		receivePhoneNumer = getIntent().getStringExtra(IntentPutKeyConstant.RECEIVE_SMS);
		if (object != null) {
			smsEntity = (SmsEntity) object;
		} else if (!TextUtils.isEmpty(receivePhoneNumer)) {
			smsEntity = new SmsEntity();
			smsEntity.setFm(receivePhoneNumer);
			smsEntity.setRealName(setRealName(receivePhoneNumer));
		}
		initView();
		initTitle();
		addListener();
		initData();
		registerMessageReceiver();
	}

	private void initData() {
		if (smsEntity != null) {
			showDefaultProgress();
			smsDetailHttp();
		}

	}


	private String setRealName(String phoneNumber) {
		realName = phoneNumber;
		int size = mAllLists.size();
		if (mAllLists == null) {
			return realName;
		}
		for (int j = 0; j < size; j++) {
			if (phoneNumber.equals(mAllLists.get(j).getPhoneNum())) {
				realName = mAllLists.get(j).getDesplayName();
				break;
			}
		}
		return realName;
	}

	private void initView() {
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull_down_sr);
		if (smsEntity != null)
			swipeRefreshLayout.setOnRefreshListener(this);
		else
			swipeRefreshLayout.setEnabled(false);
		recyclerView = (RecyclerView) findViewById(R.id.sms_detail_rv);
		selectContactLl = (LinearLayout) findViewById(R.id.select_contact_ll);
		scrollView = (ScrollView) findViewById(R.id.scrollView);

		constomEditText();
		selectContactTextView();
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setLayoutManager(layoutManager);
		smsDetailAdapter = new SmsDetailAdapter(this, list);
		smsDetailAdapter.setOnItemClickListener(this);
		recyclerView.setAdapter(smsDetailAdapter);
		smsContentEt = (EditText) findViewById(R.id.sms_content_et);
		consigneeLl = (RelativeLayout) findViewById(R.id.consignee_ll);
//        contactNameEt =(EditText)findViewById(R.id.contact_name_et);
		addContactIv = (ImageView) findViewById(R.id.add_contact_iv);
		sendSmsTv = (TextView) findViewById(R.id.send_sms_tv);
		NoNetRelativeLayout = (RelativeLayout) findViewById(R.id.NoNetRelativeLayout);
		if (smsEntity != null) {
			consigneeLl.setVisibility(View.GONE);
		} else {
			consigneeLl.setVisibility(View.VISIBLE);
		}
	}

	EditText editText;

	private void constomEditText() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		editText = new EditText(this);
		editText.setBackground(null);
		editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		editText.setInputType(InputType.TYPE_CLASS_PHONE);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, CommonTools.sp2px(getResources(), 14));
		editText.setLayoutParams(lp);
	}

	private void smsDetailHttp() {
		SmsDetailHttp smsDetailHttp = new SmsDetailHttp(this, HttpConfigUrl.COMTYPE_GET_SMS_DETAIL, User.isCurrentUser(smsEntity.getFm()) ? smsEntity.getTo() : smsEntity.getFm(), pageNumber, Constant.PAGESIZE);
		new Thread(smsDetailHttp).start();
	}

	private void addListener() {
		titleBar.getLeftText().setOnClickListener(this);
		sendSmsTv.setOnClickListener(this);
		addContactIv.setOnClickListener(this);
		NoNetRelativeLayout.setOnClickListener(this);

		smsContentEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					sendSmsTv.setTextColor(getResources().getColor(R.color.readed));
				} else {
					//友盟方法统计
					MobclickAgent.onEvent(context, INPUTPHONENUMBER);
					sendSmsTv.setTextColor(getResources().getColor(R.color.select_contacct));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					isNoFocus = true;
					showName();
				}
			}
		});
		editText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_DEL
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					if (TextUtils.isEmpty(editText.getText().toString())) {
						if (selectContactLl != null)
							if (selectContactLl.getChildCount() > 0) {
								int i = selectContactLl.getChildCount() - 1;
								LinearLayout linearLayout = (LinearLayout) selectContactLl.getChildAt(i);
								int j = linearLayout.getChildCount() - 2;
								if (j >= 0) {
									View view = linearLayout.getChildAt(j);
									linearLayout.removeView(view);
									if (j == 0)
										selectContactLl.removeView(linearLayout);
									map.remove(keyList.get(i * 3 + j));
									isDelete = true;
									selectContactTextView();
									isDelete = false;
								}
							}
						return true;
					} else {
						return false;
					}
				}
				return false;
			}
		});
	}

	private void initTitle() {
		titleBar.getTitle().setSingleLine();
		titleBar.getTitle().setEllipsize(TextUtils.TruncateAt.END);
		if (smsEntity != null) {
			if (!TextUtils.isEmpty(smsEntity.getRealName())) {
				titleBar.setTextTitle(smsEntity.getRealName());
			} else {
				if (User.isCurrentUser(smsEntity.getFm())) {
					titleBar.setTextTitle(smsEntity.getTo().split(",")[0]);
				} else {
					titleBar.setTextTitle(smsEntity.getFm().split(",")[0]);
				}
			}
			titleBar.setRightBtnText(R.string.detail_info);
			titleBar.getRightText().setOnClickListener(this);
		} else {
			titleBar.setTextTitle(R.string.new_sms);
		}
		titleBar.setLeftBtnIcon(R.drawable.btn_top_back);

	}

	private int[] headIcon = {
			R.drawable.head_icon_1,
			R.drawable.head_icon_2,
			R.drawable.head_icon_3,
			R.drawable.head_icon_4,
			R.drawable.head_icon_5,
			R.drawable.head_icon_1
	};
	private boolean isClick;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.titlebar_iv_lefttext:
				finish();
				break;
			case R.id.titlebar_iv_righttext:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKSMSDETAILINFO);
				Intent intent = new Intent(this, ContactDetailActivity.class);
				ContactBean contactBean = new ContactBean();
				if (User.isCurrentUser(smsEntity.getFm())) {
					contactBean.setPhoneNum(smsEntity.getTo());
				} else {
					contactBean.setPhoneNum(smsEntity.getFm());
				}
				contactBean.setHeader(headIcon[(int) (Math.random() * 6)]);
				contactBean.setDesplayName(smsEntity.getRealName());
				intent.putExtra("contactBean", contactBean);
				intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE_DETAIL, IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE_DETAIL);
				startActivity(intent);
				break;
			case R.id.send_sms_tv:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKSENDSMS);
				position = -1;
				isClick = true;
				String content = smsContentEt.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					CommonTools.showShortToast(this, getString(R.string.send_content_is_null));
					return;
				}
				long sendTime = System.currentTimeMillis();
				SharedUtils sharedUtils = SharedUtils.getInstance();
				SmsDetailEntity smsDetailEntity = new SmsDetailEntity();
				smsDetailEntity.setFm(sharedUtils.readString(Constant.USER_NAME));

				smsDetailEntity.setSMSTime(sendTime + "");
				smsDetailEntity.setSMSContent(content);
				smsDetailEntity.setStatus(SEND_PROGRESSING);
				String phoneNumbertemp;
				if (smsEntity != null) {

					if (User.isCurrentUser(smsEntity.getFm())) {
						phoneNumbertemp = smsEntity.getTo();
					} else {
						phoneNumbertemp = smsEntity.getFm();
					}
					smsDetailEntity.setTo(phoneNumbertemp);
					smsDetailAdapter.add(smsDetailEntity);

				} else {
					getPhoneNumber();
					if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(editText.getText().toString()))
						phoneNumber = phoneNumber + "," + editText.getText().toString();
					else if (TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(editText.getText().toString())) {
						phoneNumber = editText.getText().toString();
					}
					if (!TextUtils.isEmpty(phoneNumber)) {
						phoneNumbertemp = phoneNumber;
						smsDetailEntity.setTo(phoneNumbertemp);
						smsDetailAdapter.add(smsDetailEntity);
					} else {
						CommonTools.showShortToast(this, getString(R.string.has_no_contact));
						return;
					}
					if (smsEntity == null) {
						smsEntity = new SmsEntity();
					}
					smsEntity.setFm(SharedUtils.getInstance().readString(Constant.USER_NAME));
					smsEntity.setTo(phoneNumbertemp);
					titleBar.setTextTitle(getRealName());
					swipeRefreshLayout.setOnRefreshListener(this);
					swipeRefreshLayout.setEnabled(true);
					consigneeLl.setVisibility(View.GONE);
				}
				recyclerView.smoothScrollToPosition(recyclerView.getBottom());
				sendSmsHttp(phoneNumbertemp, content);
				break;

			case R.id.add_contact_iv:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKADDCONTACT);
				editText.clearFocus();
				showName();
				Intent intentAdd = new Intent(this, ContactActivity.class);
				startActivityForResult(intentAdd, IntentPutKeyConstant.ADD_CONTACT);
				break;
			case R.id.NoNetRelativeLayout:
				smsDetailHttp();
				break;
		}
	}

	private void showName() {
		realName = "";
		realName = setRealName(editText.getText().toString());
		this.phoneNumber = "";
		this.phoneNumber = editText.getText().toString();

		if (!TextUtils.isEmpty(phoneNumber) && !map.containsKey(phoneNumber)) {
			map.put(phoneNumber, realName);
			selectContactTextView();
			editText.setText("");
		}
	}

	String phoneNumber = "";
	String realName = "";

	private void getPhoneNumber() {
		phoneNumber = "";
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (!TextUtils.isEmpty(phoneNumber))
				phoneNumber = phoneNumber + "," + entry.getKey();
			else {
				phoneNumber = (String) entry.getKey();
			}
		}
	}

	private String getRealName() {
		realName = "";
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (!TextUtils.isEmpty(realName))
				realName = realName + "," + entry.getValue();
			else {
				realName = (String) entry.getValue();
			}
		}
		return realName;
	}


	List<String> keyList;
	LinearLayout linearLayout;

	public void selectContactTextView() {
		scrollView.smoothScrollTo(0,0);
		if (linearLayout != null) {
			linearLayout.removeAllViews();
		}
		selectContactLl.removeAllViews();
		linearLayout = new LinearLayout(this);
		int i = 0;
		keyList = new ArrayList<>();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			final String phoneNumberKey = (String) entry.getKey();
			keyList.add(phoneNumberKey);
			String realNameValue = (String) entry.getValue();
			if (i % 3 == 0) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				linearLayout = new LinearLayout(this);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout.setLayoutParams(lp);
			}
			final View view = LayoutInflater.from(this).inflate(R.layout.select_more_contact, null);
			TextView tvContact = (TextView) view.findViewById(R.id.tv_contact);
//			final ImageView deleteContactIv = (ImageView) view.findViewById(R.id.delete_contact_iv);
//			deleteContactIv.setTag(phoneNumberKey);
//			deleteContactIv.setOnClickListener(this);
//			tvContact.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					deleteContactIv.setVisibility(View.VISIBLE);
//				}
//			});
//			deleteContactIv.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					String phone = (String) v.getTag();
//					map.remove(phone);
//					view.setVisibility(View.GONE);
//				}
//			});
			tvContact.setText(realNameValue);
			linearLayout.addView(view);
			if (i % 3 == 0) {
				selectContactLl.addView(linearLayout);
			}
			i++;
		}
		if (!isNoFocus || isDelete) {
			editText.requestFocus();
		}
		linearLayout.addView(editText);
		if (i == 0) {
			selectContactLl.addView(linearLayout);
		}


	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null) {
			return;
		}
		ContactBean contactBean=new ContactBean();
		switch (resultCode){
			case RESULT_OK:
				contactBean = (ContactBean) data.getSerializableExtra("Contect");break;
			case IntentPutKeyConstant.ADD_CONTACT:
				contactBean = (ContactBean) data.getSerializableExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE);
				break;
		}

		returnData(contactBean);
	}

	private void returnData(ContactBean contactBean) {

		realName = "";
		phoneNumber = "";
		phoneNumber = contactBean.getPhoneNum();

		if (!TextUtils.isEmpty(contactBean.getDesplayName())) {
			realName = contactBean.getDesplayName();
		} else {
			realName = contactBean.getPhoneNum();
		}
		if (map.containsKey(phoneNumber)) {
			CommonTools.showShortToast(this, getString(R.string.phone_already_has));
			return;
		}
		map.put(phoneNumber, realName);
		selectContactTextView();
	}


	private void sendSmsHttp(String phoneNumber, String content) {
		SendSmsHttp sendMsgHttp = new SendSmsHttp(this, HttpConfigUrl.COMTYPE_SEND_SMS_MESSAGE, phoneNumber, content);
		new Thread(sendMsgHttp).start();
	}

	private void sendOnceSmsHttp(String SmsID) {
		SendRetryForErrorHttp sendRetryForErrorHttp = new SendRetryForErrorHttp(this, HttpConfigUrl.COMTYPE_SEND_RETRY_FOR_ERROR, SmsID);
		new Thread(sendRetryForErrorHttp).start();
	}

	@Override
	public void onRefresh() {
		pageNumber = pageNumber + 1;
		smsDetailHttp();
	}

	private int position;

	@Override
	public void onItemClick(View view, Object data) {

		position = (Integer) data;
		showDialog();

	}

	private void showDialog() {
		DialogBalance dialog = new DialogBalance(this, SMSAcivity.this, R.layout.dialog_balance, 3);
		dialog.changeText(getResources().getString(R.string.sure_once_send), getResources().getString(R.string.sure));
	}

	@Override
	public void dialogText(int type, String text) {
		SmsDetailEntity smsDetailEntity = list.get(position);
		if (!TextUtils.isEmpty(smsDetailEntity.getSMSID())) {
			smsDetailAdapter.remove(position);
			smsDetailEntity.setStatus(SEND_PROGRESSING);
			smsDetailAdapter.add(position, smsDetailEntity);
			sendOnceSmsHttp(smsDetailEntity.getSMSID());
		} else {
			String phoneNumber;
			if (User.isCurrentUser(smsDetailEntity.getFm())) {
				phoneNumber = smsDetailEntity.getTo();
			} else {
				phoneNumber = smsDetailEntity.getFm();
			}
			sendSmsHttp(phoneNumber, smsDetailEntity.getSMSContent());
		}
	}


	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		dismissProgress();
		swipeRefreshLayout.setRefreshing(false);
		if (cmdType == HttpConfigUrl.COMTYPE_GET_SMS_DETAIL) {
			SmsDetailHttp smsDetailHttp = (SmsDetailHttp) object;
			if (1 == smsDetailHttp.getStatus()) {
				List<SmsDetailEntity> smsDetailEntityList = smsDetailHttp.getSmsDetailEntityList();
				Collections.reverse(smsDetailEntityList);
				if (pageNumber == 1) {
					smsDetailAdapter.addAll(smsDetailEntityList);
					recyclerView.smoothScrollToPosition(recyclerView.getBottom());
				} else if (smsDetailEntityList.size() == 0 && pageNumber != 1) {
					CommonTools.showShortToast(this, getString(R.string.no_more_content));
				} else {
					smsDetailAdapter.addTopAll(smsDetailEntityList);
				}
			} else {

				CommonTools.showShortToast(this, smsDetailHttp.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_SEND_SMS_MESSAGE) {
			smsContentEt.setText("");
			SendSmsHttp sendSmsHttp = (SendSmsHttp) object;
			SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(smsDetailAdapter.getItemCount() - 1);
			if (1 == sendSmsHttp.getStatus()) {
				smsDetailEntity.setSMSID(sendSmsHttp.getSmsId());
			} else {
				smsDetailEntity.setStatus(SEND_FAIL);
				CommonTools.showShortToast(this, sendSmsHttp.getMsg());
			}
			smsDetailAdapter.remove(smsDetailAdapter.getItemCount() - 1);
			smsDetailAdapter.add(smsDetailEntity);


		} else if (cmdType == HttpConfigUrl.COMTYPE_SEND_RETRY_FOR_ERROR) {
			SendRetryForErrorHttp sendRetryForErrorHttp = (SendRetryForErrorHttp) object;
			SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(position);
			if (1 == sendRetryForErrorHttp.getStatus()) {
				smsDetailEntity.setStatus(SEND_SUCCEED);
			} else {
				smsDetailEntity.setStatus(SEND_FAIL);
				CommonTools.showShortToast(this, sendRetryForErrorHttp.getMsg());
			}
			smsDetailAdapter.remove(position);
			smsDetailAdapter.add(position, smsDetailEntity);
		}

	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		dismissProgress();
		swipeRefreshLayout.setRefreshing(false);
		if (pageNumber == 1 && !isClick) {
			NoNetRelativeLayout.setVisibility(View.VISIBLE);
		} else if (isClick) {
			sendFail();
		} else {
			CommonTools.showShortToast(this, errorMessage);
		}
	}

	@Override
	public void noNet() {
		dismissProgress();
		swipeRefreshLayout.setRefreshing(false);
		if (pageNumber == 1 && !isClick) {
			NoNetRelativeLayout.setVisibility(View.VISIBLE);
		} else if (isClick) {
			sendFail();
		} else {
			CommonTools.showShortToast(this, getString(R.string.no_wifi));
		}
	}

	private void sendFail() {
		if (position == -1&&smsContentEt!=null) {
			smsContentEt.setText("");
			SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(smsDetailAdapter.getItemCount() - 1);
			if(smsDetailEntity!=null){
				smsDetailEntity.setStatus(SEND_FAIL);
				smsDetailAdapter.remove(smsDetailAdapter.getItemCount() - 1);
				smsDetailAdapter.add(smsDetailEntity);
			}
		} else {
			SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(position);
			if(smsDetailEntity!=null){
				smsDetailEntity.setStatus(SEND_FAIL);
				smsDetailAdapter.remove(position);
				smsDetailAdapter.add(position, smsDetailEntity);
			}
		}
	}
}
