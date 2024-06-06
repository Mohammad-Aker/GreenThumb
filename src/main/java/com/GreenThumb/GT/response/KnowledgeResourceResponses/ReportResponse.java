package com.GreenThumb.GT.response.KnowledgeResourceResponses;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    //private Long id;
    private String resourceTitle;
    private String userEmail;
    private String reportDescription; // Only included in report response
    private Date createdDate; // Timestamp
}