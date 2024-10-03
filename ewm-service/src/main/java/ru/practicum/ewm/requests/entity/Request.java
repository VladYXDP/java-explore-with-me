package ru.practicum.ewm.requests.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.requests.enums.RequestStatus;
import ru.practicum.ewm.users.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Transient
    private Long eventId;

    @Transient
    private Long count;

    public Request(Long count, Long eventId) {
        this.count = count;
        this.eventId = eventId;
    }
}
