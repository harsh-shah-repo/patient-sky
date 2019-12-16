package com.no.patient.sky.patientsky.service;

import com.no.patient.sky.patientsky.dto.Appointment;
import com.no.patient.sky.patientsky.dto.Appointments;
import com.no.patient.sky.patientsky.initializer.JsonData;
import com.no.patient.sky.patientsky.request.AppointmentRequest;
import com.no.patient.sky.patientsky.response.AppointmentResponse;
import com.no.patient.sky.patientsky.response.AvailableTime;
import com.no.patient.sky.patientsky.response.CalendarAvailableTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    JsonData data;

    @Override
    public AppointmentResponse getAvailableTimes(AppointmentRequest request) {

        AppointmentResponse response = new AppointmentResponse();
        List<CalendarAvailableTimes> calendarAvailableTimes = new ArrayList<>();

        for (UUID calendarId : request.getCalendarIds()){
            List<Appointment> schedules = getSchedulesForCalendarId(calendarId);

            if(!schedules.isEmpty()){
                String[] timeDuration = request.getPeriodToSearch().split("/");
                Instant startTime = Instant.parse(timeDuration[0]);
                Instant endTime = Instant.parse(timeDuration[1]);

                List<Appointment> AppointmentsInPeriod = schedules.stream()
                        .filter(s -> s.getStart().after(Date.from(startTime)) ||
                                s.getEnd().after(Date.from(startTime)))
                        .filter(s -> s.getStart().before(Date.from(endTime)) ||
                                s.getEnd().before(Date.from(endTime)))
                        .collect(Collectors.toList());

                if(CollectionUtils.isEmpty(AppointmentsInPeriod)){

                    List<AvailableTime> availableSlots = getAllTimeSlotsBetweenTime(startTime, endTime, request.getDuration());

                    CalendarAvailableTimes availableTimes = new CalendarAvailableTimes();
                    availableTimes.setCalendarId(calendarId);
                    availableTimes.setAvailableTimeList(availableSlots);

                    calendarAvailableTimes.add(availableTimes);
                } else {

                    CalendarAvailableTimes availableTimes = new CalendarAvailableTimes();
                    availableTimes.setCalendarId(calendarId);

                    //Sort list by time.
                    AppointmentsInPeriod.sort(Comparator.comparing(Appointment::getStart));

                    List<AvailableTime> availableSlots = new ArrayList<>();

                    Instant startTimeSlot = startTime;

                    for (Appointment appointment : AppointmentsInPeriod){
                        if(startTimeSlot.isBefore(appointment.getStart().toInstant())){
                            availableSlots.addAll(getAllTimeSlotsBetweenTime(startTimeSlot, appointment.getStart().toInstant(), request.getDuration()));
                            startTimeSlot = appointment.getEnd().toInstant();
                        }
                    }

                    if(startTimeSlot.isBefore(endTime)){
                        availableSlots.addAll(getAllTimeSlotsBetweenTime(startTimeSlot, endTime, request.getDuration()));
                    }

                    availableTimes.setAvailableTimeList(availableSlots);
                    calendarAvailableTimes.add(availableTimes);
                }
            }
        }

        response.setAvailableTimes(calendarAvailableTimes);
        return response;
    }

    private List<AvailableTime> getAllTimeSlotsBetweenTime(Instant startTime, Instant endTime, Integer duration) {

        Instant startSlot = startTime;
        Instant endSlot = startTime.plus(duration, ChronoUnit.MINUTES);

        List<AvailableTime> availableSlots = new ArrayList<>();

        while (endSlot.isBefore(endTime)){
            AvailableTime availableTime = new AvailableTime();
            availableTime.setTimeSlot_id(UUID.randomUUID());
            availableTime.setStartTime(Date.from(startSlot));
            availableTime.setEndTime(Date.from(endSlot));

            availableSlots.add(availableTime);

            startSlot = endSlot;
            endSlot = startSlot.plus(duration, ChronoUnit.MINUTES);

        }

        return availableSlots;
    }

    private List<Appointment> getSchedulesForCalendarId(UUID calendarId) {
        Appointments appointments = data.getAppointmentByCalendarId(calendarId);
        if(appointments != null){
            return appointments.getAppointments().stream().filter(s -> s.getCalendar_id().equals(calendarId.toString())).collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
