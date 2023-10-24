package ru.practicum.mainService.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainService.event.enums.EventState;
import ru.practicum.mainService.event.model.Event;

import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByCategoryId(Long catId);

    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByInitiatorIdAndId(Long userId, Long eventId);

    Optional<Event> findByIdAndAndState(Long eventId, EventState state);

    List<Event> findAllByIdIn(List<Long> evenIdList);
}