package com.example.base_spring_boot.models.services;

import com.example.base_spring_boot.models.entities.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenService {
    // Tạo Refresh Token mới cho người dùng (Yêu cầu Câu 2)
    RefreshToken createRefreshToken(Long userId);

    // Xác minh Refresh Token hết hạn (Yêu cầu Câu 2)
    RefreshToken verifyExpiration(RefreshToken token);

    // Tìm kiếm Refresh Token bằng chuỗi token (Yêu cầu Câu 2)
    Optional<RefreshToken> findByToken(String token);

    // Thu hồi/Xóa tất cả các Refresh Token của User dựa theo ID (Yêu cầu Câu 3)
    void deleteByUserId(Long userId);
}
