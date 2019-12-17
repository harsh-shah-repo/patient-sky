package com.no.patient.sky.patientsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.no.patient.sky.patientsky.controller.AppointmentController;
import com.no.patient.sky.patientsky.dto.Appointment;
import com.no.patient.sky.patientsky.dto.Appointments;
import com.no.patient.sky.patientsky.initializer.JsonData;
import com.no.patient.sky.patientsky.request.AppointmentRequest;
import com.no.patient.sky.patientsky.service.AppointmentService;
import com.no.patient.sky.patientsky.service.AppointmentServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppointmentControllerTest {

    private static final UUID VALID_CALENDAR_ID = UUID.fromString("48cadf26-975e-11e5-b9c2-c8e0eb18c1e9");

    private MockMvc mockMvc;
    private AppointmentService service;

    @InjectMocks
    private AppointmentController controller;

    @Mock
    private JsonData jsonData;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        service = new AppointmentServiceImpl();
        setField(controller, "appointmentService", service);
        setField(service, "fileData", jsonData);
    }

    @Test
    public void calendarIdDoesNotExists() throws Exception {

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch("2019-04-23T12:20:00Z/2019-04-23T13:20:00Z");
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(15);

        when(jsonData.checkIfCalendarIdDoesNotExists(VALID_CALENDAR_ID)).thenReturn(true);

        MvcResult result = performMockMvcWithStatusOk(request);

        verify(jsonData, times(1)).checkIfCalendarIdDoesNotExists(eq(VALID_CALENDAR_ID));
        verify(jsonData, times(0)).getAppointmentByCalendarId(eq(VALID_CALENDAR_ID));

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals("{\"availableSlots\":[{\"calendarId\":\"48cadf26-975e-11e5-b9c2-c8e0eb18c1e9\",\"error\":\"Calendar Id does not exist.\"}]}", content);
    }

    @Test
    public void testPeriodValidation() throws Exception {

        AppointmentRequest request = new AppointmentRequest();
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(15);

        MvcResult result = performMockMvcWithStatusBadRequest(request);

        Assert.assertNotNull(result.getResolvedException());
        Assert.assertTrue(result.getResolvedException().getMessage().contains("Invalid Period to Search"));
    }

    @Test
    public void testCalendarIdListValidation() throws Exception {

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch("2019-04-23T12:20:00Z/2019-04-23T13:20:00Z");
        request.setDuration(15);

        MvcResult result = performMockMvcWithStatusBadRequest(request);

        Assert.assertNotNull(result.getResolvedException());
        Assert.assertTrue(result.getResolvedException().getMessage().contains("List of Calendar Ids cannot be Empty"));
    }

    @Test
    public void testDurationValidation() throws Exception {

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch("2019-04-23T12:20:00Z/2019-04-23T13:20:00Z");
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(-15);

        MvcResult result = performMockMvcWithStatusBadRequest(request);

        Assert.assertNotNull(result.getResolvedException());
        Assert.assertTrue(result.getResolvedException().getMessage().contains("Duration must be greater than zero"));
    }

    @Test
    public void testTimeSlotByMockingData() throws Exception {

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch("2019-04-23T11:30:00Z/2019-04-23T12:30:00Z");
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(15);

        when(jsonData.checkIfCalendarIdDoesNotExists(VALID_CALENDAR_ID)).thenReturn(false);
        when(jsonData.getAppointmentByCalendarId(VALID_CALENDAR_ID)).thenReturn(generateAppointments());

        MvcResult result = performMockMvcWithStatusOk(request);

        verify(jsonData, times(1)).checkIfCalendarIdDoesNotExists(eq(VALID_CALENDAR_ID));
        verify(jsonData, times(1)).getAppointmentByCalendarId(eq(VALID_CALENDAR_ID));

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals("{\"availableSlots\":[{\"calendarId\":\"48cadf26-975e-11e5-b9c2-c8e0eb18c1e9\",\"availableSlotList\":[{\"startTime\":1556019000000,\"endTime\":1556019900000},{\"startTime\":1556019900000,\"endTime\":1556020800000},{\"startTime\":1556020800000,\"endTime\":1556021700000},{\"startTime\":1556020800000,\"endTime\":1556021700000},{\"startTime\":1556021700000,\"endTime\":1556022600000}]}]}", content);
    }

    private MvcResult performMockMvcWithStatusOk(AppointmentRequest request) throws Exception {
        return mockMvc.perform(get("/getAvailableSlot")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    private MvcResult performMockMvcWithStatusBadRequest(AppointmentRequest request) throws Exception {
        return mockMvc.perform(get("/getAvailableSlot")
                .contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    private Appointments generateAppointments() {
        Appointments appointments = new Appointments();
        Appointment appointment = new Appointment();
        appointment.setEndTime(Date.from(Instant.parse("2019-04-23T12:00:00Z")));
        appointment.setStartTime(Date.from(Instant.parse("2019-04-23T12:15:00Z")));
        appointment.setCalendarId(VALID_CALENDAR_ID.toString());
        appointments.setAppointmentList(Arrays.asList(appointment));

        return appointments;
    }
}
