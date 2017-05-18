package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CheckConfirmedHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.activities.ProMainActivity.confirmedPhoneNum;

public class VertifyPhoneNumActivity extends BaseNetActivity {

	@BindView(R.id.phoneNumEditText)
	EditText phoneNumEditText;
	@BindView(R.id.nextBtn)
	Button nextBtn;
	//重复请求次数
	private int retryTime;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			checkIsConFirmed();
		}
	};
	//单线程线程池
	private ExecutorService pool = Executors.newFixedThreadPool(2);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vertify_phone_num);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.phone_vertification, 0);
	}

	@OnClick(R.id.nextBtn)
	public void onViewClicked() {
		createHttpRequest(HttpConfigUrl.COMTYPE_CONFIRMED, phoneNumEditText.getText().toString(),
				SharedUtils.getInstance().readString(Constant.ICCID));//服务器验证操作
		showProgress(R.string.vertifying);
		pool.execute(new Runnable() {
			@Override
			public void run() {
				CommonTools.delayTime(6000);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						CommonTools.showShortToast(VertifyPhoneNumActivity.this, getString(R.string.vertify_success));
						finish();
					}
				});
			}
		});
		checkIsConFirmed();
	}

	private void checkIsConFirmed() {

		createHttpRequest(HttpConfigUrl.COMTYPE_CHECK_CONFIRMED, SharedUtils.getInstance().readString(Constant.ICCID));
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_CHECK_CONFIRMED) {
			CheckConfirmedHttp http = (CheckConfirmedHttp) object;
			if (http.getStatus() == 1) {
				if (http.getEntity().isIsConfirmed()) {
					dismissProgress();
					confirmedPhoneNum = http.getEntity().getTel();
					CommonTools.showShortToast(this, getString(R.string.vertify_success));
					finish();
				} else {
					pool.execute(new Thread(new Runnable() {
						@Override
						public void run() {
							CommonTools.delayTime(3000);
							handler.sendEmptyMessage(0);
						}
					}));
				}
			} else {
				CommonTools.showShortToast(this, http.getMsg());
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pool.shutdown();
		pool = null;
	}
}
