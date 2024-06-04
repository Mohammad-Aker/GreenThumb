package com.GreenThumb.GT.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String sender;

    @NotBlank
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @NotBlank
    private String eventName;
}
