package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HitRequestDto {

    private Long id;

    @NotBlank(message = "The name of the application cannot be empty")
    private String app;

    @NotBlank(message = "URI cannot be empty")
    private String uri;

    @NotBlank(message = "User IP cannot be empty")
    private String ip;

    @NotNull(message = "The time of sending the request cannot be empty")
    private LocalDateTime timestamp;

}
