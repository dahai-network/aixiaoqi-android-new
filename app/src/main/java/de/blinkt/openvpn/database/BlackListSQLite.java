package de.blinkt.openvpn.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class BlackListSQLite extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="aixiaoqi_blacklist";
    private static final int  BLACK_LIST_DATA_VERSION=3;
    public   static final String UESR_PHONE = "user_phone";
    static final String BLACK_LIST_PHONE = "black_list_phone";
    static final String BLACK_LIST_DATA_TABLE = "blacklist";
    private  static final String BLACK_LIST_DATA_CREATE =
            "create table "+BLACK_LIST_DATA_TABLE+"( _id integer primary key autoincrement, " +
                    UESR_PHONE+"  text  not null , "+
                    BLACK_LIST_PHONE+"  text  not null);";
    public BlackListSQLite(Context context)
    {
        super(context, DATABASE_NAME, null, BLACK_LIST_DATA_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BLACK_LIST_DATA_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+BLACK_LIST_DATA_TABLE);
        onCreate(db);
    }
}
