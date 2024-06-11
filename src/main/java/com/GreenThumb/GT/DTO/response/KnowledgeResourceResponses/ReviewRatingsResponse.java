package com.GreenThumb.GT.DTO.response.KnowledgeResourceResponses;


import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.IndividualsInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRatingsResponse {
    private String resourceTitle;
    private Double averageRating;
    private List<IndividualsInfoDTO> ratings;
}