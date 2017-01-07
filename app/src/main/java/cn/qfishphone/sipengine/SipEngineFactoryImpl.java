package cn.qfishphone.sipengine;

import android.content.Context;
import android.util.Log;

public class SipEngineFactoryImpl extends SipEngineFactory {
	private native static boolean NativeInit();

	private SipEngineCore sipEngineCore;

	static {
		Log.d("*51DYT*", "Loading 51dyt_client-jni...");
		System.loadLibrary("51dyt_client-jni");
	}

	@Override
	public SipEngineCore createPhoneCore(SipEngineEventListener alistener, Context context) {
		return new SipEngineCoreImpl(alistener, context);
	}

}
