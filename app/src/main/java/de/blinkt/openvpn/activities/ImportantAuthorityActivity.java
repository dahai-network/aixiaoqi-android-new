package de.blinkt.openvpn.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.AuthorityAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.AuthorityEntity;
import de.blinkt.openvpn.model.IntentEntity;
import de.blinkt.openvpn.util.IntentWrapper;

public class ImportantAuthorityActivity extends BaseActivity {

	@BindView(R.id.authorityRecyclerView)
	RecyclerView authorityRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_important_authority);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.important_autohrity, 0);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		authorityRecyclerView.setLayoutManager(layoutManager);
		AuthorityAdapter adapter = new AuthorityAdapter(this, getPhoneTypeEntity());
		authorityRecyclerView.setAdapter(adapter);
	}

	public ArrayList<AuthorityEntity> getPhoneTypeEntity() {
		ArrayList<AuthorityEntity> data = new ArrayList<>();
		setPhoneTypeEntity(data);
		if (data.size() == 0) {
			IntentWrapper.whiteListMatters(ProMainActivity.instance, "服务的持续运行");
			finish();
		}
		return data;
	}

	public void setPhoneTypeEntity(ArrayList<AuthorityEntity> data) {
		int version = Build.VERSION.SDK_INT;
		AuthorityEntity entity = new AuthorityEntity();
		Intent shadeIntent = new Intent(this, ShadeActivity.class);
		switch (Build.MANUFACTURER) {
			case Constant.LEMOBILE:
				if (version == 21) {
					appPertectSet(entity);
					Intent letvIntent = new Intent();
					letvIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
					entity.setintentEntity(new IntentEntity(letvIntent, shadeIntent));
					data.add(new AuthorityEntity(entity));
					autoRunSet(entity);
					Intent letvGodIntent = new Intent();
					letvGodIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.BackgroundAppManageActivity"));
					entity.setintentEntity(new IntentEntity(letvGodIntent, shadeIntent));
					data.add(new AuthorityEntity(entity));
				}
				break;
			case Constant.LENOVO:
				if (version == 19 || version == 20) {
					wifiSet(entity);
					Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
					entity.setintentEntity(new IntentEntity(netWorkIntent, shadeIntent));
					data.add(new AuthorityEntity(entity));
				}
				break;
			case Constant.MEIZU:
				if (version == 22 || version == 21) {
					keepStandbySet(entity);
					Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
					meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
					meizuIntent.putExtra("packageName", ICSOpenVPNApplication.getInstance().getPackageName());
					entity.setintentEntity(new IntentEntity(meizuIntent, shadeIntent));
					data.add(new AuthorityEntity(entity));
					autoRunSet(entity);
					Intent meizuGodIntent = new Intent();
					meizuGodIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.SecurityCenterActivity"));
					entity.setintentEntity(new IntentEntity(meizuIntent, shadeIntent));
					data.add(new AuthorityEntity(entity));
					wifiSet(entity);
					Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
					entity.setintentEntity(new IntentEntity(netWorkIntent, shadeIntent));
					data.add(new AuthorityEntity(entity));
				}
				break;
			case Constant.SAMSUNG:
				if (version == 21 || version == 22) {
					autoRunSet(entity);
					Intent samsungLIntent = ICSOpenVPNApplication.getInstance().getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
					if (samsungLIntent != null) {
						entity.setintentEntity(new IntentEntity(samsungLIntent, shadeIntent));
						data.add(new AuthorityEntity(entity));
					}
					wifiSet(entity);
					Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
					entity.setintentEntity(new IntentEntity(netWorkIntent, shadeIntent));
					data.add(new AuthorityEntity(entity));
				}
				break;
			case Constant.ONEPLUS:
				wifiSet(entity);
				Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				entity.setintentEntity(new IntentEntity(netWorkIntent, shadeIntent));
				data.add(new AuthorityEntity(entity));
				break;
		}
	}

	private void appPertectSet(AuthorityEntity entity) {
		entity.setTitle(Constant.APP_PERTECT);
		entity.setTip(Constant.PHONE_NO_OMIT);
	}

	private void keepStandbySet(AuthorityEntity entity) {
		entity.setTitle(Constant.KEEP_RUN_IN_STANDBY);
		entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
	}

	private void autoRunSet(AuthorityEntity entity) {
		entity.setTitle(Constant.AUTO_RUN);
		entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
	}

	private void wifiSet(AuthorityEntity entity) {
		entity.setTitle(Constant.KEEP_WLAN_CONNECT);
		entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
	}
}
