package com.GreenThumb.GT.services.EventsSrevices;

import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.models.Events.Volunteering;
import com.GreenThumb.GT.repositories.EventsRepository.EventRepository;
import com.GreenThumb.GT.repositories.EventsRepository.PartnerRepository;
import com.GreenThumb.GT.repositories.EventsRepository.VolunteeringRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteeringService {

    @Autowired
    private VolunteeringRepository volunteeringRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventsRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    public List<Volunteering> getAllVolunteering() {
        return volunteeringRepository.findAll();
    }

    public Volunteering createVolunteering(Volunteering volunteering) {
        return volunteeringRepository.save(volunteering);
    }

    public void deleteVolunteering(Long id) {
        volunteeringRepository.deleteById(id);
    }

    public void joinEvent(Long eventId, Volunteering volunteering) {
        Events events = eventsRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Community Garden not found with id: " + eventId));
        volunteering.setEvent(events);
        volunteeringRepository.save(volunteering);

    }
}
