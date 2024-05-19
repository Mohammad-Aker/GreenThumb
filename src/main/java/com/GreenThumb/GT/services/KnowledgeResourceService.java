package com.GreenThumb.GT.services;

import com.GreenThumb.GT.models.KnowledgeResource.KnowledgeResource;
import com.GreenThumb.GT.models.KnowledgeResource.ResourceCategory;


import com.GreenThumb.GT.models.KnowledgeResource.ResourceType;
import com.GreenThumb.GT.repositories.KnowledgeResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KnowledgeResourceService {

    private final KnowledgeResourceRepository knowledgeResourceRepository;

    private final FileStorageService fileStorageService;

    @Autowired
    public KnowledgeResourceService(KnowledgeResourceRepository knowledgeResourceRepository, FileStorageService fileStorageService) {
        this.knowledgeResourceRepository = knowledgeResourceRepository;
        this.fileStorageService = fileStorageService;

    }


    ///////////////////////////////////////////// get  /////////////////////////////////////////////////

    //get all resources
    public List<KnowledgeResource> getAllResources() {
        return knowledgeResourceRepository.findAll();
    }


    //get resource by title
    public Optional<KnowledgeResource> getResourcesByTitle(String title) {
        return knowledgeResourceRepository.findByTitle(title);
    }

    public KnowledgeResource getResourceByTitle(String title) {
        return knowledgeResourceRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with title: " + title));
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

    public Optional<KnowledgeResource> createResource(KnowledgeResource resource) {
        if (knowledgeResourceRepository.existsByTitle(resource.getTitle()) ||
                knowledgeResourceRepository.existsByContentUrl(resource.getContentUrl())) {
            return Optional.empty(); // Indicates creation failure due to existing title or content URL
        }
        return Optional.of(knowledgeResourceRepository.save(resource));
    }


    public KnowledgeResource addTags(String title, Set<String> newTags, String currentUserEmail) {

        Optional<KnowledgeResource> optionalResource = knowledgeResourceRepository.findByTitle(title);
        if (optionalResource.isPresent()) {
            KnowledgeResource resource = optionalResource.get();
            if (!resource.getUser().getEmail().equals(currentUserEmail)) {
                throw new UnauthorizedException("User is not authorized to update this resource");
            }
            resource.getTags().addAll(newTags);
            return knowledgeResourceRepository.save(resource);
        } else {
            throw new ResourceNotFoundException("Resource not found with title " + title);
        }
    }

    // Custom exception class for unauthorized access
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    // Custom exception class for resource not found
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }


/////////////////////////////////////////////// update //////////////////////////////////////////////////


    //update resource info
    public KnowledgeResource updateResource(String currentTitle, String currentUserEmail,
                                            String newTitle, String content, String author, ResourceType type,
                                            ResourceCategory category, Set<String> tags) {
        KnowledgeResource existingResource = knowledgeResourceRepository.findByTitle(currentTitle)
                .orElseThrow(() -> new IllegalArgumentException("No resource found with title: " + currentTitle));

        if (!existingResource.getUser().getEmail().equals(currentUserEmail)) {
            throw new SecurityException("You are not authorized to update this resource.");
        }

        if (newTitle != null && !newTitle.isEmpty() && !newTitle.equals(currentTitle)) {
            if (knowledgeResourceRepository.findByTitle(newTitle).isPresent()) {
                throw new IllegalArgumentException("A resource with the new title already exists.");
            }
            existingResource.setTitle(newTitle);
        }

        if (content != null) {
            existingResource.setContentUrl(content);
        }
        if (author != null) {
            existingResource.setAuthor(author);
        }
        if (type != null) {
            existingResource.setType(type);
        }
        if (category != null) {
            existingResource.setCategory(category);
        }
        if (tags != null && !tags.isEmpty()) {
            existingResource.setTags(tags);
        }

        return knowledgeResourceRepository.save(existingResource);
    }


    ////////////////////////////////////// delete ////////////////////////////////////////


    //for experts
    public boolean deleteResource(String title, String userEmail) {
        Optional<KnowledgeResource> resource = knowledgeResourceRepository.findByTitle(title);
        if (resource.isPresent()) {
            KnowledgeResource existingResource = resource.get();
            if (existingResource.getUser().getEmail().equals(userEmail)) {
                knowledgeResourceRepository.delete(existingResource);
                return true;
            } else {
                throw new SecurityException("You are not authorized to delete this resource.");
            }
        } else {
            return false; // Resource not found
        }
    }

    //for admin
    public void deleteResourcesByCategory(ResourceCategory category) {
        List<KnowledgeResource> resources = knowledgeResourceRepository.findByCategory(category);
        if (resources != null && !resources.isEmpty()) {
            knowledgeResourceRepository.deleteAll(resources);
        }


    }






}