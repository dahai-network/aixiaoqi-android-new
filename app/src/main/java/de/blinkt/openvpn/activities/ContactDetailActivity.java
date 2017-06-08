package de.blinkt.openvpn.activities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import java.util.List;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.MyModules.ui.RechargeActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.AssetsDatabaseManager;
import de.blinkt.openvpn.util.DatabaseDAO;
import de.blinkt.openvpn.util.PhoneFormatUtil;
import de.blinkt.openvpn.util.PhoneNumberZone;
import de.blinkt.openvpn.views.RoundImageView;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTDETAILEDIT;
import static de.blinkt.openvpn.constant.UmengContant.CLICKDETELECONTACT;


/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class ContactDetailActivity extends BaseActivity implements View.OnClickListener, DialogInterfaceTypeBase {
	ImageView contactHeader;
	TextView contactName;
	LinearLayout llPhoneInfo;
	TextView deletePhone;
	ContactBean contactBean;
	public SQLiteDatabase sqliteDB;
	public DatabaseDAO dao;
	private String selectContactPeople = "";
	private String selectContactPeopleDetail = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle();
		initData();
		initDB();
		setContentView(R.layout.activity_contact_detail);
		initView();
		setListener();

	}

	private void initDB() {
		AssetsDatabaseManager.initManager(getApplicationContext());
		AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
		sqliteDB = mg.getDatabase("number_location.zip");
		dao = new DatabaseDAO(sqliteDB);
		List<ContactBean> mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
		int size = mAllLists.size();
		for (int i = 0; mAllLists != null && i < size; i++) {
			if (contactBean.getPhoneNum().equals(mAllLists.get(i).getPhoneNum())) {
				contactBean.setLookUpKey(mAllLists.get(i).getLookUpKey());
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			titleBar.getRightText().setVisibility(View.INVISIBLE);
		}
	}

	private void initData() {
		contactBean = (ContactBean) getIntent().getSerializableExtra("contactBean");
		selectContactPeople = getIntent().getStringExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE);
		selectContactPeopleDetail = getIntent().getStringExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE_DETAIL);
	}

	private void initTitle() {

		hasAllViewTitle(R.string.contact_personal_center,  R.drawable.edit_info_selector, -1, true);

	}

	public boolean isExist;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AssetsDatabaseManager.closeAllDatabase();
	}

	private void initView() {

		contactHeader = (RoundImageView) findViewById(R.id.contact_header);
		contactName = (TextView) findViewById(R.id.contact_name);
		llPhoneInfo = (LinearLayout) findViewById(R.id.ll_phone_info);
		deletePhone = (TextView) findViewById(R.id.delete_phone);
		setData(contactBean);
		if (!TextUtils.isEmpty(selectContactPeople) || !TextUtils.isEmpty(selectContactPeopleDetail) || !TextUtils.isEmpty(getIntent().getStringExtra(IntentPutKeyConstant.SMS_DETAIL_INFO))) {
			deletePhone.setVisibility(View.GONE);
		}
	}


	private void setData(final ContactBean contactBean) {

//		contactHeader.setBackgroundResource(contactBean.getHeader());
		Bundle b=getIntent().getExtras();
		Bitmap bmp=b.getParcelable("bitmap");
		if(bmp!=null){
			contactHeader.setImageBitmap(bmp);
		}else{
			contactHeader.setImageResource(R.drawable.contact_default_header);
		}
		if (!TextUtils.isEmpty(contactBean.getDesplayName()))
			contactName.setText(contactBean.getDesplayName());
		else {
			contactName.setText(contactBean.getPhoneNum());
		}
		String[] arrayNum = contactBean.getPhoneNum().split(",");
		int length = arrayNum.length;
		for (int i = 0; i < length; i++) {
			View view = LayoutInflater.from(this).inflate(R.layout.item_phone_numer, null);
			TextView contactPhone = (TextView) view.findViewById(R.id.contact_phone);
			TextView contactAddress = (TextView) view.findViewById(R.id.contact_address);

			String phoneNumber;
			phoneNumber = PhoneFormatUtil.deleteprefix(" ",arrayNum[i]);
			String address= PhoneNumberZone.getAddress(dao,PhoneFormatUtil.deleteprefix(" ",phoneNumber));
			final String phonenum = arrayNum[i];
			String phonetemp = PhoneNumberZone.getPhoneNumberFormat(phonenum);
			contactPhone.setText(phonetemp);
			if (!TextUtils.isEmpty(address))
				contactAddress.setText(address);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!TextUtils.isEmpty(selectContactPeople)) {
						Intent  intent=new Intent();
						contactBean.setPhoneNum(phonenum);
						intent.putExtra("Contect",contactBean);
						setResult(RESULT_OK,intent);
						ICSOpenVPNApplication.getInstance().finishActivity(ContactDetailActivity.class);
					}else{
						Intent intent=new Intent(ContactDetailActivity.this,CallDetailActivity.class);
						contactBean.setPhoneNum(phonenum);
						intent.putExtra("contactBean",contactBean);
						startActivity(intent);

					}
				}
			});
			llPhoneInfo.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){

			case R.id.delete_phone:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKDETELECONTACT);
				deleteDialog();
				break;
		}
	}






	private void setListener() {
		deletePhone.setOnClickListener(this);
	}



	@Override
	protected void onClickRightView() {
		//友盟方法统计
		MobclickAgent.onEvent(context, CLICKCONTACTDETAILEDIT);
		Intent intent = new Intent(Intent.ACTION_EDIT);
		//需要获取到数据库contacts表中lookup列中的key值，在上面遍历contacts集合时获取到
		Uri data = ContactsContract.Contacts.getLookupUri(contactBean.getContactId(), contactBean.getLookUpKey());
		intent.setDataAndType(data, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		intent.putExtra("finishActivityOnSaveCompleted", true);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		sqliteDB.close();
		dao.closeDB();
	}

	static ContactChangeDataListener contactChangeDataLis;

	public static void setNotifyFragmentDataListener(ContactChangeDataListener contactChangeDataListener) {
		contactChangeDataLis = contactChangeDataListener;
	}

	public interface ContactChangeDataListener {
		void contactChangeData(int contactId);
	}

	private void deleteDialog() {
		new AlertDialog.Builder(this)//设置对话框标题
				.setMessage(getString(R.string.delete_contact_hide))//设置显示的内容
				.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {//添加确定按钮
					@Override
					public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
						ContentResolver cr = getContentResolver();
						Uri uri = Uri.parse("content://com.android.contacts/data");
						cr.delete(uri, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =? ", new String[]{String.valueOf(contactBean.getContactId())});
						dialog.dismiss();
						contactChangeDataLis.contactChangeData(getIntent().getIntExtra("position", -1));
						finish();
					}

				}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {//添加返回按钮
			@Override
			public void onClick(DialogInterface dialog, int which) {//响应事件

				// TODO Auto-generated method stub
				dialog.dismiss();

			}

		}).show();
	}




	@Override
	public void dialogText(int type, String text) {
		Intent intent = new Intent(this, RechargeActivity.class);
		startActivity(intent);
	}
}
