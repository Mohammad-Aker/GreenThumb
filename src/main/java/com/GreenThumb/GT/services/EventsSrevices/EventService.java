package com.GreenThumb.GT.services.EventsSrevices;

import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.EventsRepository.EventRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Events> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Events createEvent(Events event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Events createEvent(Events event, String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isPresent() && userOpt.get().isRepresentative()) {
            return eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("User does not have permission to create events.");
        }
    }
/*
    public Events addPartnershipToEvent(Long eventId, PartnershipDTO partnershipDTO) {
        Optional<Events> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Events event = eventOpt.get();
            Optional<User> userOpt = userRepository.findByEmail(partnershipDTO.getUserEmail());
            if (userOpt.isPresent() && (userOpt.get().isAdmin() || userOpt.get().isRepresentative())) {
                event.setPartnerName(partnershipDTO.getPartnerName());
                event.setPartnerDescription(partnershipDTO.getPartnerDescription());
                event.setPartnerContact(partnershipDTO.getPartnerContact());
                return eventRepository.save(event);
            } else {
                throw new IllegalArgumentException("User does not have permission to add partnerships to this event.");
            }
        } else {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }
    }

 */
}
