package com.GreenThumb.GT.eventsChat;

import com.GreenThumb.GT.exceptions.UserNotAuthorizedException;
import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.models.Events.Volunteering;
import com.GreenThumb.GT.repositories.EventsRepository.EventRepository;
import com.GreenThumb.GT.repositories.EventsRepository.VolunteeringRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Autowired
    public MessageJsonController(RabbitMQJsonProducer jsonProducer, VolunteeringRepository volunteeringRepository,
                                 EventRepository eventsRepository, MessageStorageService messageStorageService,
                                 RabbitTemplate rabbitTemplate) {
        this.jsonProducer = jsonProducer;
        this.volunteeringRepository = volunteeringRepository;
        this.eventsRepository = eventsRepository;
        this.messageStorageService = messageStorageService;
        this.rabbitTemplate = rabbitTemplate;
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

        // Consume messages from the queue
        List<Message> consumedMessages = new ArrayList<>();
        while (true) {
            Message message = (Message) rabbitTemplate.receiveAndConvert("eventQueue");
            if (message == null) {
                break;
            }
            consumedMessages.add(message);
        }

        // Save consumed messages to MessageStorage
        for (Message message : consumedMessages) {
            messageStorageService.saveMessage(eventName, message);
        }

        List<Message> messages = messageStorageService.getMessagesForEvent(eventName);
        return ResponseEntity.ok(messages);
    }
}