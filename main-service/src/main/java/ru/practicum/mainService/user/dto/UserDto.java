package ru.practicum.mainService.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    private String name;

    private String email;
}