package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.practicum.ewm.location.LocationDto;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDto {

    @Size(min = 20, max = 2000)
    @NotBlank
    private String annotation;
    @NotNull
    private Long category;
    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    @Valid
    private LocationDto location;
    private boolean paid = false;
    @PositiveOrZero
    private int participantLimit = 0;
    private boolean requestModeration = true;
    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
}
