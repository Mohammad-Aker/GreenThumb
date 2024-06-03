package com.GreenThumb.GT.services.EventsSrevices;

import com.GreenThumb.GT.models.Events.Events;
import com.GreenThumb.GT.models.Events.Volunteering;
import com.GreenThumb.GT.models.User.Role;
import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.repositories.EventsRepository.EventRepository;
import com.GreenThumb.GT.repositories.EventsRepository.VolunteeringRepository;
import com.GreenThumb.GT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VolunteeringService {

    @Autowired
    private VolunteeringRepository volunteeringRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventsRepository;



    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void deleteExpiredVolunteeringRecords() {
        LocalDate today = LocalDate.now();
        List<Volunteering> expiredRecords = volunteeringRepository.findByEndDateBefore(today);
        volunteeringRepository.deleteAll(expiredRecords);
    }

    public List<Volunteering> getAllVolunteering() {
        return volunteeringRepository.findAll();
    }

    public Volunteering createVolunteering(Volunteering volunteering) {
        return volunteeringRepository.save(volunteering);
    }

    public void deleteVolunteering(Long id) {
        volunteeringRepository.deleteById(id);
    }

    public Volunteering joinEvent(String email, Long eventId, String role, String tasks, int hoursVolunteered, LocalDate startDate, LocalDate endDate, String status, String notes) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Events event = eventsRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        if (user.getRole() != Role.VOLUNTEER) {
            throw new RuntimeException("User does not have the VOLUNTEER role");
        }

        Volunteering volunteering = Volunteering.builder()
                .user(user)
                .event(event)
                .role(role)
                .tasks(tasks)
                .hoursVolunteered(hoursVolunteered)
                .startDate(startDate)
                .endDate(endDate)
                .status(status)
                .notes(notes)
                .build();

        return volunteeringRepository.save(volunteering);
    }
}
