package ru.practicum.ewm.requests.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.entity.Request;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapper {

    private final EventMapper eventMapper;

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
