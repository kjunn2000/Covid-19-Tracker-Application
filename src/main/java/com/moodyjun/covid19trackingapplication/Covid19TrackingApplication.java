package com.moodyjun.covid19trackingapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Covid19TrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.moodyjun.covid19trackingapplication.Covid19TrackingApplication.class, args);
    }


}
