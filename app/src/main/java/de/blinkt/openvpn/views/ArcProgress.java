package de.blinkt.openvpn.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by bruce on 11/6/14.
 */
public class ArcProgress extends View {
	private Paint paint;
	protected Paint textPaint;
	Paint linePaint;
	private RectF rectF = new RectF();

	private float strokeWidth;
	private float suffixTextSize;
	private float bottomTextSize;
	private String bottomText;
	private float textSize;
	private int textColor;
	private int progress = 0;
	private int step = 0;
	private int max;
	private float arcAngle;
	private String suffixText = "%";
	private float suffixTextPadding;

	private float arcBottomHeight;

	private final int default_finished_color = Color.rgb(255, 255, 255);

	private final int default_unfinished_color = Color.WHITE;
	private final int default_text_color = Color.rgb(1, 208, 192);
	private final float default_suffix_text_size;
	private final float default_suffix_padding;
	private final float default_bottom_text_size;
	private final float default_stroke_width;
	private final String default_suffix_text;
	private final int default_max = 100;

	private final float default_arc_angle = 360 * 2 / 3f;
	private float default_text_size;
	private final int min_size;
	private String default_text = "当日步数";
	private static final String INSTANCE_STATE = "saved_instance";
	private static final String INSTANCE_STROKE_WIDTH = "stroke_width";
	private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
	private static final String INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding";
	private static final String INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size";
	private static final String INSTANCE_BOTTOM_TEXT = "bottom_text";
	private static final String INSTANCE_TEXT_SIZE = "text_size";
	private static final String INSTANCE_TEXT_COLOR = "text_color";
	private static final String INSTANCE_PROGRESS = "progress";
	private static final String INSTANCE_MAX = "max";
	private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
	private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
	private static final String INSTANCE_ARC_ANGLE = "arc_angle";
	private static final String INSTANCE_SUFFIX = "suffix";
	private static final int[] SECTION_COLORS = {0xff01d0c0, 0xffff961a, 0xffed3333};
	//是否画控件
	private boolean isDraw = true;

	public ArcProgress(Context context) {
		this(context, null);
	}

	public ArcProgress(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		default_text_size = CommonTools.sp2px(getResources(), 18);
		min_size = (int) CommonTools.dp2px(getResources(), 100);
		default_text_size = CommonTools.sp2px(getResources(), 40);
		default_suffix_text_size = CommonTools.sp2px(getResources(), 15);
		default_suffix_padding = CommonTools.dp2px(getResources(), 4);
		default_suffix_text = "%";
		default_bottom_text_size = CommonTools.sp2px(getResources(), 10);
		default_stroke_width = CommonTools.dp2px(getResources(), 20);

		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0);
		initByAttributes(attributes);
		attributes.recycle();

