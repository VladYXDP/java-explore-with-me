package ru.practicum.ewm.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.location.LocationMapper;
import ru.practicum.ewm.location.LocationRepository;
import ru.practicum.ewm.requests.enums.RequestStatus;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.users.entity.User;
import ru.practicum.ewm.users.repository.UserRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final CategoryService categoryService;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    private final LocationMapper locationMapper;
    private final StatsClient statsClient;

    @Value("${app}")
    private String app;

    @Override
    public Event addEvent(Long userId, Event event) {
        checkDate(event.getEventDate());
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id =" + userId + " не найден!"));
        Long catId = event.getCategory().getId();
        Category category = categoriesRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория с id=" + catId + " не найдена!"));
        Location location = checkLocation(event.getLocation());
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEventByOwner(Long userId, Long eventId, Event event) {
        Event currentEvent = getEvent(eventId, userId);
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
            checkDate(eventDate);
            currentEvent.setEventDate(eventDate);
        }
        if (event.getLocation() != null) {
            Location location = checkLocation(event.getLocation());
            currentEvent.setLocation(location);
        }
        if (event.getPaid() != null) {
            currentEvent.setPaid(event.getPaid());
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
        currentEvent.setConfirmedRequest(requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED););
        return eventRepository.save(currentEvent);
    }

    @Override
    public Event updateEventByAdmin(Long eventId, Event updateEvent) {
        Event event = getEvent(eventId);
        if (updateEvent.getStateAction() != null) {
            StateActionAdmin stateAction = StateActionAdmin.valueOf(updateEvent.getStateAction());
            if (!event.getState().equals(PENDING) && stateAction.equals(PUBLISH_EVENT)) {
                throw new ForbiddenException("Event can't be published because it's not pending");
            }
            if (event.getState().equals(PUBLISHED) && stateAction.equals(REJECT_EVENT)) {
                throw new ForbiddenException("Event can't be rejected because it's already published.");
            }
            if (stateAction.equals(PUBLISH_EVENT)) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
        String annotation = updateEvent.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (updateEvent.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoryService.getCategoryById(updateEvent.getCategory())));
        }
        String description = updateEvent.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = updateEvent.getEventDate();
        if (eventDate != null) {
            checkActualTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(checkLocation(LocationMapper.toLocation(updateEvent.getLocation())));
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        String title = updateEvent.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        return EventMapper.toEventFullDto(eventRepository.save(event),
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getEventsByOwner(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
        return events.stream()
                .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Event getEventByOwner(Long userId, Long eventId) {
        return EventMapper.toEventFullDto(getEvent(eventId, userId),
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
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
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (states != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
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
            List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED).stream()
                    .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
            for (Event event : events) {
                ObjectMapper mapper = new ObjectMapper();
                List<ViewStats> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
                });
                if (!statsDto.isEmpty()) {
                    result.add(EventMapper.toEventFullDtoWithViews(event, statsDto.get(0).getHits(),
                            confirmedRequests.getOrDefault(event.getId(), 0L)));
                } else {
                    result.add(EventMapper.toEventFullDtoWithViews(event, 0L,
                            confirmedRequests.getOrDefault(event.getId(), 0L)));
                }
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
            throw new ValidationException("START can't ba after END.");
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
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
        List<EventShortDtoWithViews> result = new ArrayList<>();
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
            List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
            for (Event event : events) {
                ObjectMapper mapper = new ObjectMapper();
                List<ViewStats> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
                });
                if (!statsDto.isEmpty()) {
                    result.add(EventMapper.toEventShortDtoWithViews(event, statsDto.get(0).getHits(),
                            confirmedRequests.getOrDefault(event.getId(), 0L)));
                } else {
                    result.add(EventMapper.toEventShortDtoWithViews(event, 0L,
                            confirmedRequests.getOrDefault(event.getId(), 0L)));
                }
            }
            EndpointHitDto hit = new EndpointHitDto(app, request.getRequestURI(), request.getRemoteAddr(),
                    LocalDateTime.now());
            statsClient.saveHit(hit);
            return result;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDtoWithViews getEventById(Long eventId, HttpServletRequest request) {
        Event event = getEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new NotFoundException("Event must be published.");
        }
        ResponseEntity<Object> response = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(),
                List.of(request.getRequestURI()), true);
        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> statsDto = mapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        EventFullDtoWithViews result;
        if (!statsDto.isEmpty()) {
            result = EventMapper.toEventFullDtoWithViews(event, statsDto.get(0).getHits(),
                    requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        } else {
            result = EventMapper.toEventFullDtoWithViews(event, 0L,
                    requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        }
        EndpointHitDto hit = new EndpointHitDto(app, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        statsClient.saveHit(hit);
        return result;
    }

    protected void checkDate(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Неверная дата события!");
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
