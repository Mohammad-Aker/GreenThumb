package com.GreenThumb.GT.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeDTO {
    private Long id;
    private Long resourceId;
    private Long requestId;  // Add this field to link to resource requests
    private String fromUserEmail;
    private String toUserEmail;
    private String status;
}
