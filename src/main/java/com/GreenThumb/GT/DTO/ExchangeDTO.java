package com.GreenThumb.GT.DTO;

import com.GreenThumb.GT.models.Resource.ResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeDTO {
    private Long id;
    private Long resourceId;
    private Long requestId;  // Link to resource requests
    private String fromUserEmail;
    private String toUserEmail;
    private String status;
    private ResourceType resourceType;  // Add this field
}
