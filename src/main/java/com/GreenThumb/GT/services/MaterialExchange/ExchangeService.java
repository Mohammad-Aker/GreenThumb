package com.GreenThumb.GT.services.MaterialExchange;

import com.GreenThumb.GT.DTO.MaterialExchangeDTO.ExchangeDTO;
import com.GreenThumb.GT.exceptions.InvalidOperationException;
import com.GreenThumb.GT.exceptions.MaterialNotFoundException;
import com.GreenThumb.GT.models.MaterialExchange.Exchange;
import com.GreenThumb.GT.models.MaterialExchange.Material.Material;
import com.GreenThumb.GT.models.MaterialExchange.MaterialRequest;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.ExchangeRepository;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.MaterialRepository;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.MaterialRequestRepository;
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
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialRequestRepository materialRequestRepository;

    public List<ExchangeDTO> getAllExchanges() {
        return exchangeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExchangeDTO extractUsersForExchange(ExchangeDTO dto) {
        Material material = materialRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new MaterialNotFoundException("Material not found"));
        User fromUser = material.getOwner();

        MaterialRequest request = materialRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new MaterialNotFoundException("Material request not found"));
        User toUser = request.getUser();

        dto.setFromUserEmail(fromUser.getEmail());
        dto.setToUserEmail(toUser.getEmail());

        return dto;
    }

    @Transactional
    public ExchangeDTO createExchange(ExchangeDTO dto) {
        Material material = materialRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new MaterialNotFoundException("Material not found"));
        User fromUser = userRepository.findByEmail(dto.getFromUserEmail())
                .orElseThrow(() -> new MaterialNotFoundException("From user not found"));

        MaterialRequest request = materialRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new MaterialNotFoundException("Material request not found"));

        if (!"OPEN".equals(request.getStatus())) {
            throw new InvalidOperationException("Cannot create exchange for a closed request");
        }

        User toUser = userRepository.findByEmail(dto.getToUserEmail())
                .orElseThrow(() -> new MaterialNotFoundException("To user not found"));

        // Create the exchange
        Exchange exchange = new Exchange();
        exchange.setMaterial(material);
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
                .orElseThrow(() -> new MaterialNotFoundException("Exchange not found"));

        if (dto.getStatus() != null) {
            exchange.setStatus(dto.getStatus());
            // If the status is set to 'completed', transfer ownership and close the request
            if (dto.getStatus().equalsIgnoreCase("completed")) {
                // Transfer ownership of the resource
                Material material = exchange.getMaterial();
                User toUser = exchange.getToUser();
                material.setOwner(toUser);

                // Reduce the resource quantity
                MaterialRequest request = exchange.getRequest();
                int requestedQuantity = request.getQuantity();
                material.setQuantity(material.getQuantity() - requestedQuantity);
                materialRepository.save(material);

                // Change the status of the request to 'CLOSED'
                request.setStatus("CLOSED");
                materialRequestRepository.save(request);
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
        dto.setMaterialId(exchange.getMaterial().getId());
        dto.setRequestId(exchange.getRequest().getId());
        dto.setFromUserEmail(exchange.getFromUser().getEmail());
        dto.setToUserEmail(exchange.getToUser().getEmail());
        dto.setStatus(exchange.getStatus());
        return dto;
    }
}
