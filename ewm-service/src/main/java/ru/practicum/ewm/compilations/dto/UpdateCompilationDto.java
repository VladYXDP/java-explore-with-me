package ru.practicum.ewm.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationDto {
    private Long id;
    private List<Long> events;
    @Size(max = 50)
    private String title;
    private boolean pinned;
}
