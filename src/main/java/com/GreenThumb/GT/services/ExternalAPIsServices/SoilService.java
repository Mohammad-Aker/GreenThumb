
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

    private final String apiToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzE2MDcxMjEzLCJpYXQiOjE3MTYwNzA5MTMsImp0aSI6IjQ0OGI0MmUwOTEyMjQyMmZiMWRiODQ3ODJmNTI1MjVjIiwidXNlcl9pZCI6NzZ9._Gncl19eyOU6nDXnM4rGhHNe3hWITAT5gpSUp_QOR1w";
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

