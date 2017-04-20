package cn.com.johnson.model;

/**
 * Created by kim
 * on 2017/4/20.
 */

public class ChangeViewStateEvent {

    public int state;

    /**
     *
     * @param state 固件
     */
    public ChangeViewStateEvent(int state) {
        this.state = state;

    }

    public int getVersionState() {
        return state;
    }

    public void setVersionState(int state) {
        this.state = state;
    }







}
