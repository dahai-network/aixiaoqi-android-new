package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by kim
 * on 2017/4/8.
 * 用于app保存临时数据
 */

public class AppMode implements Serializable {

   private volatile static AppMode instance = null;

    public static AppMode getInstance() {
        if (instance == null) {
            synchronized (PhoneAuthonCountEntity.class) {
                if (instance == null) {
                    instance = new AppMode();
                }
            }
        }
        return instance;
    }

    public String curCharacter="";
    public boolean isClickAddDevice=false;
    public boolean isClickPackage=false;




}
