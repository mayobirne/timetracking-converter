package com.mayobirne.dto;

import java.util.Date;

/**
 * Created by Christian on 07.02.2016.
 */
public class InterflexDTO {

    String day_WD_DD;
    Date startTime;
    Date endTime;

    public String getDay_WD_DD() {
        return day_WD_DD;
    }

    public void setDay_WD_DD(String day_WD_DD) {
        this.day_WD_DD = day_WD_DD;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
