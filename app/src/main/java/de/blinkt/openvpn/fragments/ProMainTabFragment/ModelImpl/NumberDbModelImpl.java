package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.blinkt.openvpn.util.AssetsDatabaseManager;
import de.blinkt.openvpn.util.DatabaseDAO;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class NumberDbModelImpl {
    public SQLiteDatabase sqliteDB;
    public DatabaseDAO dao;
    public  DatabaseDAO initDB(Context context) {
        AssetsDatabaseManager.initManager(context.getApplicationContext());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
        sqliteDB = mg.getDatabase("number_location.zip");
        dao = new DatabaseDAO(sqliteDB);
        return dao;
    }

    public void close(){
        if(sqliteDB!=null){
            sqliteDB.close();
        }
        if(dao!=null){
            dao.closeDB();
        }
    }

}
