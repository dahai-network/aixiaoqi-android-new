package de.blinkt.openvpn.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * SharedPreferences写入工具
 *
 * @author Andy_Wang
 */
public class SharedUtils {

	private Context context;

	private final String subTAG = getClass().getSimpleName();

	private SharedPreferences mSharedPreferences;
	private final String DATA = "data";
	private static SharedUtils mSharedUtils;

	public static SharedUtils getInstance() {
		if (mSharedUtils == null) {
			synchronized (SharedUtils.class) {
				if(mSharedUtils==null){
					mSharedUtils = new SharedUtils();
				}
			}
		}
		return mSharedUtils;
	}

	private SharedUtils() {
		context = ICSOpenVPNApplication.getInstance();
		mSharedPreferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
	}

	/**
	 * 清除shared
	 */
	public void clear() {
		Editor editor = mSharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	public void writeString(String key, String value) {
		if (mSharedPreferences != null) {
			Editor editor = mSharedPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	public void writeInt(String key, int value) {
		if (mSharedPreferences != null) {
			Editor editor = mSharedPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	public void writeLong(String key, long value) {
		if (mSharedPreferences != null) {
			Editor editor = mSharedPreferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}

	public void writeBoolean(String key, Boolean value) {
		if (mSharedPreferences != null) {
			Editor editor = mSharedPreferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
		}
	}

	public void delete(String key) {
		try {
			if (mSharedPreferences != null) {
				Editor editor = mSharedPreferences.edit();
				editor.remove(key);
				editor.commit();
			}
		} catch (Exception e) {
			Log.e(subTAG, e.getMessage());
		}
	}

	public String readString(String key) {
		if (mSharedPreferences == null || !mSharedPreferences.contains(key)) {
			return "";
		}
		return mSharedPreferences.getString(key, "");
	}

	public String readString(String key,String value) {
		if (mSharedPreferences == null || !mSharedPreferences.contains(key)) {
			return null;
		}
		return mSharedPreferences.getString(key, value);
	}
	public Integer readInt(String key) {
		if (mSharedPreferences == null || !mSharedPreferences.contains(key)) {
			return 0;
		}
		return mSharedPreferences.getInt(key, 0);
	}

	public Long readLong(String key) {
		if (mSharedPreferences == null || !mSharedPreferences.contains(key)) {
			return 0L;
		}
		return mSharedPreferences.getLong(key, 0L);
	}


	public Boolean readBoolean(String key) {
		return !(mSharedPreferences == null || !mSharedPreferences.contains(key))&&mSharedPreferences.getBoolean(key, false);
	}
	public Boolean readBoolean(String key,boolean flag) {
		return !(mSharedPreferences == null || !mSharedPreferences.contains(key))&&mSharedPreferences.getBoolean(key, flag);
	}

}