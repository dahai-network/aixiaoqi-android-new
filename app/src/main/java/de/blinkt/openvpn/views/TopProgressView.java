package de.blinkt.openvpn.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

/**
 * 创建顶部提示进度自定义控件
 */

public class TopProgressView extends View {

	private final Paint mPaint;
	private final Rect mBounds;
	//进入动画
	private final Animation mShowAction;
	//退出动画
	private final Animation mHiddenAction;
	private boolean isWhiteBack;
	private final Context context;

	private static final int DEFAULT_TEXTSIZE = CommonTools.dip2px(ICSOpenVPNApplication.getContext(), 15);

	//进度
	private int progress;
	//内容
	private String content;


	public TopProgressView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		mShowAction = AnimationUtils.loadAnimation(context, R.anim.translucent_in);
		mHiddenAction = AnimationUtils.loadAnimation(context, R.anim.translucent_out);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBounds = new Rect();
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopProgressView, 0, 0);
		isWhiteBack = a.getBoolean(R.styleable.TopProgressView_isWhiteBack, false);
	}

	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}

	public void setContent(String content) {
		this.content = content;
		invalidate();
	}

	public String getContent() {
		return content;
	}

	public void showTopProgressView(String content, int time, OnClickListener listener) {
		setContent(content);
		setVisibility(VISIBLE);
		setOnClickListener(listener);
		if (time > 0) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setVisibility(GONE);
				}
			}, time);
		}

	}

	@Override
	public void setVisibility(int visibility) {
		startAnimation(visibility == View.VISIBLE ? mShowAction : mHiddenAction);
		super.setVisibility(visibility);
	}

	public void setWhiteBack(boolean whiteBack) {
		isWhiteBack = whiteBack;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int backColor = 0;
		int textColor = 0;
		int arrowDrawable = 0;
		int progressColor = 0;
		if (!isWhiteBack) {
			backColor = R.color.color_ffbfbf;
			textColor = R.color.white;
			arrowDrawable = R.drawable.white_arrow;
			progressColor = R.color.color_ffa0a0;
		} else {
			backColor = R.color.transparent_60;
			textColor = R.color.gray_text_666666;
			arrowDrawable = R.drawable.arrow;
			progressColor = R.color.transparent_60;
		}
		//画一个长方形背景
		mPaint.setColor(getResources().getColor(backColor));
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		//画进度
		mPaint.setColor(getResources().getColor(progressColor));
		canvas.drawRect(0, 0, getWidth() * progress / 100f, getHeight(), mPaint);
		//画文字
		mPaint.setTextSize(DEFAULT_TEXTSIZE);
		String text;
		if (content == null) {
			text = "";
		} else {
			text = content;
		}
		mPaint.getTextBounds(text, 0, text.length(), mBounds);
		float textHeight = mBounds.height();
		mPaint.setColor(getResources().getColor(textColor));
		canvas.drawText(text, getDp(35), getHeight() / 2
				+ textHeight / 2 - 5, mPaint);
		//画一个图片}
		Bitmap topBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.top_progress_icon);
		canvas.drawBitmap(topBitmap, getDp(15), getHeight() / 2 - topBitmap.getHeight() / 2, mPaint);
		Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), arrowDrawable);
		canvas.drawBitmap(arrowBitmap, getWidth() - getDp(15), getHeight() / 2 - arrowBitmap.getHeight() / 2, mPaint);
	}

	public int getDp(int dpInt) {
		return CommonTools.dip2px(ICSOpenVPNApplication.getContext(), dpInt);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}


}
