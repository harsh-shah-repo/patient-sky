package com.no.patient.sky.patientsky.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarAvailableSlot {

    private UUID calendarId;
    private List<AvailableSlot> availableSlotList;
    private String error;

}
