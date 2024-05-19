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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
//@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/resources")
public class KnowledgeResourceController {

    private final KnowledgeResourceService knowledgeResourceService;
    private final KnowledgeResourceRepository knowledgeResourceRepository;

    @Autowired
    public KnowledgeResourceController(KnowledgeResourceService knowledgeResourceService, ResourceRatingService resourceRatingService, KnowledgeResourceRepository knowledgeResourceRepository) {
        this.knowledgeResourceService = knowledgeResourceService;
        this.knowledgeResourceRepository = knowledgeResourceRepository;
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





    //////////////////////////////////////////////////////// add create ////////////////////////////////////////////////


    @PostMapping("/create")
    public ResponseEntity<?> createResource(@Valid @RequestBody KnowledgeResourceDTO resourceDTO, Authentication authentication) {
        try {
            String userEmail = ((User) authentication.getPrincipal()).getEmail();
            KnowledgeResource createdResource = knowledgeResourceService.createResource(resourceDTO, userEmail);
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


////////////////////////////////////////////////////////////
    @GetMapping("/resources/download/{title}")
    public ResponseEntity<Object> downloadResource(@PathVariable Long resourceId) {
        KnowledgeResource resource = knowledgeResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (resource.getContentUrl() != null && !resource.getContentUrl().isEmpty()) {
            URI googleDriveDownloadUri = URI.create(resource.getContentUrl().replace("/view?usp=sharing", "/uc?export=download"));
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(googleDriveDownloadUri);
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        }

        throw new IllegalStateException("Resource does not have a valid Google Drive URL");
    }


    }
