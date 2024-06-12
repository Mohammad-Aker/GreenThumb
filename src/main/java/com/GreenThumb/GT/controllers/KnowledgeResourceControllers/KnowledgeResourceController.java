package com.GreenThumb.GT.controllers.KnowledgeResourceControllers;

import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.KnowledgeResourceDTO;
import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceType;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.Views;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.services.KnowledgeResourceServices.KnowledgeResourceService;
import com.GreenThumb.GT.services.KnowledgeResourceServices.ResourceRatingService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
//@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/GreenThumb/api/learning-resources")
public class KnowledgeResourceController {

    private final KnowledgeResourceService knowledgeResourceService;
    private final KnowledgeResourceRepository knowledgeResourceRepository;

    @Autowired
    public KnowledgeResourceController(KnowledgeResourceService knowledgeResourceService, ResourceRatingService resourceRatingService, KnowledgeResourceRepository knowledgeResourceRepository) {
        this.knowledgeResourceService = knowledgeResourceService;
        this.knowledgeResourceRepository = knowledgeResourceRepository;
    }


    @GetMapping("/get-all")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<KnowledgeResource>> getAllResources() {
        return ResponseEntity.ok(knowledgeResourceService.getAllResources());
    }


    @GetMapping("/search")
    @JsonView(Views.Public.class)  // Apply the Public view to only serialize fields included in the Public view
    public ResponseEntity<?> getResourceByTitle(@RequestParam String title) {
        Optional<KnowledgeResource> resource = knowledgeResourceService.getResourcesByTitle(title);
        if (!resource.isPresent()) {
            // Return a ResponseEntity with a body containing the error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"No resource found with the title: " + title + "\"}");
        }
        return ResponseEntity.ok(resource.get());
    }



    @JsonView(Views.Public.class)
    @GetMapping("/sort")
    public List<KnowledgeResource> sortResources(@RequestParam(required = false) String sortField,
                                                 @RequestParam(required = false) String sortDirection) {
        return knowledgeResourceService.sortResources(sortField, sortDirection);
    }




    @GetMapping("/filter")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<KnowledgeResource>> filterResources(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) Double averageRating,
            @RequestParam(required = false) ResourceCategory category) {

        List<KnowledgeResource> filteredResources = knowledgeResourceService.filterResources(author, type, tags, averageRating, category);
        return ResponseEntity.ok(filteredResources);
    }



    @GetMapping("/download/{title}")
    public ResponseEntity<String> downloadFile(@PathVariable String title) {
        Optional<KnowledgeResource> documentOptional = knowledgeResourceRepository.findByTitle(title);

        if (!documentOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        KnowledgeResource document = documentOptional.get();
        Path path = Paths.get(System.getProperty("user.home"), "Downloads", document.getTitle() + ".pdf");

        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {
            outputStream.write(document.getData());
            return ResponseEntity.ok("File saved successfully at " + path.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file: " + e.getMessage());
        }
    }





      @PreAuthorize("hasAuthority('EXPERT')")
    @PostMapping("/create")
    public ResponseEntity<?> createResource(@Valid @RequestBody KnowledgeResourceDTO resourceDTO, @RequestParam("file") MultipartFile file) {
        try {
            String createdResource = knowledgeResourceService.storeFile(file, resourceDTO);
            return ResponseEntity.ok(createdResource);
        } catch (IllegalStateException e) {
            // Handle exceptions related to resource uniqueness
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Handle exceptions related to user existence or other argument issues
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.internalServerError().body("An error occurred while creating the resource.");
        }
    }



    @PreAuthorize("hasAuthority('EXPERT')")
    @JsonView(Views.Public.class)
    @PutMapping("/{title}/tags")
    public ResponseEntity<?> addTags(@AuthenticationPrincipal User user, @PathVariable String title, @RequestBody Set<String> tags) {
        try {
            KnowledgeResource updatedResource = knowledgeResourceService.addTags(title, tags, user.getEmail());
            return ResponseEntity.ok(updatedResource);
        } catch (KnowledgeResourceService.ResourceNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Resource not found with title: " + title);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (KnowledgeResourceService.UnauthorizedException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User is not authorized to update this resource");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }

    @PreAuthorize("hasAuthority('EXPERT')")
    @JsonView(Views.Public.class)
    @PatchMapping("/update")
    public ResponseEntity<?> updateResource(@AuthenticationPrincipal User user,
                                            @RequestParam String currentTitle,
                                            @RequestParam(required = false) String newTitle,
                                            @RequestParam(required = false) String author,
                                            @RequestParam(required = false) ResourceType type,
                                            @RequestParam(required = false) ResourceCategory category,
                                            @RequestParam(required = false) Set<String> tags) {
        try {
            KnowledgeResource resource = knowledgeResourceService.updateResource(currentTitle, user.getEmail(), newTitle, author, type, category, tags);
            return ResponseEntity.ok(resource);
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EXPERT')")
    @DeleteMapping("/deleteByTitle")
    public ResponseEntity<Void> deleteResourceByTitle(@AuthenticationPrincipal User user, @RequestParam String title) {
        try {
            String userRole = user.getRole().name();
            knowledgeResourceService.deleteResource(title, user.getEmail(), userRole);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (KnowledgeResourceService.UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteByCategory")
    public ResponseEntity<Void> deleteResourcesByCategory(@RequestParam ResourceCategory category) {
        if (category == null) {
            return ResponseEntity.badRequest().build(); // Respond with 400 Bad Request if category is null
        }

        try {
            boolean deleted = knowledgeResourceService.deleteResourcesByCategory(category);
            if (!deleted) {
                return ResponseEntity.notFound().build(); // Respond with 404 Not Found if no resources found for the category
            }
            return ResponseEntity.ok().build(); // Respond with 200 OK if deletion was successful
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Respond with 403 Forbidden if the user is not authorized
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Generic error handling for other exceptions
        }
    }



}
