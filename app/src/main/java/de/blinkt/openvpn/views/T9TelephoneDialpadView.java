package de.blinkt.openvpn.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.AppMode;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.util.ViewUtil;

import static de.blinkt.openvpn.constant.UmengContant.CLICKKEYCALLPHONE;

public class T9TelephoneDialpadView extends LinearLayout implements
        OnClickListener {
    /**
     * Interface definition for a callback to be invoked when a
     * T9TelephoneDialpadView is operated.
     */
    public interface OnT9TelephoneDialpadView {
        void onAddDialCharacter(String addCharacter);

        void onDeleteDialCharacter(String deleteCharacter);

        void onDialInputTextChanged(String curCharacter);

        void onDialInputTextChanging(String curCharacter);

    }
    public interface OnControlCallOptionListener{
        void hideT9();
        void callPhone();
    }

    private Context mContext;
    /**
     * Inflate Custom T9 phone dialpad View hierarchy from the specified xml
     * resource.
     */
    private View mDialpadView; // this Custom View As the T9TelephoneDialpadView
    // of children

    private TextView mDialDeleteBtn;
    public EditText mT9InputEt;
    public Button[] numberButton=new Button[12];
    public View[] lineView=new View[13];

    LinearLayout searchEt;
    public RelativeLayout phoneRelativeLayout;
    ImageView callImageView;
    ImageView ivPutAway;
    private OnT9TelephoneDialpadView mOnT9TelephoneDialpadView = null;
    private OnControlCallOptionListener mOnControlCallOptionListener = null;

    public TextView getDeteleBtn() {
        return mDialDeleteBtn;
    }

    public T9TelephoneDialpadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initListener();

    }

    @Override
    protected void cleanupLayoutState(View child) {
        super.cleanupLayoutState(child);
    }

    public void show() {
        ViewUtil.showView(this);
    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialpadView = inflater.inflate(R.layout.t9_telephone_dialpad_layout,
                this);
        mDialDeleteBtn = (TextView) mDialpadView
                .findViewById(R.id.dial_delete_btn);
        mT9InputEt = (EditText) mDialpadView
                .findViewById(R.id.dial_input_edit_text);
        phoneRelativeLayout = (RelativeLayout) mDialpadView
                .findViewById(R.id.phone_relativelayout);

        callImageView = (ImageView) mDialpadView
                .findViewById(R.id.callImageView);

        ivPutAway = (ImageView) mDialpadView
                .findViewById(R.id.iv_putaway);

        numberButton[0]=(Button) mDialpadView
                .findViewById(R.id.dialNum1);
        numberButton[1]=(Button) mDialpadView
                .findViewById(R.id.dialNum2);
        numberButton[2]=(Button) mDialpadView
                .findViewById(R.id.dialNum3);
        numberButton[3]=(Button) mDialpadView
                .findViewById(R.id.dialNum4);
        numberButton[4]=(Button) mDialpadView
                .findViewById(R.id.dialNum5);
        numberButton[5]=(Button) mDialpadView
                .findViewById(R.id.dialNum6);
        numberButton[6]=(Button) mDialpadView
                .findViewById(R.id.dialNum7);
        numberButton[7]=(Button) mDialpadView
                .findViewById(R.id.dialNum8);
        numberButton[8]=(Button) mDialpadView
                .findViewById(R.id.dialNum9);
        numberButton[9]=(Button) mDialpadView
                .findViewById(R.id.dialx);
        numberButton[10]=(Button) mDialpadView
                .findViewById(R.id.dialNum0);
        numberButton[11]=(Button) mDialpadView
                .findViewById(R.id.dialj);
        lineView[0]= mDialpadView
                .findViewById(R.id.line1);
        lineView[1]= mDialpadView
                .findViewById(R.id.line2);
        lineView[2]= mDialpadView
                .findViewById(R.id.line3);
        lineView[3]= mDialpadView
                .findViewById(R.id.line4);
        lineView[4]= mDialpadView
                .findViewById(R.id.line5);
        lineView[5]= mDialpadView
                .findViewById(R.id.line6);
        lineView[6]= mDialpadView
                .findViewById(R.id.line7);
        lineView[7]= mDialpadView
                .findViewById(R.id.line8);
        lineView[8]= mDialpadView
                .findViewById(R.id.line9);
        lineView[9]= mDialpadView
                .findViewById(R.id.line10);
        lineView[10]= mDialpadView
                .findViewById(R.id.line11);
        lineView[11]= mDialpadView
                .findViewById(R.id.line12);
        lineView[12]= mDialpadView
                .findViewById(R.id.line13);

        mT9InputEt.setCursorVisible(false);
        searchEt = (LinearLayout) findViewById(R.id.search_et_linearlayout);
        //初始化状态
        mDialDeleteBtn.setVisibility(View.GONE);
        mT9InputEt.setVisibility(View.GONE);
        searchEt.setBackgroundResource(R.color.transparent);
    }

    public void searchEtHidden(){
        searchEt.setVisibility(GONE);
    }

    public void setBtnColor(int textColor){
        for(int i=0;i<12;i++){
            numberButton[i].setBackground(null);
            numberButton[i].setTextColor(textColor);
        }
    }

    public void setLineBackgroundColor(int lineBackgroundColor){
        for(int i=0;i<13;i++){
            lineView[i].setBackgroundResource(lineBackgroundColor);
        }
    }


    private void initListener() {

        for (int i = 0; i < 12; i++) {
            numberButton[i].setOnClickListener(this);
        }
        phoneRelativeLayout.setOnClickListener(this);
        ivPutAway.setOnClickListener(this);
        callImageView.setOnClickListener(this);
        mT9InputEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (null != mOnT9TelephoneDialpadView) {
                    String inputStr = s.toString();
                    mOnT9TelephoneDialpadView.onDialInputTextChanging(inputStr);
                    mT9InputEt.setSelection(inputStr.length());

                }
                //根据输入框的字符来控制图片的显示
                if (s != null) {
                    if (s.toString().length() > 0) {
                        mDialDeleteBtn.setVisibility(View.VISIBLE);
                        searchEt.setBackgroundResource(R.color.bottom_bar);
                        mT9InputEt.setVisibility(View.VISIBLE);
                    } else {
                        mDialDeleteBtn.setVisibility(View.GONE);
                        mT9InputEt.setVisibility(View.GONE);
                        searchEt.setBackgroundResource(R.color.transparent);
                    }

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != mOnT9TelephoneDialpadView) {
                    String inputStr = s.toString();
                    mOnT9TelephoneDialpadView.onDialInputTextChanged(inputStr);
                    mT9InputEt.setSelection(inputStr.length());

                }
            }
        });

        mT9InputEt.setOnTouchListener(new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // In order to prevent the soft keyboard pops up,but also can
                // not make EditText get focus.
                return true; // the listener has consumed the event
            }
        });
    }


    @Override
    public void onClick(View v) {
        //友盟方法统计
        MobclickAgent.onEvent(mContext, CLICKKEYCALLPHONE);
        switch (v.getId()){
            case  R.id.dial_delete_btn:
                deleteSingleDialCharacter();
                break;
            case R.id.phone_relativelayout:
                break;
            case R.id.iv_putaway:
                if(mOnControlCallOptionListener!=null){
                    mOnControlCallOptionListener.hideT9();
                }
                break;
            case R.id.callImageView:
                if(mOnControlCallOptionListener!=null){
                    mOnControlCallOptionListener.callPhone();
                }
                break;
            default:
                addSingleDialCharacter(v.getTag().toString());
                break;
        }

    }


    public void setOnT9TelephoneDialpadView(
            OnT9TelephoneDialpadView onT9TelephoneDialpadView) {
        mOnT9TelephoneDialpadView = onT9TelephoneDialpadView;
    }
    public void setOnControlCallOptionListener(
            OnControlCallOptionListener OnControlCallOptionListener) {
        mOnControlCallOptionListener = OnControlCallOptionListener;
    }
    public void deleteSingleDialCharacter() {
        String curInputStr = mT9InputEt.getText().toString();
        if (curInputStr.length() > 0) {
            String deleteCharacter = curInputStr.substring(
                    curInputStr.length() - 1, curInputStr.length());
            if (null != mOnT9TelephoneDialpadView) {
                mOnT9TelephoneDialpadView
                        .onDeleteDialCharacter(deleteCharacter);
            }

            String newCurInputStr = curInputStr.substring(0, curInputStr.length() - 1);
            mT9InputEt.setText(newCurInputStr);
            mT9InputEt.setSelection(newCurInputStr.length());

        }
    }


    private void addSingleDialCharacter(String addCharacter) {
        String preInputStr = mT9InputEt.getText().toString();
        if (!TextUtils.isEmpty(addCharacter)) {
            mT9InputEt.setText(preInputStr + addCharacter);
            mT9InputEt.setSelection(mT9InputEt.getText().length());
            if (null != mOnT9TelephoneDialpadView) {
                mOnT9TelephoneDialpadView.onAddDialCharacter(addCharacter);
            }

        }


    }

    public String getT9Input() {
        return mT9InputEt.getText().toString();
    }

    public void clearT9Input() {
        mT9InputEt.setText("");
    }

}
