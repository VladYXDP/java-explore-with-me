package ru.practicum.ewm.compilations.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationDto {
    private Long id;
    private List<Long> events;
    private String title;
    private boolean pinned;
}
