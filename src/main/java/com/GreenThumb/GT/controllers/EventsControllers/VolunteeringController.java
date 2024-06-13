package com.GreenThumb.GT.controllers.EventsControllers;

import com.GreenThumb.GT.models.Events.Volunteering;
import com.GreenThumb.GT.services.EventsSrevices.VolunteeringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("GreenThumb/api/volunteering")
public class VolunteeringController {

    @Autowired
    private VolunteeringService volunteeringService;

    @GetMapping
    public List<Volunteering> getAllVolunteering() {
        return volunteeringService.getAllVolunteering();
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('VOLUNTEER')")
    public Volunteering createVolunteering(@RequestBody Volunteering volunteering) {
        return volunteeringService.createVolunteering(volunteering);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER')")
    public ResponseEntity<Void> deleteVolunteering(@PathVariable Long id) {
        volunteeringService.deleteVolunteering(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-expired")
    public ResponseEntity<Void> deleteExpiredVolunteeringRecords() {
        volunteeringService.deleteExpiredVolunteeringRecords();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/join")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER')")
    public ResponseEntity<Volunteering> joinEvent(@PathVariable Long eventId,@RequestBody Volunteering volunteering) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) ((org.springframework.security.core.Authentication) authentication).getPrincipal()).getUsername();
        // Set the email in the userCommunityGarden object if needed
        volunteering.getUser().setEmail(userEmail);
        volunteeringService.joinEvent(eventId,volunteering);
        return ResponseEntity.ok().build();
    }
}
