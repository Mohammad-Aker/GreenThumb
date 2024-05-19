package com.GreenThumb.GT.services;

import com.GreenThumb.GT.DTO.*;
import com.GreenThumb.GT.models.Exchange;
import com.GreenThumb.GT.models.Resource.Resource;
import com.GreenThumb.GT.models.ResourceRequest;
import com.GreenThumb.GT.repositories.ExchangeRepository;
import com.GreenThumb.GT.repositories.ResourceRepository;
import com.GreenThumb.GT.repositories.ResourceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceRequestRepository resourceRequestRepository;

    @Autowired
    private ExchangeRepository exchangeRepository;

    public List<ResourceDTO> searchResources(SearchDTO searchDTO) {
        return resourceRepository.findAll().stream()
                .filter(resource -> searchDTO.getResourceType() == null || resource.getType() == searchDTO.getResourceType())
                .filter(resource -> searchDTO.getOwnerEmail() == null || resource.getOwner().getEmail().equals(searchDTO.getOwnerEmail()))
                .map(this::convertResourceToDTO)
                .collect(Collectors.toList());
    }

    public List<ResourceRequestDTO> searchRequests(SearchDTO searchDTO) {
        return resourceRequestRepository.findAll().stream()
                .filter(request -> searchDTO.getResourceType() == null || request.getResourceType() == searchDTO.getResourceType())
                .filter(request -> searchDTO.getOwnerEmail() == null || request.getUser().getEmail().equals(searchDTO.getOwnerEmail()))
                .filter(request -> searchDTO.getRequestStatus() == null || request.getStatus().equals(searchDTO.getRequestStatus()))
                .map(this::convertRequestToDTO)
                .collect(Collectors.toList());
    }

    public List<ExchangeDTO> searchExchanges(SearchDTO searchDTO) {
        return exchangeRepository.findAll().stream()
                .filter(exchange -> searchDTO.getResourceType() == null || exchange.getResource().getType() == searchDTO.getResourceType())
                .filter(exchange -> searchDTO.getOwnerEmail() == null || exchange.getFromUser().getEmail().equals(searchDTO.getOwnerEmail()) || exchange.getToUser().getEmail().equals(searchDTO.getOwnerEmail()))
                .filter(exchange -> searchDTO.getExchangeStatus() == null || exchange.getStatus().equals(searchDTO.getExchangeStatus()))
                .map(this::convertExchangeToDTO)
                .collect(Collectors.toList());
    }

    private ResourceDTO convertResourceToDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setDescription(resource.getDescription());
        dto.setPrice(resource.getPrice());
        dto.setQuantity(resource.getQuantity());
        dto.setType(resource.getType());
        dto.setOwner(new UserDTO(resource.getOwner().getEmail()));
        return dto;
    }

    private ResourceRequestDTO convertRequestToDTO(ResourceRequest request) {
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

    private ExchangeDTO convertExchangeToDTO(Exchange exchange) {
        ExchangeDTO dto = new ExchangeDTO();
        dto.setId(exchange.getId());
        dto.setResourceId(exchange.getResource().getId());
        dto.setRequestId(exchange.getRequest().getId());
        dto.setFromUserEmail(exchange.getFromUser().getEmail());
        dto.setToUserEmail(exchange.getToUser().getEmail());
        dto.setStatus(exchange.getStatus());
        dto.setResourceType(exchange.getResource().getType());  // Set the resource type
        return dto;
    }
}
