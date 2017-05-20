package cn.qfishphone.sipengine;

import android.content.Context;

abstract public class SipEngineFactory {
	private static String factoryName = "cn.qfishphone.sipengine.SipEngineFactoryImpl";
	
	static SipEngineFactory theSipEngineFactory; 
	
	/**
	 * Indicate the name of the class used by this factory
	 * @param pathName
	 */
	public static void setFactoryClassName (String className) {
		factoryName = className;
	}
	
	public static SipEngineFactory instance() {
		try {
		if (theSipEngineFactory == null) {
			Class<?> lFactoryClass = Class.forName(factoryName);
			theSipEngineFactory = (SipEngineFactory) lFactoryClass.newInstance();
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("cannot instanciate factory ["+factoryName+"]");
		}
		return theSipEngineFactory;
	}

	abstract public SipEngineCore createPhoneCore(SipEngineEventListener alistener,Context context);
}
