package com.no.patient.sky.patientsky.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AppointmentRequest {

    @NotEmpty
    List<@NotNull(message="UUId is invalid") UUID> calendarIds;

    @Min(value = 0L, message = "The value must be positive")
    Integer duration;

    @NotBlank(message = "Invalid Period to Search")
    String periodToSearch;

}
