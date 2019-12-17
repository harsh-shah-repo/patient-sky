package com.no.patient.sky.patientsky.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointments {

    @JsonProperty("appointments")
    private List<Appointment> appointmentList;

    @JsonProperty("timeslots")
    private List<TimeSlot> timeSlotList;

}

