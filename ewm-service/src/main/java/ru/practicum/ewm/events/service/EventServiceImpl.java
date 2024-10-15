package ru.practicum.ewm.events.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.entity.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.categories.service.CategoryService;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.enums.State;
import ru.practicum.ewm.events.enums.StateActionAdmin;
import ru.practicum.ewm.events.enums.StateActionPrivate;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.repository.EventSpecification;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.location.LocationRepository;
import ru.practicum.ewm.requests.entity.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.users.entity.User;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.stat_svc.HitDto;
import ru.practicum.stat_svc.StatsClient;
import ru.practicum.stat_svc.ViewStats;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.events.enums.State.PENDING;
import static ru.practicum.ewm.events.enums.State.PUBLISHED;
import static ru.practicum.ewm.events.enums.StateActionAdmin.PUBLISH_EVENT;
import static ru.practicum.ewm.events.enums.StateActionAdmin.REJECT_EVENT;
import static ru.practicum.ewm.events.enums.StateActionPrivate.CANCEL_REVIEW;
import static ru.practicum.ewm.events.enums.StateActionPrivate.SEND_TO_REVIEW;
import static ru.practicum.ewm.requests.enums.RequestStatus.CONFIRMED;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final CategoriesRepository categoriesRepository;


    @Value("${app}")
    private String app;

    @Override
    public Event addEvent(Long userId, Event event) {
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Неверная дата события!");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id =" + userId + " не найден!"));
        Category category = categoriesRepository.findById(event.getCategoryId()).orElseThrow(() ->
                new NotFoundException("Категория с id=" + event.getCategoryId() + " не найдена!"));
        Location location = checkLocation(event.getLocation());
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setPaid(event.getPaid());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEventByOwner(Event event) {
        Event currentEvent = getEvent(event.getId(), event.getUserId());
        if (currentEvent.getState() == PUBLISHED) {
            throw new ForbiddenException("Ошибка обновления события!");
        }
        String annotation = event.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            currentEvent.setAnnotation(annotation);
        }
        if (event.getCategory() != null) {
            currentEvent.setCategory(event.getCategory());
        }
        String description = event.getDescription();
        if (description != null && !description.isBlank()) {
            currentEvent.setDescription(description);
        }
        LocalDateTime eventDate = event.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Неверная дата события!");
            }
            currentEvent.setEventDate(eventDate);
        }
        if (event.getLocation() != null) {
            currentEvent.setLocation(checkLocation(event.getLocation()));
        }
        if (event.getParticipantLimit() != null) {
            currentEvent.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            currentEvent.setRequestModeration(event.getRequestModeration());
        }
        String title = event.getTitle();
        if (title != null && !title.isBlank()) {
            currentEvent.setTitle(title);
        }
        if (event.getStateActionPrivate() != null) {
            StateActionPrivate stateActionPrivate = StateActionPrivate.valueOf(event.getStateActionPrivate().name());
            if (stateActionPrivate.equals(SEND_TO_REVIEW)) {
                currentEvent.setState(PENDING);
            } else if (stateActionPrivate.equals(CANCEL_REVIEW)) {
                currentEvent.setState(State.CANCELED);
            }
        }
        currentEvent.setConfirmedRequest(requestRepository.countByEventAndStatus(event, CONFIRMED));
        return eventRepository.save(currentEvent);
    }

    @Override
    public Event updateEventByAdmin(Long eventId, Event event) {
        Event currentEvent = getEvent(eventId);
        if (event.getStateAction() != null) {
            StateActionAdmin stateAction = StateActionAdmin.valueOf(event.getStateAction());
            if (!currentEvent.getState().equals(PENDING) && stateAction.equals(PUBLISH_EVENT)) {
                throw new ForbiddenException("Событие не может быть опубликовано без статуса pending!");
            }
            if (currentEvent.getState().equals(PUBLISHED) && stateAction.equals(REJECT_EVENT)) {
                throw new ForbiddenException("Опубликованное событие нельзя отменить!");
            }
            if (stateAction.equals(PUBLISH_EVENT)) {
                currentEvent.setState(PUBLISHED);
                currentEvent.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(REJECT_EVENT)) {
                currentEvent.setState(State.CANCELED);
            }
        }
        String annotation = event.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            currentEvent.setAnnotation(annotation);
        }
        if (event.getCategory() != null) {
            currentEvent.setCategory(categoryService.getCategoryById(event.getCategory().getId()));
        }
        String description = event.getDescription();
        if (description != null && !description.isBlank()) {
            currentEvent.setDescription(description);
        }
        LocalDateTime eventDate = event.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Неверная дата события!");
            }
            currentEvent.setEventDate(eventDate);
        }
        if (event.getLocation() != null) {
            currentEvent.setLocation(checkLocation(event.getLocation()));
        }
