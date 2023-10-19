package ru.practicum.mainService.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShortDto {
    private Long id;

    private String name;
}