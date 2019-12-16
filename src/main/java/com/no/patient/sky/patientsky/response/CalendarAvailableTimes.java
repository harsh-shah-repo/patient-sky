package com.no.patient.sky.patientsky.response;

import java.util.List;
import java.util.UUID;

public class CalendarAvailableTimes {

    private UUID calendarId;
    private List<AvailableTime> availableTimeList;

    public List<AvailableTime> getAvailableTimeList() {
        return availableTimeList;
    }

    public void setAvailableTimeList(List<AvailableTime> availableTimeList) {
        this.availableTimeList = availableTimeList;
    }

    public UUID getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(UUID calendarId) {
        this.calendarId = calendarId;
    }
}
