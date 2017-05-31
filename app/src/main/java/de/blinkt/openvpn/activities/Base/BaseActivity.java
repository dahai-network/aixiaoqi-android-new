/*
 * Copyright (c) 2012-2015 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.activities.Base;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.views.TitleBar;

public class BaseActivity extends CommenActivity  {

    protected ActionBar actionBar;
    protected TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    private void initActionBar() {
        if (actionBar == null)
            actionBar = getActionBar();

        if (titleBar == null)
            titleBar = new TitleBar(this);
        if (actionBar != null) {
            actionBar.setCustomView(titleBar);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
    }

	/**
	 * @param id         表示标题id
	 * @param leftTextId 当leftTextId<0时，左边的标签是默认的返回键，大于零时，表示自定义的值
	 */
	protected void hasLeftViewTitle(int id, int leftTextId) {
		titleBar.setTextTitle(getString(id));
		hasOnlyLeftViewOption();
		if (leftTextId <= 0) {
			titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
		} else {
			titleBar.setLeftBtnText(getString(leftTextId));
		}
	}

	protected void leftViewControl() {
		onBackPressed();
	}

	protected void hasOnlyLeftViewOption() {
		titleBar.getLeftText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					leftViewControl();
				} catch (Exception e) {
					e.printStackTrace();
                }

            }
        });
    }


	/**
	 * @param titleId       标题id
	 * @param rightTextId   右边文字或者图片的id
	 * @param leftTextId    如果leftTextId>0,则显示文字，否则显示默认的图片
	 * @param isRegihtImage 右边是否是图片
	 */
	protected void hasAllViewTitle(int titleId, int rightTextId, int leftTextId, boolean isRegihtImage) {
		titleBar.setTextTitle(getString(titleId));
		if (leftTextId <= 0) {
			titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
		} else {
			titleBar.setLeftBtnText(getString(leftTextId));
		}
		if (isRegihtImage) {
			titleBar.setRightBtnIcon(rightTextId);
		} else {
			titleBar.setRightBtnText(getString(rightTextId));
		}
		hasAllViewTitleOption();
	}

	private void hasAllViewTitleOption() {
		hasOnlyLeftViewOption();
		titleBar.getRightText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickRightView();
			}
		});
	}

	protected void onClickRightView() {

	}

	@Override
	protected void onDestroy() {
		CommonHttp.cancel();
		super.onDestroy();
	}
}
