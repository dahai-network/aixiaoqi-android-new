package com.aixiaoqi.socket;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class LPositon {

    private int vL;
    private int position;

    public LPositon(int vL, int position) {
        this.vL = vL;
        this.position = position;
    }

    public int getvL() {
        return vL;
    }

    public void setvL(int vL) {
        this.vL = vL;
    }

    public int getposition() {
        return position;
    }

    public void setposition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "LPositon{" +
                "vL=" + vL +
                ", position=" + position +
                '}';
    }
}
