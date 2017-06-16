package de.blinkt.openvpn.activities.SimOption.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
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
import com.aixiaoqi.socket.SocketConstant;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.SimOption.PresenterImpl.SmsDetailPresenterImpl;
import de.blinkt.openvpn.activities.SimOption.View.SmsDetailView;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.ExditTextWatcher;
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
public class SMSAcivity extends BaseActivity implements SmsDetailView,SwipeRefreshLayout.OnRefreshListener, DialogInterfaceTypeBase {
    Map<String, String> map = new LinkedHashMap<>();
    public static boolean isForeground = false;
    @BindView(R.id.select_contact_ll)
    LinearLayout selectContactLl;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.add_contact_iv)
    ImageView addContactIv;
    @BindView(R.id.consignee_ll)
    RelativeLayout consigneeLl;
    @BindView(R.id.sms_detail_rv)
    RecyclerView recyclerView;
    @BindView(R.id.pull_down_sr)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.deleteSmsImageView)
    ImageView deleteSmsImageView;
    @BindView(R.id.cancelSmsImageView)
    ImageView cancelSmsImageView;
    @BindView(R.id.rlSmsImageView)
    RelativeLayout rlSmsImageView;
    @BindView(R.id.sms_content_et)
    EditText smsContentEt;
    @BindView(R.id.send_sms_tv)
    TextView sendSmsTv;
    @BindView(R.id.ll_send_sms)
    LinearLayout llSendSms;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    private boolean isNoFocus;
    private boolean isDelete;
    SmsDetailPresenterImpl smsDetailPresenter;
    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        isForeground = false;
        super.onDestroy();
    }

    @Override
    public void showResendDialog() {
        showDialog();
    }

    @Override
    public void rlSmsImageViewVisible(int isVisible) {
        rlSmsImageView.setVisibility(isVisible);
    }

    @Override
    public void llSendSmsVisible(int isVisible) {
        llSendSms.setVisibility(isVisible);
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void recyclerViewBottom() {
        recyclerView.smoothScrollToPosition(recyclerView.getBottom());
    }

    @Override
    public void stopRefresh() {
        dismissProgress();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void noNetRelativeLayoutVisible(int isVisible) {
        NoNetRelativeLayout.setVisibility(isVisible);
    }

    @Override
    public String getSendSmsContent() {
        return smsContentEt.getText().toString().trim();
    }

    @Override
    public String getSendSmsPhone() {
        return editText.getText().toString().trim();
    }

    @Override
    public void combinePhoneNumber() {
        getPhoneNumber();
    }

    @Override
    public String getPhoneNumbers() {
        return phoneNumber;
    }

    @Override
    public void setSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void setTitleBar() {
        titleBar.setTextTitle(getRealName());
    }

    public void setSmsContent(String text) {
        smsContentEt.setText(text);
    }

    @Override
    public void consigneeLl(int isVisible) {
        consigneeLl.setVisibility(isVisible);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        ButterKnife.bind(this);
        smsDetailPresenter=new SmsDetailPresenterImpl(this,this);
        initView();
        initTitle();
        addListener();
        initData();

    }
    //初始化短信界面
    private void initData() {
        if (smsDetailPresenter.getSmsEntity() != null) {
            showDefaultProgress();
            smsDetailHttp();
        }
    }
    private void initView() {
        if (smsDetailPresenter.getSmsEntity() != null)
            swipeRefreshLayout.setOnRefreshListener(this);
        else
            swipeRefreshLayout.setEnabled(false);
        constomEditText();
        selectContactTextView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(smsDetailPresenter.getSmsDetailAdapter());
        consigneeLl.setVisibility(smsDetailPresenter.getSmsEntity() != null?View.GONE:View.VISIBLE);
    }

    EditText editText;

    //代码创建edittext
    private void constomEditText() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        editText = new EditText(this);
        editText.setBackground(null);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, CommonTools.sp2px(getResources(), 14));
        editText.setLayoutParams(lp);
    }

    private void smsDetailHttp() {
        smsDetailPresenter.requestGetSmsDetail(User.isCurrentUser(smsDetailPresenter.getSmsEntity().getFm()) ? smsDetailPresenter.getSmsEntity().getTo() : smsDetailPresenter.getSmsEntity().getFm());
    }

    private void addListener() {
        //监听输入框的文字变化
        new ExditTextWatcher(smsContentEt, R.id.sms_content_et) {
            @Override
            public void textChanged(CharSequence s, int id) {
                sendSmsTv.setTextColor(getResources().getColor(TextUtils.isEmpty(s)?R.color.readed:R.color.select_contacct));
                if (!TextUtils.isEmpty(s)) {
                    //友盟方法统计
                    MobclickAgent.onEvent(context, INPUTPHONENUMBER);

                }
            }
        };
//失去焦点
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
        if (smsDetailPresenter.getSmsEntity() != null) {
            if (!TextUtils.isEmpty(smsDetailPresenter.getSmsEntity().getRealName())) {
                titleBar.setTextTitle(smsDetailPresenter.getSmsEntity().getRealName());
            } else {
                setTitleContent(User.isCurrentUser(smsDetailPresenter.getSmsEntity().getFm())?smsDetailPresenter.getSmsEntity().getTo():smsDetailPresenter.getSmsEntity().getFm());
                titleBar.setRightBtnText(R.string.detail_info);
            }
        } else {
            titleBar.setTextTitle(R.string.new_sms);
        }
        titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
    }

    private void setTitleContent(String headerContent) {
        String[] array = headerContent.split(",");
        if (array.length > 1)
            titleBar.setTextTitle(array[0] + "...");
        else {
            titleBar.setTextTitle(array[0]);
        }
    }
    @Override
    public void onBackPressed() {
        if (!smsDetailPresenter.getDeleteStatue()) {
            super.onBackPressed();
        }
    }
    @OnClick({R.id.titlebar_iv_lefttext, R.id.titlebar_iv_righttext, R.id.send_sms_tv, R.id.add_contact_iv,
            R.id.NoNetRelativeLayout
            ,R.id.deleteSmsImageView
            ,R.id.cancelSmsImageView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_iv_lefttext:
                finish();
                break;
            case R.id.titlebar_iv_righttext:
                if(smsDetailPresenter.getSmsEntity()==null){
                    return;
                }
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKSMSDETAILINFO);
                smsDetailPresenter.clickRight();
                break;
            case R.id.send_sms_tv:
                if (SocketConstant.REGISTER_STATUE_CODE != 3) {
                    CommonTools.showShortToast(this, getString(R.string.sim_register_phone_tip));
                    return;
                }
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKSENDSMS);
                smsDetailPresenter.sendSms();
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
            case R.id.deleteSmsImageView:
                if (!CommonTools.isFastDoubleClick(3000)) {
                    smsDetailPresenter.deleteSmsIds();
                }
                break;
            case R.id.cancelSmsImageView:
                smsDetailPresenter.clearDeleteOption();
                break;

        }
    }

    private void showName() {
        realName = smsDetailPresenter.setRealName(editText.getText().toString());
        phoneNumber = editText.getText().toString();
        if (!TextUtils.isEmpty(phoneNumber) && !map.containsKey(phoneNumber)) {
            map.put(phoneNumber, realName);
            selectContactTextView();
            editText.setText("");
        }
    }

    String phoneNumber = "";
    String realName = "";

    //获取手机号码
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

    //获取真实用户名
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
        scrollView.smoothScrollTo(0, 0);
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
        ContactBean contactBean = new ContactBean();
        switch (resultCode) {
            case RESULT_OK:
                contactBean = (ContactBean) data.getSerializableExtra("Contect");
                break;
            case IntentPutKeyConstant.ADD_CONTACT:
                contactBean = (ContactBean) data.getSerializableExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE);
                break;
        }
        returnData(contactBean);
    }

    private void returnData(ContactBean contactBean) {
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

    @Override
    public void addMap(String phoneNumber, String realName) {
        map.put(phoneNumber, realName);
    }

    @Override
    public void onRefresh() {

        smsDetailHttp();
    }

    private void showDialog() {
        DialogBalance dialog = new DialogBalance(this, SMSAcivity.this, R.layout.dialog_balance, 3);
        dialog.changeText(getResources().getString(R.string.sure_once_send), getResources().getString(R.string.sure));
    }

    @Override
    public void dialogText(int type, String text) {
        if (type == 3) {
            //重新发送，当有短信ID的时候调用重新发送接口。没有短信id的时候调用发送接口。
            smsDetailPresenter.resendSendMsg();
        }

    }

}
