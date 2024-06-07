package com.GreenThumb.GT.eventsChat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQJsonConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQJsonConsumer.class);

    private final MessageStorageService messageStorageService;

    @Autowired
    public RabbitMQJsonConsumer(MessageStorageService messageStorageService) {
        this.messageStorageService = messageStorageService;
    }

    public void consumeJsonMessage(com.GreenThumb.GT.eventsChat.Message message) {
        LOGGER.info(String.format("Received JSON message -> %s", message.toString()));
        messageStorageService.saveMessage(message.getEventName(), message);
    }
}
