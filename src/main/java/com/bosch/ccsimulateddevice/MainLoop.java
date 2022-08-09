package com.bosch.ccsimulateddevice;

import com.bosch.ccsimulateddevice.device.SimulatedDevice;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageSentCallback;
import com.microsoft.azure.sdk.iot.device.exceptions.IotHubClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainLoop implements CommandLineRunner {

    private final SimulatedDevice device;


    @Override
    public void run(String... args) throws Exception {
        DeviceClient client = device.getDevice();
        client.open(false);
        client.sendEventAsync(new Message("The message"),new MessageSentCallbackImpl(),null);

    }

    private static class MessageSentCallbackImpl implements MessageSentCallback
    {
        @Override
        public void onMessageSent(Message sentMessage, IotHubClientException exception, Object callbackContext)
        {
            log.info("Message sent!");
        }
    }
}
