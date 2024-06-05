package com.GreenThumb.GT.services.KnowledgeResourceServices;

import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.KnowledgeResourceDTO;
import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;


import com.GreenThumb.GT.models.KnowledgeResource.ResourceExtension;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.GreenThumb.GT.services.KnowledgeResourceServices.ResourceRatingService.logger;

@Service
public class KnowledgeResourceService {

    private final KnowledgeResourceRepository knowledgeResourceRepository;

    private final UserRepository userRepository;


    @Autowired
    public KnowledgeResourceService(KnowledgeResourceRepository knowledgeResourceRepository, UserRepository userRepository) {
        this.knowledgeResourceRepository = knowledgeResourceRepository;
        this.userRepository =  userRepository;
    }


    ///////////////////////////////////////////// get  /////////////////////////////////////////////////

    //get all resources
    public List<KnowledgeResource> getAllResources() {
        return knowledgeResourceRepository.findAll();
    }


    //get resource by title
    public Optional<KnowledgeResource> getResourcesByTitle(String title) {
        try {
            return knowledgeResourceRepository.findByTitle(title);
        } catch (Exception e) {
            // Log and handle database or other exceptions appropriately
            logger.error("Error retrieving resource by title: {}", title, e);
            return Optional.empty();
        }
    }


    public List<KnowledgeResource> sortResources(String sortField, String sortDirection) {
        Sort sort = Sort.unsorted();
        if (sortField != null && !sortField.isEmpty() && sortDirection != null && !sortDirection.isEmpty()) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            sort = Sort.by(new Sort.Order(direction, sortField));
        }

        return knowledgeResourceRepository.findAll(sort);
    }


    public List<KnowledgeResource> filterResources(String author, String type, Set<String> tags, Double averageRating, ResourceCategory category) {
        // Start with all resources, then filter down
        List<KnowledgeResource> resources = knowledgeResourceRepository.findAll();

        // Filter by author if specified
        if (author != null && !author.isEmpty()) {
            resources = resources.stream()
                    .filter(resource -> author.equals(resource.getAuthor()))
                    .collect(Collectors.toList());
        }

        // Filter by type if specified
        if (type != null && !type.isEmpty()) {
            resources = resources.stream()
                    .filter(resource -> type.equals(resource.getType().name()))  // Assuming type is a String representation of the enum
                    .collect(Collectors.toList());
        }

        // Filter by averageRating if specified
        if (averageRating != null) {
            resources = resources.stream()
                    .filter(resource -> resource.getAverageRating() == averageRating)
                    .collect(Collectors.toList());
        }

        // Filter by tags if specified
        if (tags != null && !tags.isEmpty()) {
            resources = resources.stream()
                    .filter(resource -> resource.getTags() != null && resource.getTags().containsAll(tags))
                    .collect(Collectors.toList());
        }

        // Filter by category if specified
        if (category != null) {
            resources = resources.stream()
                    .filter(resource -> category.equals(resource.getCategory()))
                    .collect(Collectors.toList());
        }

        return resources;
    }

    ////////////////////////////////////////// add or create //////////////////////////////////////////////////
    public KnowledgeResource createResource(KnowledgeResourceDTO resourceDTO, String userEmail) {
        if (knowledgeResourceRepository.existsByTitle(resourceDTO.getTitle())) {
            throw new IllegalStateException("Resource with the given title already exists.");
        }

        if (knowledgeResourceRepository.existsByContentUrl(resourceDTO.getContentUrl())) {
            throw new IllegalStateException("Resource with the given content URL already exists.");
        }

        // Assuming you've mapped ResourceDTO to KnowledgeResource entity correctly
        KnowledgeResource resource = new KnowledgeResource();
        resource.setTitle(resourceDTO.getTitle());
        resource.setContentUrl(resourceDTO.getContentUrl());
        resource.setType(resourceDTO.getType());
        resource.setCategory(resourceDTO.getCategory());
        resource.setTags(resourceDTO.getTags());
        resource.setAuthor(resourceDTO.getAuthor());
        resource.setUser(userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail)));

        return knowledgeResourceRepository.save(resource);
    }


    public KnowledgeResource addTags(String title, Set<String> newTags, String currentUserEmail) {
        Optional<KnowledgeResource> optionalResource = knowledgeResourceRepository.findByTitle(title);
        if (optionalResource.isEmpty()) {
            throw new ResourceNotFoundException("Resource not found with title " + title);
        }

        KnowledgeResource resource = optionalResource.get();
        if (!resource.getUser().getEmail().equals(currentUserEmail)) {
            throw new UnauthorizedException("User is not authorized to update this resource");
        }

        resource.getTags().addAll(newTags);
        return knowledgeResourceRepository.save(resource);
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }



