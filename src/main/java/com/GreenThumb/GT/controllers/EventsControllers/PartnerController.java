package com.GreenThumb.GT.controllers.EventsControllers;

import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.models.Events.Partner;
import com.GreenThumb.GT.services.EventsSrevices.EventService;
import com.GreenThumb.GT.services.EventsSrevices.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("GreenThumb/api/partners")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Partner> getAllPartners() {
        return partnerService.getAllPartners();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Partner> getPartnerById(@PathVariable Long id) {
        Optional<Partner> partner = partnerService.getPartnerById(id);
        return partner.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{eventId}/assignPartner/{partnerId}")
    @PreAuthorize("hasAuthority('REPRESENTATIVE') or hasAuthority('ADMIN')")
    public ResponseEntity<Events> assignPartnerToEvent(@PathVariable Long eventId, @PathVariable Long partnerId) {
        try {
            Events updatedEvent = eventService.assignPartnerToEvent(eventId, partnerId);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Partner> createPartner(@RequestBody Partner partner) {
        Partner createdPartner = partnerService.createPartner(partner);
        return ResponseEntity.ok(createdPartner);
    }
}
