package com.bosch.ccsimulateddevice;

import com.bosch.ccsimulateddevice.device.SimulatedDevice;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainLoop implements CommandLineRunner {

    private final SimulatedDevice device;


    @Override
    public void run(String... args) throws Exception {
        DeviceClient client = device.getDevice();

        log.info("starting report loop...");
        Mono.fromRunnable(() -> {
            while (true) {
                try {
                    log.info("sending report...");
                    client.updateReportedProperties(device.getTwin().report(client.getTwin().getReportedProperties()));
                } catch (InterruptedException | IotHubClientException e) {
                    throw new IllegalStateException("Problem updating reported properties: ", e);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }
            }
        }).subscribe();
    }


}
