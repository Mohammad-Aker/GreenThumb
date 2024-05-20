package com.GreenThumb.GT.services.ResourceExchange;

import com.GreenThumb.GT.DTO.ResourceExchangeDTO.ResourceRequestDTO;
import com.GreenThumb.GT.DTO.ResourceExchangeDTO.UserEmailDTO;
import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.exceptions.InvalidRequestException;
import com.GreenThumb.GT.models.ResourceExchange.ResourceRequest;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.ResourceExchangeRepositories.ResourceRequestRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import com.GreenThumb.GT.repositories.ResourceExchangeRepositories.ResourceRepository;
import com.GreenThumb.GT.models.ResourceExchange.Resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceRequestService {

    @Autowired
    private ResourceRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    public List<ResourceRequestDTO> getAllRequests(String userEmail) {
        return requestRepository.findByUserEmail(userEmail).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ResourceRequestDTO createRequest(ResourceRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Resource resource = resourceRepository.findById(dto.getResourceId()).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        // Check if requested quantity is greater than available quantity
        if (dto.getQuantity() > resource.getQuantity()) {
            throw new InvalidRequestException("Requested quantity exceeds available resource quantity");
        }

        ResourceRequest request = new ResourceRequest();
        request.setUser(user);
        request.setResource(resource);
        request.setResourceType(dto.getResourceType());
        request.setQuantity(dto.getQuantity());
        request.setDescription(dto.getDescription());
        request.setStatus("OPEN");

        ResourceRequest savedRequest = requestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    public Optional<ResourceRequestDTO> getRequestById(Long id) {
        return requestRepository.findById(id)
                .map(this::convertToDTO);
    }

    public ResourceRequestDTO updateRequest(Long id, ResourceRequestDTO dto) {
        ResourceRequest request = requestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        request.setResourceType(dto.getResourceType());
        request.setQuantity(dto.getQuantity());
        request.setDescription(dto.getDescription());
        request.setStatus(dto.getStatus());

        ResourceRequest updatedRequest = requestRepository.save(request);
        return convertToDTO(updatedRequest);
    }

    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Request not found");
        }
        requestRepository.deleteById(id);
    }

    private ResourceRequestDTO convertToDTO(ResourceRequest request) {
        ResourceRequestDTO dto = new ResourceRequestDTO();
        dto.setId(request.getId());
        dto.setUserEmail(new UserEmailDTO(request.getUser().getEmail()));
        dto.setResourceId(request.getResource().getId());
        dto.setResourceType(request.getResourceType());
        dto.setQuantity(request.getQuantity());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
