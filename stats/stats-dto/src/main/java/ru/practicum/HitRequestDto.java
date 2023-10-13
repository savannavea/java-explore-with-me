package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @NotBlank(message = "The name of the application cannot be empty")
    private String app;

    @NotBlank(message = "URI cannot be empty")
    private String uri;

    @NotBlank(message = "User IP cannot be empty")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "The time of sending the request cannot be empty")
    private LocalDateTime timestamp;

}
