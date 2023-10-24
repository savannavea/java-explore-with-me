package ru.practicum.mainService.event.model;

import lombok.*;
import ru.practicum.mainService.category.model.Category;
import ru.practicum.mainService.event.enums.EventState;
import ru.practicum.mainService.location.model.Location;
import ru.practicum.mainService.request.model.Request;
import ru.practicum.mainService.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @Column(nullable = false, length = 7000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private Long participantLimit;

    @Column(nullable = false)
    private Boolean requestModeration;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventState state;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private List<Request> participationRequests;
    @Transient
    private Long confirmedRequests;
    @Transient
    private Long views;
}