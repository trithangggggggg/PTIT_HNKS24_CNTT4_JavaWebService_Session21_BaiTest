package com.example.base_spring_boot.models.dtos.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TokenRefreshRequest {
    @NotBlank(message = "refreshToken must be not empty")
    private String refreshToken;
}
