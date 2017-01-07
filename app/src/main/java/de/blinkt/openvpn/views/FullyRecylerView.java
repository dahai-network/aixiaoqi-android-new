package de.blinkt.openvpn.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/9/1.
 */
public class FullyRecylerView extends RecyclerView {

	public FullyRecylerView(Context context) {
		super(context);
	}

	public FullyRecylerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FullyRecylerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthSpec, expandSpec);
	}
}
