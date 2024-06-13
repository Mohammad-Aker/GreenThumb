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
public class MaterialRequestDTO {
    private Long id;
    private UserEmailDTO userEmail;
    private Long materialId;
    private MaterialType materialType;
    private int quantity;
    private String description;
    private String status;
}

