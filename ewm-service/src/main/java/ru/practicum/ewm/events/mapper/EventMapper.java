package ru.practicum.ewm.events.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.categories.mapper.CategoryMapper;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.location.LocationMapper;
import ru.practicum.ewm.users.mapper.UserMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final LocationMapper locationMapper;

    public Event toEvent(CreateEventDto createEventDto) {
        return Event.builder()
                .annotation(createEventDto.getAnnotation())
                .description(createEventDto.getDescription())
                .eventDate(createEventDto.getEventDate())
                .location(locationMapper.toLocation(createEventDto.getLocation()))
                .paid(createEventDto.isPaid())
                .participantLimit(createEventDto.getParticipantLimit())
                .requestModeration(createEventDto.isRequestModeration())
                .title(createEventDto.getTitle())
                .build();
    }

    public List<EventDto> toEventDto(List<Event> events) {
        return events.stream().map(this::toEventDto).toList();
    }

    public EventDto toEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequest())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .location(locationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public EventDto toEventFullDtoWithViews(Event event, Long views, Long confirmedRequests) {
        return EventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .location(locationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public EventShortDto toEventShortDto(Event event, Long confirmedRequests) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto toEventShortDtoWithViews(Event event, Long views, Long confirmedRequests) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }
}
