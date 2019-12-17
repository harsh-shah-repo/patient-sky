package com.no.patient.sky.patientsky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TimeSlot {

    private String id;
    private Date start;
    private Date end;

    @JsonProperty("calendar_id")
    private String calendarId;

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("type_id")
    private Boolean publicBookable;

    @JsonProperty("out_of_office")
    private Boolean outOfOffice;

}
