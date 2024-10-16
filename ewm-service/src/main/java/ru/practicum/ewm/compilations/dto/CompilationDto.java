package ru.practicum.ewm.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    @Size(min = 50, max = 50)
    private String title;

}
