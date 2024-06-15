package com.GreenThumb.GT.services.EventsSrevices;

import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.models.Events.Partner;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.EventsRepository.EventRepository;
import com.GreenThumb.GT.repositories.EventsRepository.PartnerRepository;
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

    @Autowired
    private PartnerRepository partnerRepository;

    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Events> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Events createEvent(Events event) {
        return eventRepository.save(event);
    }

    public Events removePartnerFromEvent(Long eventId) {
        Optional<Events> eventOpt = eventRepository.findById(eventId);

        if (eventOpt.isPresent()) {
            Events event = eventOpt.get();
            event.setPartner(null);
            return eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("Event not found.");
        }
    }

    public Events assignPartnerToEvent(Long eventId, Long partnerId) {
        Optional<Events> eventOpt = eventRepository.findById(eventId);
        Optional<Partner> partnerOpt = partnerRepository.findById(partnerId);

        if (eventOpt.isPresent() && partnerOpt.isPresent()) {
            Events event = eventOpt.get();
            event.setPartner(partnerOpt.get());
            return eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("Event or Partner not found.");
        }
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
}
