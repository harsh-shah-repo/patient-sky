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
import java.util.List;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AppointmentsServiceTest {

    private static final UUID VALID_CALENDAR_ID = UUID.fromString("48cadf26-975e-11e5-b9c2-c8e0eb18c1e9");

    @Autowired
    private AppointmentService service;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testMultipleInvalidTimePeriod_AlwaysThrowException(){
        List<String> multipleInvalidPeriods = Arrays.asList(
                "Invalid", //Not UUID
                "2019-04-23T10:00:00Z", //Period has only startTime time
                "2019-04-23/2019-04-24T10:00:00Z", //Period has invalid startTime
                "2019-04-23T10:00:00Z/2019-04-23T10:00:00Z" //Period has same startTime time and endTime time
        );

        for(String period : multipleInvalidPeriods){
            periodInvalidFormat_expectAppointmentException(period);
        }
    }

    /**
     * Period = 1 hour
     * No. of appointments = 0
     * Duration = 15 mins
     *
     * Expected results 4 timeSlotList of 15 minutes each
     */
    @Test
    public void TestWithNoAppointmens_expectFourTimeSlots(){
        String period = "2019-04-23T10:00:00Z/2019-04-23T11:00:00Z";
        AppointmentResponse response = service.getAvailableTimes(generateRequestByPeriod(period));
        assertResponse(response, 4);
    }

    /**
     * Period = 15 minutes
     * No. of appointments = 1
     * Duration = 15 mins
     *
     * Period coincides with appointmnet
     * Expected results 0 timeSlotList
     */
    @Test
    public void TestWithFullAppointmens_expectNoSlotsAvailable(){
        String period = "2019-04-23T12:15:00Z/2019-04-23T12:30:00Z";
        AppointmentResponse response = service.getAvailableTimes(generateRequestByPeriod(period));
        assertResponse(response, 0);
    }

    /**
     * Period = 1 hour
     * No. of appointments = 1
     * Duration = 15 mins
     *
     * Expected results 3 timeSlotList of 15 minutes each
     */
    @Test
    public void TestWithOneAppointmentInBetween_expectThreeTimeSlots(){
        String period = "2019-04-23T12:00:00Z/2019-04-23T13:00:00Z";
        AppointmentResponse response = service.getAvailableTimes(generateRequestByPeriod(period));
        assertResponse(response, 3);
    }

    /**
     * Period = 1 hour
     * No. of appointments = 2 Partially
     * Duration = 15 mins
     *
     * Expected results 3 timeSlotList of 15 minutes each
     */
    @Test
    public void TestWithTwoPartialAppointmentsAtStartAndEnd_expectThreeTimeSlots(){
        String period = "2019-04-23T12:20:00Z/2019-04-23T13:20:00Z";
        AppointmentResponse response = service.getAvailableTimes(generateRequestByPeriod(period));
        assertResponse(response, 3);
    }

    @Test
    public void testCalendarIdNotInData(){

        String period = "2019-04-23T10:00:00Z/2019-04-24T00:30:00Z";
        UUID randomCalendarId = UUID.randomUUID();
        AppointmentRequest request = new AppointmentRequest();
        request.setCalendarIds(Arrays.asList(randomCalendarId));
        request.setPeriodToSearch(period);
        AppointmentResponse response = service.getAvailableTimes(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAvailableSlots().size());
        Assert.assertEquals(randomCalendarId, response.getAvailableSlots().get(0).getCalendarId());
        Assert.assertNull(response.getAvailableSlots().get(0).getAvailableSlotList());
        Assert.assertEquals("Calendar Id does not exist.", response.getAvailableSlots().get(0).getError());
    }

    private void assertResponse(AppointmentResponse response, int expectedTimeSlots) {
        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getAvailableSlots().size());
        Assert.assertEquals(VALID_CALENDAR_ID, response.getAvailableSlots().get(0).getCalendarId());
        Assert.assertNull(response.getAvailableSlots().get(0).getError());
        Assert.assertNotNull(response.getAvailableSlots().get(0).getAvailableSlotList());
        Assert.assertEquals(expectedTimeSlots, response.getAvailableSlots().get(0).getAvailableSlotList().size());
    }

    private void periodInvalidFormat_expectAppointmentException(String period){

        expectedEx.expect(AppointmentException.class);
        expectedEx.expectMessage("Invalid Period to Search");

        service.getAvailableTimes(generateRequestByPeriod(period));
    }

    private AppointmentRequest generateRequestByPeriod(String period){
        AppointmentRequest request = new AppointmentRequest();
        request.setPeriodToSearch(period);
        request.setCalendarIds(Arrays.asList(VALID_CALENDAR_ID));
        request.setDuration(15);

        return request;
    }
}
