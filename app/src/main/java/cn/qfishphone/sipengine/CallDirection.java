package cn.qfishphone.sipengine;

 public class CallDirection {
	 //打出去
	public static CallDirection Outgoing = new CallDirection("CallOutgoing");
	 //打进来
	public static CallDirection Incoming = new CallDirection("Callincoming");
	private String mStringValue;
	private CallDirection(String aStringValue) {
		mStringValue = aStringValue;
	}
	public String toString() {
		return mStringValue;
	}
}
