package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.DTO.Message;
import com.GreenThumb.GT.exceptions.UserNotAuthorizedException;
import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.models.Events.Volunteering;
import com.GreenThumb.GT.repositories.EventsRepository.EventRepository;
import com.GreenThumb.GT.repositories.EventsRepository.VolunteeringRepository;
import com.GreenThumb.GT.security.config.eventsChat.RabbitMQJsonProducer;
import com.GreenThumb.GT.services.MessageStorageService;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/GreenThumb/api/chat")
public class MessageJsonController {

    private final RabbitMQJsonProducer jsonProducer;
    private final VolunteeringRepository volunteeringRepository;
    private final EventRepository eventsRepository;
    private final MessageStorageService messageStorageService;
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    @Autowired
    public MessageJsonController(RabbitMQJsonProducer jsonProducer, VolunteeringRepository volunteeringRepository,
                                 EventRepository eventsRepository, MessageStorageService messageStorageService,
                                 RabbitTemplate rabbitTemplate) {
        this.jsonProducer = jsonProducer;
        this.volunteeringRepository = volunteeringRepository;
        this.eventsRepository = eventsRepository;
        this.messageStorageService = messageStorageService;
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = new Jackson2JsonMessageConverter();
    }

    @PostMapping("/publish")
    public ResponseEntity<String> sendJsonMessage(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Message message) {
        String senderEmail = userDetails.getUsername();
        message.setSender(senderEmail);

        // Check if event exists
        Events event = eventsRepository.findByName(message.getEventName());
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        // Check if content is empty
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message content cannot be empty");
        }

        Volunteering volunteering = volunteeringRepository.findByUserEmailAndEventName(senderEmail, message.getEventName());
        if (volunteering == null) {
            throw new UserNotAuthorizedException("You are not authorized to publish to this chat");
        }

        jsonProducer.sendJsonMessage(message);
        return ResponseEntity.ok("Json message sent to rabbitMQ ...");
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String eventName) {
        String userEmail = userDetails.getUsername();

        Volunteering volunteering = volunteeringRepository.findByUserEmailAndEventName(userEmail, eventName);
        if (volunteering == null) {
            throw new UserNotAuthorizedException("You are not authorized to view this chat");
        }

        // Consume messages from the event queue
        List<Message> consumedMessages = new ArrayList<>();
        boolean keepConsuming = true;

        while (keepConsuming) {
            org.springframework.amqp.core.Message rawMessage = rabbitTemplate.receive("eventQueue");
            if (rawMessage == null) {
                break;
            }
            try {
                Message message = (Message) messageConverter.fromMessage(rawMessage);
                if (eventName.equals(message.getEventName())) {
                    consumedMessages.add(message);
                } else {
                    // Requeue the message
                    rabbitTemplate.send("eventQueue", rawMessage);
                    keepConsuming = false;
                }
            } catch (MessageConversionException e) {
                // Handle message conversion failure
                rabbitTemplate.send("eventQueue", rawMessage);
                keepConsuming = false;
            }
        }

        // Save consumed messages to MessageStorage
        for (Message message : consumedMessages) {
            messageStorageService.saveMessage(eventName, message);
        }

        List<Message> messages = messageStorageService.getMessagesForEvent(eventName);
        return ResponseEntity.ok(messages);
    }
}
