package de.blinkt.openvpn.views;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.CommonTools;


/**
 * Created by Administrator on 2016/10/7.
 */

public class PointProgressBar extends LinearLayout {
	private ImageView seekImageView1;
	private ImageView seekImageView2;
	private ImageView seekImageView3;
	private ImageView seekImageView4;
	private ImageView seekImageView5;
	private int[] images = {R.drawable.seeking1, R.drawable.seeking2, R.drawable.seeking3, R.drawable.seeking4, R.drawable.seeking5};
	private int i = Integer.MAX_VALUE-5;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			seekImageView1.setBackgroundResource(images[(i) % 5]);
			seekImageView2.setBackgroundResource(images[((i + 1) % 5)]);
			seekImageView3.setBackgroundResource(images[((i + 2) % 5)]);
			seekImageView4.setBackgroundResource(images[((i + 3) % 5)]);
			seekImageView5.setBackgroundResource(images[((i + 4) % 5)]);
			i--;
			if (i == 0) {
				i = Integer.MAX_VALUE-5;
			}
			invalidate();
		}
	};

	public PointProgressBar(Context context) {
		super(context);

	}

	public PointProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		View layout = LayoutInflater.from(context).inflate(R.layout.progress_point, this, false);
		seekImageView1 = (ImageView) layout.findViewById(R.id.seekImageView1);
		seekImageView2 = (ImageView) layout.findViewById(R.id.seekImageView2);
		seekImageView3 = (ImageView) layout.findViewById(R.id.seekImageView3);
		seekImageView4 = (ImageView) layout.findViewById(R.id.seekImageView4);
		seekImageView5 = (ImageView) layout.findViewById(R.id.seekImageView5);
		addView(layout);
	}

	public PointProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		new Thread(new Runnable() {
			@Override
			public void run() {
				CommonTools.delayTime(100);
				handler.sendEmptyMessage(0);
			}
		}).start();


	}
}
