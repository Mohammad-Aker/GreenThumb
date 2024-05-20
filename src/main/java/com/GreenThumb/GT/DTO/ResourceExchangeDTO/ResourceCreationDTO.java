package com.GreenThumb.GT.DTO.ResourceExchangeDTO;


import com.GreenThumb.GT.models.ResourceExchange.Resource.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceCreationDTO {
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private ResourceType type;
    private String owner;

}
