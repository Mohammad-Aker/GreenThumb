package com.GreenThumb.GT.DTO.MaterialExchangeDTO;

import com.GreenThumb.GT.models.MaterialExchange.Material.MaterialType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchDTO {
    private MaterialType materialType;
    private String ownerEmail;
    private String requestStatus;
    private String exchangeStatus;
}
