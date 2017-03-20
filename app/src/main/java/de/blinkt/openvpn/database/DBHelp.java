package de.blinkt.openvpn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.blinkt.openvpn.model.PreReadEntity;



/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class DBHelp {
    PreReadSimData preReadSimData;
    SQLiteDatabase db;
    public DBHelp(Context context){
        preReadSimData=new PreReadSimData(context);
        db=preReadSimData.getWritableDatabase();
    }

    public long insertPreData(PreReadEntity preReadEntity){
        ContentValues contentValues=new ContentValues();
        contentValues.put(PreReadSimData.KEY_ICCID,preReadEntity.getIccid());
        contentValues.put(PreReadSimData.KEY_IMSSI,preReadEntity.getImsi());
        contentValues.put(PreReadSimData.KEY_PRE_READ_DATA,preReadEntity.getPreReadData());
        contentValues.put(PreReadSimData.KEY_DATA_LENGTH,preReadEntity.getDataLength());
        long data=   db.insert(PreReadSimData.DATABASE_TABLE,null,contentValues);
        return data;
    }

    public void close()
    {
        preReadSimData.close();
    }

    public PreReadEntity getPreReadEntity(String iccid)
    {
        Log.e("preDataSplit","iccid="+iccid);
        PreReadEntity preReadEntity=null;

        Cursor mCursor =
                db.query(PreReadSimData.DATABASE_TABLE, new String[]{ PreReadSimData.KEY_ICCID,
                        PreReadSimData.KEY_IMSSI, PreReadSimData.KEY_PRE_READ_DATA,PreReadSimData.KEY_DATA_LENGTH}, PreReadSimData.KEY_ICCID + " = ?" , new String[]{iccid} , null, null, null, null);
        Log.e("preDataSplit","mCursor="+(mCursor==null)+"mCursor.moveToFirst()"+mCursor.moveToFirst());
        if (mCursor != null&&mCursor.moveToFirst()){
            if(preReadEntity==null){
                preReadEntity=new PreReadEntity();
            }
            preReadEntity.setIccid( mCursor.getString(mCursor.getColumnIndex(PreReadSimData.KEY_ICCID)));
            preReadEntity.setImsi( mCursor.getString(mCursor.getColumnIndex(PreReadSimData.KEY_IMSSI)));
            preReadEntity.setPreReadData(mCursor.getString(mCursor.getColumnIndex(PreReadSimData.KEY_PRE_READ_DATA)));
            preReadEntity.setDataLength( mCursor.getString(mCursor.getColumnIndex(PreReadSimData.KEY_DATA_LENGTH)));
        }
        if(preReadEntity!=null)
        Log.e("preDataSplit","preReadEntity="+preReadEntity.toString());
        else{
            Log.e("preDataSplit","preReadEntity="+(preReadEntity==null));
        }
        return  preReadEntity;
    }


}
