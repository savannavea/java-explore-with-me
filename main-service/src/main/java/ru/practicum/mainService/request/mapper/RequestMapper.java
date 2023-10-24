package ru.practicum.mainService.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainService.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainService.request.dto.ParticipationRequestDto;
import ru.practicum.mainService.request.model.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .build();
    }

    public static List<ParticipationRequestDto> toRequestDtoList(Iterable<Request> requests) {
        List<ParticipationRequestDto> result = new ArrayList<>();

        for (Request request : requests) {
            result.add(toRequestDto(request));
        }
        return result;
    }

    public static EventRequestStatusUpdateResult toUpdateResultDto(
            List<Request> confirmedRequests,
            List<Request> rejectedRequests
    ) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()))
                .build();
    }
}