package ru.practicum.mainService.compilations.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Boolean pinned = false;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private List<Long> events = new ArrayList<>();
}