		initPainters();
	}

	protected void initByAttributes(TypedArray attributes) {
		textColor = attributes.getColor(R.styleable.ArcProgress_arc_text_color, default_text_color);
		textSize = attributes.getDimension(R.styleable.ArcProgress_arc_text_size, default_text_size);
		arcAngle = attributes.getFloat(R.styleable.ArcProgress_arc_angle, default_arc_angle);
		setMax(attributes.getInt(R.styleable.ArcProgress_arc_max, default_max));
		setProgress(attributes.getInt(R.styleable.ArcProgress_arc_progress, 0));
		strokeWidth = attributes.getDimension(R.styleable.ArcProgress_arc_stroke_width, default_stroke_width);
		suffixTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_size, default_suffix_text_size);
		suffixText = TextUtils.isEmpty(attributes.getString(R.styleable.ArcProgress_arc_suffix_text)) ? default_suffix_text : attributes.getString(R.styleable.ArcProgress_arc_suffix_text);
		suffixTextPadding = attributes.getDimension(R.styleable.ArcProgress_arc_suffix_text_padding, default_suffix_padding);
		bottomTextSize = attributes.getDimension(R.styleable.ArcProgress_arc_bottom_text_size, default_bottom_text_size);
		bottomText = attributes.getString(R.styleable.ArcProgress_arc_bottom_text);
	}

	protected void initPainters() {
		textPaint = new TextPaint();
		textPaint.setColor(textColor);
		textPaint.setTextSize(textSize);
		textPaint.setAntiAlias(true);

		paint = new Paint();
		paint.setColor(default_unfinished_color);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE);

		linePaint = new Paint();
		linePaint.setColor(Color.RED);
		linePaint.setStrokeWidth(5);
	}

	@Override
	public void invalidate() {
		initPainters();
		super.invalidate();
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		this.invalidate();
	}

	public float getSuffixTextSize() {
		return suffixTextSize;
	}

	public void setSuffixTextSize(float suffixTextSize) {
		this.suffixTextSize = suffixTextSize;
		this.invalidate();
	}

	public String getBottomText() {
		return bottomText;
	}

	public void setBottomText(String bottomText) {
		this.bottomText = bottomText;
		this.invalidate();
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		if (this.progress > getMax()) {
			this.progress %= getMax();
		}
		invalidate();
	}

	public void setStep(int progress, int step) {
		this.progress = progress;
		if (this.progress > getMax()) {
			this.progress %= getMax();
		}
		this.step = step;
		invalidate();
	}

	public int getStep() {
		return step;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		if (max > 0) {
			this.max = max;
			invalidate();
		}
	}

	public float getBottomTextSize() {
		return bottomTextSize;
	}

	public void setBottomTextSize(float bottomTextSize) {
		this.bottomTextSize = bottomTextSize;
		this.invalidate();
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
		this.invalidate();
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
		this.invalidate();
	}


	public float getArcAngle() {
		return arcAngle;
	}

	public void setArcAngle(float arcAngle) {
		this.arcAngle = arcAngle;
		this.invalidate();
	}

	public String getSuffixText() {
		return suffixText;
	}

	public void setSuffixText(String suffixText) {
		this.suffixText = suffixText;
		this.invalidate();
	}

	public float getSuffixTextPadding() {
		return suffixTextPadding;
	}

	public void setSuffixTextPadding(float suffixTextPadding) {
		this.suffixTextPadding = suffixTextPadding;
		this.invalidate();
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		isDraw = visibility != View.GONE;
	}

	@Override
	protected int getSuggestedMinimumHeight() {
		return min_size;
	}

	@Override
	protected int getSuggestedMinimumWidth() {
		return min_size;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);

		rectF.set(strokeWidth / 2f, strokeWidth / 2f, width - strokeWidth / 2f, MeasureSpec.getSize(heightMeasureSpec) - strokeWidth / 2f);
		float radius = width / 2f;
		circleRadius = radius;
		float angle = (360 - arcAngle) / 2f;
		arcBottomHeight = radius * (float) (1 - Math.cos(angle / 180 * Math.PI));
	}

	private float circleRadius;

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isDraw) {
			return;
		}
		super.onDraw(canvas);
		invalidate();
		float startAngle = 300;
		float finishedSweepAngle = -progress / (float) getMax() * arcAngle;
		for (float i = startAngle; i > 60; i -= 4.32) {
			double rad = i * Math.PI / 180;
			float startX = (float) (getHeight() / 2 + 0.7 * (circleRadius - strokeWidth) * Math.sin(rad));
			float startY = (float) (getHeight() / 2.8 + 0.7 * (circleRadius - strokeWidth) * Math.cos(rad));
			float stopX = (float) (getHeight() / 2 + 0.7 * circleRadius * Math.sin(rad));
			float stopY = (float) (getHeight() / 2.8 + 0.7 * circleRadius * Math.cos(rad));
			linePaint.setColor(Color.parseColor("#ababab"));
			canvas.drawLine(startX, startY, stopX, stopY, linePaint);
		}

		for (float i = startAngle; i > startAngle + finishedSweepAngle; i -= 4.32) {
			double rad = i * Math.PI / 180;
			float startX = (float) (getHeight() / 2 + 0.7 * (circleRadius - strokeWidth) * Math.sin(rad));
			float startY = (float) (getHeight() / 2.8 + 0.7 * (circleRadius - strokeWidth) * Math.cos(rad));
			float stopX = (float) (getHeight() / 2 + 0.7 * circleRadius * Math.sin(rad));
			float stopY = (float) (getHeight() / 2.8 + 0.7 * circleRadius * Math.cos(rad));

			float section = (startAngle - i) / default_arc_angle;
			if (section <= 1.0f / 3.0f) {
				linePaint.setColor(SECTION_COLORS[0]);
			} else {
				int count = (section <= 1.0f / 3.0f * 2.0) ? 2 : 3;
				int[] colors = new int[count];
				System.arraycopy(SECTION_COLORS, 0, colors, 0, count);
				float[] positions = new float[count];
				if (count == 2) {
					positions[0] = 0.0f;
					positions[1] = 1.0f - positions[0];
				} else {
					positions[0] = 0.0f;
					positions[1] = (default_arc_angle / 3) / (startAngle - i);
					positions[2] = 1.0f - positions[0] * 2;
				}
				positions[positions.length - 1] = 1.0f;
				LinearGradient shader = new LinearGradient(3,
						3, (getWidth() - 3)
						* section, (float) (getHeight() / 1.4) - 3, colors, positions,
						Shader.TileMode.MIRROR);
				linePaint.setShader(shader);
			}
			canvas.drawLine(startX, startY, stopX, stopY, linePaint);
		}
		String text = String.valueOf(getStep());
		if (!TextUtils.isEmpty(text)) {
			textPaint.setColor(Color.parseColor("#ffffff"));
			textPaint.setTextSize(textSize / 2);
			float tipHeight = textPaint.descent() + textPaint.ascent();
			float textBaseline1 = (getHeight() - tipHeight) / 3.2f;
			canvas.drawText(default_text, (getWidth() - textPaint.measureText(default_text)) / 2.0f, textBaseline1 + tipHeight, textPaint);
			textPaint.setColor(textColor);
			textPaint.setTextSize(textSize);
			float textHeight = textPaint.descent() + textPaint.ascent();
			float textBaseline = (getHeight() - textHeight) / 2.7f;
			canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, textBaseline, textPaint);
		}

	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putFloat(INSTANCE_STROKE_WIDTH, getStrokeWidth());
		bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());
		bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, getSuffixTextPadding());
		bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, getBottomTextSize());
		bundle.putString(INSTANCE_BOTTOM_TEXT, getBottomText());
		bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
		bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
		bundle.putInt(INSTANCE_PROGRESS, getProgress());
		bundle.putInt(INSTANCE_MAX, getMax());
		bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
		bundle.putString(INSTANCE_SUFFIX, getSuffixText());
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			strokeWidth = bundle.getFloat(INSTANCE_STROKE_WIDTH);
			suffixTextSize = bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE);
			suffixTextPadding = bundle.getFloat(INSTANCE_SUFFIX_TEXT_PADDING);
			bottomTextSize = bundle.getFloat(INSTANCE_BOTTOM_TEXT_SIZE);
			bottomText = bundle.getString(INSTANCE_BOTTOM_TEXT);
			textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
			textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
			setMax(bundle.getInt(INSTANCE_MAX));
			setProgress(bundle.getInt(INSTANCE_PROGRESS));
			suffixText = bundle.getString(INSTANCE_SUFFIX);
			initPainters();
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
			return;
		}
		super.onRestoreInstanceState(state);
	}
}
