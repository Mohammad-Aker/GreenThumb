package com.GreenThumb.GT.models.ResourceRating;

import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.User.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "resource_rating")
public class ResourceRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_title", referencedColumnName = "title")
    private KnowledgeResource resource;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;

    private Double rating;
    private boolean reported;
    private String reportDescription;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public ResourceRating(User user, KnowledgeResource resource) {
    }

    public ResourceRating(User user, KnowledgeResource resource, Date date) {
    }

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }
}