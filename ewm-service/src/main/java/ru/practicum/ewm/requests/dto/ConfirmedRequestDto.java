package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmedRequestDto {
    private Long count;
    private Long event;
}
