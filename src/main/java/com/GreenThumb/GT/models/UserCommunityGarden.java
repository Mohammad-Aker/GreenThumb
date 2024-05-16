package com.GreenThumb.GT.models;

import com.GreenThumb.GT.models.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class UserCommunityGarden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    private Date dateJoined;

    private String status;

    private int contributionHours;

    private String feedback;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "garden_id", referencedColumnName = "id")
    private CommunityGarden communityGarden;
}
