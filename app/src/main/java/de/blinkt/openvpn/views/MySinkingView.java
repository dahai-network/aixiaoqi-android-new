package de.blinkt.openvpn.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Region;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
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

	private static final int DEFAULT_TEXTSIZE = CommonTools.dip2px(ICSOpenVPNApplication.getContext(), 16);
	private static final int DEFAULT_PERCENTSIZE = CommonTools.dip2px(ICSOpenVPNApplication.getContext(), 25);

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

	private String stronly;



    public void setShowPercent(boolean showPercent) {
        this.showPercent = showPercent;
    }

    private boolean showPercent=true;

	public MySinkingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	//设置球体内字体显示
	public void setStronly(String stronly) {
		this.stronly = stronly;
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

	public float getmPercent() {
		return mPercent;
	}

	public void setStatus(Status status) {
		mFlag = status;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mFlag == Status.RUNNING) {
			int width = getWidth();
			int height = getHeight();
			int halfWidth = width / 2;
			int halfHeight = height / 2;
			//裁剪成圆区域
			Path path = new Path();
			canvas.save();
			path.reset();
			canvas.clipPath(path);
			path.addCircle(halfWidth, halfHeight, halfWidth, Path.Direction.CCW);
			canvas.clipPath(path, Region.Op.UNION);

			mPaint.setColor(ContextCompat.getColor(ICSOpenVPNApplication.getContext(), R.color.color_a4d9ff));
			mPaint.setStyle(Style.FILL);
			canvas.drawCircle(width / 2, height / 2, width / 2, mPaint);
			if (mScaledBitmap == null) {
				mBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wave2);
				mScaledBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, false);
				mBitmap.recycle();
				mBitmap = null;
				mRepeatCount = (int) Math.ceil(width / mScaledBitmap.getWidth() + 0.5) + 1;
			}
			if(showPercent){
			for (int idx = 0; idx < mRepeatCount; idx++) {
				canvas.drawBitmap(mScaledBitmap, mLeft + (idx - 1) * mScaledBitmap.getWidth(), (1 - mPercent) * getHeight(), null);
				canvas.drawBitmap(mScaledBitmap, mLeft + (idx - 1.16f) * mScaledBitmap.getWidth(), (1 - mPercent) * getHeight(), null);
				canvas.drawBitmap(mScaledBitmap, mLeft + (idx - 1.32f) * mScaledBitmap.getWidth(), (1 - mPercent) * getHeight(), null);
			}
			String str = (int) (mPercent * 100) + "%";
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(mTextColor);
			textPaint.setTextSize(DEFAULT_PERCENTSIZE);
			textPaint.setStyle(Style.FILL);
            canvas.drawText(str, (width - textPaint.measureText(str)) / 2, (height + textPaint.measureText(str)) / 2, textPaint);
			}
			if (TextUtils.isEmpty(stronly)){
				stronly = ICSOpenVPNApplication.getContext().getString(R.string.only_power);
			}
			TextPaint onlyPaint = new TextPaint();
			onlyPaint.setColor(mTextColor);
			onlyPaint.setTextSize(mTextSize);
			onlyPaint.setStyle(Style.FILL);
			canvas.drawText(stronly, (width - onlyPaint.measureText(stronly)) / 2,
					halfHeight - CommonTools.dip2px(ICSOpenVPNApplication.getContext(), 5), onlyPaint);

			mLeft += mSpeed;
			if (mLeft >= mScaledBitmap.getWidth())
				mLeft = 0;
			// 绘制外圆环
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(25);
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.WHITE);
			canvas.drawCircle(halfWidth, halfHeight, halfWidth - 2, mPaint);
			postInvalidateDelayed(20);
		}
		canvas.restore();
	}

	public enum Status {
		RUNNING, NONE
	}

	public void clear() {
		if (mBitmap != null) {
			mBitmap.recycle();
		}
		if (mScaledBitmap != null) {
			mScaledBitmap.recycle();
		}
		mBitmap = null;
		mScaledBitmap = null;
	}
}
