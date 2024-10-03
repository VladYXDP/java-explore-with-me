package ru.practicum.ewm.compilations.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.events.dto.EventFullDto;

import java.util.List;

@Getter
@Setter
@Builder
public class CompilationDto {
    private Long id;
    private List<EventFullDto> events;
    private Boolean pinned;
    private String title;

}
