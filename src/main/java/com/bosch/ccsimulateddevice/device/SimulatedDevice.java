package com.bosch.ccsimulateddevice.device;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationResult;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientStatus;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
public class SimulatedDevice {

    @Getter
    private final DeviceClient device;

    public SimulatedDevice(ProvisioningDeviceService provsioning, SecurityProvider securityProvider){
        log.info("Waiting for provisioning service to register device..");
        Mono<ProvisioningDeviceClientRegistrationResult> provisioningStatusMono = provsioning.getProvisioningStatusMono();
        ProvisioningDeviceClientRegistrationResult result = provisioningStatusMono.cache().block(Duration.of(10, ChronoUnit.SECONDS));
        log.info("Registering completed.");
        if (result == null || result.getProvisioningDeviceClientStatus() != ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ASSIGNED){
            throw new IllegalStateException("Device not successfully registered :-(.");
        }
        try {
            device = new DeviceClient(result.getIothubUri(), result.getDeviceId(), securityProvider, IotHubClientProtocol.MQTT);
        } catch (IOException e) {
            throw new IllegalStateException("Problem creating device: ", e);
        }
    }

}
