package com.GreenThumb.GT.controllers.MaterialExchangeControllers;

import com.GreenThumb.GT.DTO.MaterialExchangeDTO.MaterialRequestDTO;
import com.GreenThumb.GT.exceptions.InvalidRequestException;
import com.GreenThumb.GT.exceptions.MaterialNotFoundException;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.services.MaterialExchange.MaterialRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("GreenThumb/api/requests")
public class MaterialRequestController {

    @Autowired
    private MaterialRequestService service;

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<List<MaterialRequestDTO>> getAllRequests(@AuthenticationPrincipal User user) {
        List<MaterialRequestDTO> requests = service.getAllRequests(user.getEmail());
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<?> createRequest(@RequestBody MaterialRequestDTO requestDTO, @AuthenticationPrincipal User user) {
        try {
            MaterialRequestDTO createdRequest = service.createRequest(requestDTO, user.getEmail());
            return ResponseEntity.ok(createdRequest);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (MaterialNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<MaterialRequestDTO> getRequestById(@PathVariable Long id) {
        Optional<MaterialRequestDTO> request = service.getRequestById(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<?> updateRequest(@PathVariable Long id, @RequestBody MaterialRequestDTO requestDTO) {
        try {
            MaterialRequestDTO updatedRequest = service.updateRequest(id, requestDTO);
            return ResponseEntity.ok(updatedRequest);
        } catch (MaterialNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {
        try {
            service.deleteRequest(id);
            return ResponseEntity.ok("Request has been successfully deleted.");
        } catch (MaterialNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
