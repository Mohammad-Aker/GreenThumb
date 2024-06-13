package com.GreenThumb.GT.DTO.MaterialExchangeDTO;


import com.GreenThumb.GT.models.MaterialExchange.Material.MaterialType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialCreationDTO {
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private MaterialType type;
    private String owner;

}
