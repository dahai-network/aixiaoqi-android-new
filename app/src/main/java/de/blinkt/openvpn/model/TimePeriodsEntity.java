package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/3 0003.
 */
public class TimePeriodsEntity implements Serializable {
    private String StartDateTime;
    private String EndDateTime;
    private String StepNum;
    private String Kcal;
    private String Minute;
    private String KM;

    public String getKcal() {
        return Kcal;
    }

    public void setKcal(String kcal) {
        Kcal = kcal;
    }

    public String getMinute() {
        return Minute;
    }

    public void setMinute(String minute) {
        Minute = minute;
    }

    public String getKM() {
        return KM;
    }

    public void setKM(String KM) {
        this.KM = KM;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public String getStepNum() {
        return StepNum;
    }

    public void setStepNum(String stepNum) {
        StepNum = stepNum;
    }
}
