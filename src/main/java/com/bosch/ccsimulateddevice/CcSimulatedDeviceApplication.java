package com.bosch.ccsimulateddevice;

import com.bosch.ccsimulateddevice.device.ProvisioningX509Sample;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CcSimulatedDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcSimulatedDeviceApplication.class, args);
        try {
            ProvisioningX509Sample.main(new String[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
