package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.Stat;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stat, Long> {
    @Query(value = "SELECT NEW ru.practicum.HitResponseDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.uri IN :uris AND s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri ORDER BY COUNT(s.ip) DESC")
    List<HitResponseDto> getStatsUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT NEW ru.practicum.HitResponseDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.uri IN :uris AND s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<HitResponseDto> getStatsUriUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT NEW ru.practicum.HitResponseDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri ORDER BY COUNT(s.ip) DESC")
    List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT NEW ru.practicum.HitResponseDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.uri IN :uris AND s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri ORDER BY count(DISTINCT s.ip) DESC")
    List<HitResponseDto> getStatsUnique(LocalDateTime start, LocalDateTime end);
}