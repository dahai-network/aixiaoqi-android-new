package de.blinkt.openvpn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.blinkt.openvpn.model.PreReadEntity;



/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class DBHelp {
    PreReadSimDataSQLite preReadSimData;
    SQLiteDatabase db;
    public DBHelp(Context context){
        preReadSimData=new PreReadSimDataSQLite(context);
        db=preReadSimData.getWritableDatabase();
    }

    public long insertPreData(PreReadEntity preReadEntity){
        ContentValues contentValues=new ContentValues();
        contentValues.put(PreReadSimDataSQLite.KEY_ICCID,preReadEntity.getIccid());
        contentValues.put(PreReadSimDataSQLite.KEY_IMSSI,preReadEntity.getImsi());
        contentValues.put(PreReadSimDataSQLite.KEY_PRE_READ_DATA,preReadEntity.getPreReadData());
        contentValues.put(PreReadSimDataSQLite.KEY_DATA_LENGTH,preReadEntity.getDataLength());
        long data=   db.insert(PreReadSimDataSQLite.DATABASE_TABLE,null,contentValues);
        close();
        return data;
    }

    public void close()
    {
        preReadSimData.close();
    }

    public PreReadEntity getPreReadEntity(String iccid)
    {

        PreReadEntity preReadEntity=null;

        Cursor mCursor =
                db.query(PreReadSimDataSQLite.DATABASE_TABLE, new String[]{ PreReadSimDataSQLite.KEY_ICCID,
                        PreReadSimDataSQLite.KEY_IMSSI, PreReadSimDataSQLite.KEY_PRE_READ_DATA, PreReadSimDataSQLite.KEY_DATA_LENGTH}, PreReadSimDataSQLite.KEY_ICCID + " = ?" , new String[]{iccid} , null, null, null, null);
        if (mCursor != null&&mCursor.moveToFirst()){
            if(preReadEntity==null){
                preReadEntity=new PreReadEntity();
            }
            preReadEntity.setIccid( mCursor.getString(mCursor.getColumnIndex(PreReadSimDataSQLite.KEY_ICCID)));
            preReadEntity.setImsi( mCursor.getString(mCursor.getColumnIndex(PreReadSimDataSQLite.KEY_IMSSI)));
            preReadEntity.setPreReadData(mCursor.getString(mCursor.getColumnIndex(PreReadSimDataSQLite.KEY_PRE_READ_DATA)));
            preReadEntity.setDataLength( mCursor.getString(mCursor.getColumnIndex(PreReadSimDataSQLite.KEY_DATA_LENGTH)));
        }
        close();
        return  preReadEntity;
    }


}
