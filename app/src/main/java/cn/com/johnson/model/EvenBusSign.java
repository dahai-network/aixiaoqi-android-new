package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/8.
 */

public class EvenBusSign implements Serializable {
    private boolean flg;

    public boolean isFlg() {
        return flg;
    }

    public void setFlg(boolean flg) {
        this.flg = flg;
    }


    public EvenBusSign(boolean flg) {
        this.flg = flg;
    }
}
