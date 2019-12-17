package com.no.patient.sky.patientsky.controller;

import com.no.patient.sky.patientsky.request.AppointmentRequest;
import com.no.patient.sky.patientsky.response.AppointmentResponse;
import com.no.patient.sky.patientsky.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @RequestMapping(
            value = "/getAvailableSlot",
            method = RequestMethod.GET,
            consumes = "application/json",
            produces = "application/json")
    public AppointmentResponse findAvailableSlot(@RequestBody @Validated AppointmentRequest request) {
        return appointmentService.getAvailableTimes(request);
    }

}
