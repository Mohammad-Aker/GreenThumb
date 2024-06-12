package com.GreenThumb.GT.controllers.CropControllers;

import com.GreenThumb.GT.DTO.PestDetectionPropmt.ChatRequest;
import com.GreenThumb.GT.services.CropsServices.PestDetectionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("GreenThumb/api/pest-detection")
public class PestDetectionController {

    @Autowired
    private  PestDetectionService pestDetectionService;


    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;


    @PostMapping("/detect")
    public ResponseEntity<?> detectObject(@RequestParam("file") MultipartFile file) {
        try {

            return ResponseEntity.ok(pestDetectionService.detectObject(file));

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process the file");
        }
    }

    @PostMapping("/extract-class")
    public ResponseEntity<?> extractClassName(@RequestParam("file") MultipartFile file) {
        try {
            List<Map<String, Object>> results = pestDetectionService.detectObject(file);

            return ResponseEntity.ok(pestDetectionService.extractClassName(results));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to process the file: " + e.getMessage());
        }
    }


    @PostMapping("/handle-pest")
    public ResponseEntity<String> chatWithPestIdentification(@RequestParam("file") MultipartFile file) {
        try {
            // Detect the object
            String className = pestDetectionService.extractClassName(pestDetectionService.detectObject(file));

            // Check if class name was detected
            if (className == null || className.isEmpty()) {
                return ResponseEntity.ok("No pest detected.");
            }

            // openai prompt
            String prompt = "I have this pest (" + className + ") in my garden, tell me how to deal with it and take my precautions with my crops";
            ChatRequest request = new ChatRequest(model, prompt);

            // make the api call, return result as plain text
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Failed to get response from chat service: " + response.getStatusCode());
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to process the file: " + e.getMessage());
        }
    }

}



