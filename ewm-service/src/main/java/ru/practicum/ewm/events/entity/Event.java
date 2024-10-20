package ru.practicum.ewm.events.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.categories.entity.Category;
import ru.practicum.ewm.events.enums.State;
import ru.practicum.ewm.events.enums.StateActionPrivate;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.users.entity.User;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_on", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdOn;

    @Column
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false)
    private String title;

    @Transient
    private Long confirmedRequest;

    @Transient
    private String stateAction;

    @Transient
    private Long views;

    @Transient
    private StateActionPrivate stateActionPrivate;

    @Transient
    private Long categoryId;

    @Transient
    private Long userId;
}