package com.no.patient.sky.patientsky.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class AvailableTime {

    private Date startTime;
    private Date endTime;
    private UUID timeSlot_id;

}
