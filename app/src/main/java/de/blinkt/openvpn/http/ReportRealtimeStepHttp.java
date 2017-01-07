package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/6.
 */

public class ReportRealtimeStepHttp extends BaseHttp {


	private int StepNum;
	private long StepTime;

	public ReportRealtimeStepHttp(InterfaceCallback call, int cmdType_, int StepNum, long StepTime) {
		super(call, cmdType_);
		this.StepNum = StepNum;
		this.StepTime = StepTime;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.SPORT_REPORT_HISTORY_STEP;

		params.put("StepNum", URLEncoder.encode(StepNum + "", "utf-8"));
		params.put("StepTime", URLEncoder.encode(StepTime + "", "utf-8"));
	}


}
