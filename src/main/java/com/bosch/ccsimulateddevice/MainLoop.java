package com.bosch.ccsimulateddevice;

import com.bosch.ccsimulateddevice.device.SimulatedDevice;
import com.microsoft.azure.sdk.iot.device.ConnectionStatusChangeContext;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import com.microsoft.azure.sdk.iot.device.twin.ReportedPropertiesUpdateResponse;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import com.microsoft.azure.sdk.iot.device.twin.TwinCollection;
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
    private Twin twin;


    @Override
    public void run(String... args) throws Exception {
        DeviceClient client = device.getDevice();
        client.open(false);

        client.setConnectionStatusChangeCallback(this::handleConnectionStatuschange, null);

        client.subscribeToDesiredProperties((twin, context) -> log.info("Desired properties updated: {}", twin), null);

        //not working with mqtt
        twinLoop(client);
        //working
        //messageLoop(client);


        //twin = client.getTwin();
        //client.getTwinAsync((twin, clientException, context) -> this.twin = twin, null);
        //Thread.currentThread().sleep(5000);
        //log.info("Twin: {}", twin);


    }

    private void messageLoop(DeviceClient client) {
        Mono.fromRunnable(() -> {
            int count = 0;
            while (true){
                try {
                    client.sendEventAsync(new Message("The message"),(message, exception, context) ->
                            log.info("Message sent: {}({})", message, context),count++);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }}).subscribe();
    }

    private void twinLoop(DeviceClient client) {
        try {
            twin = client.getTwin();
            log.info("Twin: {}", twin);

            TwinCollection reportedProperties = twin.getReportedProperties();
            reportedProperties.put("chargingmode", 7);


            ReportedPropertiesUpdateResponse response = client.updateReportedProperties(reportedProperties);
            twin.getReportedProperties().setVersion(response.getVersion());

            reportedProperties.put("chargingtime", "13:00");
            client.updateReportedProperties(reportedProperties);
            twin.getReportedProperties().setVersion(response.getVersion());
            log.info("Reported properties updated: {}", twin);
        } catch (Exception e) {
            log.error("Error updating reported properties: ", e);
        }
    }

    private void handleConnectionStatuschange(ConnectionStatusChangeContext connectionStatusChangeContext){
        IotHubConnectionStatus status = connectionStatusChangeContext.getNewStatus();
        IotHubConnectionStatusChangeReason statusChangeReason = connectionStatusChangeContext.getNewStatusReason();
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
//            try {
//                device.getDevice().getTwin();
//            } catch (Exception e) {
//                log.error("Error getting twin: ", e);
//            }
        }
    }


}
