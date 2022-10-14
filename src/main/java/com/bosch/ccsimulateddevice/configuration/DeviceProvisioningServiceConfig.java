package com.bosch.ccsimulateddevice.configuration;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "device.connection")
@Value
public class DeviceProvisioningServiceConfig {
    /**
     * Id scope of device provisioning service. Find yours in DPD blade overview.
     */
    String idScope;

    /**
     * global azure iot endpoint.
     */
    String globalEndpoint = "global.azure-devices-provisioning.net";

}
