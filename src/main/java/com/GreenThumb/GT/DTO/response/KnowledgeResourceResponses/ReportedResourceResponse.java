package com.GreenThumb.GT.DTO.response.KnowledgeResourceResponses;

import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportedResourceResponse {
    private String userEmail;
    private ResourceCategory category;
    private String resourceTitle;
    private String author;
    private String content;
    private String reportDescription;
}