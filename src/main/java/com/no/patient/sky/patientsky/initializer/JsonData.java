package com.no.patient.sky.patientsky.initializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.no.patient.sky.patientsky.dto.Appointments;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class JsonData {

    private Map<String, Appointments> calendarAppointments = new HashMap<>();

    @Value("classpath:*.json")
    private Resource[] resources;

    @PostConstruct
    public void loadJsonFiles(){
        for(Resource resource : resources){
            try {
                Appointments appointments =  new ObjectMapper().readValue(resource.getInputStream(), Appointments.class);
                calendarAppointments.put(removeFileNameExtension(resource.getFilename()), appointments);
            } catch (IOException e) {
                log.error("Error in Reading JSON file {}",resource.getFilename());
            }
        }
    }

    public Appointments getAppointmentByCalendarId(UUID calendarId){
        return calendarAppointments.get(calendarId.toString());
    }

    private String removeFileNameExtension(String fileName){
        return fileName.indexOf(".") > 0 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
    }

}
