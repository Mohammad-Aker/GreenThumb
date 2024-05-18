package com.GreenThumb.GT.DTO;

import com.GreenThumb.GT.models.Resource.ResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO {
    private ResourceType resourceType;
    private String ownerEmail;
    private String requestStatus;
    private String exchangeStatus;
}
