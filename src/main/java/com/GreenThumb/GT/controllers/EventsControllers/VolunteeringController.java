package com.GreenThumb.GT.controllers.EventsControllers;

import com.GreenThumb.GT.models.Events.Volunteering;
import com.GreenThumb.GT.services.EventsSrevices.VolunteeringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteering")
public class VolunteeringController {

    @Autowired
    private VolunteeringService volunteeringService;

    @GetMapping
    public List<Volunteering> getAllVolunteering() {
        return volunteeringService.getAllVolunteering();
    }

    @PostMapping
    public Volunteering createVolunteering(@RequestBody Volunteering volunteering) {
        return volunteeringService.createVolunteering(volunteering);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteering(@PathVariable Long id) {
        volunteeringService.deleteVolunteering(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteExpired")
    public ResponseEntity<Void> deleteExpiredVolunteeringRecords() {
        volunteeringService.deleteExpiredVolunteeringRecords();
        return ResponseEntity.ok().build();
    }
    @PostMapping("/join")
    public ResponseEntity<Volunteering> joinEvent(@RequestParam String email,
                                                  @RequestParam Long eventId,
                                                  @RequestParam String role,
                                                  @RequestParam String tasks,
                                                  @RequestParam int hoursVolunteered,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                  @RequestParam String status,
                                                  @RequestParam(required = false) String notes) {
        Volunteering volunteering = volunteeringService.joinEvent(email, eventId, role, tasks, hoursVolunteered, startDate, endDate, status, notes);
        return ResponseEntity.ok(volunteering);
    @PostMapping("/{eventId}/join")
    public ResponseEntity<Volunteering> joinEvent(@PathVariable Long eventId,@RequestBody Volunteering volunteering) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = ((UserDetails) ((org.springframework.security.core.Authentication) authentication).getPrincipal()).getUsername();
        // Set the email in the userCommunityGarden object if needed
        volunteering.getUser().setEmail(userEmail);
        volunteeringService.joinEvent(eventId,volunteering);
        return ResponseEntity.ok().build();
    }
}
