package com.GreenThumb.GT.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private String eventName;
    private String content;
    private String sender;
    private LocalDateTime timestamp;
}
