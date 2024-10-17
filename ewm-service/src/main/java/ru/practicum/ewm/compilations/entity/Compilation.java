package ru.practicum.ewm.compilations.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.events.entity.Event;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Boolean pinned;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;

    public Compilation(String title, Boolean pinned) {
        this.title = title;
        this.pinned = pinned;
    }

    @Transient
    private List<Long> eventsId;
}
