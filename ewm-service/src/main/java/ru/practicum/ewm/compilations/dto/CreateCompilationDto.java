package ru.practicum.ewm.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateCompilationDto {

    private List<Long> events;
    private boolean pinned = false;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
