package com.no.patient.sky.patientsky.request;

import java.util.List;
import java.util.UUID;

public class AppointmentRequest {

    List<UUID> calendarIds;
    Integer duration;
    String periodToSearch;

    public List<UUID> getCalendarIds() {
        return calendarIds;
    }

    public void setCalendarIds(List<UUID> calendarIds) {
        this.calendarIds = calendarIds;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getPeriodToSearch() {
        return periodToSearch;
    }

    public void setPeriodToSearch(String periodToSearch) {
        this.periodToSearch = periodToSearch;
    }
}
