package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;

public class RepeatActivity extends BaseActivity {

	@BindView(R.id.sundayCheckBox)
	CheckBox sundayCheckBox;
	@BindView(R.id.sundayLienarLayout)
	LinearLayout sundayLienarLayout;
	@BindView(R.id.mondayCheckBox)
	CheckBox mondayCheckBox;
	@BindView(R.id.mondayLinearLayout)
	LinearLayout mondayLinearLayout;
	@BindView(R.id.tuesDayCheckBox)
	CheckBox tuesDayCheckBox;
	@BindView(R.id.tuesdayLinearLayout)
	LinearLayout tuesdayLinearLayout;
	@BindView(R.id.wednesdayCheckBox)
	CheckBox wednesdayCheckBox;
	@BindView(R.id.wednesdayLinearLayout)
	LinearLayout wednesdayLinearLayout;
	@BindView(R.id.thursdayCheckBox)
	CheckBox thursdayCheckBox;
	@BindView(R.id.thursdayLinearLayout)
	LinearLayout thursdayLinearLayout;
	@BindView(R.id.fridayCheckBox)
	CheckBox fridayCheckBox;
	@BindView(R.id.fridayLinearLayout)
	LinearLayout fridayLinearLayout;
	@BindView(R.id.saturdayCheckBox)
	CheckBox saturdayCheckBox;
	@BindView(R.id.saturdayLinearLayout)
	LinearLayout saturdayLinearLayout;
	private ArrayList<CheckBox> checkboxs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repeat);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		checkboxs = new ArrayList<>();
		checkboxs.add(mondayCheckBox);
		checkboxs.add(tuesDayCheckBox);
		checkboxs.add(wednesdayCheckBox);
		checkboxs.add(thursdayCheckBox);
		checkboxs.add(fridayCheckBox);
		checkboxs.add(saturdayCheckBox);
		checkboxs.add(sundayCheckBox);
		hasAllViewTitle(R.string.alarm,R.string.save_alarm,R.string.cancel,false);
		daysSet();
	}

	@Override
	protected void onClickRightView() {
		ArrayList<String> repeatDayList = new ArrayList<>();
		int size = checkboxs.size();
		for (int i = 0; i < size; i++) {
			if (checkboxs.get(i).isChecked()) {
				repeatDayList.add(i + "");
			}
		}
		Intent intent = new Intent();
		intent.putStringArrayListExtra("repeatDay", repeatDayList);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void daysSet() {
		ArrayList<String> repeatDayList = getIntent().getStringArrayListExtra("repeatDay");
		if (repeatDayList.size() != 0) {
			for (String iString : repeatDayList) {
				int jday = Integer.parseInt(iString);
				switch (jday) {
					case 0:
						mondayCheckBox.setChecked(true);
						break;
					case 1:
						tuesDayCheckBox.setChecked(true);
						break;
					case 2:
						wednesdayCheckBox.setChecked(true);
						break;
					case 3:
						thursdayCheckBox.setChecked(true);
						break;
					case 4:
						fridayCheckBox.setChecked(true);
						break;
					case 5:
						saturdayCheckBox.setChecked(true);
						break;
					case 6:
						sundayCheckBox.setChecked(true);
						break;
				}
			}
		}
	}

	@OnClick({R.id.sundayLienarLayout, R.id.mondayLinearLayout, R.id.tuesdayLinearLayout, R.id.wednesdayLinearLayout, R.id.thursdayLinearLayout, R.id.fridayLinearLayout, R.id.saturdayLinearLayout})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.sundayLienarLayout:
				sundayCheckBox.setChecked(!sundayCheckBox.isChecked());
				break;
			case R.id.mondayLinearLayout:
				mondayCheckBox.setChecked(!mondayCheckBox.isChecked());
				break;
			case R.id.tuesdayLinearLayout:
				tuesDayCheckBox.setChecked(!tuesDayCheckBox.isChecked());
				break;
			case R.id.wednesdayLinearLayout:
				wednesdayCheckBox.setChecked(!wednesdayCheckBox.isChecked());
				break;
			case R.id.thursdayLinearLayout:
				thursdayCheckBox.setChecked(!thursdayCheckBox.isChecked());
				break;
			case R.id.fridayLinearLayout:
				fridayCheckBox.setChecked(!fridayCheckBox.isChecked());
				break;
			case R.id.saturdayLinearLayout:
				saturdayCheckBox.setChecked(!saturdayCheckBox.isChecked());
				break;
		}
	}
}
