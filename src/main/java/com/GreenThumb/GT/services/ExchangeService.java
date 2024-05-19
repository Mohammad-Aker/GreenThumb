package com.GreenThumb.GT.services;

import com.GreenThumb.GT.DTO.ExchangeDTO;
import com.GreenThumb.GT.exceptions.InvalidOperationException;
import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.models.Exchange;
import com.GreenThumb.GT.models.Resource.Resource;
import com.GreenThumb.GT.models.ResourceRequest;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.ExchangeRepository;
import com.GreenThumb.GT.repositories.ResourceRepository;
import com.GreenThumb.GT.repositories.ResourceRequestRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExchangeService {

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceRequestRepository resourceRequestRepository;

    public List<ExchangeDTO> getAllExchanges() {
        return exchangeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExchangeDTO extractUsersForExchange(ExchangeDTO dto) {
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        User fromUser = resource.getOwner();

        ResourceRequest request = resourceRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource request not found"));
        User toUser = request.getUser();

        dto.setFromUserEmail(fromUser.getEmail());
        dto.setToUserEmail(toUser.getEmail());

        return dto;
    }

    @Transactional
    public ExchangeDTO createExchange(ExchangeDTO dto) {
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        User fromUser = userRepository.findByEmail(dto.getFromUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("From user not found"));

        ResourceRequest request = resourceRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource request not found"));

        if (!"OPEN".equals(request.getStatus())) {
            throw new InvalidOperationException("Cannot create exchange for a closed request");
        }

        User toUser = userRepository.findByEmail(dto.getToUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("To user not found"));

        // Create the exchange
        Exchange exchange = new Exchange();
        exchange.setResource(resource);
        exchange.setFromUser(fromUser);
        exchange.setToUser(toUser);
        exchange.setRequest(request);
        exchange.setStatus("pending");

        Exchange savedExchange = exchangeRepository.save(exchange);
        return convertToDTO(savedExchange);
    }

    @Transactional
    public ExchangeDTO updateExchange(Long id, ExchangeDTO dto) {
        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found"));

        if (dto.getStatus() != null) {
            exchange.setStatus(dto.getStatus());
            // If the status is set to 'completed', transfer ownership and close the request
            if (dto.getStatus().equalsIgnoreCase("completed")) {
                // Transfer ownership of the resource
                Resource resource = exchange.getResource();
                User toUser = exchange.getToUser();
                resource.setOwner(toUser);

                // Reduce the resource quantity
                ResourceRequest request = exchange.getRequest();
                int requestedQuantity = request.getQuantity();
                resource.setQuantity(resource.getQuantity() - requestedQuantity);
                resourceRepository.save(resource);

                // Change the status of the request to 'CLOSED'
                request.setStatus("CLOSED");
                resourceRequestRepository.save(request);
            }
        }

        Exchange updatedExchange = exchangeRepository.save(exchange);
        return convertToDTO(updatedExchange);
    }

    public Optional<ExchangeDTO> getExchangeById(Long id) {
        return exchangeRepository.findById(id)
                .map(this::convertToDTO);
    }

    private ExchangeDTO convertToDTO(Exchange exchange) {
        ExchangeDTO dto = new ExchangeDTO();
        dto.setId(exchange.getId());
        dto.setResourceId(exchange.getResource().getId());
        dto.setRequestId(exchange.getRequest().getId());
        dto.setFromUserEmail(exchange.getFromUser().getEmail());
        dto.setToUserEmail(exchange.getToUser().getEmail());
        dto.setStatus(exchange.getStatus());
        return dto;
    }
}
