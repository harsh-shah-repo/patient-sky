package com.no.patient.sky.patientsky.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {

    @JsonProperty("calendar_id")
    private String calendarId;

    @JsonProperty("start")
    private Date startTime;

    @JsonProperty("end")
    private Date endTime;

}
