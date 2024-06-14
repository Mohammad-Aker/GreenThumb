package com.GreenThumb.GT.services.MaterialExchange;

import com.GreenThumb.GT.DTO.MaterialExchangeDTO.MaterialRequestDTO;
import com.GreenThumb.GT.DTO.MaterialExchangeDTO.UserEmailDTO;
import com.GreenThumb.GT.exceptions.MaterialNotFoundException;
import com.GreenThumb.GT.exceptions.InvalidRequestException;
import com.GreenThumb.GT.models.MaterialExchange.MaterialRequest;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.MaterialRequestRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.MaterialRepository;
import com.GreenThumb.GT.models.MaterialExchange.Material.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaterialRequestService {

    @Autowired
    private MaterialRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialRepository materialRepository;

    public List<MaterialRequestDTO> getAllRequests(String userEmail) {
        return requestRepository.findByUserEmail(userEmail).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MaterialRequestDTO createRequest(MaterialRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new MaterialNotFoundException("User not found"));
        Material material = materialRepository.findById(dto.getMaterialId()).orElseThrow(() -> new MaterialNotFoundException("Material not found"));

        // Check if requested quantity is greater than available quantity
        if (dto.getQuantity() > material.getQuantity()) {
            throw new InvalidRequestException("Requested quantity exceeds available material quantity");
        }

        MaterialRequest request = new MaterialRequest();
        request.setUser(user);
        request.setMaterial(material);
        request.setMaterialType(dto.getMaterialType());
        request.setQuantity(dto.getQuantity());
        request.setDescription(dto.getDescription());
        request.setStatus("OPEN");

        MaterialRequest savedRequest = requestRepository.save(request);
        return convertToDTO(savedRequest);
    }

    public Optional<MaterialRequestDTO> getRequestById(Long id, String userEmail) {
        return requestRepository.findById(id)
                .filter(request -> request.getUser().getEmail().equals(userEmail))
                .map(this::convertToDTO);
    }

    public MaterialRequestDTO updateRequest(Long id, MaterialRequestDTO dto, String userEmail) {
        MaterialRequest request = requestRepository.findById(id).orElseThrow(() -> new MaterialNotFoundException("Request not found"));

        if (!request.getUser().getEmail().equals(userEmail)) {
            throw new InvalidRequestException("You are not authorized to update this request");
        }

        request.setMaterialType(dto.getMaterialType());
        request.setQuantity(dto.getQuantity());
        request.setDescription(dto.getDescription());
        request.setStatus(dto.getStatus());

        MaterialRequest updatedRequest = requestRepository.save(request);
        return convertToDTO(updatedRequest);
    }

    public void deleteRequest(Long id, String userEmail) {
        MaterialRequest request = requestRepository.findById(id).orElseThrow(() -> new MaterialNotFoundException("Request not found"));

        if (!request.getUser().getEmail().equals(userEmail)) {
            throw new InvalidRequestException("You are not authorized to delete this request");
        }

        requestRepository.deleteById(id);
    }

    private MaterialRequestDTO convertToDTO(MaterialRequest request) {
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
}
