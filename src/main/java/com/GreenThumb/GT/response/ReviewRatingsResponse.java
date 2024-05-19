package com.GreenThumb.GT.response;


import com.GreenThumb.GT.payload.Rates.IndividualsInfoPayload;
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
    private List<IndividualsInfoPayload> ratings;
}