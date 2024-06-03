package com.GreenThumb.GT.controllers.EventsControllers;

import com.GreenThumb.GT.models.Events.Volunteering;
import com.GreenThumb.GT.services.EventsSrevices.VolunteeringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    }
}
