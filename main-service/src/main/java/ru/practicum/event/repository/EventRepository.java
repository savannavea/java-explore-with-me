package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCategoryId(Long catId);

    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    List<Event> getByIdIn(Collection<Long> ids);

    Event findByInitiatorIdAndId(Long userId, Long eventId);

    Optional<Event> findByIdAndAndState(Long eventId, EventState state);
}