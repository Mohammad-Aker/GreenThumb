package com.GreenThumb.GT.payload.Rates;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateResourcePayload {
    private String title;
    private Double rating;

}
