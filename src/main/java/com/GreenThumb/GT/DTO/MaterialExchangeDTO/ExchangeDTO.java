package com.GreenThumb.GT.DTO.MaterialExchangeDTO;

import com.GreenThumb.GT.models.MaterialExchange.Material.MaterialType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeDTO {
    private Long id;
    private Long materialId;
    private Long requestId;  // Link to resource requests
    private String fromUserEmail;
    private String toUserEmail;
    private String status;
    private MaterialType materialType;  // Add this field
}