//        if (event.getPaid() != null) {
//            currentEvent.setPaid(event.getPaid());
//        }
        if (event.getParticipantLimit() != null && !event.getParticipantLimit().equals(0)) {
            currentEvent.setParticipantLimit(event.getParticipantLimit());
        }
        String title = event.getTitle();
        if (title != null && !title.isBlank()) {
            currentEvent.setTitle(title);
        }
        currentEvent.setConfirmedRequest(requestRepository.countByEventAndStatus(currentEvent, CONFIRMED));
        return eventRepository.save(currentEvent);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getEventsByOwner(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(Request::getCount, Request::getEventId));
        return events.stream()
                .peek(event -> event.setConfirmedRequest(confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Event getEventByOwner(Long userId, Long eventId) {
        Event event = getEvent(eventId, userId);
        event.setConfirmedRequest(requestRepository.countByEventAndStatus(event, CONFIRMED));
        return event;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getEventsByAdminParams(List<Long> users, List<String> states, List<Long> categories,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Incorrectly made request.");
        }
        Specification<Event> specification = Specification.where(null);
        if (users != null) {
            specification = specification.and(EventSpecification.inInitiator(users));
        }
        if (states != null) {
            specification = specification.and(EventSpecification.inState(states));
        }
        if (categories != null) {
            specification = specification.and(EventSpecification.inCategories(categories));
        }
        if (rangeStart != null) {
            specification = specification.and(EventSpecification.atRangeStart(rangeStart));
        }
        if (rangeStart != null) {
            specification = specification.and(EventSpecification.atRangeEnd(rangeEnd));
        }
        List<Event> events = eventRepository.findAll(specification, PageRequest.of(from / size, size)).getContent();
        List<Event> result = new ArrayList<>();
        if (events.isEmpty()) {
            return result;
        } else {
            List<String> uris = events.stream()
                    .map(event -> String.format("/events/%s", event.getId()))
                    .collect(Collectors.toList());
            Optional<LocalDateTime> start = events.stream()
                    .map(Event::getCreatedOn)
                    .min(LocalDateTime::compareTo);
            ResponseEntity<Object> response = statsClient.getStats(start.get(), LocalDateTime.now(), uris, true);
            List<ViewStats> statsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });
            List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
            List<Request> rq = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED);
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED).stream()
                    .collect(Collectors.toMap(Request::getEventId, Request::getCount));
            for (Event event : events) {
                event.setConfirmedRequest(confirmedRequests.getOrDefault(event.getId(), 0L));
                if (!statsDto.isEmpty()) {
                    event.setViews(statsDto.get(0).getHits());
                }
                result.add(event);
            }
            return result;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                 Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала не может быть после даты окончания!");
        }
        Specification<Event> specification = Specification.where(null);
        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (paid != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("paid"), paid));
        }
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), PUBLISHED));
        PageRequest pageRequest;
        switch (sort) {
            case "EVENT_DATE":
                pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate"));
                break;
            case "VIEWS":
                pageRequest = PageRequest.of(from / size, size, Sort.by("views").descending());
                break;
            default:
                throw new ValidationException("Unknown sort: " + sort);
        }
        List<Event> events = eventRepository.findAll(specification, pageRequest).getContent();
        if (events.isEmpty()) {
            return events;
        } else {
            List<String> uris = events.stream()
                    .map(event -> String.format("/events/%s", event.getId()))
                    .collect(Collectors.toList());
            Optional<LocalDateTime> start = events.stream()
                    .map(Event::getCreatedOn)
                    .min(LocalDateTime::compareTo);
            ResponseEntity<Object> response = statsClient.getStats(start.get(), LocalDateTime.now(), uris, true);
            List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(Request::getCount, Request::getEventId));
            List<ViewStats> statsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });
            for (Event event : events) {
                if (!statsDto.isEmpty()) {
                    event.setViews(statsDto.get(0).getHits());
                    event.setConfirmedRequest(confirmedRequests.getOrDefault(event.getId(), 0L));
                    events.add(event);
                }
            }
            HitDto hit = new HitDto(app, request.getRequestURI(), request.getRemoteAddr(),
                    LocalDateTime.now());
            statsClient.saveHit(hit);
            return events;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Event getEventById(Long eventId, HttpServletRequest request) {
        Event event = getEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new NotFoundException("Событие с id " + eventId + " не найдено!");
        }
        ResponseEntity<Object> response = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(),
                List.of(request.getRequestURI()), true);
        List<ViewStats> statsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        event.setConfirmedRequest(requestRepository.countByEventAndStatus(event, CONFIRMED));
        if (!statsDto.isEmpty()) {
            event.setViews(statsDto.get(0).getHits());
        } else {
            event.setViews(0L);
        }
        HitDto hit = new HitDto(app, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        statsClient.saveHit(hit);
        return event;
    }

    protected void checkDate(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Неверная дата события!");
        }
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Event getEvent(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private Location checkLocation(Location location) {
        if (locationRepository.existsByLatAndLon(location.getLat(), location.getLon())) {
            return locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        } else {
            return locationRepository.save(location);
        }
    }
}
