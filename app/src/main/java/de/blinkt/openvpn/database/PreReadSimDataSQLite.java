package de.blinkt.openvpn.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class PreReadSimDataSQLite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="aixiaoqi_predata";
    private static final int  DATABASE_VERSION=3;
  public   static final String KEY_ICCID = "iccid";
    static final String KEY_IMSSI = "imssi";
    static final String KEY_PRE_READ_DATA = "prereaddata";
    static final String KEY_DATA_LENGTH = "datalength";
    static final String DATABASE_TABLE = "predata";
    private  static final String DATABASE_CREATE =
            "create table "+DATABASE_TABLE+"( _id integer primary key autoincrement, " +
                    KEY_ICCID+"  text UNIQUE not null , "+
                    KEY_IMSSI+"  text not null,"+KEY_PRE_READ_DATA+"  text not null,"+KEY_DATA_LENGTH+"  text nut null);";
 public PreReadSimDataSQLite(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        onCreate(db);
    }
}
