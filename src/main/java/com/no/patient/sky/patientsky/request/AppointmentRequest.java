package com.no.patient.sky.patientsky.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AppointmentRequest {

    @NotEmpty(message = "List of Calendar Ids cannot be Empty")
    private List<@NotNull(message="UUId is invalid") UUID> calendarIds;

    @Min(value = 0L, message = "Duration must be greater than zero")
    private Integer duration;

    @NotBlank(message = "Invalid Period to Search")
    private String periodToSearch;

    private Instant startTime;
    private Instant endTime;

}
