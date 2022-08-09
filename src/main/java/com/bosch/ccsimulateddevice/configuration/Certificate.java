package com.bosch.ccsimulateddevice.configuration;


import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;


@ConstructorBinding
@ConfigurationProperties(prefix = "certificate")
@Value
public class Certificate {
    String publicKey;

    String privateKey;
}
