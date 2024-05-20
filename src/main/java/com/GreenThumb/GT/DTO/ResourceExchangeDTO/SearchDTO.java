package com.GreenThumb.GT.DTO.ResourceExchangeDTO;

import com.GreenThumb.GT.models.ResourceExchange.Resource.ResourceType;
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
