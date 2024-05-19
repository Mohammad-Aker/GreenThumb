package com.GreenThumb.GT.services.ExternalAPIsServices;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SoilDataService {

    public Map<String, Object> getSoilData(float longitude, float latitude) throws Exception {
        String url = "https://rest.isric.org/soilgrids/v2.0/properties/query?" +
                "lon=" + longitude + "&lat=" + latitude + "&property=clay&property=sand&depth=0-5cm&value=mean&value=uncertainty";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        return response;
    }



}
