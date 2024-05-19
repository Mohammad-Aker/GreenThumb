package com.GreenThumb.GT.controllers;

import com.GreenThumb.GT.DTO.ExchangeDTO;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.services.ExchangeService;
import com.GreenThumb.GT.exceptions.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("GreenThumb/api/exchanges")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<List<ExchangeDTO>> getAllExchanges() {
        List<ExchangeDTO> exchanges = exchangeService.getAllExchanges();
        return ResponseEntity.ok(exchanges);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<ExchangeDTO> createExchange(@RequestBody ExchangeDTO exchangeDTO, @AuthenticationPrincipal User user) {
        // Extract the resource owner and request user
        ExchangeDTO tempExchange = exchangeService.extractUsersForExchange(exchangeDTO);

        // Check if the authenticated user is the owner of the resource
        if (!user.getEmail().equals(tempExchange.getFromUserEmail())) {
            throw new UnauthorizedOperationException("You are not authorized to create this exchange.");
        }

        // Create the exchange
        ExchangeDTO createdExchange = exchangeService.createExchange(tempExchange);
        return ResponseEntity.ok(createdExchange);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<ExchangeDTO> getExchangeById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Optional<ExchangeDTO> exchange = exchangeService.getExchangeById(id);
        if (exchange.isPresent() && !user.getEmail().equals(exchange.get().getFromUserEmail()) && !user.getEmail().equals(exchange.get().getToUserEmail())) {
            throw new UnauthorizedOperationException("You are not authorized to view this exchange.");
        }
        return exchange.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('EXPERT')")
    public ResponseEntity<ExchangeDTO> updateExchange(@PathVariable Long id, @RequestBody ExchangeDTO exchangeDTO, @AuthenticationPrincipal User user) {
        Optional<ExchangeDTO> existingExchange = exchangeService.getExchangeById(id);
        if (existingExchange.isPresent() && !user.getEmail().equals(existingExchange.get().getFromUserEmail()) && !user.getEmail().equals(existingExchange.get().getToUserEmail())) {
            throw new UnauthorizedOperationException("You are not authorized to update this exchange.");
        }
        ExchangeDTO updatedExchange = exchangeService.updateExchange(id, exchangeDTO);
        return ResponseEntity.ok(updatedExchange);
    }
}
