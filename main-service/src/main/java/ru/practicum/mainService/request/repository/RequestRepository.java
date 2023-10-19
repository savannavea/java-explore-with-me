package ru.practicum.mainService.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainService.request.enums.RequestStatus;
import ru.practicum.mainService.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long id);

    Request findOneByEventIdAndRequesterId(Long eventId, Long userId);

    List<Request> findByEventId(Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    List<Request> findAllByIdIn(List<Long> ids);
}