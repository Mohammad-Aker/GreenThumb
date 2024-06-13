package com.GreenThumb.GT.controllers.KnowledgeResourceControllers;

import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.KnowledgeResourceDTO;
import com.GreenThumb.GT.exceptions.MaterialNotFoundException;
import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.Views;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.services.KnowledgeResourceServices.KnowledgeResourceService;
import com.GreenThumb.GT.services.KnowledgeResourceServices.ResourceRatingService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/GreenThumb/api/learningResources")
public class KnowledgeResourceController {

    private final KnowledgeResourceService knowledgeResourceService;
    private final KnowledgeResourceRepository knowledgeResourceRepository;

    @Autowired
    public KnowledgeResourceController(KnowledgeResourceService knowledgeResourceService, ResourceRatingService resourceRatingService, KnowledgeResourceRepository knowledgeResourceRepository) {
        this.knowledgeResourceService = knowledgeResourceService;
        this.knowledgeResourceRepository = knowledgeResourceRepository;
    }



    //////////////////////////////////////////////////////// get /////////////////////////////////////////////////////

    @GetMapping("/allResources")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getAllResources() {
        try {
            List<KnowledgeResource> resources = knowledgeResourceService.getAllResources();
            if (resources.isEmpty()) {
                return ResponseEntity.noContent().build(); // Return 204 No Content when no resources are found
            }
            return ResponseEntity.ok(resources); // Return 200 OK with the list of resources
        } catch (Exception e) {
            // Log the exception details here if logging is configured
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving resources: " + e.getMessage());
        }
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

    @GetMapping("/download/{title}")
    public ResponseEntity<String> downloadFile(@PathVariable String title) {
        Optional<KnowledgeResource> documentOptional = knowledgeResourceRepository.findByTitle(title);

        if (!documentOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Resource with the title '" + title + "' not found.");
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


    @JsonView(Views.Public.class)
    @GetMapping("/sort")
    public ResponseEntity<?> sortResources(@RequestParam String sortField,
                                           @RequestParam String sortDirection) {
        try {
            List<KnowledgeResource> resources = knowledgeResourceService.sortResources(sortField, sortDirection);
            return ResponseEntity.ok(resources);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> filterResources(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Set<String> tags,
            @RequestParam(required = false) Double averageRating,
            @RequestParam(required = false) ResourceCategory category) {

        try {
            List<KnowledgeResource> filteredResources = knowledgeResourceService.filterResources(author, tags, averageRating, category);
            if (filteredResources.isEmpty()) {
                return ResponseEntity.noContent().build(); // Respond with 204 No Content if no resources match the filters
            }
            return ResponseEntity.ok(filteredResources);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error filtering resources: " + e.getMessage());
        }
    }




    //////////////////////////////////////////////////////// add create ////////////////////////////////////////////////
    @PreAuthorize("hasAuthority('EXPERT')")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("category") ResourceCategory category,
            @RequestParam(value = "tags", required = false) Set<String> tags) throws IOException {


            KnowledgeResourceDTO dto = new KnowledgeResourceDTO(title,user.getEmail(), author, category, tags);
            String response = knowledgeResourceService.storeFile(file, dto);
            return ResponseEntity.ok(response);

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

    /////////////////////////////////////////////// update //////////////////////////////////////////////////
    @PreAuthorize("hasAuthority('EXPERT')")
    @JsonView(Views.Public.class)
    @PatchMapping("/update")
    public ResponseEntity<?> updateResource(@AuthenticationPrincipal User user,
                                            @RequestParam String title,
                                            @RequestParam(required = false) String author,
                                            @RequestParam(required = false) ResourceCategory category,
                                            @RequestParam(required = false) Set<String> tags) {
        try {
            KnowledgeResource resource = knowledgeResourceService.updateResource(title, user.getEmail(), author, category, tags);
            return ResponseEntity.ok(resource);
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    /////////////////////////////////////////////////////// delete ///////////////////////////////////////////////////

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EXPERT')")
    @DeleteMapping("/deleteByTitle")
    public ResponseEntity<Void> deleteResourceByTitle(@AuthenticationPrincipal User user, @RequestParam String title) {
        try {
            String userRole = user.getRole().name();
            knowledgeResourceService.deleteResource(title, user.getEmail(), userRole);
            return ResponseEntity.ok().build();
        } catch (MaterialNotFoundException e) {
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
