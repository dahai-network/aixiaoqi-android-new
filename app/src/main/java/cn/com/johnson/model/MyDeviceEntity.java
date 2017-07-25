package cn.com.johnson.model;

/**
 * Created by Administrator on 2017/7/24.
 */

public class MyDeviceEntity {
    /**
     * 动作
     */
    private String EVENTBUSACTION;
    public MyDeviceEntity(String EVENTBUSACTION){
        this.EVENTBUSACTION=EVENTBUSACTION;
    }
    public String getAction() {
        return EVENTBUSACTION;
    }
    public void setAction(String EVENTBUSACTION) {
        this.EVENTBUSACTION = EVENTBUSACTION;
    }


}
