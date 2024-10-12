package ru.practicum.ewm.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.CreateEventDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventControllerAdmin {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventAdminDto dto) {
        return eventMapper.toEventDto(eventService.updateEventByAdmin(eventId, eventMapper.toEvent(dto)));
    }

    @GetMapping
    public List<EventFullDto> getEventsByAdminParams(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<String> states,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern =
                                                                      "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern =
                                                                      "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(value = "from", defaultValue = "0")
                                                              @PositiveOrZero Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10")
                                                              @Positive Integer size) {
        return eventMapper.toEventDto(eventService.getEventsByAdminParams(users, states, categories, rangeStart, rangeEnd, from, size));
    }
}
