package com.example.base_spring_boot.models.dtos.res;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private final String type = "Bearer";
}
