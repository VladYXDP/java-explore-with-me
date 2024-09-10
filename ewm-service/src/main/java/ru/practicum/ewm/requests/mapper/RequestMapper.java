package ru.practicum.ewm.requests.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.entity.Request;

import java.util.List;

@Component
public class RequestMapper {

    public List<RequestDto> toRequestDto(List<Request> requests) {
        return requests.stream().map(this::toRequestDto).toList();
    }

    public RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
