package cn.com.johnson.model;


import java.io.Serializable;

/**
 * Created by kim
 * on 2017/3/22.
 * phone 的实例
 */

public class PhoneAuthonCountEntity  implements Serializable{


    private static PhoneAuthonCountEntity instance = null;
      public static PhoneAuthonCountEntity getInstance() {
              if (instance == null) {
                      synchronized (PhoneAuthonCountEntity.class) {
                            if (instance == null) {
                                   instance = new PhoneAuthonCountEntity();
                                 }
                       }
                }
             return instance;
       }


    private  int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
