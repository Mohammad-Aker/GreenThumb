package com.GreenThumb.GT.repositories.EventsRepository;

import com.GreenThumb.GT.models.Events.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
}
