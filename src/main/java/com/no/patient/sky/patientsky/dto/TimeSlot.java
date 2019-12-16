package com.no.patient.sky.patientsky.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TimeSlot {

    private String id;
    private String calendar_id;
    private String type_id;
    private Date start;
    private Date end;
    private boolean public_bookable;
    private boolean out_of_office;

}
