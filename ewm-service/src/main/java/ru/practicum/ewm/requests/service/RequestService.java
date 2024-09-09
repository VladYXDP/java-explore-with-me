package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.dto.RequestResult;
import ru.practicum.ewm.requests.entity.Request;

import java.util.List;

public interface RequestService {

    Request addRequest(Long userId, Long eventId);

    RequestResult updateRequestsStatus(Long userId, Long eventId, RequestUpdateDto request);

    Request cancelRequest(Long userId, Long requestId);

    List<Request> getRequestsByEventOwner(Long userId, Long eventId);

    List<Request> getRequestsByUser(Long userId);
}
