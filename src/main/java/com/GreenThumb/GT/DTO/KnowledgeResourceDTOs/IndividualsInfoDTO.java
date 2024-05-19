package com.GreenThumb.GT.DTO.KnowledgeResourceDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualsInfoDTO {
    private String userEmail;
    private Double rating;
    private Date timestamp;
}
