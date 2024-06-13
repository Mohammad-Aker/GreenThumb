package com.GreenThumb.GT.controllers.MaterialExchangeControllers;

import com.GreenThumb.GT.DTO.MaterialExchangeDTO.MaterialCreationDTO;
import com.GreenThumb.GT.DTO.MaterialExchangeDTO.MaterialDTO;
import com.GreenThumb.GT.DTO.MaterialExchangeDTO.UserDTO;
import com.GreenThumb.GT.Views.Views;
import com.GreenThumb.GT.models.MaterialExchange.Material.Material;
import com.GreenThumb.GT.models.MaterialExchange.Material.MaterialType;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.services.MaterialExchange.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("GreenThumb/api/materials")
public class MaterialController {

    @Autowired
    private MaterialService service;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('ADMIN')")
    public ResponseEntity<?> getMaterials(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) MaterialType type) {
        try {
            List<Material> materials = service.getMaterials(type);
            List<MaterialDTO> materialDtos = mapMaterialToDtos(materials);

            Class<?> jsonView = user.getRole().name().equals("ADMIN") ? Views.ADMIN.class : Views.USER_R.class;

            // Applying the appropriate JsonView to the response
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(materialDtos);
            mappingJacksonValue.setSerializationView(jsonView);

            return ResponseEntity.ok(mappingJacksonValue);
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Failed to get material.");
        }
    }
    private List<MaterialDTO> mapMaterialToDtos(List<Material> materials) {
        return materials.stream()
                .map(this::convertToDto) // Assuming there is a method that converts entities to DTOs
                .collect(Collectors.toList());
    }
    private MaterialDTO convertToDto(Material material) {
        MaterialDTO dto = new MaterialDTO();
        UserDTO userDto = new UserDTO(material.getOwner().getEmail(), material.getOwner().getPhoneNumber(), material.getOwner().getUsername(), material.getOwner().getRole());
        dto.setId(material.getId());
        dto.setName(material.getName());
        dto.setDescription(material.getDescription());
        dto.setPrice(material.getPrice());
        dto.setQuantity(material.getQuantity());
        dto.setType(material.getType());
        if (material.getOwner() != null) {
            dto.setOwner(userDto);
        }
        return dto;
    }



    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> addMaterial(
            @RequestBody MaterialCreationDTO materialDto,
            @AuthenticationPrincipal User user) {
        String ownerEmail = user.getUsername();
        try {
            Material material = service.createMaterial(materialDto,ownerEmail);
            return ResponseEntity.ok("Material has been successfully created.");
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Failed to create material.");
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> updateMaterial(
            @PathVariable Long id,
            @RequestBody MaterialCreationDTO materialDto,
            @AuthenticationPrincipal User user) {
        try {
            service.updateMaterial(id, materialDto, user.getEmail());
            return ResponseEntity.ok("Material has been successfully updated.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> deleteMaterial(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            service.deleteMaterial(id, user.getEmail());
            return ResponseEntity.ok("Material has been successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
