package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetCurrentHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

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
		checkIsConFirmed();
	}

	private void checkIsConFirmed() {
		createHttpRequest(HttpConfigUrl.COMTYPE_GETCURRENT);
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GETCURRENT) {
			GetCurrentHttp http = (GetCurrentHttp) object;
			if (!TextUtils.isEmpty(http.getEntity().getTel())) {
				CommonTools.showShortToast(this, getString(R.string.vertify_success));
				dismissProgress();
				finish();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						CommonTools.delayTime(3000);
						handler.sendEmptyMessage(0);
					}
				}).start();

			}
		}
	}
}
