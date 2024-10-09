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

    private String annotation;
    private Long category;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private LocationDto location;
    private boolean paid = false;
    private int participantLimit = 0;
    private boolean requestModeration = true;
    private String stateAction;
    private String title;
}
