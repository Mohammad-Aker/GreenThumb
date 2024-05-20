package com.GreenThumb.GT.services.ResourceExchange;

import com.GreenThumb.GT.DTO.ResourceExchangeDTO.ResourceCreationDTO;
import com.GreenThumb.GT.models.ResourceExchange.Resource.Resource;
import com.GreenThumb.GT.models.ResourceExchange.Resource.ResourceType;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.ResourceExchangeRepositories.ResourceRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {


        @Autowired
        private ResourceRepository repository;
        @Autowired
        private UserRepository userRepository;

        public List<Resource> getResources(ResourceType type) {
            if (type == null) {
                return repository.findByQuantityIsGreaterThan(0);
            } else {
                return repository.findByTypeAndQuantityIsGreaterThan(type,0);
            }
        }

    public Resource createResource(ResourceCreationDTO dto, String ownerEmail) {
        Resource resource = new Resource();
        resource.setName(dto.getName());
        resource.setDescription(dto.getDescription());
        resource.setPrice(dto.getPrice());
        resource.setQuantity(dto.getQuantity());
        resource.setType(dto.getType());
        User owner = userRepository.findByEmail(ownerEmail).orElse(null);  // Fetch the owner by email
        resource.setOwner(owner);

        return repository.save(resource);
    }
    public Resource updateResource(Long id, ResourceCreationDTO dto, String authenticatedUserEmail) {
        Optional<Resource> resourceOpt = repository.findById(id);
        if (resourceOpt.isPresent()) {
            Resource resource = resourceOpt.get();
            if (resource.getOwner().getEmail().equals(authenticatedUserEmail)) {
                if (dto.getName() != null) resource.setName(dto.getName());
                if (dto.getDescription() != null) resource.setDescription(dto.getDescription());
                if (dto.getPrice() != 0) resource.setPrice(dto.getPrice());
                if (dto.getQuantity() != 0) resource.setQuantity(dto.getQuantity());
                if (dto.getType() != null) resource.setType(dto.getType());
                return repository.save(resource);
            } else {
                throw new UsernameNotFoundException("Unauthorized to update this resource");
            }
        } else {
            throw new UsernameNotFoundException("Resource not found");
        }
    }

    public void deleteResource(Long id, String authenticatedUserEmail) {
        Optional<Resource> resourceOpt = repository.findById(id);
        if (resourceOpt.isPresent()) {
            Resource resource = resourceOpt.get();
            if (resource.getOwner().getEmail().equals(authenticatedUserEmail)) {
                repository.delete(resource);
            } else {
                throw new UsernameNotFoundException("Unauthorized to delete this resource");
            }
        } else {
            throw new UsernameNotFoundException("Resource not found");
        }
    }



}


