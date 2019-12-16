package com.no.patient.sky.patientsky.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {

    private String id;
    private String calendar_id;
    private Date start;
    private Date end;

}
