package com.GreenThumb.GT.security.config.eventsChat;

import com.GreenThumb.GT.DTO.Message;
import com.GreenThumb.GT.services.MessageStorageService;
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

    public void consumeJsonMessage(Message message) {
        LOGGER.info(String.format("Received JSON message -> %s", message.toString()));
        messageStorageService.saveMessage(message.getEventName(), message);
    }
}