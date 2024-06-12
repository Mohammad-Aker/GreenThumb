package com.GreenThumb.GT.services.KnowledgeResourceServices;

import com.GreenThumb.GT.DTO.KnowledgeResourceDTOs.KnowledgeResourceDTO;
import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;


import com.GreenThumb.GT.repositories.KnowledgeResourceRepositories.KnowledgeResourceRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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
        try {
            Sort sort = Sort.unsorted();
            if (sortField != null && !sortField.isEmpty() && sortDirection != null && !sortDirection.isEmpty()) {
                Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());  // Handle case sensitivity
                sort = Sort.by(new Sort.Order(direction, sortField));
            }
            return knowledgeResourceRepository.findAll(sort);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort field or direction");
        }
    }


    public List<KnowledgeResource> filterResources(String author, Set<String> tags, Double averageRating, ResourceCategory category) {
        // Start with all resources, then filter down
        List<KnowledgeResource> resources = knowledgeResourceRepository.findAll();

        // Filter by author if specified
        if (author != null && !author.isEmpty()) {
            resources = resources.stream()
                    .filter(resource -> author.equals(resource.getAuthor()))
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



    public String storeFile(MultipartFile file, KnowledgeResourceDTO knowledgeResourceDTO) throws IOException {

        // Check if the document already exists
        Optional<KnowledgeResource> existingResource = knowledgeResourceRepository.findByTitle(knowledgeResourceDTO.getTitle());
        if (existingResource.isPresent()) {
            throw new DataIntegrityViolationException("Resource with the title '" + knowledgeResourceDTO.getTitle() + "' already exists.");
        }
        KnowledgeResource document = new KnowledgeResource();
        //document.setTitle(file.getOriginalFilename());
        document.setTitle(knowledgeResourceDTO.getTitle());
        document.setData(file.getBytes());
        document.setAuthor(knowledgeResourceDTO.getAuthor());
        document.setCategory(knowledgeResourceDTO.getCategory());
        document.setCreatedDate(new Date());
        document.setUser(userRepository.findByEmail(knowledgeResourceDTO.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + knowledgeResourceDTO.getUserEmail())));

        knowledgeResourceRepository.save(document);
        return "File stored successfully!";


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


    public KnowledgeResource updateResource(String title, String currentUserEmail,
                                            String author,
                                            ResourceCategory category, Set<String> tags) {
        Optional<KnowledgeResource> resourceOptional = knowledgeResourceRepository.findByTitle(title);
        if (!resourceOptional.isPresent()) {
            throw new IllegalArgumentException("No resource found with title: " + title);
        }

        KnowledgeResource existingResource = resourceOptional.get();
        if (!existingResource.getUser().getEmail().equals(currentUserEmail)) {
            throw new SecurityException("You are not authorized to update this resource.");
        }


        // Update other fields as necessary
        if (author != null) existingResource.setAuthor(author);
        if (category != null) existingResource.setCategory(category);
        if (tags != null && !tags.isEmpty()) existingResource.setTags(new HashSet<>(tags));

        return knowledgeResourceRepository.save(existingResource);
    }

    ////////////////////////////////////// delete ////////////////////////////////////////

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


    public boolean deleteResourcesByCategory(ResourceCategory category) {
        List<KnowledgeResource> resources = knowledgeResourceRepository.findByCategory(category);
        if (resources == null || resources.isEmpty()) {
            return false; // No resources to delete
        }

        knowledgeResourceRepository.deleteAll(resources);
        return true; // Resources were deleted
    }


}