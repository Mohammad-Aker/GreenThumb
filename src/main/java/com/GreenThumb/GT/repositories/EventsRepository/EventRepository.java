package com.GreenThumb.GT.repositories.EventsRepository;

import com.GreenThumb.GT.models.Events.Events;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
    @NotNull
    Optional<Events> findById(@NotNull Long id);
    Events findByName(String name);
}
