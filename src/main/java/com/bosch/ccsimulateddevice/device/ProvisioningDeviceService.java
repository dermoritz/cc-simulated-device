package com.bosch.ccsimulateddevice.device;

import com.bosch.ccsimulateddevice.configuration.DevicePrvisioningServiceConfig;
import com.google.common.base.Preconditions;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClient;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationCallback;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientRegistrationResult;
import com.microsoft.azure.sdk.iot.provisioning.device.ProvisioningDeviceClientTransportProtocol;
import com.microsoft.azure.sdk.iot.provisioning.device.internal.exceptions.ProvisioningDeviceClientException;
import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Provisions the simulated device and provides {@link com.microsoft.azure.sdk.iot.device.DeviceClient} if successful.
 */
@Component
@Slf4j
public class ProvisioningDeviceService {

    private static final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.MQTT;
    private final SecurityProvider securityProvider;
    private final DevicePrvisioningServiceConfig config;
    private final Mono<ProvisioningDeviceClientRegistrationResult> provisioningStatusMono;

    private final ProvisioningDeviceClient provisioningDeviceClient;


    public ProvisioningDeviceService(SecurityProvider securityProvider, DevicePrvisioningServiceConfig config) {
        this.securityProvider = Preconditions.checkNotNull(securityProvider);
        this.config = config;
        provisioningDeviceClient = createProvisionClient();
        provisioningStatusMono = createMono();
    }

    public Mono<ProvisioningDeviceClientRegistrationResult> getProvisioningStatusMono(){
        return provisioningStatusMono;
    }

    /**
     * Maps Device Provisioning {@link ProvisioningDeviceClientRegistrationCallback} into mono.
     * @return mono with provisioning result.
     */
    private Mono<ProvisioningDeviceClientRegistrationResult> createMono() {
        return Mono.create(sink -> {
            ProvisioningDeviceClientRegistrationCallback callback = (provisioningDeviceClientRegistrationResult, e, context) -> {
                if (e == null) {
                    log.debug("Registering device successful.");
                    sink.success(provisioningDeviceClientRegistrationResult);
                } else {
                    log.error("Regsitering device yielded error: ", e);
                    sink.error(e);
                }
            };
            try {
                provisioningDeviceClient.registerDevice(callback, null);
            } catch (ProvisioningDeviceClientException e) {
                log.error("Problem on registering device: ", e );
                sink.error(e);
            }

        });
    }

    private ProvisioningDeviceClient createProvisionClient() {
        try {
            return ProvisioningDeviceClient.create(
                    config.getGlobalEndpoint(),
                    config.getIdScope(),
                    PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL,
                    securityProvider);
        } catch (ProvisioningDeviceClientException e) {
            throw new IllegalStateException("Problem creating provisioning object: ", e);
        }
    }



}
