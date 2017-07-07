package de.blinkt.openvpn.activities.SimOption.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;
import com.aixiaoqi.socket.SocketConstant;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.FreeWorryPacketChoiceActivity;
import de.blinkt.openvpn.activities.SimOption.PresenterImpl.CallDetailPresenterImpl;
import de.blinkt.openvpn.activities.SimOption.View.CallDetailView;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;
import static de.blinkt.openvpn.util.NetworkUtils.hasWiFi;

/**
 * Created by Administrator on 2017/4/6 0006.
 * 拨打详情界面
 */

public class CallDetailActivity extends BaseActivity implements CallDetailView, XRecyclerView.LoadingListener, DialogInterfaceTypeBase {
	@BindView(R.id.user_name_tv)
	TextView userNameTv;
	@BindView(R.id.group_name_tv)
	TextView groupNameTv;
	@BindView(R.id.phone_name_tv)
	TextView phoneNameTv;
	@BindView(R.id.last_call_time_tv)
	TextView lastCallTimeTv;
	@BindView(R.id.defriend_tv)
	TextView defriendTv;
	@BindView(R.id.call_record_rv)
	XRecyclerView callRecordRv;
	@BindView(R.id.tip_record_tv)
	TextView tipRecordTv;
	Unbinder unbinder;
	CallDetailPresenterImpl callDetailPresenter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_detail);
		unbinder=ButterKnife.bind(this);
		callDetailPresenter=new CallDetailPresenterImpl(this,this);
		callDetailPresenter.initData();
		initTitle();
		initRecyclerView();

	}

	@Override
	public void setUserNameText(String textContent) {
		userNameTv.setText(textContent);
	}

	@Override
	public void setUserNameVisible(int isVisible) {
		userNameTv.setVisibility(isVisible);
	}


	@Override
	public void setPhoneNameText(String phoneNumber) {
		phoneNameTv.setText(phoneNumber);
	}

	@Override
	public void loadMoreComplete() {
		callRecordRv.loadMoreComplete();
	}

	@Override
	public void noMoreLoading() {
		callRecordRv.noMoreLoading();
	}

	@Override
	public void callRecordRvIsVisible(int isVisible) {
		callRecordRv.setVisibility(isVisible);
		tipRecordTv.setVisibility(isVisible);
	}

	@Override
	public void lastCallTimeText(String callTime) {
		lastCallTimeTv.setText(callTime);
	}

	@Override
	public void noCallTime() {
		new DialogBalance(this, this, R.layout.dialog_balance, 0);
	}

	private void initTitle() {
		if (callDetailPresenter.isExist()) {
			hasAllViewTitle(R.string.call_detail, R.drawable.edit_info_selector, -1, true);
		} else {
			hasAllViewTitle(R.string.call_detail, R.drawable.create_selector, -1, true);
		}
	}

	@Override
	protected void onClickRightView() {
		callDetailPresenter.clickRight();
        //需要重新获取系统联系人
	}
	@Override
	public void setBlackList() {
		Drawable topDrawable;
		if (callDetailPresenter.isBlackList()) {
			topDrawable = getDrawable(R.drawable.defriended,R.color.gray_text);
		} else {
			topDrawable = getDrawable(R.drawable.defriend_selector,R.color.color_00a0e9);
		}
		topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
		defriendTv.setCompoundDrawables(null, topDrawable, null, null);
	}

	private Drawable getDrawable(int drawableId,int textColorId) {
		Drawable topDrawable;
		topDrawable = getResources().getDrawable(drawableId);
		defriendTv.setTextColor(getResources().getColor(textColorId));
		return topDrawable;
	}



	private void initRecyclerView() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		callRecordRv.setLayoutManager(layoutManager);
		callRecordRv.setArrowImageView(R.drawable.iconfont_downgrey);
		callRecordRv.setPullRefreshEnabled(false);
		callRecordRv.setLoadingListener(this);
		callRecordRv.setAdapter(callDetailPresenter.getCallRecordAdapter());
	}

	@Override
	public void onRefresh() {

	}
	@Override
	public void onLoadMore() {
	callDetailPresenter.queryContactRecoder();
	}


	private void requestTimeHttp() {
		if (hasWiFi())
			callDetailPresenter.requestMaxPhoneCallTime();
	}

	@OnClick({R.id.sms_tv, R.id.net_call_tv, R.id.dual_standby_king_tv, R.id.defriend_tv})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sms_tv:
			callDetailPresenter.sendSms();
				break;
			case R.id.net_call_tv:
				requestTimeHttp();
				break;
			case R.id.dual_standby_king_tv:
				if (SocketConstant.REGISTER_STATUE_CODE == 3) {
					callDetailPresenter.callPhone();
				} else {
					if(ICSOpenVPNApplication.uartService!=null&&ICSOpenVPNApplication.uartService.isConnectedBlueTooth()){
					CommonTools.showShortToast(this, getString(R.string.sim_register_phone_tip));
					}else{
						CommonTools.showShortToast(this, getString(R.string.unconnection_device));
					}
				}
				break;
			case R.id.defriend_tv:
				if (CommonTools.isFastDoubleClick(1000)) {
					e("defriend_tv");
					return;
				}

				if (hasWiFi())
					if (!callDetailPresenter.isBlackList()) {
						callDetailPresenter.requestAddBlackList();
					} else {
						callDetailPresenter.requestDeleteBlackList();
					}
				break;
		}
	}


	@Override
	protected void onDestroy() {
		callDetailPresenter.onDestroy();
		if(unbinder!=null){
			unbinder.unbind();
		}
		super.onDestroy();
	}


	@Override
	public void dialogText(int type, String text) {
		if (type == 1) {
			callDetailPresenter.callPhone();
		} else if (type == 2) {
			Intent intent = new Intent(CallDetailActivity.this, FreeWorryPacketChoiceActivity.class);
			startActivity(intent);
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        callDetailPresenter.initData();
    }
}
