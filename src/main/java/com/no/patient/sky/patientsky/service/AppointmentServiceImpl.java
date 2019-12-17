package com.no.patient.sky.patientsky.service;

import com.no.patient.sky.patientsky.dto.Appointment;
import com.no.patient.sky.patientsky.exception.AppointmentException;
import com.no.patient.sky.patientsky.initializer.JsonData;
import com.no.patient.sky.patientsky.request.AppointmentRequest;
import com.no.patient.sky.patientsky.response.AppointmentResponse;
import com.no.patient.sky.patientsky.response.AvailableSlot;
import com.no.patient.sky.patientsky.response.CalendarAvailableSlot;
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
    JsonData fileData;

    @Override
    public AppointmentResponse getAvailableTimes(AppointmentRequest request) {

        validateTimePeriod(request);

        AppointmentResponse response = new AppointmentResponse();
        List<CalendarAvailableSlot> availableTimeSlot = new ArrayList<>();

        for (UUID calendarId : request.getCalendarIds()) {
            if (fileData.checkIfCalendarIdDoesNotExists(calendarId)) {
                //Calendar Id does not exists hence form response with Error statement.
                availableTimeSlot.add(CalendarAvailableSlot.builder().calendarId(calendarId).error("Calendar Id does not exist.").build());
            } else {

                List<Appointment> schedules = getSchedulesForCalendarIdByTimePeriod(calendarId, request.getStartTime(), request.getEndTime());

                if (schedules.isEmpty()) {
                    //There are no appointments for this calendarId. Split available time in duration slots.
                    List<AvailableSlot> availableSlotList = getAllTimeSlotsBetweenTime(request.getStartTime(), request.getEndTime(), request.getDuration());

                    availableTimeSlot.add(CalendarAvailableSlot.builder()
                            .calendarId(calendarId)
                            .availableSlotList(availableSlotList)
                            .build());
                } else {

                    //Sort list by startTime time.
                    schedules.sort(Comparator.comparing(Appointment::getStartTime));

                    List<AvailableSlot> availableSlotList = new ArrayList<>();

                    /**
                     * Iterate the list of schedules and get slots in between the available time.
                     * Increment startTime time slot on each iteration.
                     */
                    Instant startTimeSlot = request.getStartTime();
                    for (Appointment appointment : schedules) {
                        if (startTimeSlot.isBefore(appointment.getStartTime().toInstant())) {
                            availableSlotList.addAll(getAllTimeSlotsBetweenTime(startTimeSlot, appointment.getStartTime().toInstant(), request.getDuration()));
                        }
                        startTimeSlot = appointment.getEndTime().toInstant();
                    }

                    //After iteration if there is slot available between endTime time Add to list.
                    if (startTimeSlot.isBefore(request.getEndTime())) {
                        availableSlotList.addAll(getAllTimeSlotsBetweenTime(startTimeSlot, request.getEndTime(), request.getDuration()));
                    }

                    availableTimeSlot.add(CalendarAvailableSlot.builder()
                            .calendarId(calendarId)
                            .availableSlotList(availableSlotList)
                            .build());
                }
            }
        }
        response.setAvailableSlots(availableTimeSlot);
        return response;
    }

    /**
     * This method validates Time period and sets StartTime and End time in object.
     * Period valid format = <Start date>/<End date>
     * example : 2019-04-23T10:00:00Z/2019-04-24T00:30:00Z
     *
     * @param request
     * @throws AppointmentException if format is invalid
     */
    private void validateTimePeriod(AppointmentRequest request) {

        if (!request.getPeriodToSearch().contains("/")) {
            log.error("Request periodToSearch={} string is missing slash ", request.getPeriodToSearch());
            throw new AppointmentException("Invalid Period to Search");
        }

        try {
            String[] timeDuration = request.getPeriodToSearch().split("/");
            request.setStartTime(Instant.parse(timeDuration[0]));
            request.setEndTime(Instant.parse(timeDuration[1]));

            if (!request.getEndTime().isAfter(request.getStartTime())) {
                log.error("EndTime={} is less than or equal to StartTime={}", request.getEndTime(), request.getStartTime());
                throw new AppointmentException("Invalid Period to Search");
            }
        } catch (DateTimeParseException exception) {
            log.error("DateTimeParseException in in parsing {}", request.getPeriodToSearch());
            throw new AppointmentException("Invalid Period to Search");
        }
    }

    /**
     * Get the Appointments of calendarId.
     * The List is filtered based on following criteria
     * 1. Appointment Calendar Id mathes Calendar If from Request
     * 2. Filter All appointments that are with Start time and after Start time.
     * 3. Filter All appointments that are with End time and before End time.
     *
     * @param calendarId
     * @param startTime
     * @param endTime
     * @return List of Appointments
     */
    private List<Appointment> getSchedulesForCalendarIdByTimePeriod(UUID calendarId, Instant startTime, Instant endTime) {
        return fileData.getAppointmentByCalendarId(calendarId).getAppointmentList().stream()
                .filter(s -> s.getCalendarId().equals(calendarId.toString()))
                .filter(s -> s.getStartTime().after(Date.from(startTime)) ||
                        s.getStartTime().equals(Date.from(startTime)) ||
                        s.getEndTime().after(Date.from(startTime)))
                .filter(s -> s.getStartTime().before(Date.from(endTime)) ||
                        s.getEndTime().equals(Date.from(endTime)) ||
                        s.getEndTime().before(Date.from(endTime)))
                .collect(Collectors.toList());
    }

    /**
     * Initiate startTime slot with startTime Time.
     * Initiate endTime slot with Start time plus duration of apointment.
     * <p>
     * If endslot is within the time range Initiate Available time with UUID and slot time
     * after addition increment the times and continue till all slots of period are reached.
     *
     * @param startTime
     * @param endTime
     * @param duration
     * @return List of available Time.
     */
    private List<AvailableSlot> getAllTimeSlotsBetweenTime(Instant startTime, Instant endTime, Integer duration) {

        Instant startSlot = startTime;
        Instant endSlot = startTime.plus(duration, ChronoUnit.MINUTES);

        List<AvailableSlot> availableSlots = new ArrayList<>();

        while (endSlot.isBefore(endTime) || endSlot.equals(endTime)) {

            availableSlots.add(AvailableSlot.builder().startTime(Date.from(startSlot)).endTime(Date.from(endSlot)).build());

            startSlot = endSlot;
            endSlot = startSlot.plus(duration, ChronoUnit.MINUTES);
        }

        return availableSlots;
    }
}
