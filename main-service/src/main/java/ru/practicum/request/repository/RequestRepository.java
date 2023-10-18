package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long id);

    Request findOneByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByEventInitiatorIdAndEventId(Long initiatorId, Long eventId);

    List<Request> findByEventId(Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    List<Request> findAllByIdIn(List<Long> ids);
}
