package com.no.patient.sky.patientsky.service;

import com.no.patient.sky.patientsky.dto.Appointment;
import com.no.patient.sky.patientsky.exception.AppointmentException;
import com.no.patient.sky.patientsky.initializer.JsonData;
import com.no.patient.sky.patientsky.request.AppointmentRequest;
import com.no.patient.sky.patientsky.response.AppointmentResponse;
import com.no.patient.sky.patientsky.response.AvailableTime;
import com.no.patient.sky.patientsky.response.CalendarAvailableTimes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    JsonData data;

    @Override
    public AppointmentResponse getAvailableTimes(AppointmentRequest request) {

        validateTimePeriod(request.getPeriodToSearch());


        AppointmentResponse response = new AppointmentResponse();
        List<CalendarAvailableTimes> calendarAvailableTimes = new ArrayList<>();

        for (UUID calendarId : request.getCalendarIds()){
            if(data.checkIfCalendarIdDoesNotExists(calendarId)){
                /**
                 * Calendar Id does not exists
                 * Form Error statement
                 */
                log.info("Calendar Id {} does not exist in data", calendarId);
                CalendarAvailableTimes availableTimes = new CalendarAvailableTimes(calendarId, "Calendar Id does not exist.");
                calendarAvailableTimes.add(availableTimes);
            } else {

                String[] timeDuration = request.getPeriodToSearch().split("/");
                Instant startTime = Instant.parse(timeDuration[0]);
                Instant endTime = Instant.parse(timeDuration[1]);

                List<Appointment> schedules = getSchedulesForCalendarIdByTimePeriod(calendarId, startTime, endTime);

                if(schedules.isEmpty()){
                    /**
                     * There are no appointments for this calendarId.
                     * Split available time in duration slots.
                     */
                    log.info("There are no appointments in provided time Frame for Calendar Id = {} all slots available", calendarId);
                    List<AvailableTime> availableSlots = getAllTimeSlotsBetweenTime(startTime, endTime, request.getDuration());
                    CalendarAvailableTimes availableTimes = new CalendarAvailableTimes(calendarId, availableSlots);

                    calendarAvailableTimes.add(availableTimes);
                } else {

                    log.info("The time slots will be calculated based on unavailable appointments for Calendar Id = {}", calendarId);

                    //Sort list by start time.
                    schedules.sort(Comparator.comparing(Appointment::getStart));

                    List<AvailableTime> availableSlots = new ArrayList<>();

                    /**
                     * Iterate the list of schedules and get slots in between the available time.
                     * Increment start time slot on each iteration.
                     */
                    Instant startTimeSlot = startTime;
                    for (Appointment appointment : schedules){
                        if(startTimeSlot.isBefore(appointment.getStart().toInstant())){
                            availableSlots.addAll(getAllTimeSlotsBetweenTime(startTimeSlot, appointment.getStart().toInstant(), request.getDuration()));
                        }
                        startTimeSlot = appointment.getEnd().toInstant();
                    }

                    //After iteration if there is slot available between end time Add to list.
                    if(startTimeSlot.isBefore(endTime)){
                        availableSlots.addAll(getAllTimeSlotsBetweenTime(startTimeSlot, endTime, request.getDuration()));
                    }

                    CalendarAvailableTimes availableTimes = new CalendarAvailableTimes(calendarId, availableSlots);
                    calendarAvailableTimes.add(availableTimes);
                }
            }
        }

        response.setAvailableTimes(calendarAvailableTimes);
        return response;
    }

    /**
     * @param periodToSearch
     * Period valid format = <Start date>/<End date>
     * Date is in ISO8601 format
     *
     * example : 2019-04-23T10:00:00Z/2019-04-24T00:30:00Z
     *
     * @throws AppointmentException if format is invalid
     *
     */
    private void validateTimePeriod(String periodToSearch) {

        if(!periodToSearch.contains("/")){
            log.error("Request periodToSearch={} string is missing slash ", periodToSearch);
            throw new AppointmentException("Invalid Period to Search");
        }

        try{
            String[] timeDuration = periodToSearch.split("/");
            Instant startTime = Instant.parse(timeDuration[0]);
            Instant endTime = Instant.parse(timeDuration[1]);

            if(!endTime.isAfter(startTime)){
                log.error("EndTime={} is less than or equal to StartTime={}", endTime, startTime);
                throw new AppointmentException("Invalid Period to Search");
            }
        } catch (DateTimeParseException exception){
            log.error("DateTimeParseException in converting the period to Date for period={}", periodToSearch);
            throw new AppointmentException("Invalid Period to Search");
        }
    }

    /**
     * Get the Appointments of calendarId.
     * The List is filtered based on following criteria
     * 1. Appointment Calendar Id mathes Calendar If from Request
     * 2. Filter All appointments that are with Start time and after Start time.
     * 3. Filter All appointments that are with End time and before End time.
     * @param calendarId
     * @param startTime
     * @param endTime
     * @return List of Appointments
     */
    private List<Appointment> getSchedulesForCalendarIdByTimePeriod(UUID calendarId, Instant startTime, Instant endTime) {
        return data.getAppointmentByCalendarId(calendarId).getAppointments().stream()
                .filter(s -> s.getCalendar_id().equals(calendarId.toString()))
                .filter(s -> s.getStart().after(Date.from(startTime)) ||
                        s.getStart().equals(Date.from(startTime)) ||
                        s.getEnd().after(Date.from(startTime)))
                .filter(s -> s.getStart().before(Date.from(endTime)) ||
                        s.getEnd().equals(Date.from(endTime)) ||
                        s.getEnd().before(Date.from(endTime)))
                .collect(Collectors.toList());
    }

    /**
     * Initiate start slot with start Time.
     * Initiate end slot with Start time plus duration of apointment.
     *
     * If endslot is within the time range Initiate Available time with UUID and slot time
     * after addition increment the times and continue till all slots of period are reached.
     *
     * @param startTime
     * @param endTime
     * @param duration
     * @return List of available Time.
     */
    private List<AvailableTime> getAllTimeSlotsBetweenTime(Instant startTime, Instant endTime, Integer duration) {

        Instant startSlot = startTime;
        Instant endSlot = startTime.plus(duration, ChronoUnit.MINUTES);

        List<AvailableTime> availableSlots = new ArrayList<>();

        while (endSlot.isBefore(endTime) || endSlot.equals(endTime)){
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
}
