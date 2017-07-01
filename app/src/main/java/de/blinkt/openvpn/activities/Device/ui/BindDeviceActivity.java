package de.blinkt.openvpn.activities.Device.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.BindDevcieAdapter;
import cn.com.johnson.listener.MyItemClickListener;
import cn.com.johnson.model.GetBindsIMEIHttpEntity;
import de.blinkt.openvpn.activities.Device.PresenterImpl.BindDevicePresenterImpl;
import de.blinkt.openvpn.activities.Device.View.BindDeviceView;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.GetBindsIMEIHttp;
import de.blinkt.openvpn.model.BluetoothEntity;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.contact.DividerDecoration;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;


public class BindDeviceActivity extends BluetoothBaseActivity implements BindDeviceView, DialogInterfaceTypeBase,MyItemClickListener {
	@BindView(R.id.stopTextView)
	TextView stopTextView;
	@BindView(R.id.tip_search)
	TextView tip_search;
	@BindView(R.id.search_bluetooth)
	TextView search_bluetooth;
	@BindView(R.id.findedImageView)
	ImageView findedImageView;
	@BindView(R.id.seekImageView)
	ImageView seekImageView;
    @BindView(R.id.outerRing)
	ImageView outerRing;
	@BindView(R.id.uniImageView)
	ImageView uniImageView;
    @BindView(R.id.iv_back)
	TextView iv_back;
	SharedUtils utils = SharedUtils.getInstance();
	private DialogBalance noDevicedialog;

	private UartService mService = ICSOpenVPNApplication.uartService;//
	//设备名称：类型不同名称不同，分别有【unitoys、unibox】
	private String bluetoothName = Constant.UNITOYS;
	private Handler mHandler=new Handler(){};

	BindDevicePresenterImpl bindDevicePresenter;
    private TranslateAnimation HiddenAmin;

    @Override
	public void showToast(String showContent) {
		super.showToast(showContent);
	}

	@Override
	public void showToast(int showContentId) {
		super.showToast(showContentId);
	}

	@Override
	public void finishView() {
		finish();
	}

	@Override
	public String getDeviceName() {
		return bluetoothName;
	}

	@Override
	public void tipSearchText(int tipText) {
		tip_search.setText(getString(tipText));
	}

	@Override
	public void SetUniImageViewBackground(int sourceId) {
		uniImageView.setBackgroundResource(sourceId);
	}

	@Override
	protected void findDevices(BluetoothDevice device, int rssi, byte[] scanRecord) {
		bindDevicePresenter.findDevices(device,rssi,scanRecord);
	}

