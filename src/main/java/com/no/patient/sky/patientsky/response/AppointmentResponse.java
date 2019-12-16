package com.no.patient.sky.patientsky.response;

import java.util.List;

public class AppointmentResponse {

    private List<CalendarAvailableTimes> availableTimes;

    public List<CalendarAvailableTimes> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<CalendarAvailableTimes> availableTimes) {
        this.availableTimes = availableTimes;
    }
}
