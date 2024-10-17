package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResult {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
