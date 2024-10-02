package ru.practicum.ewm.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestResult;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.service.RequestService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerPrivate {

    private final EventMapper eventMapper;
    private final EventService eventService;
    private final RequestMapper requestMapper;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid CreateEventDto createEventDto) {
        return eventMapper.toEventDto(eventService.addEvent(userId, eventMapper.toEvent(createEventDto)));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @RequestBody @Valid EventFullDto dto) {
        return eventMapper.toEventDto(eventService.updateEventByOwner(userId, eventId, eventMapper.toEvent(dto)));
    }

    @PatchMapping("/{eventId}/requests")
    public RequestResult updateRequestsStatus(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @RequestBody RequestUpdateDto dto) {
        return requestService.updateRequestsStatus(userId, eventId, dto);
    }

    @GetMapping
    List<EventShortDto> getEventsByOwner(@PathVariable Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return eventMapper.toEventShortDto(eventService.getEventsByOwner(userId, from, size));
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventMapper.toEventDto(eventService.getEventByOwner(userId, eventId));
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEventOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestMapper.toRequestDto(requestService.getRequestsByEventOwner(userId, eventId));
    }
}
