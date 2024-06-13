package com.GreenThumb.GT.services.MaterialExchange;

import com.GreenThumb.GT.DTO.MaterialExchangeDTO.*;
import com.GreenThumb.GT.models.MaterialExchange.Exchange;
import com.GreenThumb.GT.models.MaterialExchange.Material.Material;
import com.GreenThumb.GT.models.MaterialExchange.MaterialRequest;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.ExchangeRepository;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.MaterialRepository;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.MaterialRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialRequestRepository materialRequestRepository;

    @Autowired
    private ExchangeRepository exchangeRepository;

    public List<MaterialDTO> searchMaterials(SearchDTO searchDTO) {
        return materialRepository.findAll().stream()
                .filter(material -> searchDTO.getMaterialType() == null || material.getType() == searchDTO.getMaterialType())
                .filter(material -> searchDTO.getOwnerEmail() == null || material.getOwner().getEmail().equals(searchDTO.getOwnerEmail()))
                .map(this::convertResourceToDTO)
                .collect(Collectors.toList());
    }

    public List<MaterialRequestDTO> searchRequests(SearchDTO searchDTO) {
        return materialRequestRepository.findAll().stream()
                .filter(request -> searchDTO.getMaterialType() == null || request.getMaterialType() == searchDTO.getMaterialType())
                .filter(request -> searchDTO.getOwnerEmail() == null || request.getUser().getEmail().equals(searchDTO.getOwnerEmail()))
                .filter(request -> searchDTO.getRequestStatus() == null || request.getStatus().equals(searchDTO.getRequestStatus()))
                .map(this::convertRequestToDTO)
                .collect(Collectors.toList());
    }

    public List<ExchangeDTO> searchExchanges(SearchDTO searchDTO) {
        return exchangeRepository.findAll().stream()
                .filter(exchange -> searchDTO.getMaterialType() == null || exchange.getMaterial().getType() == searchDTO.getMaterialType())
                .filter(exchange -> searchDTO.getOwnerEmail() == null || exchange.getFromUser().getEmail().equals(searchDTO.getOwnerEmail()) || exchange.getToUser().getEmail().equals(searchDTO.getOwnerEmail()))
                .filter(exchange -> searchDTO.getExchangeStatus() == null || exchange.getStatus().equals(searchDTO.getExchangeStatus()))
                .map(this::convertExchangeToDTO)
                .collect(Collectors.toList());
    }

    private MaterialDTO convertResourceToDTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        dto.setId(material.getId());
        dto.setName(material.getName());
        dto.setDescription(material.getDescription());
        dto.setPrice(material.getPrice());
        dto.setQuantity(material.getQuantity());
        dto.setType(material.getType());
        dto.setOwner(new UserDTO(material.getOwner().getEmail()));
        return dto;
    }

    private MaterialRequestDTO convertRequestToDTO(MaterialRequest request) {
        MaterialRequestDTO dto = new MaterialRequestDTO();
        dto.setId(request.getId());
        dto.setUserEmail(new UserEmailDTO(request.getUser().getEmail()));
        dto.setMaterialId(request.getMaterial().getId());
        dto.setMaterialType(request.getMaterialType());
        dto.setQuantity(request.getQuantity());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        return dto;
    }

    private ExchangeDTO convertExchangeToDTO(Exchange exchange) {
        ExchangeDTO dto = new ExchangeDTO();
        dto.setId(exchange.getId());
        dto.setMaterialId(exchange.getMaterial().getId());
        dto.setRequestId(exchange.getRequest().getId());
        dto.setFromUserEmail(exchange.getFromUser().getEmail());
        dto.setToUserEmail(exchange.getToUser().getEmail());
        dto.setStatus(exchange.getStatus());
        dto.setMaterialType(exchange.getMaterial().getType());  // Set the resource type
        return dto;
    }
}
