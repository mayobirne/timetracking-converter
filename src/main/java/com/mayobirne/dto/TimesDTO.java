package com.mayobirne.dto;

import java.util.Calendar;

/**
 * Created by Christian on 08.02.2016.
 */
public class TimesDTO {

    private Calendar date;
    private String startTime;
    private String endTime;
    private Integer projectNr;
    private Integer subNr;
    private String description;

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getProjectNr() {
        return projectNr;
    }

    public void setProjectNr(Integer projectNr) {
        this.projectNr = projectNr;
    }

    public Integer getSubNr() {
        return subNr;
    }

    public void setSubNr(Integer subNr) {
        this.subNr = subNr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
