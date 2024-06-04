package com.GreenThumb.GT.rabbitMQ;


import com.GreenThumb.GT.models.ChatMessage;
import com.GreenThumb.GT.repositories.ChatMessageRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @RabbitListener(queues = "eventQueue")
    public void receiveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }
}
