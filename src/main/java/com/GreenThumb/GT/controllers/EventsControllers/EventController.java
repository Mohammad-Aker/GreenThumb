package com.GreenThumb.GT.controllers.EventsControllers;

import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.services.EventsSrevices.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("GreenThumb/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Events> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Events> getEventById(@PathVariable Long id) {
        Optional<Events> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REPRESENTATIVE') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('REPRESENTATIVE')")
    public ResponseEntity<Events> createEvent(@RequestBody Events event, @RequestParam String userEmail, @RequestParam Long partnerId) {
        try {
            Events createdEvent = eventService.createEvent(event, userEmail, partnerId);
            return ResponseEntity.ok(createdEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(null);
        }
    }
}
