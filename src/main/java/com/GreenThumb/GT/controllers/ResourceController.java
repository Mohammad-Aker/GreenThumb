package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.DTO.ResourceCreationDTO;
import com.GreenThumb.GT.DTO.ResourceDTO;
import com.GreenThumb.GT.DTO.UserDTO;
import com.GreenThumb.GT.Views.Views;
import com.GreenThumb.GT.models.Resource.Resource;
import com.GreenThumb.GT.models.Resource.ResourceType;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GreenThumb/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService service;

    @GetMapping
    @PreAuthorize("hasAuthority('USER')or hasAuthority('ADMIN')")
    public ResponseEntity<?> getResources(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) ResourceType type) {
        try {
            List<Resource> resources = service.getResources(type);
            List<ResourceDTO> resourceDtos = mapResourcesToDtos(resources);

            Class<?> jsonView = user.getRole().name().equals("ADMIN") ? Views.ADMIN.class : Views.USER_R.class;

            // Applying the appropriate JsonView to the response
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(resourceDtos);
            mappingJacksonValue.setSerializationView(jsonView);

            return ResponseEntity.ok(mappingJacksonValue);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Failed to get resource.");
        }
    }
    private List<ResourceDTO> mapResourcesToDtos(List<Resource> resources) {
        return resources.stream()
                .map(this::convertToDto) // Assuming there is a method that converts entities to DTOs
                .collect(Collectors.toList());
    }
    private ResourceDTO convertToDto(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        UserDTO userDto = new UserDTO(resource.getOwner().getEmail(),resource.getOwner().getPhoneNumber(),resource.getOwner().getUsername(),resource.getOwner().getRole());
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setDescription(resource.getDescription());
        dto.setPrice(resource.getPrice());
        dto.setQuantity(resource.getQuantity());
        dto.setType(resource.getType());
        if (resource.getOwner() != null) {
            dto.setOwner(userDto);
        }
        return dto;
    }



    @PostMapping
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> addResource(
            @RequestBody ResourceCreationDTO resourceDto,
            @AuthenticationPrincipal User user) {
        String ownerEmail = user.getUsername();
        try {
            Resource resource = service.createResource(resourceDto,ownerEmail);
            return ResponseEntity.ok("Resource has been successfully created.");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Failed to create resource.");
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> updateResource(
            @PathVariable Long id,
            @RequestBody ResourceCreationDTO resourceDto,
            @AuthenticationPrincipal User user) {
        try {
            service.updateResource(id, resourceDto, user.getEmail());
            return ResponseEntity.ok("Resource has been successfully updated.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> deleteResource(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            service.deleteResource(id, user.getEmail());
            return ResponseEntity.ok("Resource has been successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
