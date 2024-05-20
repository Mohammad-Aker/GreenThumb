package com.GreenThumb.GT.models.ResourceExchange.Resource;

import com.GreenThumb.GT.models.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="resources")
public class Resource {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotBlank
    private Long id;
    @NotBlank

    private String name;

    private String description;

    private double price;

    private int quantity ;
    @Enumerated(EnumType.STRING)
    private ResourceType type;


    @ManyToOne()
    @JoinColumn(name = "owner_email")
    private User owner;



}
