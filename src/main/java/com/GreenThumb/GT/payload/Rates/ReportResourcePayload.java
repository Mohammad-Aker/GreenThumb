package com.GreenThumb.GT.payload.Rates;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResourcePayload {
    private String title;
    private String reportDescription;

}
