package com.GreenThumb.GT.DTO;

import lombok.Data;

@Data
public class Message {
    private String eventName;
    private String content;
    private String sender; // This will be set from the authentication token
}