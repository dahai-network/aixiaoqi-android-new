 package cn.qfishphone.sipengine;

import java.util.Vector;

 public class CallStatus {
	 	static private Vector<CallStatus> values = new Vector<>();
	 //呼叫成功
		public static CallStatus CallSuccess = new CallStatus(0,"CallSuccess");
	 //呼叫受阻
		public static CallStatus CallAborted = new CallStatus(1,"CallAborted");

		public static CallStatus CallMissed = new CallStatus(2,"CallMissed");
	 //占线
		public static CallStatus CallDeclined = new CallStatus(3,"CallDeclined");

		private String mStringValue;
		private int mValue;
		private CallStatus(int value, String aStringValue) {
			mValue = value;
			mStringValue = aStringValue;
			values.addElement(this);
		}
		public String toString() {
			return mStringValue;
		}
		
		public static CallStatus fromInt(int value) {

			for (int i=0; i<values.size();i++) {
				CallStatus state = values.elementAt(i);
				if (state.mValue == value) return state;
			}
			throw new RuntimeException("state not found ["+value+"]");
		}
	}