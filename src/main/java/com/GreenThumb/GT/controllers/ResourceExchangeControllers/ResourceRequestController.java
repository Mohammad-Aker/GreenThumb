package com.GreenThumb.GT.controllers.ResourceExchangeControllers;

import com.GreenThumb.GT.DTO.ResourceExchangeDTO.ResourceRequestDTO;
import com.GreenThumb.GT.exceptions.InvalidRequestException;
import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.services.ResourceExchange.ResourceRequestService;
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
public class ResourceRequestController {

    @Autowired
    private ResourceRequestService service;

    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<List<ResourceRequestDTO>> getAllRequests(@AuthenticationPrincipal User user) {
        List<ResourceRequestDTO> requests = service.getAllRequests(user.getEmail());
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<?> createRequest(@RequestBody ResourceRequestDTO requestDTO, @AuthenticationPrincipal User user) {
        try {
            ResourceRequestDTO createdRequest = service.createRequest(requestDTO, user.getEmail());
            return ResponseEntity.ok(createdRequest);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<ResourceRequestDTO> getRequestById(@PathVariable Long id) {
        Optional<ResourceRequestDTO> request = service.getRequestById(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<?> updateRequest(@PathVariable Long id, @RequestBody ResourceRequestDTO requestDTO) {
        try {
            ResourceRequestDTO updatedRequest = service.updateRequest(id, requestDTO);
            return ResponseEntity.ok(updatedRequest);
        } catch (ResourceNotFoundException e) {
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
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
