package de.blinkt.openvpn.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import cn.com.aixiaoqi.R;

/**
 * Created by Administrator on 2017/4/7.
 */

public class EditTextWithDel extends android.support.v7.widget.AppCompatEditText {

    private final static String TAG = "EditTextWithDel";
    private Drawable imgInable;
    private Context mContext;

    public EditTextWithDel(Context context) {
        this(context, null, 0);
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextWithDel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        imgInable = mContext.getResources().getDrawable(R.drawable.image_delete_button_nor);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setDrawable();
            }
        });

        setDrawable();
    }

    // 设置删除图片
    private void setDrawable() {
        if (length() < 1) {
          setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
           setCompoundDrawablesWithIntrinsicBounds(null, null, imgInable, null);
        }
    }

    // 处理删除操作


    @Override
    public boolean onTouchEvent(MotionEvent event) {


     //   switch (event.getAction()==MotionEvent.ACTION_DOWN)
       /* if (imgInable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Log.d(TAG, "(" + eventX + ", " + eventY + ")");
          //  Rect rect = new Rect();
          //  getGlobalVisibleRect(rect);
          //  rect.left = rect.right - 70;
        //    Log.d(TAG, rect.toString());
          *//*  if (rect.contains(eventX, eventY)) {
                setText("");
            }*/
       // }
       /* if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL){//监听到删除按钮被按下
            String text = memoEt.getText().toString();
            if(text.length() > 0 ){//判断文本框是否有文字，如果有就去掉最后一位
                String newText = text.substring(0, text.length() - 1);
                memoEt.setText(newText);
                memoEt.setSelection(newText.length());//设置焦点在最后
            };
        }*/

        Log.d(TAG, "(" + "aixiaoqi"  + ")");
        return super.onTouchEvent(event);
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
