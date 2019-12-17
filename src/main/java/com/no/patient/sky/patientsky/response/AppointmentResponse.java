package com.no.patient.sky.patientsky.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppointmentResponse {

    private List<CalendarAvailableSlot> availableSlots;

}
