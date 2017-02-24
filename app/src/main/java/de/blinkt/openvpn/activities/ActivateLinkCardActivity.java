package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.widget.CustomWebViewLayout;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.model.ReWebChomeClient;


public class ActivateLinkCardActivity extends BaseActivity implements ReWebChomeClient.OpenFileChooserCallBack {

	@BindView(R.id.activateWebView)
	CustomWebViewLayout activateWebView;
	private ValueCallback<Uri> mUploadMsg;
	private static final int REQUEST_CODE_PICK_IMAGE = 0;
	private static final int REQUEST_CODE_IMAGE_CAPTURE = 1;
	private ValueCallback<Uri[]> mUploadMsgs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String ACTIVATE_CARD_URL = "http://m.10010.com/html/tencent/tx-active-query.html?u=aoq3zQGx5a71aCkIhkjFM+O1lBy4/CBeWxzohSoEX/w=&withuStatus=true";
		setContentView(R.layout.activity_activate_link_card);
		ButterKnife.bind(this);
		hasLeftViewTitle(R.string.card_activate, -1);
		activateWebView.mWebView.setWebChromeClient(new ReWebChomeClient(this));
//		activateWebView.mWebView.setWebViewClient(new ReWebViewClient());
		activateWebView.setUrl(this, ACTIVATE_CARD_URL, ACTIVATE_CARD_URL);
		activateWebView.mWebView.addJavascriptInterface(new UploadVideoJavascriptInterface(), "");
	}

	@Override
	public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
		mUploadMsg = uploadMsg;
		showOptions();
	}

	@Override
	public void openFileChooserCallBacks(ValueCallback<Uri[]> uploadMsg, String acceptType) {
		mUploadMsgs = uploadMsg;
		showOptions();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_CANCELED) {
			mUploadMsg.onReceiveValue(null);
		} else if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
			case REQUEST_CODE_IMAGE_CAPTURE:
			case REQUEST_CODE_PICK_IMAGE: {
				try {
					if (mUploadMsg == null || mUploadMsgs == null) {
						return;
					}
					String sourcePath = retrievePath(data);
					if (TextUtils.isEmpty(sourcePath) || !new File(sourcePath).exists()) {
						break;
					}
					Uri uri = Uri.fromFile(new File(sourcePath));
					if (mUploadMsg != null) {
						mUploadMsg.onReceiveValue(uri);
					} else {
						mUploadMsgs.onReceiveValue(new Uri[]{uri});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private String retrievePath(Intent data) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = {MediaStore.Images.Media.DATA};

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return picturePath;
	}


	public void showOptions() {
		Intent i = new Intent(
				Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, REQUEST_CODE_PICK_IMAGE);
	}
	class UploadVideoJavascriptInterface {

		public UploadVideoJavascriptInterface() {
			// TODO Auto-generated constructor stub
		}

		@JavascriptInterface
		public void uploadVideo() {
			// 启动本地相册
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

		}

	}


}
