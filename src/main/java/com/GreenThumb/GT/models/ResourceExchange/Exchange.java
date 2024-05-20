package com.GreenThumb.GT.models.ResourceExchange;

import com.GreenThumb.GT.models.ResourceExchange.Resource.Resource;
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
@Table(name = "exchanges")
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ResourceRequest request;

    @ManyToOne
    @JoinColumn(name = "from_user_email")
    private User fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_email")
    private User toUser;

    private String status;  // e.g., "pending", "completed"

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
