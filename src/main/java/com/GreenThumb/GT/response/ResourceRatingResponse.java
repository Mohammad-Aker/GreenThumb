package com.GreenThumb.GT.response;

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


    private Long id;
    private String resourceTitle;
    private String userEmail;
    private Double rating; // Only included in rating response
    private String reportDescription; // Only included in report response
    private Date createdDate; // Timestamp


}
