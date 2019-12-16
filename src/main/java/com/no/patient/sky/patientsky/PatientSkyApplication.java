package com.no.patient.sky.patientsky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PatientSkyApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(PatientSkyApplication.class)
				.run(args);
	}

}
