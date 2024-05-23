package com.GreenThumb.GT.services.CropsServices;

import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PestDetectionService {

    private final String apiUrl = "https://yolo-object-detection-ddg3beiipq-uc.a.run.app/detect";
    private final RestTemplate restTemplate = new RestTemplate();  // Initialize RestTemplate here or via @Autowired if configured globally

    public List<Map<String, Object>> detectObject(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource fileResource = new InputStreamResource(file.getInputStream()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename(); // Provide filename to resolve multipart issues
            }

            @Override
            public long contentLength() {
                return file.getSize(); // Provide content length
            }
        };

        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(apiUrl, requestEntity, List.class);
    }




    public String extractClassName(List<Map<String, Object>> detectionResults) {
        if (detectionResults != null && !detectionResults.isEmpty()) {
            Map<String, Object> firstResult = detectionResults.get(0);
            return (String) firstResult.get("class");
        }
        return null;
    }

}
