package com.GreenThumb.GT.services;

import com.GreenThumb.GT.DTO.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MessageStorageService {

    private final ConcurrentMap<String, List<Message>> messagesByEvent = new ConcurrentHashMap<>();

    public void saveMessage(String eventName, Message message) {
        messagesByEvent.computeIfAbsent(eventName, k -> new ArrayList<>()).add(message);
    }

    public List<Message> getMessagesForEvent(String eventName) {
        return messagesByEvent.getOrDefault(eventName, new ArrayList<>());
    }

    public void clearMessagesForEvent(String eventName) {
        messagesByEvent.remove(eventName);
    }
}
