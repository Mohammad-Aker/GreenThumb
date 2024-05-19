package com.GreenThumb.GT.payload.Rates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualsInfoPayload {
    private String userEmail;
    private Double rating;
    private Date timestamp;
}
