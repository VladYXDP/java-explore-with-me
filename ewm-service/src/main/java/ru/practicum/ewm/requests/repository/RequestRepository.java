package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.requests.entity.Request;
import ru.practicum.ewm.requests.enums.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findAllByEvent(Event event);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventAndIdInAndStatus(Event event, List<Long> requestId, RequestStatus status);

    Boolean existsByRequesterIdAndEvent(Long userId, Event event);

    long countByEventAndStatus(Event event, RequestStatus status);

    @Query("SELECT new ru.practicum.ewm.requests.entity.Request(COUNT(DISTINCT r.id), r.event.id) " +
            "FROM Request AS r " +
            "WHERE r.event.id IN (:ids) AND r.status = :status " +
            "GROUP BY (r.event)")
    List<Request> findAllByEventIdInAndStatus(@Param("ids")List<Long> ids, @Param("status")RequestStatus status);
}
