package com.bosch.ccsimulateddevice;

import com.bosch.ccsimulateddevice.configuration.GeneralConfig;
import com.microsoft.azure.sdk.iot.device.ConnectionStatusChangeContext;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import com.microsoft.azure.sdk.iot.device.twin.ReportedPropertiesUpdateResponse;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import com.microsoft.azure.sdk.iot.device.twin.TwinCollection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainLoop implements CommandLineRunner {

    private final DeviceClient client;

    private final GeneralConfig config;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    @Override
    public void run(String... args) throws Exception {
        client.open(false);

        client.setConnectionStatusChangeCallback(this::handleConnectionStatuschange, null);

        client.subscribeToDesiredProperties((twinData, context) -> log.info("Desired properties updated: {}", twinData), null);


        executor.scheduleWithFixedDelay(
                ()->twinLoop(client),
                0,
                //for values below 1 it will only execute once
                config.getMessageIntervalSeconds() > 0 ? config.getMessageIntervalSeconds() : Long.MAX_VALUE,
                TimeUnit.SECONDS);

    }

    @SneakyThrows
    private void twinLoop(DeviceClient client) {
        Twin twin = client.getTwin();
            log.info("Twin: {}", twin);

            TwinCollection reportedProperties = twin.getReportedProperties();
            reportedProperties.put("chargingmode", 7);
            reportedProperties.put("chargingtime", "13:00");


            ReportedPropertiesUpdateResponse response = client.updateReportedProperties(reportedProperties);
            twin.getReportedProperties().setVersion(response.getVersion());

            client.updateReportedProperties(reportedProperties);
            log.info("Reported properties updated: {}", twin);
    }

    private void handleConnectionStatuschange(ConnectionStatusChangeContext connectionStatusChangeContext){
        IotHubConnectionStatus status = connectionStatusChangeContext.getNewStatus();
        Throwable throwable = connectionStatusChangeContext.getCause();

        if (throwable != null)
        {
            log.error("Connection status change callback threw an exception: ", throwable);
        }

        if (status == IotHubConnectionStatus.DISCONNECTED)
        {
            log.info("The connection was lost, and is not being re-established." +
                    " Look at provided exception for how to resolve this issue." +
                    " Cannot send messages until this issue is resolved, and you manually re-open the device client");
        }
        else if (status == IotHubConnectionStatus.DISCONNECTED_RETRYING)
        {
            log.info("The connection was lost, but is being re-established." +
                    " Can still send messages, but they won't be sent until the connection is re-established");
        }
        else if (status == IotHubConnectionStatus.CONNECTED)
        {
            log.info("The connection was successfully established. Can send messages.");
        }
    }


}
