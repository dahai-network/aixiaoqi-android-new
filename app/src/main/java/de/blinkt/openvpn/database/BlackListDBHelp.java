package de.blinkt.openvpn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.BlackListEntity;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class BlackListDBHelp {

    BlackListSQLite blackListSQLite;
    SQLiteDatabase db;
    public BlackListDBHelp(Context context){
        blackListSQLite=new BlackListSQLite(context);
        db=blackListSQLite.getWritableDatabase();
    }


    public long  insertOneDefriend(String blackListPhone){
        ContentValues contentValues=new ContentValues();
        contentValues.put(BlackListSQLite.UESR_PHONE, SharedUtils.getInstance().readString(Constant.USER_NAME));
        contentValues.put(BlackListSQLite.BLACK_LIST_PHONE,blackListPhone);
        long data=   db.insert(BlackListSQLite.BLACK_LIST_DATA_TABLE,null,contentValues);

        return data;
    }
    public void deleteDefriend(String blackListPhone){
        db.delete(BlackListSQLite.BLACK_LIST_DATA_TABLE,BlackListSQLite.UESR_PHONE + " = "+SharedUtils.getInstance().readString(Constant.USER_NAME)+" and " +BlackListSQLite.BLACK_LIST_PHONE+" = ? " , new String[]{blackListPhone});
    }
    public void deleteAllDefriend(){
        db.delete(BlackListSQLite.BLACK_LIST_DATA_TABLE,null , null);
    }

    public void insertDefriendList(List<BlackListEntity> defriendList){
        db.beginTransaction();
        try{
            for(int i=0;i<defriendList.size();i++)
                insertOneDefriend(defriendList.get(i).getBlackNum());
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
            close();
        }
    }

    public void close()
    {
        blackListSQLite.close();
    }

    public boolean isBlackList(String blackListPhone)
    {

        boolean isBlackList=false;

        Cursor mCursor =
                db.query(BlackListSQLite.BLACK_LIST_DATA_TABLE, new String[]{ BlackListSQLite.BLACK_LIST_PHONE}, BlackListSQLite.UESR_PHONE + " = "+SharedUtils.getInstance().readString(Constant.USER_NAME)+" and " +BlackListSQLite.BLACK_LIST_PHONE+" = ? " , new String[]{blackListPhone} , null, null, null, null);
        if (mCursor != null&&mCursor.moveToFirst()){
            isBlackList=true;
        }

        return  isBlackList;
    }
}
