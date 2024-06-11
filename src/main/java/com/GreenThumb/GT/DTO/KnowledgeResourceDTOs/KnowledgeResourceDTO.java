package com.GreenThumb.GT.DTO.KnowledgeResourceDTOs;


import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeResourceDTO {
    private String title;
    private String userEmail;
    private String author;
    private ResourceType type;
    private ResourceCategory category;
    private Set<String> tags;


}