package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmedRequestDto {
    private long count;
    private Long event;
}
