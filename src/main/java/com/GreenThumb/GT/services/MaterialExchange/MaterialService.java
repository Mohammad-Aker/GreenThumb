package com.GreenThumb.GT.services.MaterialExchange;

import com.GreenThumb.GT.DTO.MaterialExchangeDTO.MaterialCreationDTO;
import com.GreenThumb.GT.models.MaterialExchange.Material.Material;
import com.GreenThumb.GT.models.MaterialExchange.Material.MaterialType;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.MaterialExchangeRepositories.MaterialRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {


        @Autowired
        private MaterialRepository repository;
        @Autowired
        private UserRepository userRepository;

        public List<Material> getMaterials(MaterialType type) {
            if (type == null) {
                return repository.findByQuantityIsGreaterThan(0);
            } else {
                return repository.findByTypeAndQuantityIsGreaterThan(type,0);
            }
        }

    public Material createMaterial(MaterialCreationDTO dto, String ownerEmail) {
        Material material = new Material();
        material.setName(dto.getName());
        material.setDescription(dto.getDescription());
        material.setPrice(dto.getPrice());
        material.setQuantity(dto.getQuantity());
        material.setType(dto.getType());
        User owner = userRepository.findByEmail(ownerEmail).orElse(null);  // Fetch the owner by email
        material.setOwner(owner);

        return repository.save(material);
    }
    public Material updateMaterial(Long id, MaterialCreationDTO dto, String authenticatedUserEmail) {
        Optional<Material> materialOpt = repository.findById(id);
        if (materialOpt.isPresent()) {
            Material material = materialOpt.get();
            if (material.getOwner().getEmail().equals(authenticatedUserEmail)) {
                if (dto.getName() != null) material.setName(dto.getName());
                if (dto.getDescription() != null) material.setDescription(dto.getDescription());
                if (dto.getPrice() != 0) material.setPrice(dto.getPrice());
                if (dto.getQuantity() != 0) material.setQuantity(dto.getQuantity());
                if (dto.getType() != null) material.setType(dto.getType());
                return repository.save(material);
            } else {
                throw new UsernameNotFoundException("Unauthorized to update this material");
            }
        } else {
            throw new UsernameNotFoundException("Material not found");
        }
    }

    public void deleteMaterial(Long id, String authenticatedUserEmail) {
        Optional<Material> materialOpt = repository.findById(id);
        if (materialOpt.isPresent()) {
            Material material = materialOpt.get();
            if (material.getOwner().getEmail().equals(authenticatedUserEmail)) {
                repository.delete(material);
            } else {
                throw new UsernameNotFoundException("Unauthorized to delete this material");
            }
        } else {
            throw new UsernameNotFoundException("Material not found");
        }
    }



}


