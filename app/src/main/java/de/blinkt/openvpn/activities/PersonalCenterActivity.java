package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.widget.GlideCircleTransform;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.ModifyPersonInfoHttp;
import de.blinkt.openvpn.http.UploadHeaderHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.UtilsImageProcess;


import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogPicker;
import de.blinkt.openvpn.views.dialog.DialogSexAndHeaderAndMyPacket;
import de.blinkt.openvpn.views.dialog.DialogTimePicker;

import static de.blinkt.openvpn.constant.UmengContant.CHANGEBIRTHDAY;
import static de.blinkt.openvpn.constant.UmengContant.CHANGEGENDER;
import static de.blinkt.openvpn.constant.UmengContant.CHANGEHEADER;
import static de.blinkt.openvpn.constant.UmengContant.CHANGEHEIGHT;
import static de.blinkt.openvpn.constant.UmengContant.CHANGENAME;
import static de.blinkt.openvpn.constant.UmengContant.CHANGESPORTTARGET;
import static de.blinkt.openvpn.constant.UmengContant.CHANGEWEIGHT;

public class PersonalCenterActivity extends BaseActivity implements InterfaceCallback, View.OnClickListener, DialogInterfaceTypeBase {

	@BindView(R.id.headImageView)
	ImageView headImageView;
	@BindView(R.id.changeHeadTextView)
	TextView changeHeadTextView;
	@BindView(R.id.nameTextView)
	TextView nameTextView;
	@BindView(R.id.nameTableRow)
	LinearLayout nameTableRow;
	@BindView(R.id.sexTextView)
	TextView sexTextView;
	@BindView(R.id.sexTableRow)
	LinearLayout sexTableRow;
	@BindView(R.id.ageTextView)
	TextView ageTextView;
	@BindView(R.id.ageTableRow)
	LinearLayout ageTableRow;
	@BindView(R.id.statureTextView)
	TextView statureTextView;
	@BindView(R.id.statureTableRow)
	LinearLayout statureTableRow;
	@BindView(R.id.weightTextView)
	TextView weightTextView;
	@BindView(R.id.weightTableRow)
	LinearLayout weightTableRow;
	@BindView(R.id.motionTargetTextView)
	TextView motionTargetTextView;
	@BindView(R.id.motionTargetTableRow)
	LinearLayout motionTargetTableRow;
	@BindView(R.id.BMITextView)
	TextView BMITextView;

	/* 头像文件 */
	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

	/* 请求识别码 */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CODE_RESULT_REQUEST = 0xa2;

	// 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。（生成bitmap貌似有时要报错？可试下把大小弄小点）
	private static int output_X = 150;
	private static int output_Y = 150;