/////////////////////////////////////////////// update //////////////////////////////////////////////////


    public KnowledgeResource updateResource(String currentTitle, String currentUserEmail,
                                            String newTitle, String content, String author, ResourceExtension type,
                                            ResourceCategory category, Set<String> tags) {
        Optional<KnowledgeResource> resourceOptional = knowledgeResourceRepository.findByTitle(currentTitle);
        if (!resourceOptional.isPresent()) {
            throw new IllegalArgumentException("No resource found with title: " + currentTitle);
        }

        KnowledgeResource existingResource = resourceOptional.get();
        if (!existingResource.getUser().getEmail().equals(currentUserEmail)) {
            throw new SecurityException("You are not authorized to update this resource.");
        }

        if (newTitle != null && !newTitle.isEmpty() && !newTitle.equals(currentTitle)) {
            if (knowledgeResourceRepository.findByTitle(newTitle).isPresent()) {
                throw new IllegalArgumentException("A resource with the new title '" + newTitle + "' already exists.");
            }
            existingResource.setTitle(newTitle);
        }

        // Update other fields as necessary
        if (content != null) existingResource.setContentUrl(content);
        if (author != null) existingResource.setAuthor(author);
        if (type != null) existingResource.setType(type);
        if (category != null) existingResource.setCategory(category);
        if (tags != null && !tags.isEmpty()) existingResource.setTags(new HashSet<>(tags)); // ensure tags are updated properly

        return knowledgeResourceRepository.save(existingResource);
    }

    ////////////////////////////////////// delete ////////////////////////////////////////
/*
    public void deleteResource(String title, String userEmail) throws ResourceNotFoundException, UnauthorizedException {
        Optional<KnowledgeResource> resource = knowledgeResourceRepository.findByTitle(title);
        if (!resource.isPresent()) {
            throw new ResourceNotFoundException("Resource not found with title: " + title);
        }

        KnowledgeResource existingResource = resource.get();
        if (existingResource.getUser().getEmail().equals(userEmail) || existingResource.getUser().getRole().name().equals("ADMIN")) {
            knowledgeResourceRepository.delete(existingResource);
        } else {
            throw new UnauthorizedException("You are not authorized to delete this resource.");
        }

    }*/

    public void deleteResource(String title, String userEmail, String userRole) throws ResourceNotFoundException, UnauthorizedException {
        try {
            Optional<KnowledgeResource> resourceOpt = knowledgeResourceRepository.findByTitle(title);
            if (!resourceOpt.isPresent()) {
                throw new ResourceNotFoundException("Resource not found with title: " + title);
            }

            KnowledgeResource resource = resourceOpt.get();
            boolean isOwner = resource.getUser().getEmail().equals(userEmail) && resource.getUser().getRole().name().equals("EXPERT");
            boolean isAdmin = userRole.equals("ADMIN");

            if (isOwner || isAdmin) {
                knowledgeResourceRepository.delete(resource);
            } else {
                throw new UnauthorizedException("You are not authorized to delete this resource.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Database error occurred while deleting the resource.", e);
        }
    }

    //for admin
    public boolean deleteResourcesByCategory(ResourceCategory category) {
        List<KnowledgeResource> resources = knowledgeResourceRepository.findByCategory(category);
        if (resources == null || resources.isEmpty()) {
            return false; // No resources to delete
        }

        knowledgeResourceRepository.deleteAll(resources);
        return true; // Resources were deleted
    }

///////////////////////////////////////// Download ///////////////////////////////////////////


    private String transformToDownloadUrl(String url) {
        if (url.contains("/file/d/") && url.contains("/view")) {
            return url.replaceAll("/view.*", "/uc?export=download");
        }
        return null;  // Consider returning an exception or error message here instead of null
    }
    public String fetchUrlByTitle(String title) throws ResourceNotFoundException {
        KnowledgeResource resource = knowledgeResourceRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Resource titled '" + title + "' not found"));
        return resource.getContentUrl();
    }

    public ResponseEntity<InputStreamResource> downloadFile(String title) {
        try {
            String originalUrl = fetchUrlByTitle(title);
            String downloadUrl = transformToDownloadUrl(originalUrl);

            if (downloadUrl == null) {
                throw new IllegalStateException("Invalid Google Drive URL or transformation failed.");
            }

            RestTemplate restTemplate = new RestTemplate();
            InputStreamResource resource = restTemplate.execute(
                    URI.create(downloadUrl),
                    HttpMethod.GET,
                    null,
                    clientHttpResponse -> new InputStreamResource(clientHttpResponse.getBody()));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + title.replaceAll("[^\\w\\s]", "_") + ".pdf\""); // Assume PDF, adjust if needed
            headers.setContentType(MediaType.APPLICATION_PDF); // Assume PDF, adjust if needed

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}