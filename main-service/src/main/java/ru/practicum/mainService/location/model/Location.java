package ru.practicum.mainService.location.model;

import lombok.*;
import ru.practicum.mainService.location.enums.LocationState;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Float lat;

    private Float lon;

    private String name;

    private Float radius;

    @Enumerated(EnumType.STRING)
    @Column(name = "locationState")
    private LocationState locationState;

    public Location(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}