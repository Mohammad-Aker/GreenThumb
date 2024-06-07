package com.GreenThumb.GT.models.Events;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private Date date;

    @NotBlank
    private String location;

    @NotBlank
    private String duration;

    @Positive
    private int maxParticipants;

    @NotBlank
    private String organizer;

    @NotBlank
    private String eventType;


    @ManyToOne
    private Partner partner;
}
