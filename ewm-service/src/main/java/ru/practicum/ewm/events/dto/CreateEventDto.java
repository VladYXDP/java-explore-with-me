package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.location.LocationDto;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDto {

    @NotNull
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @NotNull
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private boolean paid = false;
    @PositiveOrZero
    private Integer participantLimit = 0;
    private boolean requestModeration = true;
    private String stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
