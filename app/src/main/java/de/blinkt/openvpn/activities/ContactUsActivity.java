package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.util.CommonTools;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEEMAIL;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEPHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEWEIXINHAO;

/**
 * Created by Administrator on 2016/8/27.
 */
public class ContactUsActivity extends BaseActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSet();
	}

	private void initSet() {
		setContentView(R.layout.activity_contact_us);
		hasLeftViewTitle(R.string.connect_us, 0);
		TextView emailLinearLayout = (TextView) findViewById(R.id.emailLinearLayout);
		TextView phoneLinearLayout = (TextView) findViewById(R.id.phoneLinearLayout);
		TextView weixinLinearLayout = (TextView) findViewById(R.id.weixinLinearLayout);
		emailLinearLayout.setOnClickListener(this);
		phoneLinearLayout.setOnClickListener(this);
		weixinLinearLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.emailLinearLayout:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKCONTACTOURUSEEMAIL);
				String[] email = {getResources().getString(R.string.web_of_service)}; // 需要注意，email必须以数组形式传入
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822"); // 设置邮件格式
				intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
				intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
				startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
				break;
			case R.id.phoneLinearLayout:
				if (!CommonTools.isFastDoubleClick(4000)) {
					//友盟方法统计
					MobclickAgent.onEvent(context, CLICKCONTACTOURUSEPHONE);
					Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getResources().getString(R.string.service_phone)));
					startActivity(phoneIntent);
				}
				break;
			case R.id.weixinLinearLayout:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKCONTACTOURUSEWEIXINHAO);
				Intent mmintent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
				if (mmintent != null)
					startActivity(mmintent);
				break;
		}
	}
}
