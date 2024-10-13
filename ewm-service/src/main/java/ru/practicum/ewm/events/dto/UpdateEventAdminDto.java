package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.location.LocationDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventAdminDto {

    @Size(min = 20, max = 2000)
    private String annotation;
    private Long category;
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
