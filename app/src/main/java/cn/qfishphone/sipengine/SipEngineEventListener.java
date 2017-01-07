package cn.qfishphone.sipengine;

public interface SipEngineEventListener {
	/**< 引擎启动事件
	 * @return */
	void OnSipEngineState(int code);
	
	/**< SIP 账号注册事件
	 * @return */
	void OnRegistrationState(int code, int error_code);
			
	/**< 新来电
	 * @return */
	void OnNewCall(int CallDir, String peer_caller, boolean is_video_call);
		
	/**< 呼叫处理中
	 * @return */
	void OnCallProcessing();
	
	/**< 对端震铃
	 * @return */
	void OnCallRinging();
	
	/**< 呼叫接通
	* @return */
	void OnCallConnected();
		
	/**< 媒体接通
	* @return */
	void OnCallStreamsRunning(boolean is_video_call);
	
	/**< 媒体接通 
	typedef enum {
    TRANS_RTP = 0,
    TRANS_ICE
	} MediaTransMode;
	* @return */
	void OnCallMediaStreamConnected(int mode);
		
	
		/*本地保持呼叫*/
		void OnCallPaused();
	
		/*本地呼叫恢复*/
		void OnCallResuming();

		/*远端保持呼叫*/
		void OnCallPausedByRemote();
		
		/*远端恢复呼叫*/
		void OnCallResumingByRemote();
		/*呼叫结束*/
		void OnCallEnded();
		
	/**< 呼叫失败
	 * @return */
	void OnCallFailed(/*CallErrorCode*/ int status);

		/**< 网络延迟，及VOS余额返回值
		 * @return */
		void OnNetworkQuality(int ms, String vos_balance);
		/**< 对方按下dtmf
		 * @return */
		void OnRemoteDtmfClicked(int dtmf);
		/**< 话单反馈
		 * @return */
		void OnCallReport(long nativePtr);
}
