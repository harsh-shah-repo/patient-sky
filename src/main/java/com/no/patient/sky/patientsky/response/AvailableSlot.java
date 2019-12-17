package com.no.patient.sky.patientsky.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class AvailableSlot {

    private Date startTime;
    private Date endTime;

}
