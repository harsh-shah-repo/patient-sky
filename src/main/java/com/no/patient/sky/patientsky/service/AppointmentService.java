package com.no.patient.sky.patientsky.service;

import com.no.patient.sky.patientsky.request.AppointmentRequest;
import com.no.patient.sky.patientsky.response.AppointmentResponse;

public interface AppointmentService {

    public AppointmentResponse getAvailableTimes(AppointmentRequest request);

}
