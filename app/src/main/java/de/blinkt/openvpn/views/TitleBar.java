package de.blinkt.openvpn.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.aixiaoqi.R;


public class TitleBar extends LinearLayout{

	private Context context;
	
	private TextView leftText;

	private TextView title;
	private TextView rightText;
	
	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.titlebar, this);
		initLayout();
	}

	public TitleBar(Context context) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.titlebar, this);
		initLayout();
	}

    public TextView getLeftText() {
        return leftText;
    }

    public TextView getTitle() {
        return title;
    }


    public TextView getRightText() {
        return rightText;
    }

    private void initLayout() {
		leftText = (TextView) findViewById(R.id.titlebar_iv_lefttext);
		title = (TextView) findViewById(R.id.titlebar_iv_title);
		rightText = (TextView) findViewById(R.id.titlebar_iv_righttext);
	}
	
	public void setLeftBtnIcon(int resId) {
		Drawable leftDrawable = getResources().getDrawable(resId);
		if(leftDrawable!=null){
		leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
		leftText.setCompoundDrawables(leftDrawable, null, null, null);
		}
		leftText.setText("");
	}
	
	public void setLeftBtnText(String text) {
		leftText.setCompoundDrawables(null, null, null, null);
		leftText.setText(text);
	}
	


	public void setLeftIvIconAndText(int resId, String text) {
		Drawable leftDrawable = getResources().getDrawable(resId);
		if(leftDrawable!=null) {
			leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
			leftText.setCompoundDrawables(leftDrawable, null, null, null);
			leftText.setText(text);
		}
	}
	public void setTextTitle(String text) {
		title.setText(text);
	}
    public void setTextTitle(int resId) {
        title.setText(resId);
    }
	
	public void setRightBtnIcon(int resId) {
		Drawable rightDrawable = getResources().getDrawable(resId);
		if(rightDrawable!=null){
		rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
		rightText.setCompoundDrawables(null, null, rightDrawable, null);
		}
		rightText.setText("");
	}
	
	public void setRightBtnText(String text) {
		rightText.setCompoundDrawables(null, null, null, null);
		rightText.setText(text);
	}
	public void setRightBtnText(int resid) {
		rightText.setCompoundDrawables(null, null, null, null);
		rightText.setText(resid);
	}
	public void setRightBtnIconAndText(int resId,String text) {
		Drawable rightDrawable = getResources().getDrawable(resId);
		if(rightDrawable!=null){
		rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
		rightText.setCompoundDrawables(null, null, rightDrawable, null);
		}
		rightText.setText(text);
	}
}
