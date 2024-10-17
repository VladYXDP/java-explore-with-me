package ru.practicum.ewm.events.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.events.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event addEvent(Long userId, Event event);

    Event updateEventByOwner(Event updateEvent);

    Event updateEventByAdmin(Long eventId, Event updateEvent);

    List<Event> getEventsByOwner(Long userId, Integer from, Integer size);

    Event getEventByOwner(Long userId, Long eventId);

    List<Event> getEventsByAdminParams(List<Long> users, List<String> states, List<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                       Integer from, Integer size);

    List<Event> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                          LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                          Integer size, HttpServletRequest request);

    Event getEventById(Long eventId, HttpServletRequest request);
}
