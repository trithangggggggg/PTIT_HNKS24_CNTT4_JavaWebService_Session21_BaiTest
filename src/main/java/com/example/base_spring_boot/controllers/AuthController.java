package com.example.base_spring_boot.controllers;

import com.example.base_spring_boot.models.dtos.req.LoginReq;
import com.example.base_spring_boot.models.dtos.req.RegisterReq;
import com.example.base_spring_boot.models.dtos.req.TokenRefreshRequest; // Import DTO mới
import com.example.base_spring_boot.models.dtos.res.TokenRefreshResponse; // Import DTO mới
import com.example.base_spring_boot.models.dtos.wrapper.DataRes;
import com.example.base_spring_boot.models.entities.RefreshToken; // Import Entity mới
import com.example.base_spring_boot.models.services.IAuthService;
import com.example.base_spring_boot.models.services.IRefreshTokenService; // Import service mới
import com.example.base_spring_boot.security.jwt.JwtUtils; // Import JwtUtils để sinh access token mới
import com.example.base_spring_boot.security.principal.MyUserDetails; // Import MyUserDetails để lấy user từ context
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder; // Import context bảo mật
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final IAuthService authService;
    private final IRefreshTokenService refreshTokenService; // Tiêm IRefreshTokenService
    private final JwtUtils jwtUtils; // Tiêm JwtUtils

    /**
     * @param req LoginReq
     * @apiNote handle login with { username , password }
     */
    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody LoginReq req)
    {
        return ResponseEntity.status(HttpStatus.OK).body(
                DataRes.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(authService.login(req))
                        .build()
        );
    }

    /**
     * @param req RegisterReq
     * @apiNote handle register with { fullName , username , password }
     */
    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @RequestBody RegisterReq req)
    {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                DataRes.builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data("Register successfully")
                        .build()
        );
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> handleRefreshToken(@Valid @RequestBody TokenRefreshRequest req)
    {
        // Kiểm tra xem refresh token có tồn tại trong CSDL không
        RefreshToken token = refreshTokenService.findByToken(req.getRefreshToken())
                .orElseThrow(() -> new com.example.base_spring_boot.exceptions.HttpBadRequestException("Refresh token is not valid or does not exist!"));

        // Xác minh xem Refresh Token có hết hạn không
        refreshTokenService.verifyExpiration(token);

        // Tạo ra Access Token mới bằng JwtUtils
        String newAccessToken = jwtUtils.generateToken(token.getUser().getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(
                DataRes.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(TokenRefreshResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(token.getToken())
                                .build())
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> handleLogout()
    {
        // Lấy thông tin User hiện tại từ Security Context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof MyUserDetails userDetails) {
            // Truy vấn và xóa toàn bộ bản ghi RefreshToken của User đó trong CSDL
            refreshTokenService.deleteByUserId(userDetails.getUser().getId());
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                DataRes.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Logout successfully, refresh tokens revoked.")
                        .build()
        );
    }

}