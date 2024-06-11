package com.GreenThumb.GT.DTO.response.KnowledgeResourceResponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRatingResponse {


    //private Long id;
    private String resourceTitle;
    private String userEmail;
    private Double rating; // Only included in rating response
    private Date createdDate; // Timestamp


}
