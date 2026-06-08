package com.example.base_spring_boot.models.services.impl;

import com.example.base_spring_boot.exceptions.HttpBadRequestException;
import com.example.base_spring_boot.exceptions.HttpNotFoundException;
import com.example.base_spring_boot.models.entities.RefreshToken;
import com.example.base_spring_boot.models.entities.User;
import com.example.base_spring_boot.models.repositories.IRefreshTokenRepository;
import com.example.base_spring_boot.models.repositories.IUserRepository;
import com.example.base_spring_boot.models.services.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    @Value("${jwt.expired.refresh}")
    private Long refreshExpirationMs;

    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        // Tìm kiếm User theo ID, nếu không tìm thấy ném lỗi HttpNotFoundException
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFoundException("User not found with id: " + userId));

        // Trước khi tạo mới, chúng ta có thể xóa các Refresh Token cũ của user này để tối ưu DB
        refreshTokenRepository.deleteByUser(user);

        // Tạo ra một UUID mới, thiết lập thời gian hết hạn và trạng thái chưa revoked (Yêu cầu Câu 2)
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        // Kiểm tra xem Refresh Token có bị thu hồi trước đó chưa
        if (token.isRevoked()) {
            throw new HttpBadRequestException("Refresh token has been revoked");
        }

        // Kiểm tra xem Refresh Token còn hạn hay không. Nếu hết hạn, ném ra Exception và xóa khỏi CSDL (Yêu cầu Câu 2)
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new HttpBadRequestException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFoundException("User not found with id: " + userId));
        // Xóa toàn bộ bản ghi RefreshToken của User đó trong CSDL (Yêu cầu Câu 3)
        refreshTokenRepository.deleteByUser(user);
    }
}
