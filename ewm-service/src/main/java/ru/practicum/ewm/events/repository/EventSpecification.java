package ru.practicum.ewm.events.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.events.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecification {

    public static Specification<Event> inInitiator(List<Long> users) {
        return (root, query, cb) -> cb.and(root.get("initiator").get("id").in(users));
    }

    public static Specification<Event> inState(List<String> states) {
        return (root, query, cb) -> cb.and(root.get("state").as(String.class).in(states));
    }

    public static Specification<Event> inCategories(List<Long> categories) {
        return (root, query, cb) -> root.get("category").get("id").in(categories);
    }

    public static Specification<Event> atRangeStart(LocalDateTime rangeStart) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdOn"), rangeStart);
    }

    public static Specification<Event> atRangeEnd(LocalDateTime rangeEnd) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
    }
}
