package com.GreenThumb.GT.models.ResourceExchange;

import com.GreenThumb.GT.models.ResourceExchange.Resource.Resource;
import com.GreenThumb.GT.models.ResourceExchange.Resource.ResourceType;
import com.GreenThumb.GT.models.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="resource_requests")
public class ResourceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email")
    private User user;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    private ResourceType resourceType;
    private int quantity;
    private String description;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
