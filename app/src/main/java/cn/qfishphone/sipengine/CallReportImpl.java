package cn.qfishphone.sipengine;


public class CallReportImpl implements CallReport {

	protected final long nativePtr;
	private native String getFrom(long nativePtr);
	private native String getTo(long nativePtr);
	private native boolean isIncoming(long nativePtr);
	private native int getDuration(long nativePtr);
	private native int getCallStatus(long nativePtr);
	private native  String getRecordFile(long nativePtr);
	private native  String getDateTime(long nativePtr);
	private native  boolean getCallRecord(long nativePtr);
	
	public CallReportImpl(long aNativePtr)  {
		nativePtr = aNativePtr;
	}
	
	public CallDirection getDirection() {
		return isIncoming(nativePtr)?CallDirection.Incoming:CallDirection.Outgoing;
	}

	public CallStatus getCallStatus() {
		return CallStatus.fromInt(getCallStatus(nativePtr));
	}
	
	public String getFrom() {
		return  getFrom(nativePtr);
	}

	public String getTo() {
		return  getTo(nativePtr);
	}

	public int getDuration()
	 {
		return getDuration(nativePtr);
	}

	public String getRecordFile() {
		return getRecordFile(nativePtr);
	}
	
	public String getDateTime(){
		return getDateTime(nativePtr);
	}
	@Override
	public boolean getCallRecord() {
		return getCallRecord(nativePtr);
	}
}
