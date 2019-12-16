package com.no.patient.sky.patientsky.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarAvailableTimes {

    private UUID calendarId;
    private List<AvailableTime> availableTimeList;
    private String error;

    public CalendarAvailableTimes(UUID calendarId, List<AvailableTime> availableTimeList) {
        this.calendarId = calendarId;
        this.availableTimeList = availableTimeList;
    }

    public CalendarAvailableTimes(UUID calendarId, String error) {
        this.calendarId = calendarId;
        this.error = error;
    }
}
