/*package com.GreenThumb.GT.services;

import com.GreenThumb.GT.DTO.ResourceRequestDTO;
import com.GreenThumb.GT.DTO.UserEmailDTO;
import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.exceptions.UnauthorizedOperationException;
import com.GreenThumb.GT.models.ResourceRequest;
import com.GreenThumb.GT.repositories.ResourceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceRequestService {

    @Autowired
    private ResourceRequestRepository repository;

    public List<ResourceRequestDTO> getAllRequests() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ResourceRequestDTO createRequest(ResourceRequest request) {
        ResourceRequest savedRequest = repository.save(request);
        return convertToDTO(savedRequest);
    }

    public Optional<ResourceRequestDTO> getRequestById(Long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public ResourceRequestDTO updateRequest(Long id, ResourceRequest updatedRequest, String authenticatedUserEmail) {
        Optional<ResourceRequest> requestOpt = repository.findById(id);
        if (requestOpt.isPresent()) {
            ResourceRequest request = requestOpt.get();
            if (request.getUser().getEmail().equals(authenticatedUserEmail)) {
                request.setResourceType(updatedRequest.getResourceType());
                request.setQuantity(updatedRequest.getQuantity());
                request.setDescription(updatedRequest.getDescription());
                request.setStatus(updatedRequest.getStatus());
                return convertToDTO(repository.save(request));
            } else {
                throw new UnauthorizedOperationException("Unauthorized to update this request");
            }
        } else {
            throw new ResourceNotFoundException("Request not found");
        }
    }

    public void deleteRequest(Long id, String authenticatedUserEmail) {
        Optional<ResourceRequest> requestOpt = repository.findById(id);
        if (requestOpt.isPresent()) {
            ResourceRequest request = requestOpt.get();
            if (request.getUser().getEmail().equals(authenticatedUserEmail)) {
                repository.delete(request);
            } else {
                throw new UnauthorizedOperationException("Unauthorized to delete this request");
            }
        } else {
            throw new ResourceNotFoundException("Request not found");
        }
    }

    private ResourceRequestDTO convertToDTO(ResourceRequest request) {
        UserEmailDTO userDto = new UserEmailDTO(request.getUser().getEmail());
        ResourceRequestDTO dto = new ResourceRequestDTO();
        dto.setId(request.getId());
        dto.setUser(userDto);
        dto.setResourceType(request.getResourceType());
        dto.setQuantity(request.getQuantity());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }
}
*/


package com.GreenThumb.GT.services;

        import com.GreenThumb.GT.DTO.ResourceRequestDTO;
        import com.GreenThumb.GT.DTO.UserEmailDTO;
        import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
        import com.GreenThumb.GT.models.ResourceRequest;
        import com.GreenThumb.GT.models.User.User;
        import com.GreenThumb.GT.repositories.ResourceRequestRepository;
        import com.GreenThumb.GT.repositories.UserRepository;
        import com.GreenThumb.GT.repositories.ResourceRepository;
        import com.GreenThumb.GT.models.Resource.Resource;
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

    public List<ResourceRequestDTO> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ResourceRequestDTO createRequest(ResourceRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Resource resource = resourceRepository.findById(dto.getResourceId()).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

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

