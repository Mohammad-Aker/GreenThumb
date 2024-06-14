
package com.GreenThumb.GT.services.ExternalAPIsServices;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SoilService {

    private final String apiToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoicmVmcmVzaCIsImV4cCI6MTcxODQ1NTk1NSwiaWF0IjoxNzE4MzY5NTU1LCJqdGkiOiJiNDI4ZDZiNDRjNTU0Y2VlOTRlMzZiOTdjNjcwYmM5OCIsInVzZXJfaWQiOjc2fQ.6K9XfYj8cMLOP1imwk6w8p9sUOUoDnrdb2mXC3VFcD0";
    private final RestTemplate restTemplate;

    public SoilService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> getSoilData(float longitude, float latitude) throws Exception {
        String url = "https://soil.narc.gov.np/soil/soildata/?lon=" + longitude + "&lat=" + latitude;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}