	@Override
	public void setFindedImageView(int isVisible) {
		findedImageView.setVisibility(isVisible);
		if(isVisible==View.VISIBLE){
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_trans_seek_over);
			findedImageView.startAnimation(anim);
		}else{
			findedImageView.clearAnimation();
		}
	}

	@Override
	public void searchBluetoothText(int searchId) {
		search_bluetooth.setText(getString(searchId));
	}

	@Override
	public void toActivity() {
		toActivity(MyDeviceActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_device);
		bluetoothName = getIntent().getStringExtra(Constant.BRACELETNAME);
		ButterKnife.bind(this);
		bindDevicePresenter=new BindDevicePresenterImpl(this);
		if(bluetoothIsOpen()){
            bindDevicePresenter.clearListData();
			setAnimation();
			scanLeDevice(true);
		}
		if (bluetoothName != null && bluetoothName.contains(Constant.UNIBOX)) {
			initUnibox();
		}
		initEvent();
	}

    /**
     * 返回键的处理
     */
    private void initEvent() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
	public void connect(String macAddress) {

        super.connect(macAddress);
	}

	@Override
	public void scanLeDevice(boolean enable) {
		super.scanLeDevice(enable);
	}

	@Override
	public void scanNotFindDevice() {
		bindDevicePresenter.scanNotFindDevice();
	}

	private void initUnibox() {
		search_bluetooth.setText(getString(R.string.searching_unibox_strap));
		tip_search.setText(getString(R.string.please_makesure_bind));
		uniImageView.setBackgroundResource(R.drawable.pic_sdw);
	}

	//蓝牙服务是否已经打开
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					setAnimation();
					scanLeDevice(true);
				} else {
					Log.d(TAG, "蓝牙未打开");
					finish();
				}
				break;
		}
	}

	//设置动画
	private void setAnimation() {
		if (seekImageView.getAnimation() != null)
			seekImageView.clearAnimation();
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_seek);
		anim.setInterpolator(new LinearInterpolator());//代码设置插补器
		seekImageView.startAnimation(anim);
	}

	@Override
	public void onBackPressed() {
		stopTextView.performClick();
	}

	//连接成功设备以后
	@Override
	public void afterConnDevice() {
		if (bluetoothName != null) {
			if (bluetoothName.contains(Constant.UNIBOX)) {
				showIsBindLayout();
			} else {
				finish();
			}
		}
	}
    GetBindsIMEIHttpEntity bindsIMEIHttpEntity;
    LinearLayout pop_layout;
    RecyclerView recyclerview;
	@Override
	public void showDeviceView(List<BluetoothEntity> list,GetBindsIMEIHttp http) {
        bindsIMEIHttpEntity = http.getBindsIMEIHttpEntity();
        recyclerview = (RecyclerView)findViewById(R.id.bind_recyclerview);
        pop_layout = (LinearLayout)findViewById(R.id.pop_layout);
        //搜索到动画
        findDeviceAnimal(pop_layout);
        initRecyclerData(recyclerview,list);

	}

    private void findDeviceAnimal(LinearLayout pop_layout) {
        search_bluetooth.setVisibility(View.GONE);
        tip_search.setVisibility(View.GONE);
        seekImageView.clearAnimation();
        stopTextView.setText(R.string.frist_connect);
        showAimal();
        pop_layout.startAnimation(mShowAnim);
        pop_layout.setVisibility(View.VISIBLE);
        startPropertyAnim(stopTextView,300,-580);
        setAnimatorSet(uniImageView,-320,true);
        setAnimatorSet(seekImageView,-320,true);
        setAnimatorSet(outerRing,-320,true);
        iv_back.setVisibility(View.VISIBLE);

    }

    private void connectSuccessAnimal(LinearLayout pop_layout){
        hideAnimal();
        pop_layout.startAnimation(HiddenAmin);
        pop_layout.setVisibility(View.GONE);
        startPropertyAnim(stopTextView,300,0);
        setAnimatorSet(uniImageView,0,false);
        setAnimatorSet(seekImageView,0,false);
        setAnimatorSet(outerRing,0,false);
        stopTextView.setText(R.string.stop_bind);
        iv_back.setVisibility(View.GONE);
    }

    BindDevcieAdapter bindDevcieAdapter;
	private void initRecyclerData(RecyclerView mRecyclerView,List<BluetoothEntity> list ) {
        Log.d(TAG, "initRecyclerData: "+list.size());
        if(list.size()>0&&bindsIMEIHttpEntity!=null) {
            bindDevcieAdapter = new BindDevcieAdapter(this,bindsIMEIHttpEntity);
            bindDevcieAdapter.addAll(list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setAdapter(bindDevcieAdapter);
            mRecyclerView.addItemDecoration(new DividerDecoration(this));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            bindDevcieAdapter.setOnItemClickListener(this);
        }
    }
    TranslateAnimation mShowAnim;

    /**
     * 显示动画
     */
    public void showAimal(){
        mShowAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAnim.setDuration(300);

    }

    /**
     * 隐藏动画
     */
    public void hideAnimal()
    {
        HiddenAmin = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF
                ,0.0f,Animation.RELATIVE_TO_SELF,1.0f);
        HiddenAmin.setDuration(300);

    }


    /**
     * @param stopTextView 控件
     * @param time 时间
     * @param distance 距离
     */
	private void startPropertyAnim(View stopTextView,int time,int distance) {
		// Y轴方向上的坐标
		float translationY = stopTextView.getTranslationY();
		// 向右移动500pix
		ObjectAnimator anim = ObjectAnimator.ofFloat(stopTextView, "translationY", translationY, distance);
		anim.setDuration(time);
		// 回调监听，可以有也可以无。
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				//float value = (Float) animation.getAnimatedValue();
			}
		});
		// 正式开始启动执行动画
		anim.start();
	}

    /**
     * 动画组合
     * @param view
     * @param distance
     */
	private void setAnimatorSet(View  view,int distance,boolean isScale){
        float translationY = view.getTranslationY();
        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;
        if(isScale) {
            scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f);
            scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f);
        }else {
            scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1f);
            scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1f);

        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", translationY, distance);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(anim).with(scaleX).with(scaleY);
        animatorSet.start();
        if(!isScale) {
            search_bluetooth.setVisibility(View.VISIBLE);
            tip_search.setVisibility(View.VISIBLE);
        }

    }


	@Override
	public void showNotSearchDeviceDialog() {
		showDialog();
	}
	//创建提示对话框
	private void showDialog() {
		//不能按返回键，只能二选其一
		if(noDevicedialog!=null)
			noDevicedialog.show();
		if(noDevicedialog==null) {
			noDevicedialog = new DialogBalance(this, this, R.layout.dialog_balance, 2);
			if (bluetoothName != null && bluetoothName.contains(Constant.UNIBOX)) {
				noDevicedialog.changeText(getString(R.string.no_find_unibox), getResources().getString(R.string.retry));
			} else {
				noDevicedialog.changeText(getResources().getString(R.string.no_find_unitoys), getResources().getString(R.string.retry));
			}
		}
	}

	//停止搜索和隐藏对话框
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		if (noDevicedialog != null  && noDevicedialog.isShowing()) {
			noDevicedialog.dismiss();
		}
	}

	//回收数据，取消订阅
	@Override
	protected void onDestroy() {
		super.onDestroy();
		bindDevicePresenter.onDestory();
		seekImageView.clearAnimation();
		if (noDevicedialog != null) {
			noDevicedialog.dismiss();
			noDevicedialog = null;
		}

	}

	@OnClick(R.id.stopTextView)
	  public void onClick() {
                //停止绑定，清除设备信息
                if(getResources().getString(R.string.stop_seek).equals(stopTextView.getText().toString())) {
                    Log.d(TAG, "onClick: 停止绑定");
                    scanLeDevice(false);
                    mService.disconnect();
                    ICSOpenVPNApplication.isConnect = false;
                    utils.delete(Constant.IMEI);
                    utils.delete(Constant.BRACELETNAME);
                    finish();
                }else if(getResources().getString(R.string.frist_connect).equals(stopTextView.getText().toString())){
                    //首选连接
                    Log.d(TAG, "onClick: "+getResources().getString(R.string.frist_connect));
					//连接第一个
                    String address = bindDevcieAdapter.getData().get(0).getAddress();
                    SharedUtils.getInstance().writeString(Constant.BRACELETNAME, address);
                    connect(address);
                    //暂不绑定
                }else if(getResources().getString(R.string.stop_bind).equals(stopTextView.getText().toString())){
                    findDeviceAnimal(pop_layout);
                    scanLeDevice(false);
                    mService.disconnect();
                    ICSOpenVPNApplication.isConnect = false;
                }

	}
	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			scanLeDevice(true);
		} else {
			stopTextView.performClick();

		}
	}

	//连接设备成功，提示用户绑定
	private void showIsBindLayout() {
        if(pop_layout!=null)
            connectSuccessAnimal(pop_layout);
		tip_search.setText(getString(R.string.finded_bracelet));
		search_bluetooth.setText(getString(R.string.click_bracelet_sure_bind));
		seekImageView.clearAnimation();
		if (bluetoothName != null) {
			if (bluetoothName.equals(Constant.UNIBOX)) {
				seekImageView.setBackgroundResource(R.drawable.seek_finish_pic);
				setFindedImageView(View.VISIBLE);
			}
		}
	}

    @Override
    public void onItemClick(View view, int postion) {

    if(bindDevcieAdapter!=null) {
        String address = bindDevcieAdapter.getData().get(postion).getAddress();
        Log.d("test____", "onItemClick: ------"+address);
        SharedUtils.getInstance().writeString(Constant.BRACELETNAME, address);
        connect(address);
        }
    }


}



