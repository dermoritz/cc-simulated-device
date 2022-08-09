package com.bosch.ccsimulateddevice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CcSimulatedDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcSimulatedDeviceApplication.class, args);

    }

}
