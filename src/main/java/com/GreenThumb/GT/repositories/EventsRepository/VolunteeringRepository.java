package com.GreenThumb.GT.repositories.EventsRepository;

import com.GreenThumb.GT.models.Events.Volunteering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VolunteeringRepository extends JpaRepository<Volunteering, Long> {
    //boolean existsByUserAndEvent(User user, Events event);
}
