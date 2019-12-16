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
    AppointmentService appointmentService;


    @RequestMapping(
            value = "/getAvailableTimes",
            method = RequestMethod.GET,
            consumes = "application/json",
            produces = "application/json")
    public AppointmentResponse findAvailableTime(@RequestBody @Validated AppointmentRequest request){

        if(request.getCalendarIds() != null && !request.getCalendarIds().isEmpty()){
            return appointmentService.getAvailableTimes(request);
        }

        return new AppointmentResponse();
    }
}