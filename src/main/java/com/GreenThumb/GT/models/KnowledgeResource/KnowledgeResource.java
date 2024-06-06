package com.GreenThumb.GT.models.KnowledgeResource;

import com.GreenThumb.GT.models.ResourceRating.ResourceRating;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "knowledge_resource")
public class KnowledgeResource {

    @Id
    @NotBlank
    @Column(unique = true)
    @JsonView(Views.Public.class)
    private String title;

    @NotBlank
    @JsonView(Views.Public.class)
    @Lob
    private byte[] data;



    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    @JsonIgnoreProperties(value = {"role","password", "phoneNumber", "enabled", "username", "authorities", "accountNonExpired", "credentialsNonExpired", "accountNonLocked"})
    @JsonView(Views.Public.class)
    private User user;

    @JsonView(Views.Public.class)
    private String author;


    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ResourceType type;


    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private ResourceCategory category;

    @ElementCollection
    @JsonView(Views.Public.class)
    private Set<String> tags;

    @JsonView(Views.Public.class)
    private double averageRating;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Public.class)
    private Date createdDate;

    @JsonView(Views.Internal.class)
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceRating> ratings;

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
}