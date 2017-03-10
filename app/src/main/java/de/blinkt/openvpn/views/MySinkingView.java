package de.blinkt.openvpn.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Region.Op;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

/**
 * 水波浪球形进度View
 * <p>
 * import static android.R.attr.author;
 *
 * @author caizhiming
 */
public class MySinkingView extends FrameLayout {
	private static final int DEFAULT_TEXTCOLOT = 0xFFFFFFFF;

	private static final int DEFAULT_TEXTSIZE = CommonTools.dip2px(ICSOpenVPNApplication.getContext(),14);

	private float mPercent;

	private Paint mPaint = new Paint();

	private Bitmap mBitmap;

	private Bitmap mScaledBitmap;

	private float mLeft;

	private int mSpeed = 5;

	private int mRepeatCount = 0;

	private Status mFlag = Status.NONE;

	private int mTextColor = DEFAULT_TEXTCOLOT;

	private int mTextSize = DEFAULT_TEXTSIZE;

	public MySinkingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTextColor(int color) {
		mTextColor = color;
	}

	public void setTextSize(int size) {
		mTextSize = size;
	}

	public void setPercent(float percent) {
		mFlag = Status.RUNNING;
		mPercent = percent;
		postInvalidate();
	}

	public float getmPercent()
	{
		return mPercent;
	}

	public void setStatus(Status status) {
		mFlag = status;
	}

//	public void clear() {
//		mFlag = Status.NONE;
//		if (mScaledBitmap != null) {
//			mScaledBitmap.recycle();
//			mScaledBitmap = null;
//		}
//
//		if (mBitmap != null) {
//			mBitmap.recycle();
//			mBitmap = null;
//		}
//	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		int width = getWidth();
		int height = getHeight();

		//裁剪成圆区域
		Path path = new Path();
		canvas.save();
		path.reset();
		canvas.clipPath(path);
		path.addCircle(width / 2, height / 2, width / 2, Direction.CCW);
		canvas.clipPath(path, Op.REPLACE);

		if (mFlag == Status.RUNNING) {
			if (mScaledBitmap == null) {
				mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wave2);
				mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, width, getHeight(), false);
				mBitmap.recycle();
				mBitmap = null;
				mRepeatCount = (int) Math.ceil(width / mScaledBitmap.getWidth() + 0.5) + 1;
			}
			for (int idx = 0; idx < mRepeatCount; idx++) {
				canvas.drawBitmap(mScaledBitmap, mLeft + (idx - 1) * mScaledBitmap.getWidth(), (1 - mPercent) * getHeight(), null);
			}
			String str = (int) (mPercent * 100) + "%";
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(mTextColor);
			textPaint.setTextSize(mTextSize);
			textPaint.setStyle(Style.FILL);
			canvas.drawText(str, (getWidth() - textPaint.measureText(str)) / 2, getHeight() / 2, textPaint);
			String stronly = "剩余电量";
			TextPaint onlyPaint = new TextPaint();
			onlyPaint.setColor(mTextColor);
			onlyPaint.setTextSize(mTextSize);
			onlyPaint.setStyle(Style.FILL);
			canvas.drawText(stronly, (getWidth() - onlyPaint.measureText(stronly)) / 2, (getHeight() + textPaint.measureText(str)+20) / 2, onlyPaint);

			mLeft += mSpeed;
			if (mLeft >= mScaledBitmap.getWidth())
				mLeft = 0;
			// 绘制外圆环
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(CommonTools.dip2px(ICSOpenVPNApplication.getContext(),15));
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.rgb(94, 94, 94));
			canvas.drawCircle(width / 2, height / 2, width / 2 - 2, mPaint);

			postInvalidateDelayed(20);
		}
		canvas.restore();

	}

	public enum Status {
		RUNNING, NONE
	}

}