	private ImageView headImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_center);
		ButterKnife.bind(this);
		hasLeftViewTitle(R.string.personal_info, 0);
		initSet();

	}


	//创建初始化
	private void initSet() {
		//设置成个人中心
		Glide.with(ICSOpenVPNApplication.getContext()).load(SharedUtils.getInstance().readString(Constant.USER_HEAD)).placeholder(R.drawable.default_head).error(R.drawable.default_head).
				transform(new GlideCircleTransform(this)).into(headImageView);
		SharedUtils sharedUtils = SharedUtils.getInstance();
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.NICK_NAME))) {
			nameTextView.setText(sharedUtils.readString(Constant.NICK_NAME));
		}
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.GENDER))) {
			sexTextView.setText(sharedUtils.readString(Constant.GENDER).equals("0") ? getString(R.string.man) : getString(R.string.women));
		}
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.BRITHDAY))) {
		String str=	sharedUtils.readString(Constant.BRITHDAY);
			if(str.endsWith(getString(R.string.month)))
			ageTextView.setText(str);
			else{
				ageTextView.setText(str+getString(R.string.month));
			}
		}
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.HEIGHT))) {
			statureTextView.setText(sharedUtils.readString(Constant.HEIGHT) + getString(R.string.cm));
		}
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.WEIGHT))) {
			weightTextView.setText(sharedUtils.readString(Constant.WEIGHT) + getString(R.string.kg));
		}
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.SOPRT_TARGET))) {
			motionTargetTextView.setText(sharedUtils.readString(Constant.SOPRT_TARGET) + getString(R.string.step));
		}
		mesuBMI();

	}

	private void mesuBMI() {
		SharedUtils sharedUtils = SharedUtils.getInstance();
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.WEIGHT)) && !TextUtils.isEmpty(sharedUtils.readString(Constant.HEIGHT))) {
			int height = Integer.parseInt(sharedUtils.readString(Constant.HEIGHT));
			int weight = Integer.parseInt(sharedUtils.readString(Constant.WEIGHT));
			if (weight != 0 && height != 0) {
				BMITextView.setText(weight * 100 * 100 / (height * height) + "");
			}
		}
	}

	public static final int PHEADER_PIC = 0;
	public static final int NICK_NAME = 1;
	public static final int GENDLER = 2;
	public static final int BIRTHDAY = 3;
	public static final int HEIGHT = 4;
	public static final int WEIGHT = 5;
	public static final int SPORT_TARGET = 6;
	public static final int SEX = 7;

	@OnClick({R.id.headImageView, R.id.nameTableRow, R.id.sexTableRow, R.id.ageTableRow, R.id.statureTableRow, R.id.weightTableRow, R.id.motionTargetTableRow})
	public void onClick(View view) {
		SharedUtils sharedUtils = SharedUtils.getInstance();
		switch (view.getId()) {

			case R.id.headImageView:
				//友盟方法统计
				MobclickAgent.onEvent(this, CHANGEHEADER);
				DialogSexAndHeaderAndMyPacket(PHEADER_PIC);

				break;
			case R.id.nameTableRow:
				//友盟方法统计
				MobclickAgent.onEvent(this, CHANGENAME);
				String name;
				if (TextUtils.isEmpty(nameTextView.getText().toString())) {
					name = "";
				} else {
					name = nameTextView.getText().toString();
				}
				toActivity(new Intent(this,EditNameActivity.class).putExtra(IntentPutKeyConstant.REAL_NAME_EDIT,name).putExtra(IntentPutKeyConstant.EDIT_TYPE,IntentPutKeyConstant.EDIT_NICKNAME));
				break;
			case R.id.sexTableRow:
				//友盟方法统计
				MobclickAgent.onEvent(this, CHANGEGENDER);
				DialogSexAndHeaderAndMyPacket(SEX);
				break;
			case R.id.ageTableRow:
				//友盟方法统计
				MobclickAgent.onEvent(this, CHANGEBIRTHDAY);
				dialogTime();
				break;
			case R.id.statureTableRow:
                //友盟方法统计
				MobclickAgent.onEvent(this, CHANGEHEIGHT);
				String height = sharedUtils.readString(Constant.HEIGHT);
				int heightInt;
				if (!TextUtils.isEmpty(height))
					heightInt = Integer.parseInt(height);
				else {
					heightInt = 0;
				}
				dialogPicker(HEIGHT, heightInt);
				break;
			case R.id.weightTableRow:
				//友盟方法统计
				MobclickAgent.onEvent(this, CHANGEWEIGHT);
				String weight = sharedUtils.readString(Constant.WEIGHT);
				int weightInt;
				if (!TextUtils.isEmpty(weight))
					weightInt = Integer.parseInt(weight);
				else {
					weightInt = 0;
				}
				dialogPicker(WEIGHT, weightInt);
				break;
			case R.id.motionTargetTableRow:
				//友盟方法统计
				MobclickAgent.onEvent(this, CHANGESPORTTARGET);
				String target = sharedUtils.readString(Constant.SOPRT_TARGET);
				int targetInt;
				if (!TextUtils.isEmpty(target))
					targetInt = Integer.parseInt(target);
				else {
					targetInt = 0;
				}
				dialogPicker(SPORT_TARGET, targetInt);
				break;


		}
	}

	private void DialogSexAndHeaderAndMyPacket(int type) {
		new DialogSexAndHeaderAndMyPacket(this, this, R.layout.choice_pic_way_popupwindow, type);
	}

	private void dialogTime() {
		new DialogTimePicker(this, this, R.layout.picker_time_layout, BIRTHDAY);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedUtils sharedUtils = SharedUtils.getInstance();
		if (!TextUtils.isEmpty(sharedUtils.readString(Constant.NICK_NAME))) {
			nameTextView.setText(sharedUtils.readString(Constant.NICK_NAME));
		}
	}

	@Override
	public void dialogText(int type, String text) {
		switch (type) {
			case HEIGHT:
				statureTextView.setText(text + getString(R.string.cm));
				break;
			case WEIGHT:
				weightTextView.setText(text + getString(R.string.kg));
				break;
			case SPORT_TARGET:
				motionTargetTextView.setText(text + getString(R.string.step));
				break;
			case NICK_NAME:
				if (TextUtils.isEmpty(text)) {
					CommonTools.showShortToast(PersonalCenterActivity.this, getString(R.string.name_is_numm));
				} else {
					nameTextView.setText(text);
				}
				break;
			case BIRTHDAY:
				long time = DateUtils.getStringToDate(text);
				if (time > System.currentTimeMillis()) {
					CommonTools.showShortToast(this, getString(R.string.more_current_time));
					return;
				}
				ageTextView.setText(text.substring(0, 7).replace("-", getString(R.string.year))+getString(R.string.month));
				text = time + "";
				break;
			case PHEADER_PIC:

				if ("1".equals(text)) {
					choseHeadImageFromCameraCapture();
				} else if ("0".equals(text)) {
					choseHeadImageFromGallery();
				}
				break;
			case SEX:
				if ("1".equals(text)) {
					sexTextView.setText(getString(R.string.women));
				} else if ("0".equals(text)) {
					sexTextView.setText(getString(R.string.man));
				}
				type = GENDLER;

				break;

		}
		if (type != PHEADER_PIC)
			modifyPersonInfoHttp(text, type);
	}

	private void dialogPicker(int type, int defaultVaule) {
		new DialogPicker(this, this, R.layout.picker_layout, type, defaultVaule);

	}


	private void modifyPersonInfoHttp(String content, int type) {
		ModifyPersonInfoHttp modifyPersonInfoHttp = new ModifyPersonInfoHttp(this);
		if (type == GENDLER) {
			modifyPersonInfoHttp.setSex(content, HttpConfigUrl.COMTYPE_POST_MODIFY_GENDER);
		} else if (type == BIRTHDAY) {
			modifyPersonInfoHttp.setBirthday(Long.parseLong(content) / 1000 + "", HttpConfigUrl.COMTYPE_POST_MODIFY_AGE);
		} else if (type == HEIGHT) {
			modifyPersonInfoHttp.setHeight(content, HttpConfigUrl.COMTYPE_POST_MODIFY_HEIGHT);
		} else if (type == WEIGHT) {
			modifyPersonInfoHttp.setWeight(content, HttpConfigUrl.COMTYPE_POST_MODIFY_WEIGHT);
		} else if (type == SPORT_TARGET) {
			modifyPersonInfoHttp.setMovingTarget(content, HttpConfigUrl.COMTYPE_POST_MODIFY_SPORT_TARGET);
		}
		new Thread(modifyPersonInfoHttp).start();
	}


	// 从本地相册选取图片作为头像
	private void choseHeadImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, CODE_GALLERY_REQUEST);
	}


	// 启动手机相机拍摄照片作为头像
	private void choseHeadImageFromCameraCapture() {

		Intent intent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		//下面这句指定调用相机拍照后的照片存储的路径
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(),
							IMAGE_FILE_NAME)));

		}
		startActivityForResult(intent, CODE_CAMERA_REQUEST);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {

		// 用户没有进行有效的设置操作，返回
		if (resultCode != Activity.RESULT_CANCELED) {

			switch (requestCode) {
				case CODE_GALLERY_REQUEST:
					if (intent == null) {
						return;
					}
					cropRawPhoto(intent.getData());
					break;

				case CODE_CAMERA_REQUEST:
					if (hasSdcard()) {
						File tempFile = new File(Environment.getExternalStorageDirectory()
								+ "/" + IMAGE_FILE_NAME);
						cropRawPhoto(Uri.fromFile(tempFile));

					} else {
						Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
								.show();
					}

					break;

				case CODE_RESULT_REQUEST:

					if (intent != null) {
						setImageToHeadView(intent);
					}

					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * 裁剪原始的图片
	 */
	public void cropRawPhoto(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX , aspectY :宽高的比例
		if (android.os.Build.MANUFACTURER.contains("HUAWEI")) {//华为特殊处理 不然会显示圆
			intent.putExtra("aspectX", 9998);
			intent.putExtra("aspectY", 9999);
		} else {
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
		}
		// outputX , outputY : 裁剪图片宽高
		intent.putExtra("outputX", output_X);
		intent.putExtra("outputY", output_Y);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CODE_RESULT_REQUEST);
	}

	/**
	 * 提取保存裁剪之后的图片数据，并设置头像部分的View
	 */
	private void setImageToHeadView(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			uploadHeaderHttp(UtilsImageProcess.getPath(photo));
		} else {
			Uri mImageCaptureUri = intent.getData();
			if (mImageCaptureUri != null) {
				try {
					Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
					Glide.with(ICSOpenVPNApplication.getContext()).load(UtilsImageProcess.getPath(photo)).
							transform(new GlideCircleTransform(this)).into(headImageView);
					uploadHeaderHttp(mImageCaptureUri.getPath());
				} catch (IOException e) {
					CommonTools.showShortToast(this,getString(R.string.update_fail));
					e.printStackTrace();
				}
			}else{
				CommonTools.showShortToast(this,getString(R.string.update_fail));
			}
		}

	}

	private void uploadHeaderHttp(String url){
		UploadHeaderHttp uploadHeaderHttp=new UploadHeaderHttp(this, HttpConfigUrl.COMTYPE_UPLOAD_HEADER,url);
		new Thread(uploadHeaderHttp).start();
	}

	/**
	 * 检查设备是否存在SDCard的工具方法
	 */
	public static boolean hasSdcard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_UPLOAD_HEADER) {
			UploadHeaderHttp uploadHeaderHttp = (UploadHeaderHttp) object;
			if(uploadHeaderHttp.getStatus()==1) {
				SharedUtils sharedUtils = SharedUtils.getInstance();
				sharedUtils.writeString(Constant.USER_HEAD, uploadHeaderHttp.getImageEntity().getUserHead());
				Glide.with(ICSOpenVPNApplication.getContext()).load(uploadHeaderHttp.getImageEntity().getUserHead()).
						transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(headImageView);
			}else{
				CommonTools.showShortToast(this,getString(R.string.update_fail));
			}
		} else {
			ModifyPersonInfoHttp modifyPersonInfoHttp = (ModifyPersonInfoHttp) object;
			CommonTools.showShortToast(this, modifyPersonInfoHttp.getMsg());
			if (modifyPersonInfoHttp.getStatus() == 1) {
				SharedUtils sharedUtils = SharedUtils.getInstance();
				if (cmdType == HttpConfigUrl.COMTYPE_POST_MODIFY_NICK)
					sharedUtils.writeString(Constant.NICK_NAME, nameTextView.getText().toString());
				else if (cmdType == HttpConfigUrl.COMTYPE_POST_MODIFY_GENDER)
					sharedUtils.writeString(Constant.GENDER, sexTextView.getText().toString().equals(getString(R.string.man)) ? "0" : "1");
				else if (cmdType == HttpConfigUrl.COMTYPE_POST_MODIFY_AGE)
					sharedUtils.writeString(Constant.BRITHDAY, ageTextView.getText().toString());
				else if (cmdType == HttpConfigUrl.COMTYPE_POST_MODIFY_WEIGHT) {
					String weight = weightTextView.getText().toString();
					sharedUtils.writeString(Constant.WEIGHT, weight.substring(0, weight.length() - 2));
					mesuBMI();
				} else if (cmdType == HttpConfigUrl.COMTYPE_POST_MODIFY_HEIGHT) {
					String height = statureTextView.getText().toString();
					sharedUtils.writeString(Constant.HEIGHT, height.substring(0, height.length() - 2));
					mesuBMI();
				} else if (cmdType == HttpConfigUrl.COMTYPE_POST_MODIFY_SPORT_TARGET) {
					String targetDis = motionTargetTextView.getText().toString();
					sharedUtils.writeString(Constant.SOPRT_TARGET, targetDis.substring(0, targetDis.length() - 1));
				}
			}
		}

	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {

	}
}
