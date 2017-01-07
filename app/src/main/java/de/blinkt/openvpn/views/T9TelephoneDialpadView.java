package de.blinkt.openvpn.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.ViewUtil;

import static de.blinkt.openvpn.constant.UmengContant.CLICKKEYCALLPHONE;


public class T9TelephoneDialpadView extends LinearLayout implements
        OnClickListener {
    private static final char DIAL_1_SECOND_MEANING = ' ';
    private static final char DIAL_X_SECOND_MEANING = ',';
    private static final char DIAL_0_SECOND_MEANING = '+';
    private static final char DIAL_J_SECOND_MEANING = ';';

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

    private Context mContext;
    /**
     * Inflate Custom T9 phone dialpad View hierarchy from the specified xml
     * resource.
     */
    private View mDialpadView; // this Custom View As the T9TelephoneDialpadView
    // of children

    private ImageView mDialDeleteBtn;
    public EditText mT9InputEt;
    private View topView;
    private OnT9TelephoneDialpadView mOnT9TelephoneDialpadView = null;

    public  ImageView getDeteleBtn(){
        return mDialDeleteBtn;
    }

    public T9TelephoneDialpadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        initListener();

    }

    public void show() {
        ViewUtil.showView(this);
    }


    private void initData() {

    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialpadView = inflater.inflate(R.layout.t9_telephone_dialpad_layout,
                this);
        mDialDeleteBtn = (ImageView) mDialpadView
                .findViewById(R.id.dial_delete_btn);
        mT9InputEt = (EditText) mDialpadView
                .findViewById(R.id.dial_input_edit_text);
        mT9InputEt.setCursorVisible(false);
    }

    private void initListener() {
        /**
         * set click listener for button("0-9",'*','#')
         */
        for (int i = 0; i < 12; i++) {
            View v = mDialpadView.findViewById(R.id.dialNum1 + i);

            v.setOnClickListener(this);
        }

        /**
         * set long click listener for button('1','*','0','#')
         * */
//        View view1 = mDialpadView.findViewById(R.id.dialNum1);
//        view1.setOnLongClickListener(this);
//
//        View viewX = mDialpadView.findViewById(R.id.dialx);
//        viewX.setOnLongClickListener(this);
//
//        View viewO = mDialpadView.findViewById(R.id.dialNum0);
//        viewO.setOnLongClickListener(this);
//
//        View viewJ = mDialpadView.findViewById(R.id.dialj);
//        viewJ.setOnLongClickListener(this);

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
        if (v.getId() == R.id.dial_delete_btn) {
            deleteSingleDialCharacter();
        } else if (v.getId() == R.id.dial_input_edit_text) {

        } else {
            addSingleDialCharacter(v.getTag().toString());
        }



    }

//    @Override
//    public boolean onLongClick(View v) {
//        if (v.getId() == R.id.dialNum1) {
//            addSingleDialCharacter(String.valueOf(DIAL_1_SECOND_MEANING));
//        } else if (v.getId() == R.id.dialx) {
//            addSingleDialCharacter(String.valueOf(DIAL_X_SECOND_MEANING));
//        } else if (v.getId() == R.id.dialNum0) {
//            addSingleDialCharacter(String.valueOf(DIAL_0_SECOND_MEANING));
//        } else if (v.getId() == R.id.dialj) {
//            addSingleDialCharacter(String.valueOf(DIAL_J_SECOND_MEANING));
//        }
//        return true;
//    }

    public void setOnT9TelephoneDialpadView(
            OnT9TelephoneDialpadView onT9TelephoneDialpadView) {
        mOnT9TelephoneDialpadView = onT9TelephoneDialpadView;
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
