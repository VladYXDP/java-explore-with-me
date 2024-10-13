package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.enums.State;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.dto.RequestResult;
import ru.practicum.ewm.requests.entity.Request;
import ru.practicum.ewm.requests.enums.RequestStatus;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.users.entity.User;
import ru.practicum.ewm.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.ewm.requests.enums.RequestStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;


    @Override
    public Request addRequest(Long userId, Long eventId) {
        if (eventId == 0) {
            throw new ForbiddenException("Ошибка добавления запроса на участие!");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        User user = getUser(userId);
        if (requestRepository.existsByRequesterIdAndEvent(userId, event)) {
            throw new ForbiddenException("Запрос на участие уже существует!");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Initiator can't send request to his own event.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Participation is possible only in published event.");
        }
        checkParticipantLimit(event);
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if (!event.getRequestModeration() && event.getParticipantLimit() > 0) {
            request.setStatus(CONFIRMED);
        } else if (event.getParticipantLimit() == 0) {
            request.setStatus(CONFIRMED);
        } else {
            request.setStatus(PENDING);
        }
        return requestRepository.save(request);
    }

    @Override
    public RequestResult updateRequestsStatus(Long userId, Long eventId,
                                              RequestUpdateDto requestUpdateDto) {
        User initiator = getUser(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found."));
        if (!event.getInitiator().equals(initiator)) {
            throw new ValidationException("User isn't initiator.");
        }
        long confirmedRequests = requestRepository.countByEventAndStatus(event, CONFIRMED);
        if (confirmedRequests > 0 && confirmedRequests == event.getParticipantLimit()) {
            throw new ForbiddenException("The participant limit has been reached.");
        }
        List<RequestDto> confirmed = new ArrayList<>();
        List<RequestDto> rejected = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByEventAndIdInAndStatus(event,
                requestUpdateDto.getRequestIds(), PENDING);
        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            if (RequestStatus.valueOf(requestUpdateDto.getStatus()).equals(REJECTED)) {
                request.setStatus(REJECTED);
                rejected.add(requestMapper.toRequestDto(request));
            }
            if (RequestStatus.valueOf(requestUpdateDto.getStatus()).equals(CONFIRMED)
                    && event.getParticipantLimit() > 0 && confirmedRequests < event.getParticipantLimit()) {
                request.setStatus(CONFIRMED);
                confirmed.add(requestMapper.toRequestDto(request));
            } else {
                request.setStatus(REJECTED);
                rejected.add(requestMapper.toRequestDto(request));
            }
        }
        requestRepository.saveAll(requests);
        return new RequestResult(confirmed, rejected);
    }

    @Override
    public Request cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId);
        request.setStatus(RequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Request> getRequestsByEventOwner(Long userId, Long eventId) {
        checkUser(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
        return requestRepository.findAllByEvent(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Request> getRequestsByUser(Long userId) {
        checkUser(userId);
        return requestRepository.findAllByRequesterId(userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    private void checkParticipantLimit(Event event) {
        long requestCount = requestRepository.countByEventAndStatus(event, CONFIRMED);
        if (requestCount > 0 && requestCount == event.getParticipantLimit()) {
            throw new ForbiddenException("Превышено число участников события!");
        }
    }
}
