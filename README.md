# Practice Assignment
Practice Application that lists available times for meeting several participants.
This is Spring Boot Application built on java 8. The Application uses RESTful API to fetch available time slots.

The code can be downloaded from [here](https://github.com/harsh-shah-repo/patient-sky)

## Request
The request is sample that takes 3 input parameters. Based on Input parameters it will return the available times.

|#|Parameter name  |Type                           |
|-|----------------|-------------------------------|
|1|`calendarIds`   |List\<UUID>                    |
|2|`duration`      |Integer                        |
|3|`periodToSearch`|String (ISO 8601 time interval)|

This is sample request .
~~~json
{
	"calendarIds": [
		"48cadf26-975e-11e5-b9c2-c8e0eb18c1e9",
		"48cadf26-975e-11e5-b9c2-c8e0eb18c1d9"
	],
	"duration": 100,
	"periodToSearch": "2019-04-23T10:00:00Z/2019-04-24T00:30:00Z"
}
~~~
## Response
The response will have list of Available time slots for each calendar Id.
If the Calendar Id is not available in data the error message is added `Calendar Id does not exist.`
This is done to give other Time slots in case one fails.
~~~json
{
    "availableSlots": [
        {
            "calendarId": "48cadf26-975e-11e5-b9c2-c8e0eb18c1e9",
            "availableSlotList": [
                {
                    "startTime": "2019-04-23T10:00:00.000+0000",
                    "endTime": "2019-04-23T11:40:00.000+0000"
                },
                {
                    "startTime": "2019-04-23T22:35:00.000+0000",
                    "endTime": "2019-04-24T00:15:00.000+0000"
                }
            ]
        },
        {
            "calendarId": "48cadf26-975e-11e5-b9c2-c8e0eb18c1d9",
            "error": "Calendar Id does not exist."
        }
    ]
}
~~~
## Business logic
- The list if appointments is present in json files with all the details. The files are added in classpath and these are loaded on startup. The files are loaded by extension  **.json**  so new file can be added without any changes.
- The application has Config file `PatientSkyApplication.java`  which can be used to start the Application.
- The Application runs on port 8080 and endpoint to get Time is http://localhost:8080/getAvailableSlot
- The requests are validated and retrurn HTTP status code 200 bad request if fails.
- The application throws custom Exception if period is invalid.
- First All the booked appoints for user are fetched that is between given request time interval.
- Then iterate through appointments and add timeslots in between available space.
- This list is set in response and sent to user.

### Assumptions
- The available appointments are based on individual calendar Id
- Json file Valid and without errors.

## Test cases
The are multiple test cases added to support validations, exceptions and expected output.
The test cases are or different types including mocking the data and without mocking data.
The Tests cases also check at response content and response status at Controller level.

## Future scope

- Fetch available time in provided timeslots.
- Allow user to book appointment.

## Personal Details
:boy: Harsh Shah

:email: shah.harsh70@yahoo.in

:iphone:  +47 93924184

:memo:  https://github.com/harsh-shah-repo/patient-sky

