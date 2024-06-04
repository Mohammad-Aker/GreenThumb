package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.DTO.Chatting.ChatMessageDTO;
import com.GreenThumb.GT.models.ChatMessage;
import com.GreenThumb.GT.services.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("GreenThumb/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessageDTO chatMessageDTO, @AuthenticationPrincipal UserDetails userDetails) {
        chatMessageService.sendMessage(chatMessageDTO, userDetails.getUsername());
    }

    @GetMapping("/messages/{eventName}")
    public List<ChatMessage> getMessagesForEvent(@PathVariable String eventName, @AuthenticationPrincipal UserDetails userDetails) {
        return chatMessageService.getMessagesForEvent(eventName, userDetails.getUsername());
    }
}
