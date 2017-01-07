package cn.qfishphone.sipengine;

/**
 * 记录拨打状态
 */
public interface CallReport {
	/**
	 * 谁打电话
	 * @return
     */
	String getFrom();

	/**
	 * 打给谁
	 * @return
     */
	String getTo();

	/**
	 * 拨打方向
	 * @return
     */
	CallDirection getDirection();
	
	int getDuration();

	/**
	 * 呼叫状态
	 * @return
     */
	CallStatus getCallStatus();
	
	String getRecordFile();
	
	String getDateTime();
	
	boolean getCallRecord();
}
