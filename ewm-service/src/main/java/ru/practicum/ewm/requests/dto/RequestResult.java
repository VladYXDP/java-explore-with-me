package ru.practicum.ewm.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.requests.entity.Request;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestResult {
    private List<Request> confirmedRequests;
    private List<Request> rejectedRequests;
}
