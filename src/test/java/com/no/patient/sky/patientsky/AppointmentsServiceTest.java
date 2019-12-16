package com.no.patient.sky.patientsky;

import com.no.patient.sky.patientsky.exception.AppointmentException;
import com.no.patient.sky.patientsky.request.AppointmentRequest;
import com.no.patient.sky.patientsky.response.AppointmentResponse;
import com.no.patient.sky.patientsky.service.AppointmentService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.Arrays;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AppointmentsServiceTest {

    private static final String PERIOD = "2019-04-23T10:00:00Z/2019-04-24T00:30:00Z";
    private static final UUID VALID_CALENDAR_ID = UUID.fromString("48cadf26-975e-11e5-b9c2-c8e0eb18c1e9");
    private static final int DURATION = 15;

    @Autowired
    private AppointmentService service;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testInvalidCalendarId(){

        UUID randomCalendarId = UUID.randomUUID();
        AppointmentRequest request = new AppointmentRequest();
        request.setCalendarIds(Arrays.asList(randomCalendarId));
        request.setPeriodToSearch(PERIOD);
        AppointmentResponse response = service.getAvailableTimes(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAvailableTimes().size());
        Assert.assertEquals(randomCalendarId, response.getAvailableTimes().get(0).getCalendarId());
        Assert.assertNull(response.getAvailableTimes().get(0).getAvailableTimeList());
        Assert.assertEquals("Calendar Id does not exist.", response.getAvailableTimes().get(0).getError());
    }

    @Test
    public void periodInvalidFormat1_expectAppointmentException(){
        String period = "Invalid";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);

        expectedEx.expect(AppointmentException.class);
        expectedEx.expectMessage("Invalid Period to Search");

        AppointmentResponse response = service.getAvailableTimes(request);

    }

    @Test
    public void periodInvalidFormat2_expectAppointmentException(){
        //Period has only start time
        String period = "2019-04-23T10:00:00Z";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);

        expectedEx.expect(AppointmentException.class);
        expectedEx.expectMessage("Invalid Period to Search");

        AppointmentResponse response = service.getAvailableTimes(request);

    }

    @Test
    public void periodInvalidFormat3_expectAppointmentException(){
        //Period has only start time but invalid format
        String period = "2019-04-23/2019-04-24T10:00:00Z";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);

        expectedEx.expect(AppointmentException.class);
        expectedEx.expectMessage("Invalid Period to Search");

        AppointmentResponse response = service.getAvailableTimes(request);

    }

    @Test
    public void periodInvalidFormat4_expectAppointmentException(){
        //Period has same start time and end time
        String period = "2019-04-23T10:00:00Z/2019-04-23T10:00:00Z";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);

        expectedEx.expect(AppointmentException.class);
        expectedEx.expectMessage("Invalid Period to Search");

        AppointmentResponse response = service.getAvailableTimes(request);

    }

    /**
     * Period = 1 hour
     * No. of appointments = 0
     * Duration = 15 mins
     *
     * Expected results 4 timeslots of 15 minutes each
     */
    @Test
    public void TestWithNoAppointmens_expectFourTimeSlots(){
        //Period has same start time and end time
        String period = "2019-04-23T10:00:00Z/2019-04-23T11:00:00Z";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(DURATION);

        AppointmentResponse response = service.getAvailableTimes(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAvailableTimes().size());
        Assert.assertEquals(VALID_CALENDAR_ID, response.getAvailableTimes().get(0).getCalendarId());
        Assert.assertNull(response.getAvailableTimes().get(0).getError());
        Assert.assertNotNull(response.getAvailableTimes().get(0).getAvailableTimeList());
        Assert.assertEquals(4, response.getAvailableTimes().get(0).getAvailableTimeList().size());

    }

    /**
     * Period = 15 minutes
     * No. of appointments = 1
     * Duration = 15 mins
     *
     * Period coincides with appointmnet
     * Expected results 0 timeslots
     */
    @Test
    public void TestWithFullAppointmens_expectNoSlotsAvailable(){
        //Period has same start time and end time
        String period = "2019-04-23T12:15:00Z/2019-04-23T12:30:00Z";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(DURATION);

        AppointmentResponse response = service.getAvailableTimes(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAvailableTimes().size());
        Assert.assertEquals(VALID_CALENDAR_ID, response.getAvailableTimes().get(0).getCalendarId());
        Assert.assertNull(response.getAvailableTimes().get(0).getError());
        Assert.assertNotNull(response.getAvailableTimes().get(0).getAvailableTimeList());
        Assert.assertEquals(0, response.getAvailableTimes().get(0).getAvailableTimeList().size());

    }

    /**
     * Period = 1 hour
     * No. of appointments = 1
     * Duration = 15 mins
     *
     * Expected results 3 timeslots of 15 minutes each
     */
    @Test
    public void TestWithOneAppointmentInBetween_expectThreeTimeSlots(){
        //Period has same start time and end time
        String period = "2019-04-23T12:00:00Z/2019-04-23T13:00:00Z";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(DURATION);

        AppointmentResponse response = service.getAvailableTimes(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAvailableTimes().size());
        Assert.assertEquals(VALID_CALENDAR_ID, response.getAvailableTimes().get(0).getCalendarId());
        Assert.assertNull(response.getAvailableTimes().get(0).getError());
        Assert.assertNotNull(response.getAvailableTimes().get(0).getAvailableTimeList());
        Assert.assertEquals(3, response.getAvailableTimes().get(0).getAvailableTimeList().size());

    }

    /**
     * Period = 1 hour
     * No. of appointments = 1
     * Duration = 15 mins
     *
     * Expected results 3 timeslots of 15 minutes each
     */
    @Test
    public void TestWithTwoPartialAppointmentsAtStartAndEnd_expectThreeTimeSlots(){
        String period = "2019-04-23T12:20:00Z/2019-04-23T13:20:00Z";

        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(DURATION);

        AppointmentResponse response = service.getAvailableTimes(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAvailableTimes().size());
        Assert.assertEquals(VALID_CALENDAR_ID, response.getAvailableTimes().get(0).getCalendarId());
        Assert.assertNull(response.getAvailableTimes().get(0).getError());
        Assert.assertNotNull(response.getAvailableTimes().get(0).getAvailableTimeList());
        Assert.assertEquals(3, response.getAvailableTimes().get(0).getAvailableTimeList().size());

    }

}
