package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceType;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.payload.Rates.Views;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.services.FileStorageService;
import com.GreenThumb.GT.services.KnowledgeResourceService;
import com.GreenThumb.GT.services.ResourceRatingService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.GreenThumb.GT.services.ResourceRatingService.logger;

@RestController
//@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/resources")
public class KnowledgeResourceController {

    private final KnowledgeResourceService knowledgeResourceService;
    private final FileStorageService fileStorageService;

    @Autowired
    public KnowledgeResourceController(KnowledgeResourceService knowledgeResourceService, ResourceRatingService resourceRatingService, KnowledgeResourceRepository knowledgeResourceRepository
            , FileStorageService fileStorageService
    ) {
        this.knowledgeResourceService = knowledgeResourceService;
        this.fileStorageService = fileStorageService;
    }


    //////////////////////////////////////////////////////// get /////////////////////////////////////////////////////
    // get all resources
    @GetMapping("/allResources")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<KnowledgeResource>> getAllResources() {
        return ResponseEntity.ok(knowledgeResourceService.getAllResources());
    }

    //get resource by title
    @GetMapping("/search")
    public ResponseEntity<KnowledgeResource> getResourceByTitle(@RequestParam String title) {
        Optional<KnowledgeResource> resource = knowledgeResourceService.getResourcesByTitle(title);
        return resource.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/sort")
    public List<KnowledgeResource> sortResources(@RequestParam(required = false) String sortField,
                                                 @RequestParam(required = false) String sortDirection) {
        return knowledgeResourceService.sortResources(sortField, sortDirection);
    }


    @GetMapping("/filter")
    public ResponseEntity<List<KnowledgeResource>> filterResources(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) Double averageRating,
            @RequestParam(required = false) ResourceCategory category) {

        List<KnowledgeResource> filteredResources = knowledgeResourceService.filterResources(author, type, tags, averageRating, category);
        return ResponseEntity.ok(filteredResources);
    }



    @GetMapping("/view/{title}")
    public ResponseEntity<?> viewFile(@PathVariable String title, HttpServletRequest request) {
        try {
            KnowledgeResource resource = knowledgeResourceService.getResourceByTitle(title);

            // Load file as Resource
            Resource file = fileStorageService.loadFileAsResource(resource.getContentUrl());

            // Try to determine file's content type
            String contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";  // Default MIME type
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getContentUrl() + "\"")
                    .body(file);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body("Could not determine file type.");
        }
    }


    //////////////////////////////////////////////////////// add create ////////////////////////////////////////////////



    @PostMapping("/upload")
    public ResponseEntity<?> uploadFileAndCreateResource(@AuthenticationPrincipal User user,
                                                         @RequestParam("file") MultipartFile file,
                                                         @RequestParam("title") String title,
                                                         @RequestParam("type") ResourceType type,
                                                         @RequestParam("category") ResourceCategory category,
                                                         @RequestParam(required = false) Set<String> tags,
                                                         @RequestParam(required = false) String author) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is required and cannot be empty.");
        }

        // Store the file and get the storage path
        String fileDownloadUri;
        try {
            fileDownloadUri = fileStorageService.storeFile(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store file.");
        }

        // Create a new resource with the file path
        KnowledgeResource resource = new KnowledgeResource();
        resource.setAuthor(author);
        resource.setTitle(title);
        resource.setContentUrl(fileDownloadUri);
        resource.setType(type);
        resource.setCategory(category);
        resource.setTags(tags);
        resource.setUser(user);



        // Save the resource using your service
        Optional<KnowledgeResource> createdResourceOpt = knowledgeResourceService.createResource(resource);
        if (createdResourceOpt.isPresent()) {
            return ResponseEntity.ok(createdResourceOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Resource with given title or content URL already exists.");
        }
    }
    @GetMapping("/download/{title}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String title) {
        KnowledgeResource resource = knowledgeResourceService.getResourceByTitle(title);
        if (resource == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }

        Resource file = fileStorageService.loadFileAsResource(resource.getContentUrl());
        if (file == null || !file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        String filename = URLEncoder.encode(Objects.requireNonNull(file.getFilename()), StandardCharsets.UTF_8);
        logger.info("Preparing to download file: {}", filename);

        String contentType = "application/octet-stream";  // Default MIME type
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    // Exception handler in your @Controller or @ControllerAdvice
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }




    //add tags to existing resource
    @PutMapping("/{title}/tags")
    public ResponseEntity<KnowledgeResource> addTags(@AuthenticationPrincipal User user, @PathVariable String title, @RequestBody Set<String> tags) {
        try {
            KnowledgeResource updatedResource = knowledgeResourceService.addTags(title, tags, user.getEmail());
            return ResponseEntity.ok(updatedResource);
        } catch (KnowledgeResourceService.ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (KnowledgeResourceService.UnauthorizedException e) {
            return ResponseEntity.status(403).body(null); // 403 Forbidden
        }
    }

    /////////////////////////////////////////////// update //////////////////////////////////////////////////


    //update resource info
    @PutMapping("/update")
    public ResponseEntity<KnowledgeResource> updateResource(@AuthenticationPrincipal User user,
                                                            @RequestParam String currentTitle,
                                                            @RequestParam(required = false) String newTitle,
                                                            @RequestParam(required = false) String content,
                                                            @RequestParam(required = false) String author,
                                                            @RequestParam(required = false) ResourceType type,
                                                            @RequestParam(required = false) ResourceCategory category,
                                                            @RequestParam(required = false) Set<String> tags) {
        try {
            ResourceType resourceType = (type != null && !type.name().isEmpty()) ? ResourceType.valueOf(type.name()) : null;
            ResourceCategory resourceCategory = (category != null && !category.name().isEmpty()) ? ResourceCategory.valueOf(category.name()) : null;
            KnowledgeResource resource = knowledgeResourceService.updateResource(currentTitle, user.getEmail(), newTitle, content, author, resourceType, resourceCategory, tags);
            return ResponseEntity.ok(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /////////////////////////////////////////////////////// delete ///////////////////////////////////////////////////


    // Delete a specific resource by Title
    @DeleteMapping("/deleteByTitle")
    public ResponseEntity<Void> deleteResourceByTitle(@AuthenticationPrincipal User user, @RequestParam String title//, @RequestParam String userEmail
    ) {
        try {
            boolean deleted = knowledgeResourceService.deleteResource(title, user.getEmail());
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    //for admin
    @DeleteMapping("/deleteByCategory")
    public ResponseEntity<Void> deleteResourcesByCategory(@RequestParam ResourceCategory category) {
        knowledgeResourceService.deleteResourcesByCategory(category);
        return ResponseEntity.ok().build();
    }




    }
