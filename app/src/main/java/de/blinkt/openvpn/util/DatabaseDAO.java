package de.blinkt.openvpn.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

import java.util.Map;

public class DatabaseDAO {
	private SQLiteDatabase db;

	public DatabaseDAO(SQLiteDatabase db) {
		this.db = db;
	}

	/**
	 * 获取指定区号的省份和地区名.
	 */
	public Map<String, String> queryAeraCode(String number) {
		return queryNumber("0", number);
	}

	/**
	 * 获取指定号码的省份和地区名。
	 * <code>select city_id from number_0 limit arg1,arg2.</code>
	 * arg1表示从第几行（行数从零开始）开始，arg2表示查询几行数据.
	 */
	public Map<String, String> queryNumber(String prefix, String center) {
		if (center.isEmpty() || !isTableExists("Number_" + prefix))
			return new HashMap<>();

//		int num = Integer.parseInt(center) - 1;
//		String sql1 = "select city_id from number_" + prefix + " limit " + num + ",1";//空格不能少
//		String sql2 = "select province_id from city where _id = (" + sql1 + ")";
//		String sql="select a.name as provinceName,b.name as cityName from Number_130 inner join Province as a on Number_130.province_id=a.id inner join City as b on Number_130.city_id=b.id where number=\"0000\";";
		String sql = "select Province.name as provinceName,City.name as cityName from Number_"+prefix+ " inner join Province  on Number_"+prefix+  ".province_id=Province.id inner join City on Number_"+prefix+".city_id=City.id where number='"+center+"'";
//	String sql="select name  from sqlite_master where type='table'";
//		String sql ="select * from Number_177 where id=1";
			return getCursorResult(sql);
	}






	/**
	 * 查询城市区号。
	 */
	public Map<String, String> queryCity(String city) {
		if (city.isEmpty())
			return null;
		String sql1 = "select _id from city where city ='" + city + "'";
		String sql = "select rowid as rownumber from number_0 where city_id =(" + sql1 + ")";
		return getCursorResult(sql);
	}

	/**
	 * 查询国家的代号。
	 */
	public Map<String, String> queryCountry(String country) {
		if (country.isEmpty())
			return null;
		String sql1 = "select _id from country where country  ='" + country + "'";
		String sql = "select rowid as rownumber from number_00 where country_id =(" + sql1 + ")";
		return getCursorResult(sql);
	}

	/**
	 * 返回查询结果集
	 */
	private synchronized Map<String, String> getCursorResult(String sql) {
		Cursor cursor = getCursor(sql);
		int col_len = cursor.getColumnCount();
		Map<String, String> map = new HashMap<>();
		try {
			while (cursor.moveToNext()) {
				for (int i = 0; i < col_len; i++) {
					String columnName = cursor.getColumnName(i);
					String columnValue = cursor.getString(cursor.getColumnIndex(columnName));
					Log.e("DatabaseDAO",columnName+"\n"+columnValue);
					if (columnValue == null)
						columnValue = "";
					map.put(columnName, columnValue);
				}
			}
		} finally {
			cursor.close();
		}
		return map;
	}

	private Cursor getCursor(String sql) {
		return db.rawQuery(sql, null);
	}

	/**
	 * 判断指定的表是否存在。
	 */
	public boolean isTableExists(String tableName) {
		boolean result = false;
		if (tableName == null)
			return false;
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type='table' and " +
					"name = '" + tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0)
					result = true;
			}
		} catch (Exception e) {

		}finally {
			if(cursor!=null)
			cursor.close();
		}
		return result;
	}

	/**
	 * 关闭数据库。
	 */
	public void closeDB() {
		if (db != null) {
			db.close();
			db = null;
		}
	}
}