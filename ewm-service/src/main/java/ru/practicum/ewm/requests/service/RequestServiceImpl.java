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
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.dto.RequestResult;
import ru.practicum.ewm.requests.entity.Request;
import ru.practicum.ewm.requests.enums.RequestStatus;
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

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Request addRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        User user = getUser(userId);
        if (requestRepository.existsByRequesterIdAndEvent(userId, event)) {
            throw new ForbiddenException("Request is already exist.");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Initiator can't send request to his own event.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ForbiddenException("Participation is possible only in published event.");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <=
                requestRepository.countByEventAndStatus(event, CONFIRMED)) {
            throw new ForbiddenException("Participant limit has been reached.");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(PENDING);
        } else {
            request.setStatus(CONFIRMED);
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
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ForbiddenException("The participant limit has been reached.");
        }
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByEventAndIdInAndStatus(event,
                requestUpdateDto.getRequestIds(), PENDING);
        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            if (requestUpdateDto.getStatus() == REJECTED) {
                request.setStatus(REJECTED);
                rejected.add(request);
            }
            if (requestUpdateDto.getStatus() == CONFIRMED && event.getParticipantLimit() > 0 &&
                    (confirmedRequests + i) < event.getParticipantLimit()) {
                request.setStatus(CONFIRMED);
                confirmed.add(request);
            } else {
                request.setStatus(REJECTED);
                rejected.add(request);
            }
        }
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
}
