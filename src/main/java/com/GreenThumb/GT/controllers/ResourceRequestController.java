/*package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.DTO.ResourceRequestDTO;
import com.GreenThumb.GT.exceptions.*;
import com.GreenThumb.GT.models.ResourceRequest;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.services.ResourceRequestService;
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
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<List<ResourceRequestDTO>> getAllRequests() {
        List<ResourceRequestDTO> requests = service.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<ResourceRequestDTO> createRequest(
            @RequestBody ResourceRequest request,
            @AuthenticationPrincipal User user) {
        request.setUser(user);
        ResourceRequestDTO createdRequest = service.createRequest(request);
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<ResourceRequestDTO> getRequestById(@PathVariable Long id) {
        Optional<ResourceRequestDTO> request = service.getRequestById(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> updateRequest(
            @PathVariable Long id,
            @RequestBody ResourceRequest updatedRequest,
            @AuthenticationPrincipal User user) {
        try {
            ResourceRequestDTO request = service.updateRequest(id, updatedRequest, user.getEmail());
            return ResponseEntity.ok("Request has been updated successfully");
        } catch (ResourceNotFoundException | UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<String> deleteRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            service.deleteRequest(id, user.getEmail());
            return ResponseEntity.ok("Request has been successfully deleted.");
        } catch (ResourceNotFoundException | UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
*/
package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.DTO.ResourceRequestDTO;
import com.GreenThumb.GT.exceptions.ResourceNotFoundException;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.services.ResourceRequestService;
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
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<List<ResourceRequestDTO>> getAllRequests() {
        List<ResourceRequestDTO> requests = service.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<ResourceRequestDTO> createRequest(@RequestBody ResourceRequestDTO requestDTO,
                                                            @AuthenticationPrincipal User user) {
        ResourceRequestDTO createdRequest = service.createRequest(requestDTO, user.getEmail());
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<ResourceRequestDTO> getRequestById(@PathVariable Long id) {
        Optional<ResourceRequestDTO> request = service.getRequestById(id);
        return request.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
    public ResponseEntity<ResourceRequestDTO> updateRequest(@PathVariable Long id, @RequestBody ResourceRequestDTO requestDTO) {
        ResourceRequestDTO updatedRequest = service.updateRequest(id, requestDTO);
        return ResponseEntity.ok(updatedRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')or hasAuthority('EXPERT')")
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
