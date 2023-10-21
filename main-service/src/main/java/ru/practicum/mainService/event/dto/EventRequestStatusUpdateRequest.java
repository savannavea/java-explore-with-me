package ru.practicum.mainService.event.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;

    @NotBlank
    private String status;
}
