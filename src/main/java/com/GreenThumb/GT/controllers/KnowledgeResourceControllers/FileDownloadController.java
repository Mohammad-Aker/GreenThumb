package com.GreenThumb.GT.controllers.KnowledgeResourceControllers;

import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.services.KnowledgeResourceServices.KnowledgeResourceService;
import com.GreenThumb.GT.services.KnowledgeResourceServices.ResourceRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.GreenThumb.GT.services.KnowledgeResourceServices.ResourceRatingService.logger;

@RestController
@RequestMapping("/file")
public class FileDownloadController {
    private final KnowledgeResourceRepository knowledgeResourceRepository;

    @Autowired
    public FileDownloadController(KnowledgeResourceRepository knowledgeResourceRepository) {
        this.knowledgeResourceRepository = knowledgeResourceRepository;
    }

    @GetMapping("/download/{title}")
    public ResponseEntity<InputStreamResource> downloadResource(@PathVariable String title, @RequestParam(required = false) String fileType) {
        try {
            KnowledgeResource resource = knowledgeResourceRepository.findByTitle(title)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource titled '" + title + "' not found"));

            String downloadUrl = transformToDownloadUrl(resource.getContentUrl());
            if (downloadUrl == null) {
                throw new IllegalStateException("Failed to transform the Google Drive URL");
            }

            RestTemplate restTemplate = new RestTemplate();
            ByteArrayResource fileResource = restTemplate.execute(
                    URI.create(downloadUrl),
                    HttpMethod.GET,
                    null,
                    response -> {
                        try (InputStream is = response.getBody(); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                            int nRead;
                            byte[] data = new byte[1024];
                            while ((nRead = is.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, nRead);
                            }
                            buffer.flush();
                            return new ByteArrayResource(buffer.toByteArray());
                        }
                    });

            HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
            String mimeType = connection.getContentType();

            // Fallback to application/octet-stream if mimeType is null
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            String fileExtension = getExtensionByMimeType(mimeType);
            String filename = title.replaceAll("[^\\w\\s]", "_") + fileExtension;
            MediaType mediaType = MediaType.parseMediaType(mimeType);

            HttpHeaders headers = new HttpHeaders();
            if (mimeType.contains("pdf") || (fileType != null && fileType.equalsIgnoreCase("pdf"))) {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
                headers.setContentType(MediaType.APPLICATION_PDF);
            } else if (mimeType.contains("mp4") || (fileType != null && fileType.equalsIgnoreCase("mp4"))) {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"");
                headers.setContentType(MediaType.parseMediaType("video/mp4"));
            } else {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
                headers.setContentType(mediaType);
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileResource.contentLength())
                    .body(new InputStreamResource(fileResource.getInputStream()));
        } catch (ResourceNotFoundException | IllegalStateException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String getExtensionByMimeType(String mimeType) {
        if (mimeType == null) {
            return "";
        } else if (mimeType.contains("pdf")) {
            return ".pdf";
        } else if (mimeType.contains("mp4")) {
            return ".mp4";
        } else if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
            return ".jpg";
        } else if (mimeType.contains("png")) {
            return ".png";
        } else {
            return ""; // default or unknown types
        }
    }

    private String transformToDownloadUrl(String url) {
        if (url != null && url.contains("drive.google.com/file/d/")) {
            String fileId = null;
            Pattern pattern = Pattern.compile("/file/d/([^/]+)/");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                fileId = matcher.group(1);  // This will extract the file ID from the URL
            }

            if (fileId != null) {
                return "https://drive.google.com/uc?export=download&id=" + fileId;
            }
        }
        return null;  // Return null or original URL if no transformation is needed
    }
}