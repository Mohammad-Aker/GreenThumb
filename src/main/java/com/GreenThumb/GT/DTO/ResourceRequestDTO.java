package com.GreenThumb.GT.DTO;

        import com.GreenThumb.GT.models.Resource.*;
        import lombok.AllArgsConstructor;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceRequestDTO {
    private Long id;
    private UserEmailDTO userEmail;
    private Long resourceId;
    private ResourceType resourceType;
    private int quantity;
    private String description;
    private String status;
}

