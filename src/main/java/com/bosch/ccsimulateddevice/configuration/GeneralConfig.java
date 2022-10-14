package com.bosch.ccsimulateddevice.configuration;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "device.general")
@ConstructorBinding
@Value
public class GeneralConfig {

    /**
     * Every n seconds device sends it's state. 0 means never.
     */
    int messageIntervalSeconds;

}
