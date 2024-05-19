package com.GreenThumb.GT.DTO.KnowledgeResourceDTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResourceDTO {
    private String title;
    private String reportDescription;

}
