package ru.practicum.ewm.requests.mapper;

import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getId(),
                participationRequest.getCreated(),
                participationRequest.getEvent().getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus()
        );
    }
}
