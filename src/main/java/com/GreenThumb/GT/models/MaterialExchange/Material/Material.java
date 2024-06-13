package com.GreenThumb.GT.models.MaterialExchange.Material;

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
@Table(name= "materials")
public class Material {
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
    private MaterialType type;


    @ManyToOne()
    @JoinColumn(name = "owner_email")
    private User owner;



}
